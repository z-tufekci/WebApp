package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.User_db;

@Repository
public interface UserdbRepository extends JpaRepository<User_db, Integer> {
	List<User_db> findByUsername(String username);
}
