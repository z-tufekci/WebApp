package com.example.demo.entity;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.demo.exception.BedRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.UserRepository;

@RestController
public class BookControler {

	@Autowired
    BookRepository bookRepository;
	
	@Autowired
    UserRepository userRepository;
	
	
	@RequestMapping(path = "/books" ,method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
    public List<Book> index(){
        return bookRepository.findAll();
    }
	
	@RequestMapping(path = "/books/{id}" ,method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK) 
	public Book getBook(@PathVariable UUID id) {
		List<Book> books = bookRepository.findById(id);
		if(books.isEmpty())
			throw new NotFoundException();
					
	    return books.get(0);
	}

	@RequestMapping(path = "/books/{id}" ,method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT) 
	public void delete(@PathVariable UUID id) {
		List<Book> books = bookRepository.findById(id);
		if(books.isEmpty())
			throw new NotFoundException() ;
		SecurityContextHolder.getContext().setAuthentication(null);			
		bookRepository.delete(books.get(0));
	}
	
	@RequestMapping(path = "/books" ,method = RequestMethod.POST, produces = "application/json",consumes = "application/json")
	@ResponseStatus(HttpStatus.OK)
    public Book generateBook(@RequestBody Book book,Principal principal){
		   /* {
			  "title": "Computer Networks",
			  "author": "Andrew S. Tanenbaum",
			  "isbn": "978-0132126953",
			  "published_date": "May, 2020"
			}*/
		
		if(book == null || book.getAuthor()==null || book.getTitle() ==null || book.getIsbn() == null || book.getPublished_date() == null) 
   		 throw new BedRequestException();
		
		/*Check if ISBN number already exists or not*/
		List<Book> bList =  bookRepository.findByIsbn(book.getIsbn());
		if(!bList.isEmpty())
			throw new BedRequestException();
		
		Authentication authentication = (Authentication) principal;
  	  	org.springframework.security.core.userdetails.User user = 
  			  (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
  	  	String username = user.getUsername();

  	  	List<User> list = userRepository.findByUsername(username); /*retrieve user from database*/
  	  	User realUser = list.get(0);
  	  	
		Book newBook = new Book();
		UUID uuid = java.util.UUID.randomUUID();
		newBook.setId(uuid);
		newBook.setTitle(book.getTitle()); 
		newBook.setAuthor(book.getAuthor());
		newBook.setIsbn(book.getIsbn());
		newBook.setPublished_date(book.getPublished_date());
		newBook.setUser_id(realUser.getId());
		newBook.setBook_created(new Date());
		
		SecurityContextHolder.getContext().setAuthentication(null);
        return bookRepository.save(newBook);
    }
	
}
