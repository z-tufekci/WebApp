ASSIGNMENT #1 WEB APP : RESTFUL API

Prerequisites for building and deploying application
------------------------------------
Operating System: Ubuntu 18.04

Programming Language: Java(OpenJDK11)

Project: Apache Maven4.0 

Relational Database: MySQL 14.14

Backend Framework: Spring RESTFUL API
		   Spring Boot 2.4.2

[Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
[Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
[Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
[Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)



POST METHOD (/v1/user) 
No Authentication

GET (/v1/user/self)
Basic Access Authentication:

PUT (/v1/user/self)
Basic Access Authentication:


POST REQUEST:
{
  "first_name": "Merhaba",
  "last_name": "Dunya",
  "password": "mouse",
  "username": "m.dunya@email.com"
}

(201 HttpStatus.Created ) : SUCCESS
--------------------------------------------------
POST RESPONSE:
{
    "id": "20451d4c-7486-48e7-9748-b261af23591b",
    "username": "merhaba.galaxy@email.com",
    "account_created": "2021-02-16T02:32:09.092+00:00",
    "account_updated": "2021-02-16T02:32:09.092+00:00",
    "firstname": "Merhaba",
    "lastname": "Galaxy"
}


GET RESPONSE: (200 HttpStatus.OK )
{
    "id": "5721bc36-2567-4011-bf0b-a0533155b62e",
    "username": "john.doe@example.com",
    "account_created": "2021-02-15T02:38:25.000+00:00",
    "account_updated": "2021-02-15T02:38:25.000+00:00",
    "firstname": "Jane",
    "lastname": "Doe"
}


PUT REQUEST:
{
  "first_name": "John",
  "last_name": "Doe",
  "password": "ali"
}

