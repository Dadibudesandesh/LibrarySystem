package com.librarySystem.repository;

import com.librarySystem.entity.Book;
import com.librarySystem.entity.BookRequest;
import com.librarySystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRequestRepository extends JpaRepository<BookRequest, Long> {

    List<BookRequest> findByStatus(String status);

    long countByStatus(String pending);

    boolean existsByStudentAndBookAndStatus(
            User student,
            Book book,
            String status
    );

    List<BookRequest> findByStudent(User student);
}

