package com.workspace.demo.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.workspace.demo.domain.Book;

@Service
public class BookRestService {
	
	private final RestTemplate restTemplate;
	
	public BookRestService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.rootUri("/rest/test").build();
	}
	
	public Book getRestBook() {
		return this.restTemplate.getForObject("/rest/test", Book.class);
	}

}
