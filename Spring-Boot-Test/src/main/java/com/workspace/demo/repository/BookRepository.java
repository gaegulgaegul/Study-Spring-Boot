package com.workspace.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workspace.demo.domain.Book;

public interface BookRepository extends JpaRepository<Book, Integer> {

}
