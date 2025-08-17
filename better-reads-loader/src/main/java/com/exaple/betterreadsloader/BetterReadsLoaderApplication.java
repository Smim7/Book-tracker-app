package com.exaple.betterreadsloader;

import com.exaple.betterreadsloader.model.Author;
import com.exaple.betterreadsloader.model.Book;
import com.exaple.betterreadsloader.repository.AuthorRepository;

import com.exaple.betterreadsloader.repository.BookRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@SpringBootApplication
public class BetterReadsLoaderApplication {
    @Value("${datadump.location.author}")
    private String authorDumpLocation;

    @Value("${datadump.location.works}")
    private String worksDumpLocation;

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public static void main(String[] args) {
        SpringApplication.run(BetterReadsLoaderApplication.class, args);
    }

    private void initAuthors() {
        Path path = Paths.get(authorDumpLocation);
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> {
                try {
                    // Extract JSON part
                    String jsonString = line.substring(line.indexOf("{"));

// Parse with org.json
                    JSONObject jsonObject = new JSONObject(jsonString);

// Construct Author object
                    Author author = new Author();
                    author.setId(jsonObject.optString("key").replace("/authors/", ""));
                    author.setName(jsonObject.optString("name"));
                    author.setPersonalName(jsonObject.optString("personal_name"));

                    // Save to Cassandra
                    System.out.println("Saving author: " + author.getName());
                    authorRepository.save(author);

                } catch (Exception e) {
                    System.err.println("Failed to process line: " + e.getMessage());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    private void initWorks() {
//        Path path = Paths.get(worksDumpLocation);
//        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//        try (Stream<String> lines = Files.lines(path)) {
//            lines.forEach(line -> {
//                try {
//                    String jsonString = line.substring(line.indexOf("{"));
//                    JSONObject jsonObject = new JSONObject(jsonString);
//
//                    Book book = new Book();
//                    book.setId(jsonObject.optString("key").replace("/works/", ""));
//                    book.setName(jsonObject.optString("title"));
//
//                    JSONObject descriptionObj = jsonObject.optJSONObject("description");
//                    if (descriptionObj != null) {
//                        book.setDescription(descriptionObj.optString("value" ));
//                    }
//
//                    JSONObject createdObj = jsonObject.optJSONObject("created");
//                    if (createdObj != null) {
//                        String dateStr = createdObj.optString("value");
//                        if (dateStr.length() >= 10) {
//                            try {
//                                book.setPublishedDate(LocalDate.parse(dateStr.substring(0, 10)));
//                            } catch (Exception e) {
//                                System.err.println("Invalid date format: " + dateStr);
//                            }
//                        }
//                    }
//
//                    JSONArray coverJsonArr = jsonObject.optJSONArray("covers");
//                    if (coverJsonArr != null) {
//                        List<String> coverIds = new ArrayList<>();
//                        for (int i = 0; i < coverJsonArr.length(); i++) {
//                            coverIds.add(String.valueOf(coverJsonArr.get(i)));
//                        }
//                        book.setCoverIds(coverIds);
//                    }
//
//                    JSONArray authorIdsJsonArr = jsonObject.optJSONArray("authors");
//                    if (authorIdsJsonArr != null) {
//                        List<String> authorIds = new ArrayList<>();
//                        for (int i = 0; i < authorIdsJsonArr.length(); i++) {
//                            String authorId = authorIdsJsonArr
//                                    .getJSONObject(i)
//                                    .getJSONObject("author")
//                                    .getString("key")
//                                    .replace("/authors/", "");
//                            authorIds.add(authorId);
//                        }
//                        book.setAuthorIds(authorIds);
//
//                        List<String> authorNames = authorIds.stream()
//                                .map(id -> authorRepository.findById(id)
//                                        .map(Author::getName)
//                                        .orElse("unknown author"))
//                                .collect(Collectors.toList());
//                        book.setAuthorNames(authorNames);
//                    }
//
//                    bookRepository.save(book);
//
//                } catch (Exception e) {
//                    System.err.println("Failed to process work line: " + e.getMessage());
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
private void initWorks() {
    Path path = Paths.get(worksDumpLocation);
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    try (Stream<String> lines = Files.lines(path)) {
        lines.forEach(line -> {
            if (!line.contains("{")) {
                return; // skip malformed lines
            }

            try {
                String jsonString = line.substring(line.indexOf("{"));
                JSONObject jsonObject = new JSONObject(jsonString);

                Book book = new Book();
                book.setId(jsonObject.optString("key").replace("/works/", ""));
                book.setName(jsonObject.optString("title"));

                JSONObject descriptionObj = jsonObject.optJSONObject("description");
                if (descriptionObj != null) {
                    book.setDescription(descriptionObj.optString("value"));
                }

                JSONObject createdObj = jsonObject.optJSONObject("created");
                if (createdObj != null) {
                    String dateStr = createdObj.optString("value");
                    try {
                        LocalDate publishedDate = LocalDate.parse(dateStr.substring(0, 10));
                        book.setPublishedDate(publishedDate);
                    } catch (Exception e) {
                        System.err.println("Invalid date format: " + dateStr);
                    }
                }

                JSONArray coverJsonArr = jsonObject.optJSONArray("covers");
                if (coverJsonArr != null) {
                    List<String> coverIds = new ArrayList<>();
                    for (int i = 0; i < coverJsonArr.length(); i++) {
                        coverIds.add(String.valueOf(coverJsonArr.get(i)));
                    }
                    book.setCoverIds(coverIds);
                }

                JSONArray authorIdsJsonArr = jsonObject.optJSONArray("authors");
                if (authorIdsJsonArr != null) {
                    List<String> authorIds = new ArrayList<>();
                    List<String> authorNames = new ArrayList<>();
                    for (int i = 0; i < authorIdsJsonArr.length(); i++) {
                        String authorId = authorIdsJsonArr
                                .getJSONObject(i)
                                .getJSONObject("author")
                                .getString("key")
                                .replace("/authors/", "");
                        authorIds.add(authorId);

                        authorRepository.findById(authorId)
                                .map(Author::getName)
                                .ifPresentOrElse(authorNames::add,
                                        () -> authorNames.add("unknown author"));
                    }
                    book.setAuthorIds(authorIds);
                    book.setAuthorNames(authorNames);
                }

                bookRepository.save(book);

            } catch (Exception e) {
                System.err.println("Failed to process work line: " + e.getMessage());
            }
        });
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    @PostConstruct
    public void start() {
        initAuthors();
        initWorks();
    }
}