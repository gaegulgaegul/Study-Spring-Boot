package com.workspace.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.workspace.web.annotation.SocialUser;
import com.workspace.web.domain.User;

@Controller
public class LoginController {
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
//	spring boot ver. 1.5.14
//	인증된 User 정보를 세션에 저장해주는 기능
//	@GetMapping(value = "/{facebook|google|kakao}/complete")
//	public String loginComplete(HttpSession session) {
//		OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
//		Map<String, Object> map = (HashMap<String, Object>) authentication.getUserAuthentication().getDetails();
//		session.setAttribute("user", User.builder()
//				.name(String.valueOf(map.get("name")))
//				.email(String.valueOf(map.get("email")))
//				.principal(String.valueOf(map.get("id")))
//				.socialType(SocialType.FACEBOOK)
//				.createdDate(LocalDateTime.now())
//				.build()
//				);
//		return "redirect:/board/list";
//	}
	
//	spring boot ver. 1.5.14
//	어노테이션을 사용해 축소
//	@GetMapping(value = "/{facebook|google|kakao}/complete")
//	public String loginComplete(@SocialUser User user) {
//		return "redirect:/board/list";
//	}
	
//	spring boot ver. 2.0.3
	@GetMapping("/loginSuccess")
	public String loginComplete(@SocialUser User user) {
		return "redirect:/board/list";
	}
	
}
