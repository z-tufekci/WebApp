package com.example.demo.entity;

import java.security.Principal;
import java.util.ArrayList;
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
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class BookControler {
	
	private final static Logger logger =LoggerFactory.getLogger(BookControler.class);
	
	private static final StatsDClient statsd = new NonBlockingStatsDClient("csye6225.webapp", "localhost", 8125);
	
	@Autowired
    BookRepository bookRepository;
	
	@Autowired
    UserRepository userRepository;
	
	@Autowired
    FileRepository fileRepository;
	
	
	@RequestMapping(path = "/books" ,method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
    public List<BookWithImages> index(){
		long start = System.currentTimeMillis();
		statsd.incrementCounter("getbooks");
		
		List<BookWithImages> bwis = new ArrayList<BookWithImages>();
		List<Book> allbooks = bookRepository.findAll();
		for(Book b: allbooks) {
			BookWithImages bwi = getABook(b);
			bwis.add(bwi);
		}
		long end = System.currentTimeMillis();
		statsd.recordExecutionTime("getbooks.time", end-start);
		logger.info("All books are showed");
        return bwis; 
    }
	
	@RequestMapping(path = "/books/{id}" ,method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK) 
	public BookWithImages getBook(@PathVariable UUID id) {
		
		statsd.incrementCounter("getbook");
		long start = System.currentTimeMillis();
		
		List<Book> books = bookRepository.findById(id);
		long query_end = System.currentTimeMillis();
		statsd.recordExecutionTime("query_findbook", query_end-start);
		
		if(books.isEmpty()) {
			logger.info("The book is not found");
			throw new NotFoundException();
		}
		BookWithImages bwi= getABook(books.get(0));
		
		long end = System.currentTimeMillis();
		statsd.recordExecutionTime("getbook.time", end-start);
		logger.info("The book is showed");
		return bwi;
	}

	@RequestMapping(path = "/books/{id}" ,method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT) 
	public void delete(@PathVariable UUID id) {
		statsd.incrementCounter("deletebook");
		long start = System.currentTimeMillis();
		
		List<Book> books = bookRepository.findById(id);
		
		if(books.isEmpty()) {
			logger.error("Book is not found");
			throw new NotFoundException() ;
		}
		
		Book currentBook = books.get(0);
		UUID userId= currentBook.getUser_id();
		
		List<User> userl = userRepository.findById(userId);
		String username = SecurityContextHolder.getContext().getAuthentication().getName();		
		
		if(!userl.get(0).getUsername().equalsIgnoreCase(username))
			throw new NotFoundException() ;

		/*Connect to s3 bucket*/
		
		Region region = Region.US_EAST_1; //region(region).
		
		S3Client s3 = S3Client.builder()
	              .credentialsProvider(InstanceProfileCredentialsProvider.builder().build()).region(region)
	              .build();
	    
		String bucketName = System.getProperty("s3_BUCKET");

		
		List<File> images= fileRepository.findByUserId(userId);
		
		long s3_service_start = System.currentTimeMillis();
		if(!images.isEmpty()) {
			for(File image : images) {
				String s3name = image.getS3_object_name();
				String bookID= s3name.substring(11,s3name.indexOf('/'));
				UUID uuid = UUID.fromString(bookID);
				if(uuid.equals(currentBook.getId())) {

					//delete image from s3 and database					
					fileRepository.delete(image);

					String objectName = ""+uuid+"/"+image.getId()+""+image.getFilename();
					ArrayList<ObjectIdentifier> toDelete = new ArrayList<ObjectIdentifier>();
			        toDelete.add(ObjectIdentifier.builder().key(objectName).build());

			        try {
			            DeleteObjectsRequest dor = DeleteObjectsRequest.builder()
			                    .bucket(bucketName)
			                    .delete(Delete.builder().objects(toDelete).build())
			                    .build();
			            s3.deleteObjects(dor);
			        } catch (S3Exception e) {
			            System.err.println(e.awsErrorDetails().errorMessage());
			            System.exit(1);
			        }
			 
				}
			}
		}
		long s3_service_end = System.currentTimeMillis();
		statsd.recordExecutionTime("s3service_deletebook", s3_service_end -s3_service_start);
		
		long query_start = System.currentTimeMillis();
		bookRepository.delete(books.get(0));
		long query_end = System.currentTimeMillis();
		statsd.recordExecutionTime("query_deletebook", query_end-query_start);
		
		logger.info("Book and related images are deleted from the system");
		SecurityContextHolder.getContext().setAuthentication(null);	
		
		long end = System.currentTimeMillis();
		statsd.recordExecutionTime("deletebook.time", end-start);
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
		statsd.incrementCounter("postbook");
		long start = System.currentTimeMillis();
		
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
		
		long query_start = System.currentTimeMillis();
		Book lbook = bookRepository.save(newBook);
		long query_end = System.currentTimeMillis();
		statsd.recordExecutionTime("query_savebook", query_end-query_start);
		
		SecurityContextHolder.getContext().setAuthentication(null);
		logger.info("Book is addded to the system");
		
		long end = System.currentTimeMillis();
		statsd.recordExecutionTime("postbook.time", end-start);
		return lbook;
		
    }
	private BookWithImages getABook(Book book) {
		List<File> bookImages = new ArrayList<File>();
		List<File> images= fileRepository.findByUserId(book.getUser_id());
		if(!images.isEmpty()) {
			for(File image : images) {
				String s3name = image.getS3_object_name();
				String bookID= s3name.substring(11,s3name.indexOf('/'));
				UUID uuid = UUID.fromString(bookID);
				if(uuid.equals(book.getId()))
					bookImages.add(image);
			}
		}
		return new BookWithImages(book, bookImages);
	}
}
