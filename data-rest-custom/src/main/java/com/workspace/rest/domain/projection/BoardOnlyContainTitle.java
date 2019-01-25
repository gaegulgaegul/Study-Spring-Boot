package com.workspace.rest.domain.projection;

import org.springframework.data.rest.core.config.Projection;

import com.workspace.rest.domain.Board;

@Projection(name = "getOnlyTitle", types = { Board.class })
public interface BoardOnlyContainTitle {
	
	String getTitle();

}
