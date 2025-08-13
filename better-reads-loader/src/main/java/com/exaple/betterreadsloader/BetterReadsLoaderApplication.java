package com.exaple.betterreadsloader;

import com.exaple.betterreadsloader.repository.AuthorRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@RequiredArgsConstructor
@SpringBootApplication
public class BetterReadsLoaderApplication {
   @Value( "${datadump.location.author}")
    private String authorDumpLocation;

    @Value( "${datadump.location.works}")
    private String worksDumpLocation;

    private final AuthorRepository authorRepository;

    public static void main(String[] args) {

        SpringApplication.run(BetterReadsLoaderApplication.class, args);

    }

    @PostConstruct
    public void start(){
        System.out.println("Application started...");
        System.out.println(authorDumpLocation);

    }

}
