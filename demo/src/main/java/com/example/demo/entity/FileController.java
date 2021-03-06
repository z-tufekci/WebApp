package com.example.demo.entity;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;

@RestController
public class FileController {

	
	@Autowired
    UserRepository userRepository;
	
	@Autowired
    BookRepository bookRepository;
	
	@Autowired
    FileRepository fileRepository;
	
	@RequestMapping(path = "/books/{book_id}/image" ,method = RequestMethod.POST ,consumes = "multipart/form-data",produces = "application/json") // @NotNull
	@ResponseStatus(HttpStatus.OK)
    public File index(@PathVariable UUID book_id, @RequestPart("file") @Valid @NotNull @NotBlank  MultipartFile file){
		//@RequestPart("file") @Valid @NotNull @NotBlank MultipartFile file
		System.out.println("Book_id "+book_id);
		String contentType = file.getContentType();
		System.out.println("Content Type: "+contentType);
		if(!contentType.equalsIgnoreCase("image/png") && !contentType.equalsIgnoreCase("image/jpeg") ) {
			throw new BedRequestException();
		}
		
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();		
		List<Book> bookl = bookRepository.findById(book_id);
		List<User> userl = userRepository.findByUsername(username);
		System.out.println("Book owner: "+bookl.get(0).getUser_id());
		System.out.println("Authanticated User:"+userl.get(0).getId());
		if(!bookl.get(0).getUser_id().equals(userl.get(0).getId()))
					throw new NotFoundException() ;
		
		byte[] mediaBytes = null; 
		System.out.println("Geldim");
		try {
			mediaBytes = file.getBytes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Region region = Region.US_EAST_1; //region(region).
		
		
		
		
		S3Client s3 = S3Client.builder()
	              .credentialsProvider(InstanceProfileCredentialsProvider.builder().build()).region(region)
	              .build();
		
		System.out.println(s3);
		
        String bucketName =  System.getProperty("s3_BUCKET");  //"webapp.firstname.lastname";
        System.out.println("Uploading object...");
        System.out.println(bucketName);
        File newFile = new File();
		UUID uuid = java.util.UUID.randomUUID();
		
		//ObjectKey:bookid/imageid/filename
        String key = ""+book_id+"/"+uuid+""+file.getOriginalFilename();

        Map<String,String> metadata = new HashMap<>();          
        String value = "x-amz-meta-"+book_id+"/"+uuid+"/"+file.getOriginalFilename();
        metadata.put("My metadata", value);
        //Metadata: My metadata, x-amz-meta-bookid/imageid/filename
        System.out.println("Before Put Request...");
        PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucketName).key(key).metadata(metadata).build();
        System.out.println("After Put Request...");
        s3.putObject(objectRequest, RequestBody.fromBytes(mediaBytes));
        System.out.println("After Put Object...");
				
		newFile.setId(uuid);
		newFile.setCreated_date(new Date());
		newFile.setUserId(bookl.get(0).getUser_id());
		newFile.setFilename(file.getOriginalFilename());
		newFile.setS3_object_name(value);
		SecurityContextHolder.getContext().setAuthentication(null);
		System.out.println(newFile);
        return fileRepository.save(newFile); 
    }
	
	@RequestMapping(path = "/books​/{book_id}​/image​/{image_id}" ,method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT) 
	public void delete(@PathVariable UUID book_id,UUID image_id) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();		
		List<User> userl = userRepository.findByUsername(username);//Authanticated user
		
		//System.out.println("Image" +userl.get(0));
		
		if(!userl.get(0).getUsername().equalsIgnoreCase(username))
			throw new NotFoundException() ;
		
		List<Book> books = bookRepository.findById(book_id);
		if(books.isEmpty())
			throw new NotFoundException() ;
		
		Book currentBook = books.get(0);
		UUID userId= currentBook.getUser_id();//The owner of the book
		if(!userId.equals(userl.get(0).getId()))
			throw new NotFoundException() ;
		
		List<File> files = fileRepository.findById(image_id);
		if(files.isEmpty())
			throw new NotFoundException() ;
		
		if(!files.get(0).getUserId().equals(userId))
			throw new NotFoundException() ;
		
		Region region = Region.US_EAST_1; //region(region).
		

		
		S3Client s3 = S3Client.builder()
	              .credentialsProvider(InstanceProfileCredentialsProvider.builder().build()).region(region)
	              .build();
	    
		String bucketName = System.getProperty("s3_BUCKET");  //"webapp.firstname.lastname";
		
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
        System.out.println("Done!");
		
		SecurityContextHolder.getContext().setAuthentication(null);			
		fileRepository.delete(files.get(0));
	}
	
	
   
    

}
