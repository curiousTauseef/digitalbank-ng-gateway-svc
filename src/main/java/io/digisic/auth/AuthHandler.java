package io.digisic.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class AuthHandler {
	
	@Autowired
	private Environment environment;
	
	private static final String CLIENT_ID = "gateway";
	private static final String SCOPE = "openid";
	private static final String GRANT_TYPE = "password";
	
//	@Value("${spring.security.oauth2.client.registration." + CLIENT_ID + ".client-secret}")
	private String CLIENT_SECRET;
	
//	@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
	private String PROVIDER_URI;

		
	public Mono<ServerResponse> auth(ServerRequest request) {
		
		try {
			
			CLIENT_SECRET = environment.getRequiredProperty("spring.security.oauth2.client.registration." + CLIENT_ID + ".client-secret");
			PROVIDER_URI = environment.getRequiredProperty("${spring.security.oauth2.resourceserver.jwt.issuer-uri}");
			
		} catch (IllegalStateException e) {
			
			return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).build();
			
		}
		
			
		
		//	String authURI = "http://localhost:8080/realms/digitalbank" + "/protocol/openid-connect/token";
			String authURI = PROVIDER_URI + "/protocol/openid-connect/token";
			
			
			return request.exchange().getFormData().flatMap(s -> {
				
				// check is username and password was sent in request
				if (s.getFirst("username") == null || s.getFirst("password") == null ||
					s.getFirst("username").isBlank() || s.getFirst("password").isBlank() ||
					s.getFirst("username").isEmpty() || s.getFirst("password").isEmpty()) {
					
				
					
					return ServerResponse.badRequest()
										 .contentType(MediaType.APPLICATION_JSON)
										 .body(Mono.just("Username and Password must be provided in request."), String.class);
				}
			
				s.add("client_id", CLIENT_ID);
			//	s.add("client_secret", "9b43a1aa-463c-41a6-b963-a4f495b260c0");
				s.add("client_secret", CLIENT_SECRET);
				s.add("scope", SCOPE);
				s.add("grant_type", GRANT_TYPE);
				
				Mono<String> response = WebClient.create().post().uri(authURI)
							 .contentType(MediaType.APPLICATION_FORM_URLENCODED)
							 .body(BodyInserters.fromFormData(s))
							 .retrieve().bodyToMono(String.class);
				
				return ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(response, String.class);
			});
			
			/*.onErrorResume(e -> Mono.just("Error " + e.getMessage())
			          .flatMap(l -> ServerResponse.ok()
			                  .contentType(MediaType.APPLICATION_JSON)
			                  .bodyValue(l)))*/

	  }
	

}
