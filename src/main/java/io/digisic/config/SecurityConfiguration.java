package io.digisic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {
	
	private static final String[] GATEWAY_PULIC_RESOURCES = {
			
			"/api/v1/auth/**",
			"/actuator/**"
	};
	
	
	@Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        http.authorizeExchange()
            .pathMatchers(GATEWAY_PULIC_RESOURCES).permitAll()
            .anyExchange().authenticated()
            .and().oauth2Login()
            .and().csrf().disable()
            .oauth2ResourceServer().jwt();
                
        return http.build();
	}

}
