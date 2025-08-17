package com.exaple.betterreadsloader.repository;

import com.exaple.betterreadsloader.model.Book;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends CassandraRepository<Book,String> {
}
