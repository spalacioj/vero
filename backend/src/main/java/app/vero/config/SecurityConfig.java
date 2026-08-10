package app.vero.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import app.vero.auth.VeroUserPrincipal;
import app.vero.user.UserAccountRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}

	@Bean
	UserDetailsService userDetailsService(UserAccountRepository userAccountRepository) {
		return email -> userAccountRepository.findByEmail(email)
				.map(VeroUserPrincipal::from)
				.orElseThrow(() -> new UsernameNotFoundException("Invalid email or password."));
	}

	@Bean
	AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return new ProviderManager(provider);
	}

	@Bean
	SecurityContextRepository securityContextRepository() {
		return new HttpSessionSecurityContextRepository();
	}

	@Bean
	SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new SessionFixationProtectionStrategy();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityContextRepository securityContextRepository)
			throws Exception {
		return http
				.csrf(csrf -> csrf
						.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
						.ignoringRequestMatchers("/api/auth/register", "/api/auth/login"))
				.securityContext(context -> context.securityContextRepository(securityContextRepository))
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
						.sessionFixation(sessionFixation -> sessionFixation.migrateSession()))
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/csrf").permitAll()
						.anyRequest().authenticated())
				.exceptionHandling(exceptionHandling -> exceptionHandling
						.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
				.httpBasic(httpBasic -> httpBasic.disable())
				.formLogin(formLogin -> formLogin.disable())
				.build();
	}
}
