package com.agrilink.security;

import com.agrilink.repository.UserRepository;
import jakarta.servlet.*; import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException; import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwt; private final UserRepository users;
    public JwtFilter(JwtService jwt, UserRepository users){this.jwt=jwt;this.users=users;}
    @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain) throws ServletException,IOException {
        String h=req.getHeader("Authorization");
        if(h!=null&&h.startsWith("Bearer ")) try {
            var c=jwt.parse(h.substring(7)); var user=users.findByMobile(c.getSubject()).orElseThrow();
            var auth=new UsernamePasswordAuthenticationToken(user.mobile,null,List.of(new SimpleGrantedAuthority("ROLE_"+user.role.name())));
            auth.setDetails(user.id); SecurityContextHolder.getContext().setAuthentication(auth);
        } catch(Exception ignored) { SecurityContextHolder.clearContext(); }
        chain.doFilter(req,res);
    }
}
