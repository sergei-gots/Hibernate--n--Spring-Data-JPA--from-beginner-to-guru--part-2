package com.example.springbootbook2.repository;

import com.example.springbootbook2.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Created by sergei on 18/02/2025
 */
public interface AuthorRepository extends JpaRepository<Author, UUID> {
}
