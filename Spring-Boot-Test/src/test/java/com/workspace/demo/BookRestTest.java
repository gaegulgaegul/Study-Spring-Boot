package com.workspace.demo;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;

import com.workspace.demo.service.BookRestService;

@RunWith(SpringRunner.class)
@RestClientTest(BookRestService.class)
public class BookRestTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Autowired
	private BookRestService bookRestService;
	
	@Autowired
	private MockRestServiceServer server;
	
	@Test
	public void rest_test() {
		this.server.expect(requestTo("/rest/rest"))
			.andRespond(withSuccess(new ClassPathResource("/test.json", getClass()), MediaType.APPLICATION_JSON));
	}
	
	@Test
	public void rest_error_test() {
		this.server.expect(requestTo("/rest/test"))
			.andRespond(withServerError());
		this.thrown.expect(HttpServerErrorException.class);
		this.bookRestService.getRestBook();
	}

}
