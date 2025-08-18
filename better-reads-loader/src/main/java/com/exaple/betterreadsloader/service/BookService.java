package com.exaple.betterreadsloader.service;
import com.exaple.betterreadsloader.model.Book;
import com.exaple.betterreadsloader.repository.BookRepository;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;

@Service
public class BookService implements iBookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Slice<Book> getAllBooks(int pageSize, String pagingState) {
        CassandraPageRequest pageRequest;

        if (pagingState != null && !pagingState.isEmpty()) {
            ByteBuffer state = ByteBuffer.wrap(Base64.getDecoder().decode(pagingState));
            pageRequest = CassandraPageRequest.of(CassandraPageRequest.first(pageSize), state);
        } else {
            pageRequest = CassandraPageRequest.first(pageSize);
        }

        Slice<Book> slice = bookRepository.findAll(pageRequest);

        String nextPageToken = null;
        if (slice.hasNext()) {
            CassandraPageRequest next = (CassandraPageRequest) slice.nextPageable();
            ByteBuffer nextState = next.getPagingState();
            if (nextState != null) {
                byte[] bytes = new byte[nextState.remaining()];
                nextState.duplicate().get(bytes);
                nextPageToken = Base64.getEncoder().encodeToString(bytes);
            }
        }

        System.out.println("Next Page Token: " + nextPageToken);

        return slice;
    }

    @Override
    public Book getBookByName(String bookName) {

        return bookRepository.findByName(bookName);
    }

    @Override
    public List<Book> getBooksByAuthorName(String authorName) {

        return bookRepository.findByAuthorName(authorName);
    }
}