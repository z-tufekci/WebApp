package com.example.demo.entity;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.bind.annotation.RestController;
import com.example.demo.exception.BedRequestException;
import com.example.demo.repository.AuthorityRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserdbRepository;
import com.fasterxml.jackson.annotation.JsonView;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;


@RestController
public class UserController  {
	private final static Logger logger =LoggerFactory.getLogger(UserController.class);
	private static final StatsDClient statsd = new NonBlockingStatsDClient("csye6225.webapp", "localhost", 8125);

	@Autowired
    UserRepository userRepository;
	
	@Autowired
    UserdbRepository userdbRepository;
	
	@Autowired
    AuthorityRepository authRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	
	@RequestMapping(path = "/v1/user/self" ,method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK) 
	@JsonView(User.WithoutPasswordView.class)
	@ResponseBody
	public User foo(Principal principal) {
		
	  statsd.incrementCounter("getuser");	
	  long start = System.currentTimeMillis();
		
	  Authentication authentication = (Authentication) principal;
	  org.springframework.security.core.userdetails.User user = 
			  (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
	  
	  String username = user.getUsername(); 
	  
	  long query_start = System.currentTimeMillis();
	  List<User> list = userRepository.findByUsername(username); /*retrieve user from database*/
	  long query_end = System.currentTimeMillis();
	  statsd.recordExecutionTime("query_finduser", query_end-query_start);
	  
	  User realUser = list.get(0); 
	  SecurityContextHolder.getContext().setAuthentication(null);
	  
	  long end = System.currentTimeMillis();
	  statsd.recordExecutionTime("getuser.time", end-start);
		
	  return realUser;
	}
	
	
	@RequestMapping(path= "/v1/user", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
	@JsonView(User.WithoutPasswordView.class)
	@ResponseBody
    public User create(@RequestBody UserRequested userreq ) {
		statsd.incrementCounter("postuser");
		long start = System.currentTimeMillis();

		/*If one of the fields is absent, then return bad request error*/
		if(userreq == null || userreq.getUsername() == null || userreq.getPassword() == null || userreq.getFirst_name()== null || userreq.getLast_name() == null) {
			logger.error("One of the required fields is absent");
			throw new BedRequestException();
		}
	
		String username = userreq.getUsername();		
		List<User> userL = userRepository.findByUsername(username); /*If username exists, bad request error */
		if(!userL.isEmpty()) {
			 logger.error("The username does already exists");
			 throw new BedRequestException();
		}
		
		//check if username has email pattern 
		String regex = "^[A-Za-z0-9+_.-]+@(.+)$";		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(username);
		if(!matcher.matches()) {
			logger.error("The email address is unvalid");
			throw new BedRequestException(); /*If it is not email address, throw bad request error*/
		}
	
		//Check password
		//at least one lowercase, uppercase, number, and symbol exist in a 8+ character length password: "aA9.12345"
		String password = userreq.getPassword();
		regex = "^(?=\\P{Ll}*\\p{Ll})(?=\\P{Lu}*\\p{Lu})(?=\\P{N}*\\p{N})(?=[\\p{L}\\p{N}]*[^\\p{L}\\p{N}])[\\s\\S]{8,}$";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(password);
		if(!matcher.matches()) {
			logger.error("The password is not strong");
			throw new BedRequestException(); /*If it is not strong password, throw bad request error*/
		}
		String encodedPassword = passwordEncoder.encode(password);
		
		UUID uuid = java.util.UUID.randomUUID();
		String first_name = userreq.getFirst_name();
		String last_name = userreq.getLast_name();
		
		User user = new User(uuid,first_name,last_name,encodedPassword,username);
		User_db user_db = new User_db(username,encodedPassword,true); 
		Authority aut= new Authority(username,"ROLE_USER");
		userdbRepository.save(user_db);
		authRepository.save(aut);
		

		/*Run query*/
		long query_start = System.currentTimeMillis();
		User nUser = userRepository.save(user);
		long end = System.currentTimeMillis();
		statsd.recordExecutionTime("query_saveuser", end-query_start);
		
		statsd.recordExecutionTime("postuser.time", end-start);
		logger.info("The username is saved");
		return nUser;
    }

	@RequestMapping(path = "/v1/user/self", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public User update(@RequestBody  UserUpdated userup, Principal principal) {
		
		statsd.incrementCounter("updateuser");	
		long start = System.currentTimeMillis();
				
		/*If one of the fields is absent, then return bad request error*/
    	if(userup == null || (userup.getFirst_name()==null || userup.getLast_name()==null || userup.getPassword()== null) || userup.getUsername() != null || userup.getId() != null) {
    		 logger.error("One of the required fields is absent");
    		 throw new BedRequestException();
    	}
    		
    	Authentication authentication = (Authentication) principal;
  	  	org.springframework.security.core.userdetails.User user = 
  			  (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
  	  	String username = user.getUsername();

  	  	List<User> list = userRepository.findByUsername(username); /*retrieve user from database*/
  	  	User realUser = list.get(0);
  	    String encoded ="";
		 if(userup.getFirst_name() != null)
			 realUser.setFirstName(userup.getFirst_name());
		 if(userup.getLast_name() != null)
			 realUser.setLastName(userup.getLast_name());
		 if(userup.getPassword() != null) {
			 
			 String password = userup.getPassword();
			 //Check password
			 //at least one lowercase, uppercase, number, and symbol exist in a 8+ character length password: "aA9.12345"
			 String regex = "^(?=\\P{Ll}*\\p{Ll})(?=\\P{Lu}*\\p{Lu})(?=\\P{N}*\\p{N})(?=[\\p{L}\\p{N}]*[^\\p{L}\\p{N}])[\\s\\S]{8,}$";
			 Pattern pattern = Pattern.compile(regex);
			 Matcher matcher = pattern.matcher(password);
			 if(!matcher.matches()) {
				    logger.error("The password is not strong");
					throw new BedRequestException(); /*If it is not strong password, throw bad request error*/	
			 }
			 encoded = passwordEncoder.encode(password);
			 realUser.setPassword(encoded);

		 }
		 User_db user_db = new User_db(username,encoded,true); 
		 userdbRepository.save(user_db);			/*password updated in authorities table */		 
		 realUser.setAccount_updated(new Date());   /* Updating date-time*/
		 
		 
		 SecurityContextHolder.getContext().setAuthentication(null);
		 /*run queries*/
		 long query_start = System.currentTimeMillis();
		 User rUser = userRepository.save(realUser);
		 long end = System.currentTimeMillis();
		 statsd.recordExecutionTime("query_updateuser", end-query_start);
 		 statsd.recordExecutionTime("updateuser.time", end-start);
 		 logger.info("The username is updated");
 		 return rUser;
    }	
}