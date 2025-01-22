package init.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import init.config.security.filter.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

	UserDetailsService userDetailsService;
	PasswordEncoder passwordEncoder;
	JwtAuthenticationFilter jwtAuthenticationFilter;
	
    public SecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder,
    												JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
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
            .sessionManagement(sessMang -> sessMang.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(daoAuthenticationProvider(userDetailsService, passwordEncoder))
            .authorizeHttpRequests(authorize -> authorize
            							.requestMatchers("/public/**").permitAll() 
            							.anyRequest().authenticated()
            )
            .build();
    }
	
}
