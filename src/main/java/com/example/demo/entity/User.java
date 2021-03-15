package com.example.demo.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "usersdata", catalog = "user_db") 
public class User {
	

	@Id    
	@GeneratedValue(strategy = GenerationType.AUTO)
	@org.hibernate.annotations.Type(type="uuid-char")
	private UUID id;/* read only */
	private String first_name;
	private String last_name;
	private String password;
	private String username;
	private Date account_created;/* read only */
	private Date account_updated;/* read only */

	public void setAccount_updated(Date account_updated) {
		this.account_updated = account_updated;
	}
	
	@JsonView(WithoutPasswordView.class)
	public UUID getId() {
		return id;
	}
	
	@JsonView(WithoutPasswordView.class)
	public String getFirstname() {
		return first_name;
	}
	
	@JsonView(WithoutPasswordView.class)
	public String getLastname() {
		return last_name;
	}

	@JsonView(WithoutPasswordView.class)
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	@JsonView(WithoutPasswordView.class)
	public Date getAccount_created() {
		return account_created;
	}
	@JsonView(WithoutPasswordView.class)
	public Date getAccount_updated() {
		return account_updated;
	}

	public User(String first_name, String last_name, String password, String username) {
		super();
		this.first_name = first_name;
		this.last_name = last_name;
		this.password = password;
		this.username = username;
		account_created = new Date();
		account_updated = new Date();
	}

	public void setFirstName(String first_name) {
		this.first_name = first_name;
	}

	public void setLastName(String last_name) {
		this.last_name = last_name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public User() {
		super();
	}
	public User(UUID uuid,String first_name, String last_name, String password, String username) {
		super();
		this.id= uuid;
		this.first_name = first_name;
		this.last_name = last_name;
		this.password = password;
		this.username = username;
		account_created = new Date();
		account_updated = new Date();
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", first_name=" + first_name + ", last_name=" + last_name + ", password=" + password
				+ "]";
	}

	public interface WithoutPasswordView {};

}
