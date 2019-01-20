package com.workspace.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workspace.rest.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
