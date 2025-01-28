package init.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
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
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	UserDetailsService userDetailsService;
	PasswordEncoder passwordEncoder;
	JwtAuthenticationFilter jwtAuthenticationFilter;
	CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
	CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
	CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	
    public SecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder,
			JwtAuthenticationFilter jwtAuthenticationFilter,
			CustomAuthenticationFailureHandler customAuthenticationFailureHandler,
			CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler,
			CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
		this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
		this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
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
	AuthenticationManager authenticationManager() {
	    return new ProviderManager(daoAuthenticationProvider(userDetailsService, passwordEncoder));
	}

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	
    	UsernamePasswordAuthenticationFilter loginFilter = new UsernamePasswordAuthenticationFilter();
        loginFilter.setAuthenticationManager(authenticationManager());
        loginFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);
        loginFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);
        loginFilter.setFilterProcessesUrl("/login");
        
        http
        	.exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(customAuthenticationEntryPoint))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sessMang -> sessMang.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class) 
            //Deshabilito LogoutFilter poque voy a usar mi implementación personalizada
            .logout(logout -> logout.disable()) 
            .authorizeHttpRequests(authorize -> authorize
            							.requestMatchers("/altaUsuario", "/usuarioExiste",
            									"/usuarioEsAdmin").permitAll() 
            							.requestMatchers("/borrarUsuario", "/cambiarPassword", 
            									"/crearAdmin", "/logout", "/desbloquearCuenta").authenticated()
            							.anyRequest().authenticated()
            );
            return http.build();
    }
	
}
