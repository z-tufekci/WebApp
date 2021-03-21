Zeynep Tufekci

WEB APPLICATION : RESTFUL API
------------------------------------

Prerequisites for building and deploying application

	Operating System: Ubuntu 18.04

	Programming Language: Java(OpenJDK11)

	Project: Apache Maven4.0 

	Relational Database: MySQL 14.14

	Backend Framework: Spring RESTFUL API, JPA, Spring Boot 2.4.2




User{

	UUID id	   example: d290f1ee-6c54-4b01-90e6-d701748f0851 (readOnly)

	String first_name
	
	String last_name
	
	String password	 (writeOnly) [BCyrpted]
	
	String username	(email) example: jane.doe@example.com
	
	account_created	string($date-time) 	example: 2016-08-29T09:12:33.001Z
	(readOnly)

	account_updated	string($date-time)  example: 2016-08-29T09:12:33.001Z
	(readOnly)
} 

Book{

	id*	string($uuid)
	example: d6193106-a192-46db-aae9-f151004ee453
	readOnly: true
	
	title*	string
	example: Computer Networks
	
	author*	string
	example: Andrew S. Tanenbaum
	
	isbn*	string
	example: 978-0132126953
	
	published_date*	string
	example: May, 2020
	
	book_created	string($date-time)
	example: 2016-08-29T09:12:33.001Z
	readOnly: true
	
	user_id	string($uuid)
	example: d290f1ee-6c54-4b01-90e6-d701748f0851
	readOnly: true
 
}


POST REQUEST{

	"first_name": "Merhaba",
	"last_name": "Dunya",
	"password": "mouse",
  	"username": "m.dunya@email.com"

}

(201 HttpStatus.Created )
POST RESPONSE{

    	"id": "20451d4c-7486-48e7-9748-b261af23591b",
    	"username": "merhaba.galaxy@email.com",
    	"account_created": "2021-02-16T02:32:09.092+00:00",
    	"account_updated": "2021-02-16T02:32:09.092+00:00",
    	"firstname": "Merhaba",
    	"lastname": "Galaxy"

}

(200 HttpStatus.OK )
GET RESPONSE{

    	"id": "5721bc36-2567-4011-bf0b-a0533155b62e",
    	"username": "john.doe@example.com",
    	"account_created": "2021-02-15T02:38:25.000+00:00",
    	"account_updated": "2021-02-15T02:38:25.000+00:00",
    	"firstname": "Jane",
    	"lastname": "Doe"

}

URL (/v1/user/self)
PUT REQUEST{

	"first_name": "John",
	"last_name": "Doe",
	"password": "ali"

}



POST METHOD (/v1/user) 
Public Access

GET (/v1/user/self)
Basic Access Authentication

PUT (/v1/user/self)
Basic Access Authentication

POST (/books)
Basic Access Authentication

DELETE (​/books​/{id})
Basic Access Authentication

GET (/books)
Public Access

GET (/books​/{id})
Public Access



