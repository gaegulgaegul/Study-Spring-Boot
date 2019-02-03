package com.workspace.batch.domain.enums;

public enum SocialType {
	FACEBOOK("facebook"),
	GOOGLE("google"),
	KAKAO("kakao");
	
	private final String ROLE_PREFIX = "ROLE_";
	private String name;
	
	SocialType(String name) {
		this.name = name;
	}
	
	public String getRoleType() {
		return ROLE_PREFIX + name.toUpperCase();
	}
	
	public String getValue() {
		return name;
	}
	
	public boolean isEquals(String authority) {
		return this.getRoleType().equals(authority);
	}
	
}
