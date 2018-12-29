package com.workspace.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workspace.web.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	User findByEmail(String email);

}
