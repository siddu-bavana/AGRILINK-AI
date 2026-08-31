package com.agrilink;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL","spring.jpa.hibernate.ddl-auto=create-drop"})
@AutoConfigureMockMvc
class AgriLinkIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test void signUpSignInAndSearchAreStored() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
            .content("{\"mobile\":\"9876543210\",\"name\":\"Ramesh\",\"password\":\"farmer123\",\"district\":\"Ongole\"}"))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.user.mobile").value("9876543210"));
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"mobile\":\"9876543210\",\"password\":\"wrong-password\"}"))
            .andExpect(status().isUnauthorized());
        String login=mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"mobile\":\"9876543210\",\"password\":\"farmer123\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.user.loginCount").value(2))
            .andReturn().getResponse().getContentAsString();
        JsonNode node=json.readTree(login);String token=node.get("token").asText();
        mvc.perform(post("/api/services/7/analyze").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON)
            .content("{\"values\":[\"Tomato\",\"2000\",\"A\",\"Ongole\",\"2\"],\"language\":\"en\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.recordId").isNumber()).andExpect(jsonPath("$.results.length()").value(3));
        mvc.perform(post("/api/services/2/analyze").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON)
            .content("{\"values\":[\"2\",\"45\",\"2000\",\"50000\"],\"language\":\"en\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.results[0]").value("₹90,000")).andExpect(jsonPath("$.results[2]").value("₹40,000"));
        mvc.perform(post("/api/location/detect").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON)
            .content("{\"latitude\":100,\"longitude\":80}"))
            .andExpect(status().isBadRequest());
        mvc.perform(get("/api/services/history").header("Authorization","Bearer "+token))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].userId").value(node.get("user").get("id").asLong()));
        mvc.perform(get("/robots.txt")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("/sitemap.xml")));
        mvc.perform(get("/sitemap.xml")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("<loc>http://localhost/</loc>")));
    }
}
