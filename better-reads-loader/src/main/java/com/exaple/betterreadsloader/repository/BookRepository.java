package com.exaple.betterreadsloader.repository;

import com.exaple.betterreadsloader.model.Book;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends CassandraRepository<Book,String> {
    @Query("SELECT * FROM book_by_id WHERE book_name=?0 ALLOW FILTERING")
    Book findByName(String bookName);

    @Query("SELECT * FROM book_by_id WHERE author_names CONTAINS ?0 ALLOW FILTERING")
    List<Book> findByAuthorName(String authorName);
}
