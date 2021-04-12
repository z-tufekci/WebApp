package com.example.demo.entity;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
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
import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Exception;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;
import software.amazon.awssdk.services.sts.model.StsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONObject;
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
	
	@SuppressWarnings("unchecked")
	@RequestMapping(path = "/" ,method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
    public String welcome(){
		JSONObject json = new JSONObject();
		json.put("Date: ", new Date());
		json.put("NAME: ", "Zeynep Tufekci");
		EC2MetadataUtils.getPrivateIpAddress();
		json.put("Private IP Address: ",EC2MetadataUtils.getPrivateIpAddress());
		json.put("Instance Id: ",EC2MetadataUtils.getInstanceId());
        return json.toString(); 
    }
	
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
			logger.error("The book is not found");
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
			        	logger.error("S3 service image delete error while deleting book.");
			            System.err.println(e.awsErrorDetails().errorMessage());
			            System.exit(1);
			        }
			 
				}
			}
		}
		long s3_service_end = System.currentTimeMillis();
		statsd.recordExecutionTime("s3service_deletebook", s3_service_end -s3_service_start);
		
		long query_start = System.currentTimeMillis();
		bookRepository.delete(currentBook);
		long query_end = System.currentTimeMillis();
		statsd.recordExecutionTime("query_deletebook", query_end-query_start);
		
		logger.info("Book and related images are deleted from the system");
		SecurityContextHolder.getContext().setAuthentication(null);	
		
		long end = System.currentTimeMillis();
		statsd.recordExecutionTime("deletebook.time", end-start);
		
		StsClient stsClient = StsClient.builder()
                .region(region)
                .credentialsProvider(InstanceProfileCredentialsProvider.builder().build())
                .build();
		String accountId = "831195153875";
		try {
            GetCallerIdentityResponse response = stsClient.getCallerIdentity();
            accountId =	response.account();
        } catch (StsException e) {
            System.err.println(e.getMessage());
            logger.error(e.getMessage());
            System.exit(1);
        }
		stsClient.close();
		/*Connect SNS Client*/
		SnsClient snsClient = SnsClient.builder()
	                .region(region)
	                .credentialsProvider(InstanceProfileCredentialsProvider.builder().build())
	                .build();
        
		String message = currentBook+" username="+username+"\n BOOK IS DELETED"; 
		String topicArn ="arn:aws:sns:us-east-1:"+accountId+":sns-topic";
		try {
	        PublishRequest request = PublishRequest.builder()
	            .message(message)
	            .topicArn(topicArn)
	            .build();

	        PublishResponse result = snsClient.publish(request);
	        //System.out.println(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());
	        logger.info(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());
	     } catch (SnsException e) {
	        //System.err.println(e.awsErrorDetails().errorMessage());
	    	logger.error(e.awsErrorDetails().errorMessage());
	    	System.exit(1);
	     }
		snsClient.close();
	}
	
	@RequestMapping(path = "/books" ,method = RequestMethod.POST, produces = "application/json",consumes = "application/json")
	@ResponseStatus(HttpStatus.OK)
    public Book generateBook(@RequestBody Book book,@RequestBody Principal principal,@PathVariable HttpServletRequest req){
		
		
		   /* {
			  "title": "Computer Networks",
			  "author": "Andrew S. Tanenbaum",
			  "isbn": "978-0132126953",
			  "published_date": "May, 2020"
			}*/
		statsd.incrementCounter("postbook");
		long start = System.currentTimeMillis();
		
		if(book == null || book.getAuthor()==null || book.getTitle() ==null || book.getIsbn() == null || book.getPublished_date() == null) {
			logger.error("One of the required fields is empty");
			throw new BedRequestException();
		}
   		 
		
		/*Check if ISBN number already exists or not*/
		List<Book> bList =  bookRepository.findByIsbn(book.getIsbn());
		if(!bList.isEmpty()) {
			logger.error("Book ISBN number already exists.");
			throw new BedRequestException();
		}
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
		
		Region region = Region.US_EAST_1; //region(region).	
		
		StsClient stsClient = StsClient.builder()
                .region(region)
                .credentialsProvider(InstanceProfileCredentialsProvider.builder().build())
                .build();
		String accountId = "831195153875";
		try {
            GetCallerIdentityResponse response = stsClient.getCallerIdentity();
            accountId =	response.account();
        } catch (StsException e) {
            //System.err.println(e.getMessage());
            logger.error(e.getMessage());
            System.exit(1);
        }
		stsClient.close();
		/*Connect SNS Client*/		
		SnsClient snsClient = SnsClient.builder()
	                .region(region)
	                .credentialsProvider(InstanceProfileCredentialsProvider.builder().build())
	                .build();
		
		String path = req.getRequestURL().toString();//((ServletWebRequest)req).getRequest().getRequestURI();
		Book lastBook = bookRepository.findByIsbn(newBook.getIsbn()).get(0);
		String message = newBook+" username="+username+"\n"+path+"/"+lastBook.getId()+"\n BOOK IS ADDED";
		String topicArn ="arn:aws:sns:us-east-1:"+accountId+":sns-topic";
		try {
	        PublishRequest request = PublishRequest.builder()
	            .message(message)
	            .topicArn(topicArn)
	            .build();

	        PublishResponse result = snsClient.publish(request);
	        //System.out.println(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());
	        logger.info(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());
	     } catch (SnsException e) {
	        //System.err.println(e.awsErrorDetails().errorMessage());
	        logger.error(e.awsErrorDetails().errorMessage());
	        System.exit(1);
	     }
		snsClient.close();
		return lbook;
		
    }
	private BookWithImages getABook(Book book) {
		List<File> bookImages = new ArrayList<File>();
		
		long query_start = System.currentTimeMillis();
		List<File> images= fileRepository.findByUserId(book.getUser_id());
		long query_end = System.currentTimeMillis();
		statsd.recordExecutionTime("query_getimagesofbook", query_end-query_start);
		
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
