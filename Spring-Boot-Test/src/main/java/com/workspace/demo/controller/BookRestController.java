package com.workspace.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workspace.demo.domain.Book;
import com.workspace.demo.service.BookRestService;

@RestController
public class BookRestController {
	
	@Autowired
	private BookRestService bookRestService;
	
	@GetMapping(path="/rest/test", produces = MediaType.APPLICATION_JSON_VALUE)
	public Book getRestBooks() {
		return bookRestService.getRestBook();
	}

}
