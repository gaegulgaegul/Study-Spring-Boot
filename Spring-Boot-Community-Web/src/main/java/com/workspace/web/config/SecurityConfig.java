package com.workspace.web.config;

import static com.workspace.web.enums.SocialType.FACEBOOK;
import static com.workspace.web.enums.SocialType.GOOGLE;
import static com.workspace.web.enums.SocialType.KAKAO;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.workspace.web.oauth2.CustomOAuth2Provider;

// spring boot ver.1.5.14 주석 처리
@Configuration
@EnableWebSecurity
//@EnableOAuth2Client
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
//	@Autowired
//	private OAuth2ClientContext oAuth2ClientContext;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		http
			.authorizeRequests()
				.antMatchers("/", "/oauth2/**", "/login/**", "/css/**", "/images/**", "/js/**", "/console.**").permitAll()
				.antMatchers("/facebook").hasAuthority(FACEBOOK.getRoleType())
				.antMatchers("/google").hasAuthority(GOOGLE.getRoleType())
				.antMatchers("kakao").hasAuthority(KAKAO.getRoleType())
				.anyRequest().authenticated()
			.and()
				.oauth2Login()
				.defaultSuccessUrl("/loginSuccess")
				.failureUrl("/loginFailure")
			.and()
				.headers().frameOptions().disable()
			.and()
				.exceptionHandling()
				.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
			.and()
				.formLogin()
				.successForwardUrl("/board/list")
			.and()
				.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/")
				.deleteCookies("JSESSIONID")
				.invalidateHttpSession(true)
			.and()
				.addFilterBefore(filter, CsrfFilter.class)
//				.addFilterBefore(oauth2Filter(), BasicAuthenticationFilter.class)
				.csrf()
				.disable();
	}
	
//	@Bean
//	public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
//		FilterRegistrationBean registration = new FilterRegistrationBean();
//		registration.setFilter(filter);
//		registration.setOrder(-100);
//		return registration;
//	}
//	
//	private Filter oauth2Filter() {
//		CompositeFilter filter = new CompositeFilter();
//		List<Filter> filters = new ArrayList<>();
//		filters.add(oauth2Filter(facebook(), "/login/facebook", FACEBOOK));
//		filters.add(oauth2Filter(google(), "/login/google", GOOGLE));
//		filters.add(oauth2Filter(kakao(), "/login/kakao", KAKAO));
//		filter.setFilters(filters);
//		return filter;
//	}
//	
//	private Filter oauth2Filter(ClientResources client, String path, SocialType socialType) {
//		OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
//		OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oAuth2ClientContext);
//		filter.setRestTemplate(template);
//		filter.setTokenServices(new UserTokenService(client, socialType));
//		filter.setAuthenticationSuccessHandler((request, response, authentication)
//				-> response.sendRedirect("/" + socialType.getValue() + "/complete"));
//		filter.setAuthenticationFailureHandler((request, response, exception)
//				-> response.sendRedirect("/error"));
//		return filter;
//	}
//	
//	@Bean
//	@ConfigurationProperties("facebook")
//	public ClientResources facebook() {
//		return new ClientResources();
//	}
//	
//	@Bean
//	@ConfigurationProperties("google")
//	public ClientResources google() {
//		return new ClientResources();
//	}
//	
//	@Bean
//	@ConfigurationProperties("kakao")
//	public ClientResources kakao() {
//		return new ClientResources();
//	}
	
	@Bean
	public ClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties oAuth2ClientProperties,
																		@Value("${custom.oauth2.kakao.client-id}") String kakaoClientId) {
		List<ClientRegistration> registrations = oAuth2ClientProperties
				.getRegistration().keySet().stream()
				.map(client -> getRegistration(oAuth2ClientProperties, client))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		
		registrations.add(CustomOAuth2Provider.KAKAO.getBuilder("kakao")
				.clientId(kakaoClientId)
				.clientSecret("test")
				.jwkSetUri("test")
				.build()
				);
		
		return new InMemoryClientRegistrationRepository(registrations);
	}
	
	private ClientRegistration getRegistration(OAuth2ClientProperties clientProperties, String client) {
		if("google".equals(client)) {
			OAuth2ClientProperties.Registration registration = clientProperties.getRegistration().get("google");
			return CommonOAuth2Provider.GOOGLE.getBuilder(client)
					.clientId(registration.getClientId())
					.clientSecret(registration.getClientSecret())
					.scope("email", "profile")
					.build();
		}
		if("facebook".equals(client)) {
			OAuth2ClientProperties.Registration registration = clientProperties.getRegistration().get("facebook");
			return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
					.clientId(registration.getClientId())
					.clientSecret(registration.getClientSecret())
					.userInfoUri("https://graph.facebook.com/me?fields=id,name,email.link")
					.scope("email")
					.build();
		}
		return null;
	}

}
