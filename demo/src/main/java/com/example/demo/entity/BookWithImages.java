package com.example.demo.entity;

import java.util.List;

public class BookWithImages {
 /*
  * "id": "d6193106-a192-46db-aae9-f151004ee453",
    "title": "Computer Networks",
    "author": "Andrew S. Tanenbaum",
    "isbn": "978-0132126953",
    "published_date": "May, 2020",
    "book_created": "2016-08-29T09:12:33.001Z",
    "user_id": "d290f1ee-6c54-4b01-90e6-d701748f0851",
    "book_images": [
      {
        "file_name": "image.jpg",
        "s3_object_name": "ad79de23-6820-482c-8d2b-d513885b0e17/9afdf82d-7e8e-4491-90d3-ff0499bf6afe/image.jpg",
        "file_id": "9afdf82d-7e8e-4491-90d3-ff0499bf6afe",
        "created_date": "2020-08-29T09:12:33.001Z",
        "user_id": "d290f1ee-6c54-4b01-90e6-d701748f0851"
      }
    ]
  * */
	private String id;
	private String title;
	private String author;
	private String isbn;
	private String published_date;
	private String book_created;
	private String user_id;
	private List<File> book_images;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public String getPublished_date() {
		return published_date;
	}
	public void setPublished_date(String published_date) {
		this.published_date = published_date;
	}
	public String getBook_created() {
		return book_created;
	}
	public void setBook_created(String book_created) {
		this.book_created = book_created;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public List<File> getBook_images() {
		return book_images;
	}
	public void setBook_images(List<File> book_images) {
		this.book_images = book_images;
	}
	public BookWithImages(Book book, List<File> book_images) {
		super();
		this.id = ""+book.getId();
		this.title = book.getTitle();
		this.author = book.getAuthor();
		this.isbn = book.getIsbn();
		this.published_date = book.getPublished_date();
		this.book_created = ""+book.getBook_created();
		this.user_id = ""+book.getUser_id();
		this.book_images = book_images;
	}
}
