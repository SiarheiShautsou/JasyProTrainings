package com.sheva.security;

import com.sheva.filter.AuthenticationTokenFilter;
import com.sheva.security.jwt.JwtTokenHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    private final JwtTokenHelper tokenUtils;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Bean
    public AuthenticationTokenFilter authenticationTokenFilterBean(AuthenticationManager authenticationManager) throws Exception {
        AuthenticationTokenFilter authenticationTokenFilter = new AuthenticationTokenFilter(tokenUtils, userDetailsService);
        authenticationTokenFilter.setAuthenticationManager(authenticationManager);
        return authenticationTokenFilter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable()
                .exceptionHandling()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/v3/api-docs/**", "/configuration/ui/**", "/swagger-resources/**",
                        "/configuration/security/**", "/swagger-ui/**", "/webjars/**", "/javainuse-openapi/**").permitAll()
                .antMatchers(HttpMethod.GET, "/swagger-ui/index").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/data/user/**").permitAll()
                .antMatchers("/data/roles/**").permitAll()
                .antMatchers("/data/gyms/**").permitAll()
                .antMatchers("/data/training_types/**").permitAll()
                .antMatchers("/data/trainings/**").permitAll()
                .antMatchers("/data/subscription/**").permitAll()
                .antMatchers("/body-params/**").permitAll()
                .antMatchers("/admin/**").hasAnyRole("ADMIN", "MODERATOR")
                .anyRequest()
                .authenticated();

        // Custom JWT based authentication
        httpSecurity
                .addFilterBefore(authenticationTokenFilterBean(authenticationManagerBean()), UsernamePasswordAuthenticationFilter.class);
    }

    //For swagger access only
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/v3/api-docs/**", "/configuration/ui/**", "/swagger-resources/**",
                        "/configuration/security/**", "/swagger-ui/**", "/webjars/**", "/javainuse-openapi/**");
    }
}
