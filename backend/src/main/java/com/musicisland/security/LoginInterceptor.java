package com.musicisland.security;

import com.musicisland.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader(Constants.TOKEN_HEADER);

        if (header == null || !header.startsWith(Constants.TOKEN_PREFIX)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"Not logged in\"}");
            return false;
        }

        String token = header.substring(Constants.TOKEN_PREFIX.length());
        if (!jwtUtils.validateToken(token)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"Token invalid or expired\"}");
            return false;
        }

        // Store user info in request attributes
        request.setAttribute("userId", jwtUtils.getUserIdFromToken(token));
        request.setAttribute("username", jwtUtils.parseToken(token).get("username"));
        request.setAttribute("role", jwtUtils.parseToken(token).get("role"));

        return true;
    }
}
