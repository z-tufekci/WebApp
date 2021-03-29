package com.example.demo.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

//import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.demo.exception.BedRequestException;
import com.example.demo.exception.NotFoundException;

import com.example.demo.repository.BookRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;

@RestController
public class FileController {
	private final static Logger logger =LoggerFactory.getLogger(UserController.class);
	private static final StatsDClient statsd = new NonBlockingStatsDClient("csye6225.webapp", "localhost", 8125);
	
	@Autowired
    UserRepository userRepository;
	
	@Autowired
    BookRepository bookRepository;
	
	@Autowired
    FileRepository fileRepository;
	
	@RequestMapping(path = "/books/{book_id}/image" ,method = RequestMethod.POST ,consumes = "multipart/form-data",produces = "application/json") // @NotNull
	@ResponseStatus(HttpStatus.OK)
    public File index(@PathVariable UUID book_id, @RequestPart("file") @Valid @NotNull @NotBlank  MultipartFile file){

		statsd.incrementCounter("postimage");	
		long start = System.currentTimeMillis();

		String contentType = file.getContentType();
		if(!contentType.equalsIgnoreCase("image/png") && !contentType.equalsIgnoreCase("image/jpeg") ) {
			logger.error("The content type is invalid.PNG,JPG or JPEG is valid content.");
			throw new BedRequestException();
		}
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();		
		List<Book> bookl = bookRepository.findById(book_id);
		List<User> userl = userRepository.findByUsername(username);
		
		if(!bookl.get(0).getUser_id().equals(userl.get(0).getId()))
					throw new NotFoundException() ;
		
		byte[] mediaBytes = null; 
		try {
			mediaBytes = file.getBytes();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Region region = Region.US_EAST_1;
		long s3_service_start = System.currentTimeMillis();
		S3Client s3 = S3Client.builder()
	              .credentialsProvider(InstanceProfileCredentialsProvider.builder().build()).region(region)
	              .build();
		
        String bucketName = System.getProperty("s3_BUCKET");  
        File newFile = new File();
		UUID uuid = java.util.UUID.randomUUID();
		
		//ObjectKey:bookid/imageid/filename
        String key = ""+book_id+"/"+uuid+""+file.getOriginalFilename();
        
        Map<String,String> metadata = new HashMap<>();          
        String value = "x-amz-meta-"+book_id+"/"+uuid+"/"+file.getOriginalFilename();
        metadata.put("My metadata", value);
        //Metadata: My metadata, x-amz-meta-bookid/imageid/filename
        PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucketName).key(key).metadata(metadata).build();
        s3.putObject(objectRequest, RequestBody.fromBytes(mediaBytes));
        
        long s3_service_end = System.currentTimeMillis();
        statsd.recordExecutionTime("s3service_putimage", s3_service_end -s3_service_start);
        
		newFile.setId(uuid);
		newFile.setCreated_date(new Date());
		newFile.setUserId(bookl.get(0).getUser_id());
		newFile.setFilename(file.getOriginalFilename());
		newFile.setS3_object_name(value);
		
		long query_start = System.currentTimeMillis();
		File nFile =  fileRepository.save(newFile); 
        long end = System.currentTimeMillis();
        statsd.recordExecutionTime("query_saveimage", end-query_start);
        
  	    statsd.recordExecutionTime("postimage.time", end-start);
  	    logger.info("The image is saved.");
  	    SecurityContextHolder.getContext().setAuthentication(null);
		
  	    return nFile;
    }
	
	@RequestMapping(path = "/books/{book_id}/image/{image_id}" ,method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT) 
	public void delete(@PathVariable UUID book_id,@PathVariable UUID image_id) {
		statsd.incrementCounter("deleteimage");	
		long start = System.currentTimeMillis();
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();		
		List<User> userl = userRepository.findByUsername(username);//Authanticated user

		List<Book> books = bookRepository.findById(book_id);
		if(books.isEmpty()) {
			logger.error("The book is not found.");
			throw new NotFoundException() ;
		}
		Book currentBook = books.get(0);
		UUID userId= currentBook.getUser_id();//The owner of the book
		
		if(!userId.equals(userl.get(0).getId()))
			throw new NotFoundException() ;
		
		List<File> files = fileRepository.findById(image_id);
		if(files.isEmpty()) {
			logger.error("The image is not found.");
			throw new NotFoundException() ;
		}
		
		Region region = Region.US_EAST_1; //region(region).
		long s3_service_start = System.currentTimeMillis();
		S3Client s3 = S3Client.builder()
	              .credentialsProvider(InstanceProfileCredentialsProvider.builder().build()).region(region)
	              .build();

		String bucketName = System.getProperty("s3_BUCKET"); 
		
		String objectName = ""+book_id+"/"+files.get(0).getId()+""+files.get(0).getFilename();
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
        long s3_service_end = System.currentTimeMillis();
        statsd.recordExecutionTime("s3service_deleteimage", s3_service_end -s3_service_start);
		
        
        long query_start = System.currentTimeMillis();
		fileRepository.delete(files.get(0));
		long query_end = System.currentTimeMillis();
		statsd.recordExecutionTime("query_deleteimage", query_end-query_start);
		
		SecurityContextHolder.getContext().setAuthentication(null);			
		
		
		long end = System.currentTimeMillis();
  	    statsd.recordExecutionTime("deletetimage.time", end-start);
  	    logger.info("The image is deleted.");
	}
	
	
   
    

}
