package com.workspace.web.resolver;

import static com.workspace.web.enums.SocialType.FACEBOOK;
import static com.workspace.web.enums.SocialType.GOOGLE;
import static com.workspace.web.enums.SocialType.KAKAO;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.workspace.web.annotation.SocialUser;
import com.workspace.web.domain.User;
import com.workspace.web.enums.SocialType;
import com.workspace.web.repository.UserRepository;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
	
	private UserRepository userRepository;
	
	public UserArgumentResolver(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterAnnotation(SocialUser.class) != null &&
				parameter.getParameterType().equals(User.class);
	}
	
	public Object resolveArgument(MethodParameter parameter,
									ModelAndViewContainer mavContainer,
									NativeWebRequest webRequest,
									WebDataBinderFactory binderFactory) throws Exception {
		HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
		User user = (User) session.getAttribute("user");
		return getUser(user, session);
	}
	
	private User getUser(User user, HttpSession session) {
		if(user == null) {
			try {
//				spring boot ver. 1.5.14
//				OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
//				Map<String, String> map = (HashMap<String, String>) authentication.getUserAuthentication().getDetails();
//				User convertUser = convertUser(String.valueOf(authentication.getAuthorities().toArray()[0]), map);
				
				OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
				Map<String, Object> map = authentication.getPrincipal().getAttributes();
				User convertUser = convertUser(authentication.getAuthorizedClientRegistrationId(), map);
				
				user = userRepository.findByEmail(convertUser.getEmail());
				if(user == null) { user = userRepository.save(convertUser); }
				
				setRoleIfNotSame(user, authentication, map);
				session.setAttribute("user", user);
			} catch (ClassCastException e) {
				return user;
			}
		}
		return user;
	}
	
//	spring boot ver. 1.5.14
//	private User convertUser(String authority, Map<String, String> map) {
//	spring boot ver. 2.0.3
	private User convertUser(String authority, Map<String, Object> map) {
//		spring boot ver. 1.5.14
//		if(FACEBOOK.isEquals(authority)) return getModernUser(FACEBOOK, map);
//		else if(GOOGLE.isEquals(authority)) return getModernUser(GOOGLE, map);
//		else if(KAKAO.isEquals(authority)) return getKakaoUser(map);
		
//		spring boot ver. 2.0.3
		if(FACEBOOK.getValue().equals(authority)) return getModernUser(FACEBOOK, map);
		else if(GOOGLE.getValue().equals(authority)) return getModernUser(GOOGLE, map);
		else if(KAKAO.getValue().equals(authority)) return getModernUser(KAKAO, map);
		return null;
	}
	
	private User getModernUser(SocialType socialType, Map<String, Object> map) {
//		spring boot ver.1.5.14
//		return User.builder()
//				.name(map.get("name"))
//				.email(map.get("email"))
//				.principal(map.get("id"))
//				.socialType(socialType)
//				.createdDate(LocalDateTime.now())
//				.build();
		
//		spring boot ver.2.0.3
		return User.builder()
				.name(String.valueOf(map.get("name")))
				.email(String.valueOf(map.get("email")))
				.principal(String.valueOf(map.get("id")))
				.socialType(socialType)
				.createdDate(LocalDateTime.now())
				.build();
	}
	
	private User getKakaoUser(Map<String, Object> map) {
		HashMap<String, String> propertyMap = (HashMap<String, String>) map.get("properties");
		return User.builder()
				.name(propertyMap.get("nickname"))
				.email(String.valueOf(map.get("kaccount_email")))
				.principal(String.valueOf(map.get("id")))
				.socialType(KAKAO)
				.createdDate(LocalDateTime.now())
				.build();
	}
	
	private void setRoleIfNotSame(User user, OAuth2AuthenticationToken authentication, Map<String, Object> map) {
		if(!authentication.getAuthorities().contains(new SimpleGrantedAuthority(user.getSocialType().getRoleType()))) {
			SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(map, "N/A", AuthorityUtils.createAuthorityList(user.getSocialType().getRoleType())));
		}
	}

}
