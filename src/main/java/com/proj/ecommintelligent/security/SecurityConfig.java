package com.proj.ecommintelligent.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //definir les utilisateurs en memoire dans un premier temps
    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager(){
        PasswordEncoder passwordEncoder = passwordEncoder();
        return new InMemoryUserDetailsManager(
                User.withUsername("user1")
                        .password(passwordEncoder.encode("user123")).
                        authorities("USER").
                        build(),
                User.withUsername("admin").
                        password(passwordEncoder.encode("admin123"))
                        .authorities("ADMIN","USER")
                        .build()
        );

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();

    }

    //Configuration principale de sécurité
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception{
        return http
                //statless, pas de session serveur
                .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //si on utilise une authentification stateless , il faut desactiver le csrf
                .csrf(csrf -> csrf.disable())
                //autoriser les endpoints publics
                .authorizeHttpRequests(ar->ar
                        .requestMatchers("/api/products/**", "/api/carts/**", "/api/orders/**", "/h2-console/**")
                        .permitAll()
                        .anyRequest().authenticated())
                //permettre l'utilisation de la console H2
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                // Authentification Basic dans le premier ensuite on la remplace avec JWT
                .httpBasic(Customizer.withDefaults())
                .build();


    }


}