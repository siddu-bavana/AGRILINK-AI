package com.agrilink.controller;

import com.agrilink.model.User;
import com.agrilink.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/location")
public class LocationController {
    private final UserRepository users;
    private final ObjectMapper json;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(4)).build();

    public LocationController(UserRepository users, ObjectMapper json) {
        this.users = users;
        this.json = json;
    }

    public record LocationRequest(Double latitude, Double longitude) {}

    @PostMapping("/detect")
    public Map<String,Object> detect(@RequestBody LocationRequest request, Authentication authentication) {
        if (request.latitude()==null || request.longitude()==null ||
            !Double.isFinite(request.latitude()) || !Double.isFinite(request.longitude()) ||
            request.latitude() < -90 || request.latitude() > 90 ||
            request.longitude() < -180 || request.longitude() > 180) {
            throw new IllegalArgumentException("Invalid device coordinates");
        }
        if (authentication==null || authentication.getDetails()==null) throw new SecurityException("Please sign in");

        User user = users.findById((Long) authentication.getDetails()).orElseThrow();
        Place place = reverseGeocode(request.latitude(), request.longitude());
        user.latitude = request.latitude();
        user.longitude = request.longitude();
        user.detectedLocation = place.name();
        if (place.district()!=null && !place.district().isBlank()) user.district = place.district();
        users.save(user);

        Map<String,Object> response = new LinkedHashMap<>();
        response.put("location", user.detectedLocation);
        response.put("district", user.district);
        response.put("latitude", user.latitude);
        response.put("longitude", user.longitude);
        response.put("message", "Device location saved to your account");
        return response;
    }

    private Place reverseGeocode(double latitude, double longitude) {
        String fallback = String.format("%.5f, %.5f", latitude, longitude);
        try {
            URI uri = URI.create("https://nominatim.openstreetmap.org/reverse?format=jsonv2&zoom=14&addressdetails=1&lat=" + latitude + "&lon=" + longitude);
            HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(6))
                .header("Accept", "application/json")
                .header("User-Agent", "AgriLinkAI-Hackathon/1.0")
                .GET().build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode()!=200) return new Place(fallback, null);
            JsonNode address = json.readTree(response.body()).path("address");
            String locality = first(address,"village","town","city","municipality","suburb","county");
            String district = first(address,"state_district","county","state");
            String state = first(address,"state");
            String name = joinDistinct(locality, district, state);
            return new Place(name.isBlank()?fallback:name, district);
        } catch (Exception ignored) {
            return new Place(fallback, null);
        }
    }

    private String first(JsonNode node, String... fields) {
        for (String field: fields) {
            String value = node.path(field).asText("").trim();
            if (!value.isBlank()) return value;
        }
        return null;
    }

    private String joinDistinct(String... values) {
        StringBuilder result = new StringBuilder();
        for (String value: values) {
            if (value==null || value.isBlank()) continue;
            if (result.toString().toLowerCase().contains(value.toLowerCase())) continue;
            if (!result.isEmpty()) result.append(", ");
            result.append(value);
        }
        return result.toString();
    }

    private record Place(String name, String district) {}
}
