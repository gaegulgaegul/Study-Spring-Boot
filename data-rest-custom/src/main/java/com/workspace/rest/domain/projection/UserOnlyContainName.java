package com.workspace.rest.domain.projection;

import org.springframework.data.rest.core.config.Projection;

import com.workspace.rest.domain.User;

@Projection(name = "getOnlyName", types = { User.class })
public interface UserOnlyContainName {
	
	String getName();

}
