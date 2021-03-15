package com.example.demo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "authorities", catalog = "user_db") 
public class Authority {
	@Id
	@Column(name="username")
	private String username;
	
	@Column(name="authority")
	private String role;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public Authority() {
		super();
	}
	public Authority(String username, String role) {
		super();
		this.username = username;
		this.role = role;
	}
}
