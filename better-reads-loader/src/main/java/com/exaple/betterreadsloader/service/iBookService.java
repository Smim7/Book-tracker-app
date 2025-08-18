package com.exaple.betterreadsloader.service;

import com.exaple.betterreadsloader.model.Book;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface iBookService {
    public Slice<Book> getAllBooks(int pageSize, String pagingState);
    public Book getBookByName(String bookName);
    public List<Book> getBooksByAuthorName(String authorName);
}
