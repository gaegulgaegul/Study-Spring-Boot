package com.workspace.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.contains;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.workspace.demo.controller.BookController;
import com.workspace.demo.domain.Book;
import com.workspace.demo.service.BookService;

@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private BookService bookService;
	
	@Test
	public void Book_mvc_Test() throws Exception {
		Book book = new Book("String Boot Book", LocalDateTime.now());
		given(bookService.getBookList()).willReturn(Collections.singletonList(book));
		mvc.perform(get("/books"))
			.andExpect(status().isOk())
			.andExpect(view().name("book"))
			.andExpect(model().attributeExists("bookList"))
			.andExpect(model().attribute("bookList", contains(book)));
	}

}
