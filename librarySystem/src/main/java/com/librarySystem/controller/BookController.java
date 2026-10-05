package com.librarySystem.controller;

import com.librarySystem.entity.Book;
import com.librarySystem.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    private static final List<String> CATEGORIES = List.of(
        "Fiction", "Psychology", "Science", "Technology", "History", "Mathematics"
    );

    @GetMapping("/updateBook/{id}")
    public String updateBook(@PathVariable("id") int id, Model model) {
        Book b = bookService.getBookById(id);
        model.addAttribute("categories", CATEGORIES);
        model.addAttribute("book", b);
        return "admin/updateBook";
    }
}