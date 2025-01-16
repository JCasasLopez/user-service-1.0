package init.config.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import init.service.CustomUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	CustomUserDetailsManager customUserDetailsManager;
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsManager);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	
    	http
    		
    		.httpBasic(Customizer.withDefaults())
    	
    		.authorizeHttpRequests(authorize -> authorize
						                .requestMatchers("/public/**").permitAll() 
						                .anyRequest().authenticated())
    		
    		/*.cors(c -> {CorsConfigurationSource source = request -> {
    			CorsConfiguration config = new CorsConfiguration();
    			config.setAllowedOrigins(List.of("biblioteca-Enrique-Granados.es"));
    			config.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT"));
    			return config;
    			};
    			c.configurationSource(source);
    		})*/
    	
    		.csrf(csrf -> csrf.disable());

        return http.build();
    }

    
    
    	
}
