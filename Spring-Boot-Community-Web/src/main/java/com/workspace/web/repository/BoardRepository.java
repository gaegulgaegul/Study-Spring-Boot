package com.workspace.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workspace.web.domain.Board;
import com.workspace.web.domain.User;

public interface BoardRepository extends JpaRepository<Board, Long> {
	
	Board findByUser(User user);

}
