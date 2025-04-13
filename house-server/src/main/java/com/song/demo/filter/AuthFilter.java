package com.song.demo.filter;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.song.demo.common.RedisService;
import com.song.demo.service.impl.CustomUserDetails;
import com.song.demo.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private RedisService redisService;

    /**
     * 仅作对token是否有效进行校验
     * 如果未携带token会被放行，具体是否有权限，需要在SecurityConfig当中进行配置
     * 如果login接口带了token，也会被校验
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 获取进入的路由
        String requestUrl = request.getRequestURI();
        // 放行无须校验的路由
        if (requestUrl.contains("/permit")) {
            filterChain.doFilter(request,response);
            return;
        }

        String authorization = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(authorization)) {
            String token = authorization.substring(7);
            Claims claims = JwtUtils.getClaimsFromToken(token);
            if (claims != null) {
                CustomUserDetails userDetail = (CustomUserDetails) redisService.get(claims.getSubject());
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                // 更新redis里用户信息的过期时间
                redisService.expire(claims.getSubject(), 60 * 30);
            }
        }

        filterChain.doFilter(request, response);
    }
}
