package com.workspace.batch.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workspace.batch.domain.User;
import com.workspace.batch.domain.enums.Grade;
import com.workspace.batch.domain.enums.UserStatus;

public interface UserRepository extends JpaRepository<User, Long> {
	
	List<User> findByUpdatedDateBeforeAndStatusEquals(LocalDateTime localDateTime, UserStatus status, Grade grade);

}
