package com.example.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.demo.entity.Book;
import com.example.demo.entity.BookControler;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.timgroup.statsd.StatsDClient;


@AutoConfigureTestDatabase(replace=Replace.NONE)
@WebMvcTest(BookControler.class)
public class BookControllerTest {
	
	@Autowired
    private MockMvc mockmvc;
	
	@MockBean
	private BookRepository bookRepository;
	
	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private FileRepository fileRepository;
	
	@MockBean
	private DataSource dataSource;
	
	@Test
	public void testGetBooks() throws Exception {
		List<Book> listBook = new ArrayList<Book>();
		listBook.add(new Book(java.util.UUID.randomUUID(),"123-456-789","ABC","21 March", "author", new Date(), java.util.UUID.randomUUID()));
		Mockito.when(bookRepository.findAll()).thenReturn(listBook);		
		String url = "/books";
		mockmvc.perform(MockMvcRequestBuilders.get(url)).andExpect(MockMvcResultMatchers.status().isOk());		
	}
}
