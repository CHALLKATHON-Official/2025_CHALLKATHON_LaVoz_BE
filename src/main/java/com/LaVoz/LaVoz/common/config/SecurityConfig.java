package com.LaVoz.LaVoz.common.config;

import com.LaVoz.LaVoz.common.exception.AuthenticationException;
import com.LaVoz.LaVoz.common.security.CustomUserDetailsService;
import com.LaVoz.LaVoz.common.security.JwtAuthorizationFilter;
import com.LaVoz.LaVoz.common.security.JwtUtil;
import com.LaVoz.LaVoz.repository.MemberRepository;
import com.LaVoz.LaVoz.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.LaVoz.LaVoz.common.security.JwtUserLoginFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final CustomUserDetailsService customUserDetailsService;
    private final TokenService tokenService;
    private final AuthenticationException authenticationException;
    private final MemberRepository memberRepository;

    @Bean
    public SecurityFilterChain commonFilterChain(HttpSecurity http) throws Exception{
        //csrf, Form 로그인 방식, http basic 인증 방식 disable (세션 방식이 아닌 jwt 방식 로그인을 사용하기 때문)
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf((auth) -> auth.disable())
                .formLogin((auth) -> auth.disable())
                .httpBasic((auth) -> auth.disable())
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/member/login", "/member/register", "/swagger-ui/**", "/member/check-duplicated-loginId").permitAll()
                        .requestMatchers(HttpMethod.GET, "/boards/**").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/boards/**").authenticated()
                        .requestMatchers(HttpMethod.POST,"/boards/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE,"/boards/**").authenticated()
                        .requestMatchers("/member/test", "/organization/**", "/organizations/**", "/notes/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterAt(jwtUserLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationException)
                );

        return http.build();
    }

    @Bean
    public JwtUserLoginFilter jwtUserLoginFilter() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        JwtUserLoginFilter jwtUserLoginFilter = new JwtUserLoginFilter(
                providerManager,
                jwtUtil,
                objectMapper,
                tokenService);
        jwtUserLoginFilter.setAuthenticationManager(providerManager);
        jwtUserLoginFilter.setFilterProcessesUrl("/member/login");
        return jwtUserLoginFilter;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthorizationFilter jwtFilter() {
        return JwtAuthorizationFilter.builder()
                .jwtUtil(jwtUtil)
                .authenticationException(authenticationException)
                .memberRepository(memberRepository)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*")); // 모든 origin 허용
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Set-Cookie"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
