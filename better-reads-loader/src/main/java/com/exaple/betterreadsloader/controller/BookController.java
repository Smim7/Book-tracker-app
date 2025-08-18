package com.exaple.betterreadsloader.controller;


import com.exaple.betterreadsloader.model.Book;

import com.exaple.betterreadsloader.service.iBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;

import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class BookController {

    private final iBookService bookService;




    @GetMapping("/books")
    public ResponseEntity<?> getAllBooks(
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "pagingState", required = false) String pagingState) {

        Slice<Book> books = bookService.getAllBooks(pageSize, pagingState);

        return ResponseEntity.ok(Map.of(
                "books", books.getContent(),
                "hasNext", books.hasNext(),
                "nextPageState",
                books.hasNext() ? ((CassandraPageRequest) books.getPageable()).getPagingState().toString() : null
        ));
    }
    @GetMapping("/books/name")
    public ResponseEntity<Book> getBookByName(@RequestParam String name) {
        Book book = bookService.getBookByName(name);
        if (book != null) {
            return ResponseEntity.ok(book);
        } else {
            return ResponseEntity.notFound().build();
        }

    }
    @GetMapping("/books/author/{authorName}")
    public ResponseEntity<List<Book>> getBooksByAuthorName(@PathVariable String authorName) {

        List<Book> books=bookService.getBooksByAuthorName(authorName);
        if (books != null && !books.isEmpty()) {
            return ResponseEntity.ok(books);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}


