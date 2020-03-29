package com.librarium.repository;

import com.librarium.entity.Book;
import com.librarium.entity.dto.Items;
import com.librarium.service.BookService;
import com.librarium.utils.FieldParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class BookApiRepository {
    private RestTemplate restTemplate = new RestTemplate();
    final private String SEARCH_URL = "https://www.googleapis.com/books/v1/volumes";
    final private String FIELD_PARAM = "items(volumeInfo(title,categories,authors,publishedDate,industryIdentifiers,description))";
    @Autowired
    private BookService bookService;

    public List<Book> getBookListByTitle(String title, int maxResult) throws RestClientResponseException {
        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(SEARCH_URL)
                .queryParam("fields", FIELD_PARAM)
                .queryParam("maxResults", maxResult)
                .queryParam("q", "title:" + title.replace(" ", "&"))
                .build();
        try {
            ResponseEntity<Items> response = restTemplate.getForEntity(uriBuilder.toUriString(), Items.class);
            List<Book> list = new ArrayList<>();
            if (Objects.requireNonNull(response.getBody()).getItems() == null) return list;
            for (com.librarium.entity.dto.Book b : response.getBody().getItems()) {
                list.add(bookService.castBookFromDtoBook(b));
            }
            return list;
        } catch (RestClientResponseException e) {
            if (HttpStatus.valueOf(e.getRawStatusCode()) != HttpStatus.NOT_FOUND) {
                throw e;
            } else {
                return new ArrayList<>();
            }
        }
    }

    public List<Book> getBookListByIsbn(String isbn, int maxResult) throws RestClientResponseException {
        isbn = isbn.replace("-", "");
        isbn = isbn.replace(" ", "");
        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(SEARCH_URL)
                .queryParam("fields", FIELD_PARAM)
                .queryParam("maxResults", maxResult)
                .queryParam("q", "isbn:" + isbn.replace(" ", "&"))
                .build();
        try {

            ResponseEntity<Items> response = restTemplate.getForEntity(uriBuilder.toUriString(), Items.class);
            List<Book> list = new ArrayList<>();
            if (Objects.requireNonNull(response.getBody()).getItems() == null) return list;
            for (com.librarium.entity.dto.Book b : response.getBody().getItems()) {
                list.add(bookService.castBookFromDtoBook(b));
            }
            return list;
        } catch (RestClientResponseException e) {
            if (HttpStatus.valueOf(e.getRawStatusCode()) != HttpStatus.NOT_FOUND) {
                throw e;
            } else {
                return new ArrayList<>();
            }
        }
    }

    public List<Book> getBookListByGenre(String genre, int maxResult) throws RestClientResponseException {
        if (FieldParser.haveCyrillicSymbols(genre)) {
            genre = bookService.getEngGenre(genre);
        }
        genre = genre.replace(" & ", "&");
        genre = genre.replace(" ", "&");
        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(SEARCH_URL)
                .queryParam("fields", FIELD_PARAM)
                .queryParam("maxResults", maxResult)
                .queryParam("q", "subject:" + genre)
                .build();
        try {
            ResponseEntity<Items> response = restTemplate.getForEntity(uriBuilder.toUriString(), Items.class);
            List<Book> list = new ArrayList<>();
            if (Objects.requireNonNull(response.getBody()).getItems() == null) return list;
            for (com.librarium.entity.dto.Book b : response.getBody().getItems()) {
                list.add(bookService.castBookFromDtoBook(b));
            }
            return list;
        } catch (RestClientResponseException e) {
            if (HttpStatus.valueOf(e.getRawStatusCode()) != HttpStatus.NOT_FOUND) {
                throw e;
            } else {
                return new ArrayList<>();
            }
        }
    }

    public List<Book> getBookListByAuthor(String author, int maxResult) throws RestClientResponseException {
        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(SEARCH_URL)
                .queryParam("fields", FIELD_PARAM)
                .queryParam("maxResults", maxResult)
                .queryParam("q", "authors:" + author.replace(" ", "&"))
                .build();
        try {
            ResponseEntity<Items> response = restTemplate.getForEntity(uriBuilder.toUriString(), Items.class);
            List<Book> list = new ArrayList<>();
            if (Objects.requireNonNull(response.getBody()).getItems() == null) return list;
            for (com.librarium.entity.dto.Book b : response.getBody().getItems()) {
                list.add(bookService.castBookFromDtoBook(b));
            }
            return list;
        } catch (RestClientResponseException e) {
            if (HttpStatus.valueOf(e.getRawStatusCode()) != HttpStatus.NOT_FOUND) {
                throw e;
            } else {
                return new ArrayList<>();
            }
        }
    }

    public List<com.librarium.entity.dto.Book> findBooksByGenresAndAuthors( List<String> rec_genres, List<String> rec_authors) {
        List<com.librarium.entity.dto.Book> result = new ArrayList<>();
        for (String author : rec_authors) {
            UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(SEARCH_URL)
                    .queryParam("fields", FIELD_PARAM)
                    .queryParam("maxResults", 10)
                    .queryParam("q", "authors:" + author.replace(" ", "&"))
                    .build();
            try {
                ResponseEntity<Items> response = restTemplate.getForEntity(uriBuilder.toUriString(), Items.class);

                if (Objects.requireNonNull(response.getBody()).getItems() != null) {
                    result.addAll(response.getBody().getItems());
                }
            } catch (RestClientResponseException e) {
                if (HttpStatus.valueOf(e.getRawStatusCode()) != HttpStatus.NOT_FOUND) {
                    throw e;
                }
            }
        }
        for (String genre : rec_genres) {
            UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(SEARCH_URL)
                    .queryParam("fields", FIELD_PARAM)
                    .queryParam("maxResults", 15)
                    .queryParam("q", "subject:" + genre)
                    .build();
            try {
                ResponseEntity<Items> response = restTemplate.getForEntity(uriBuilder.toUriString(), Items.class);
                if (Objects.requireNonNull(response.getBody()).getItems() != null) {
                    result.addAll(response.getBody().getItems());
                }
            } catch (RestClientResponseException e) {
                if (HttpStatus.valueOf(e.getRawStatusCode()) != HttpStatus.NOT_FOUND) {
                    throw e;
                }
            }
        }
        return result;
    }
}
