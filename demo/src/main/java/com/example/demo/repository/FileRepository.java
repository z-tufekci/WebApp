package com.example.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.File;

@Repository
public interface FileRepository extends JpaRepository<File, Integer>{
	List<File> findById(UUID id);
	List<File> findByUserId(UUID userId);
}
