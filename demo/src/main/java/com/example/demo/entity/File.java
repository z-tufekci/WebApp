package com.example.demo.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "files", catalog = "user_db" )
public class File {

	@Id    
	@GeneratedValue(strategy = GenerationType.AUTO)
	@org.hibernate.annotations.Type(type="uuid-char")
	UUID id;
	@NotNull
	String filename;
	@NotNull
	String s3_object_name; // ad79de23-6820-482c-8d2b-d513885b0e17/9afdf82d-7e8e-4491-90d3-ff0499bf6afe/image.jpg
	@NotNull
	Date created_date;	
	
	
	@NotNull
	@Column (name ="user_id")
	@org.hibernate.annotations.Type(type="uuid-char")
	UUID userId;
	
	public File(){
		
	}
	
	public File(@NotNull UUID id, @NotNull String filename, @NotNull String s3_object_name, @NotNull Date created_date,
			@NotNull UUID userId) {
		super();
		this.id = id;
		this.filename = filename;
		this.s3_object_name = s3_object_name;
		this.created_date = created_date;
		this.userId = userId;
	}
	
	@Override
	public String toString() {
		return "File [id=" + id + ", filename=" + filename + ", s3_object_name=" + s3_object_name
				+ ", created_date=" + created_date + ", userId=" + userId + "]";
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getS3_object_name() {
		return s3_object_name;
	}
	public void setS3_object_name(String s3_object_name) {
		this.s3_object_name = s3_object_name;
	}
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	public UUID getUserId() {
		return userId;
	}
	public void setUserId(UUID userId) {
		this.userId = userId;
	}

}
