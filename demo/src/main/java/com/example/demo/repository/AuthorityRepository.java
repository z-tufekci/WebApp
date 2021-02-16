package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Authority;;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
	List<Authority> findByUsername(String username);
}
