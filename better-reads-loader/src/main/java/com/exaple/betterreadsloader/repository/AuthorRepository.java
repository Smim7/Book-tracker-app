package com.exaple.betterreadsloader.repository;

import com.exaple.betterreadsloader.model.Author;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends CassandraRepository<Author,String> {
}
