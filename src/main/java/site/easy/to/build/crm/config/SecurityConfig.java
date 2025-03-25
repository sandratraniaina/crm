package site.easy.to.build.crm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import site.easy.to.build.crm.config.api.ApiAuthenticationEntryPoint;
import site.easy.to.build.crm.config.api.ApiUserDetails;
import site.easy.to.build.crm.config.api.JWTFilter;
import site.easy.to.build.crm.config.oauth2.CustomOAuth2UserService;
import site.easy.to.build.crm.config.oauth2.OAuthLoginSuccessHandler;

@Configuration
public class SecurityConfig {

	private final OAuthLoginSuccessHandler oAuth2LoginSuccessHandler;

	private final CustomOAuth2UserService oauthUserService;

	private final CrmUserDetails crmUserDetails;

	private final CustomerUserDetails customerUserDetails;

	private final JWTFilter jwtFilter;

	private final ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;

	private final ApiUserDetails apiUserDetails;

	// Constant
	private static final String LOGIN_ROUTE = "/login";
	private static final String CUSTOMER_LOGIN_ROUTE = "/customer-login";
	private static final String MANAGER = "MANAGER";

	@Autowired
	public SecurityConfig(OAuthLoginSuccessHandler oAuth2LoginSuccessHandler, CustomOAuth2UserService oauthUserService,
			CrmUserDetails crmUserDetails,
			CustomerUserDetails customerUserDetails, JWTFilter jwtFilter,
			ApiAuthenticationEntryPoint apiAuthenticationEntryPoint,
			ApiUserDetails apiUserDetails) {
		this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
		this.oauthUserService = oauthUserService;
		this.crmUserDetails = crmUserDetails;
		this.customerUserDetails = customerUserDetails;
		this.jwtFilter = jwtFilter;
		this.apiAuthenticationEntryPoint = apiAuthenticationEntryPoint;
		this.apiUserDetails = apiUserDetails;
	}

	@Bean
	@Order(3)
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		HttpSessionCsrfTokenRepository httpSessionCsrfTokenRepository = new HttpSessionCsrfTokenRepository();
		httpSessionCsrfTokenRepository.setParameterName("csrf");

		http.csrf(csrf -> csrf
				.csrfTokenRepository(httpSessionCsrfTokenRepository));

		http.authorizeHttpRequests(authorize -> authorize

				.requestMatchers("/register/**").permitAll()
				.requestMatchers("/set-employee-password/**").permitAll()
				.requestMatchers("/change-password/**").permitAll()
				.requestMatchers("/font-awesome/**").permitAll()
				.requestMatchers("/fonts/**").permitAll()
				.requestMatchers("/images/**").permitAll()
				.requestMatchers("/save").permitAll()
				.requestMatchers("/js/**").permitAll()
				.requestMatchers("/css/**").permitAll()
				.requestMatchers(AntPathRequestMatcher.antMatcher("/**/manager/**")).hasRole(MANAGER)
				.requestMatchers("/employee/**").hasAnyRole(MANAGER, "EMPLOYEE")
				.requestMatchers("/customer/**").hasRole("CUSTOMER")
				.anyRequest().authenticated())

				.formLogin(form -> form
						.loginPage(LOGIN_ROUTE)
						.loginProcessingUrl(LOGIN_ROUTE)
						.defaultSuccessUrl("/", true)
						.failureUrl(LOGIN_ROUTE)
						.permitAll())
				.userDetailsService(crmUserDetails)
				.oauth2Login(oauth2 -> oauth2
						.loginPage(LOGIN_ROUTE)
						.userInfoEndpoint(userInfo -> userInfo
								.userService(oauthUserService))
						.successHandler(oAuth2LoginSuccessHandler))
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl(LOGIN_ROUTE)
						.permitAll())
				.exceptionHandling(exception -> 
					exception.accessDeniedHandler(accessDeniedHandler())
				);

		return http.build();
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return new CustomAccessDeniedHandler();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain customerSecurityFilterChain(HttpSecurity http) throws Exception {

		HttpSessionCsrfTokenRepository httpSessionCsrfTokenRepository = new HttpSessionCsrfTokenRepository();
		httpSessionCsrfTokenRepository.setParameterName("csrf");

		http.csrf(csrf -> csrf
				.csrfTokenRepository(httpSessionCsrfTokenRepository));

		http.securityMatcher("/customer-login/**").authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/set-password/**").permitAll()
				.requestMatchers("/font-awesome/**").permitAll()
				.requestMatchers("/fonts/**").permitAll()
				.requestMatchers("/images/**").permitAll()
				.requestMatchers("/js/**").permitAll()
				.requestMatchers("/css/**").permitAll()
				.requestMatchers(AntPathRequestMatcher.antMatcher("/**/manager/**")).hasRole(MANAGER)
				.anyRequest().authenticated())

				.formLogin(form -> form
						.loginPage(CUSTOMER_LOGIN_ROUTE)
						.loginProcessingUrl(CUSTOMER_LOGIN_ROUTE)
						.failureUrl(CUSTOMER_LOGIN_ROUTE)
						.defaultSuccessUrl("/", true)
						.permitAll())
				.userDetailsService(customerUserDetails)
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl(CUSTOMER_LOGIN_ROUTE)
						.permitAll());

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Order(1)
	public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
		http
				.securityMatcher("/api/**")
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/login").permitAll()
						.anyRequest().authenticated())
				.exceptionHandling(exception -> exception
						.authenticationEntryPoint(apiAuthenticationEntryPoint) // Use the custom entry point
				)
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder)
			throws Exception {
		AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		authBuilder
				.userDetailsService(apiUserDetails)
				.passwordEncoder(passwordEncoder);
		return authBuilder.build();
	}
}