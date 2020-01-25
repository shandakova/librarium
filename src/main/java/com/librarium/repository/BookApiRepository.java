package com.librarium.repository;

import com.librarium.entity.Book;
import com.librarium.entity.dto.Items;
import com.librarium.entity.dto.VolumeInfo;
import com.librarium.utils.FieldParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

@Repository
public class BookApiRepository {
    final private RestTemplate restTemplate = new RestTemplate();
    final private String SEARCH_URL = "https://www.googleapis.com/books/v1/volumes";
    final private String FIELD_PARAM = "items(volumeInfo(title,categories,authors,publishedDate,industryIdentifiers))";
    final private HashMap<String, String> genres = getGenres();

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
                list.add(castBookFromDtoBook(b));
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
                list.add(castBookFromDtoBook(b));
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
            genre = getEngGenre(genre);
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
                list.add(castBookFromDtoBook(b));
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
                list.add(castBookFromDtoBook(b));
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

    private Book castBookFromDtoBook(com.librarium.entity.dto.Book book) {
        VolumeInfo vi = book.getVolumeInfo();
        Book result = new Book(vi.getTitle());
        StringBuilder author = new StringBuilder();
        if (vi.getAuthors() != null) {
            Iterator<String> iter = vi.getAuthors().iterator();
            while (iter.hasNext()) {
                author.append(iter.next());
                author.append(iter.hasNext() ? " ," : "");
            }
        }
        result.setAuthor(author.toString());
        if (vi.getPublishedDate() != null) {
            int date = Integer.parseInt(vi.getPublishedDate().substring(0, 4));
            result.setYear(date);
        } else {
            result.setYear(0);
        }
        if (vi.getIndustryIdentifiers() != null && vi.getIndustryIdentifiers().get(0).get("type").startsWith("ISBN")) {
            String isbn = vi.getIndustryIdentifiers().get(0).get("identifier");
            result.setISBN(isbn);
        } else {
            result.setISBN("");
        }
        String genre = "";
        if (vi.getCategories() != null) {
            genre = getRuGenre(vi.getCategories().get(0));
        }
        result.setGenre(genre);
        return result;
    }

    private String getRuGenre(String engGenre) {
        for (String key : genres.keySet()) {
            if (key.equals(engGenre.toLowerCase())) return genres.get(key);
        }
        return "другое";
    }

    private String getEngGenre(String ruGenre) {
        for (Map.Entry<String, String> e : genres.entrySet()) {
            if (e.getValue().equals(ruGenre.toLowerCase())) return e.getKey();
        }
        for (Map.Entry<String, String> e : genres.entrySet()) {
            if (e.getValue().contains(ruGenre.toLowerCase())) return e.getKey();
        }
        return "other";
    }

    private HashMap<String, String> getGenres() {
        File file =
                new File("src/main/resources/bisac_genres.txt");
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        HashMap<String, String> genres = new HashMap<>();
        if (sc != null) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] str = line.split(":");
                genres.put(str[0], str[1]);
            }
        }
        return genres;
    }
}
