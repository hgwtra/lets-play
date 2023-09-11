package com.example.letsplay.filter;

import com.example.letsplay.service.JwtService;
import com.example.letsplay.service.UserInfoService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component

public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
   @Autowired
   private UserInfoService userInfoService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String authHeader = request.getHeader("Authorization");
            String username = null;
            String token = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                    try {
                        username = jwtService.extractUsername(token);
                    } catch(Exception e) {
                        sendErrorResponse(response, "Invalid JWT token.");
                        return;
                    }
            }


            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
               UserDetails userDetails = userInfoService.loadUserByUsername(username);
                //System.out.println(userDetails);
                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } else if (!request.getRequestURI().equals("/users/login") &&
                    !request.getRequestURI().equals("/users/create") &&
                    !request.getRequestURI().equals("/products") &&
                    request.getRequestURI().matches("/products/get/\\d+")) {  // For URIs with variable parts like {id}, you can use regex. Here \d+ matches one or more digits.
                sendErrorResponse(response, "Authentication is needed");
                return;
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            sendErrorResponse(response, "Invalid JWT token or User is not authenticated");
        }

    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("Error: " + message);
    }
}


