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
    final private String FIELD_PARAM = "items(volumeInfo(title,categories,authors,publishedDate,industryIdentifiers,description))";
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

    public List<com.librarium.entity.dto.Book> getRecommendation(List<Book> books) {
        Map<String, Integer> authors = new HashMap<>();
        Map<String, Integer> genres = new HashMap<>();
        List<com.librarium.entity.dto.Book> result = new ArrayList<>();
        for (Book b : books) {
            String gen = (FieldParser.haveCyrillicSymbols(b.getGenre())) ? getEngGenre(b.getGenre()) : b.getGenre();
            String[] auth = b.getAuthor().split(", ");
            if (genres.containsKey(gen)) {
                genres.put(gen, genres.get(gen) + 1);
            } else {
                genres.put(gen, 0);
            }
            for (String a : auth) {
                if (authors.containsKey(a)) {
                    authors.put(a, authors.get(a) + 1);
                } else {
                    authors.put(a, 0);
                }
            }
        }
        List<String> rec_genres = get5orLessMaxKeys(genres);
        List<String> rec_authors = get5orLessMaxKeys(authors);
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
        List<com.librarium.entity.dto.Book> resultFin = new ArrayList<>();
        for (Book b : books) {
            for (com.librarium.entity.dto.Book b1 : result) {
                if (b1.getVolumeInfo().getIndustryIdentifiers() != null && b1.getVolumeInfo().getIndustryIdentifiers().get(0).get("type").startsWith("ISBN")) {
                    String isbn = b1.getVolumeInfo().getIndustryIdentifiers().get(0).get("identifier");
                    if (!isbn.equals(FieldParser.parseISBN(b.getISBN()))) {
                        resultFin.add(b1);
                    }
                }
            }
        }
        while (resultFin.size() > 20) {
            int index = (int) (Math.random() * resultFin.size());
            resultFin.remove(index);
        }
        for (com.librarium.entity.dto.Book b : resultFin) {
            if (b.getVolumeInfo().getCategories() != null) {
                List<String> genre = new ArrayList<>();
                for (String cat : b.getVolumeInfo().getCategories()) {
                    genre.add(getRuGenre(cat));
                }
                b.getVolumeInfo().setCategories(genre);
            }
        }
        return resultFin;
    }

    private List<String> get5orLessMaxKeys(Map<String, Integer> map) {
        if (map.size() <= 5) {
            return new ArrayList<>(map.keySet());
        }
        List<String> values = new ArrayList<>();
        while (values.size() <= 5) {
            Integer max = getMax(map);
            List<String> temp = getListKeysWithValues(max, map);
            if (temp.size() <= 5 - values.size()) {
                values.addAll(temp);
                for (String k : temp) {
                    map.remove(k);
                }
            } else {
                String randomKey = temp.get((int) (Math.random() * temp.size()));
                values.add(randomKey);
                map.remove(randomKey);
            }
        }
        return values;
    }

    private <K, V extends Comparable<V>> V getMax(Map<K, V> map) {
        Optional<Map.Entry<K, V>> maxEntry = map.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());
        return maxEntry.get().getValue();
    }

    private List<String> getListKeysWithValues(Integer value, Map<String, Integer> map) {
        List<String> res = new ArrayList<>();
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            if (e.getValue().equals(value)) res.add(e.getKey());
        }
        return res;
    }

    public Book castBookFromDtoBook(com.librarium.entity.dto.Book book) {
        VolumeInfo vi = book.getVolumeInfo();
        Book result = new Book(vi.getTitle());
        StringBuilder author = new StringBuilder();
        if (vi.getAuthors() != null) {
            Iterator<String> iter = vi.getAuthors().iterator();
            while (iter.hasNext()) {
                author.append(iter.next());
                author.append(iter.hasNext() ? ", " : "");
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
