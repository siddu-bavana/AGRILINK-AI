package com.agrilink.controller;

import com.agrilink.model.User; import com.agrilink.repository.UserRepository; import com.agrilink.security.JwtService;
import jakarta.validation.Valid; import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.time.LocalDateTime;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users; private final JwtService jwt; private final PasswordEncoder passwords;
    AuthController(UserRepository u,JwtService j,PasswordEncoder p){users=u;jwt=j;passwords=p;}

    public record Register(
        @NotBlank(message="Name is required") @Size(max=100) String name,
        @NotBlank @Pattern(regexp="[0-9]{10}",message="Enter a valid 10 digit phone number") String mobile,
        @NotBlank @Size(min=6,max=72,message="Password must contain at least 6 characters") String password,
        String language,
        @Size(max=100) String district
    ){}
    public record Login(
        @NotBlank @Pattern(regexp="[0-9]{10}",message="Enter a valid 10 digit phone number") String mobile,
        @NotBlank String password
    ){}

    @PostMapping("/register") ResponseEntity<?> register(@Valid @RequestBody Register r){
        User u=users.findByMobile(r.mobile()).orElseGet(User::new);
        if(u.id!=null && u.passwordHash!=null) return ResponseEntity.status(409).body(Map.of("message","An account already exists for this phone number. Please sign in."));
        u.name=r.name().trim();u.mobile=r.mobile();u.passwordHash=passwords.encode(r.password());u.language=r.language()==null?"en":r.language();u.district=r.district();u.role=User.Role.FARMER;u.lastLoginAt=LocalDateTime.now();u.loginCount=(u.loginCount==null?0:u.loginCount)+1;users.save(u);
        return ResponseEntity.status(201).body(response(u));
    }

    @PostMapping("/login") ResponseEntity<?> login(@Valid @RequestBody Login r){
        User u=users.findByMobile(r.mobile()).orElse(null);
        if(u==null || u.passwordHash==null || !passwords.matches(r.password(),u.passwordHash)) return ResponseEntity.status(401).body(Map.of("message","Incorrect phone number or password"));
        u.lastLoginAt=LocalDateTime.now();u.loginCount=(u.loginCount==null?0:u.loginCount)+1;users.save(u);
        return ResponseEntity.ok(response(u));
    }

    @GetMapping("/me") ResponseEntity<?> me(Authentication authentication){
        if(authentication==null || authentication.getDetails()==null) return ResponseEntity.status(401).body(Map.of("message","Please sign in"));
        return users.findById((Long)authentication.getDetails()).<ResponseEntity<?>>map(u->ResponseEntity.ok(userData(u))).orElseGet(()->ResponseEntity.notFound().build());
    }

    private Map<String,Object> userData(User u){
        Map<String,Object> data=new LinkedHashMap<>();
        data.put("id",u.id);data.put("name",u.name);data.put("mobile",u.mobile);data.put("role",u.role);data.put("language",u.language);data.put("district",u.district);data.put("latitude",u.latitude);data.put("longitude",u.longitude);data.put("detectedLocation",u.detectedLocation);data.put("loginCount",u.loginCount==null?0:u.loginCount);
        return data;
    }
    private Map<String,Object> response(User u){return Map.of("token",jwt.create(u.mobile,u.role.name(),u.id),"user",userData(u));}
}
