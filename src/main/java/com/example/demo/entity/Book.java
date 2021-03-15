package com.example.demo.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "books", catalog = "user_db") 
public class Book {
	@Id    
	@GeneratedValue(strategy = GenerationType.AUTO)
	@org.hibernate.annotations.Type(type="uuid-char")
	UUID id;   
	
	@Column(unique=true)
	@NotNull
	String isbn;
	
	@NotNull
	String title;
	@NotNull
	String published_date;
	@NotNull
	String author;
	@NotNull
	Date book_created;
	@NotNull
	@org.hibernate.annotations.Type(type="uuid-char")
	UUID user_id; 
	
	public Book() {}
			
	
	public Book(UUID id, @NotNull String isbn, @NotNull String title, @NotNull String published_date,
			@NotNull String author, @NotNull Date book_created, @NotNull UUID user_id) {
		super();
		this.id = id;
		this.isbn = isbn;
		this.title = title;
		this.published_date = published_date;
		this.author = author;
		this.book_created = book_created;
		this.user_id = user_id;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPublished_date() {
		return published_date;
	}
	public void setPublished_date(String published_date) {
		this.published_date = published_date;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Date getBook_created() {
		return book_created;
	}
	public void setBook_created(Date book_created) {
		this.book_created = book_created;
	}
	public UUID getUser_id() {
		return user_id;
	}
	public void setUser_id(UUID user_id) {
		this.user_id = user_id;
	}

}
