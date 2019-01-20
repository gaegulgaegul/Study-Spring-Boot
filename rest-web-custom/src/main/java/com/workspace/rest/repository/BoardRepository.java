package com.workspace.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workspace.rest.domain.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {

}
