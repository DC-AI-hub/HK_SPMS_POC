package com.spms.backend.config;

import com.spms.backend.service.idm.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private UserService userService;

    @Bean
    public RetryTemplate getRetryTemplate(){
        return  new RetryTemplate();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        config.setAllowedMethods(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // 放行 Swagger 相关路径
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // API 路径要求认证但禁用重定向
                        .requestMatchers("/api/v1/**").authenticated()

                        // OAuth2 相关路径允许所有访问（由Spring Security处理跳转）
                        .requestMatchers("/oauth2/**", "/login/**").permitAll()

                        // 其他路径根据需求配置
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(oidcUserService())
                        )
                        .successHandler(new AuthenticationSuccessHandler() {
                            @Override
                            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                                System.out.println(request.getRequestURI());
                                response.sendRedirect("http://localhost:5173/");
                            }
                        })
                ).oidcLogout(c ->
                        c.backChannel(Customizer.withDefaults())).csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        // 为API路径配置401响应（禁用重定向）

                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/api/v1/**")
                        )
                )
                .csrf(AbstractHttpConfigurer::disable)

        ;
        return http.build();
    }



    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        return userRequest -> {
            // Create User from OIDC token claims
            OidcUser oidcUser = new OidcUserService().loadUser(userRequest);
            String provider = userRequest.getClientRegistration().getRegistrationId();
            String providerId = oidcUser.getSubject();
            var linkedUserInfo = userService.getUserByProviderInformation(provider,providerId);
            if(linkedUserInfo.isEmpty()){
                linkedUserInfo = userService.linkUserToProvider(provider,providerId,oidcUser.getName());
            }
            if(linkedUserInfo.isPresent()) {
                //TODO: fetch user role
                SpmsOidcUser spmsOidcUser = new SpmsOidcUser(
                        oidcUser.getAuthorities(),
                        oidcUser.getIdToken(),
                        oidcUser.getUserInfo(),
                        IdTokenClaimNames.SUB,
                        linkedUserInfo.get()
                );
                return spmsOidcUser;
            }
            // TODO: deny the user
            return oidcUser;
        };
    }
    /*
    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(this.clientRegistrationRepository);

        // Sets the location that the End-User's User Agent will be redirected to
        // after the logout has been performed at the Provider
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");

        return oidcLogoutSuccessHandler;
    }*/
}
