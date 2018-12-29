package com.workspace.demo;

import java.time.LocalDateTime;
import java.util.List;

import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.workspace.demo.domain.Book;
import com.workspace.demo.repository.BookRepository;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.contains;


@RunWith(SpringRunner.class)
@DataJpaTest
public class BookJpaTest {
	
	private final static String BOOT_TEST_TITLE = "Spring Boot Test Book";
	
	@Autowired
	private TestEntityManager testEntityManager;
	
	@Autowired
	private BookRepository bookRepository;
	
	@Test
	public void Book_Save_Test() {
		Book book = Book.builder().title(BOOT_TEST_TITLE)
				.publishedAt(LocalDateTime.now()).build();
		testEntityManager.persist(book);
		assertThat(bookRepository.getOne(book.getIdx()), is(book));
	}
	
	@Test
	public void Book_Save_And_Find_Test() {
		Book book1 = Book.builder().title(BOOT_TEST_TITLE + "1")
				.publishedAt(LocalDateTime.now()).build();
		testEntityManager.persist(book1);
		Book book2 = Book.builder().title(BOOT_TEST_TITLE + "2")
				.publishedAt(LocalDateTime.now()).build();
		testEntityManager.persist(book2);
		Book book3 = Book.builder().title(BOOT_TEST_TITLE + "3")
				.publishedAt(LocalDateTime.now()).build();
		testEntityManager.persist(book3);
		
		List<Book> bookList = bookRepository.findAll();
		assertThat(bookList, hasSize(3));
		assertThat(bookList, contains(book1, book2, book3));
	}
	
	@Test
	public void BookList_Save_And_Remove_Test() {
		Book book1 = Book.builder().title(BOOT_TEST_TITLE + "1")
				.publishedAt(LocalDateTime.now()).build();
		testEntityManager.persist(book1);
		Book book2 = Book.builder().title(BOOT_TEST_TITLE + "2")
				.publishedAt(LocalDateTime.now()).build();
		testEntityManager.persist(book2);
		
		bookRepository.deleteAll();
		assertThat(bookRepository.findAll(), IsEmptyCollection.empty());
	}

}
