package com.example.demo.repository;


import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
	List<Book> findById(UUID id);
	List<Book> findByIsbn(String isbn);
}