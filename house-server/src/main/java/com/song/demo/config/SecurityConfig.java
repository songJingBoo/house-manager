package com.song.demo.config;

import com.song.demo.filter.AuthFilter;
import com.song.demo.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

//@EnableGlobalMethodSecurity(prePostEnabled = true) // 开启权限配置
@EnableWebSecurity // 想去掉光注释没用，还得把spring-boot-starter-security依赖干掉
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private AuthFilter authFilter;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // CRSF禁用
                .csrf().disable()
                // 基于token，不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 认证失败处理类
                .exceptionHandling()
                .accessDeniedHandler(restfulAccessDeniedHandler()) // 403
                .authenticationEntryPoint(restAuthenticationEntryPoint()) // 666
                .and()

                // 过滤请求
                .authorizeRequests()
                .antMatchers("/files/**").permitAll() // 静态资源访问路径
                .antMatchers("/permit/**", "/**/permit/**").permitAll()
//                .antMatchers("/user-api/detail").hasAuthority("admin")
//                .antMatchers("/user-api/detail").hasRole("abc")
                .anyRequest().authenticated()
                .and()

//
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .headers()
                .frameOptions().disable()
                .cacheControl().disable();
    }

    @Bean
    public RestfulAccessDeniedHandler restfulAccessDeniedHandler() {
        return new RestfulAccessDeniedHandler();
    }


    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

    // 需定义用，否则UserServiceImpl当中用不了
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // 必须有，否则密码无法完成加密、校验
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 因为前台传递的md5, 数据库存储加盐后的MD5，所以需要使用自定义校验
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceImpl)
                .passwordEncoder(getPasswordEncoder()); // 直接使用BCrypt校验
    }
}
