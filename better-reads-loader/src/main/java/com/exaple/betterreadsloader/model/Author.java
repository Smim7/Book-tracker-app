package com.exaple.betterreadsloader.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "author_by_id")
public class Author {


    @Id @PrimaryKeyColumn(name="author_id",ordinal=0,type= PrimaryKeyType.PARTITIONED)
    private String id;

    @Column("author_name")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String name;

    @Column("personal_name")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String personalName;
}
