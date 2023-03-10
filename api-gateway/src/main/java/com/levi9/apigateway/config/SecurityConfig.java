package com.levi9.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	@Bean
	public SecurityWebFilterChain filterChain(ServerHttpSecurity http,
			KeyAuthenticationManager keyAuthenticationManager, KeyAuthenticationConverter keyAuthenticationConverter) {
		final AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(keyAuthenticationManager);
		authenticationWebFilter.setServerAuthenticationConverter(keyAuthenticationConverter);

		return http.authorizeExchange().pathMatchers("/social-network/**").permitAll().anyExchange().authenticated()
				.and().addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION).httpBasic()
				.disable().csrf().disable().formLogin().disable().logout().disable().build();
	}

}