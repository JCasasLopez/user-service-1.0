package init.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	UserDetailsService userDetailsService;
	PasswordEncoder passwordEncoder;
	
    public SecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
	}

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService, 
    																	PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())  
            .authorizeHttpRequests(authorize -> authorize
            							.requestMatchers("/public/**").permitAll() 
            							.anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .build();
    }
	
}
