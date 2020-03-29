package com.librarium.repository;

import com.librarium.entity.Book;
import com.librarium.entity.dto.Items;
import com.librarium.entity.dto.VolumeInfo;
import com.librarium.service.BookService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(SpringRunner.class)
public class BookApiRepositoryTest {
    @MockBean
    BookService bookService;
    @Mock
    private RestTemplate restTemplate = new RestTemplate();
    @Autowired
    @InjectMocks
    BookApiRepository bookApiRepository;

    @TestConfiguration
    static class TestContextConfiguration {
        @Bean
        public BookApiRepository bookApiRepository() {
            return new BookApiRepository();
        }
    }

    @Before
    public void init() {
        Mockito.when(bookService.castBookFromDtoBook(any())).thenCallRealMethod();
        Mockito.when(bookService.getEngGenre(any())).thenReturn("другое");
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getBookListByTitle_NotFound_returnEmptyList() {
        Mockito.when(restTemplate.getForEntity(anyString(), any()))
                .thenThrow(new RestClientResponseException("Not found", 404, "404", HttpHeaders.EMPTY,
                        new byte[0], Charset.defaultCharset()));
        List<Book> bookListByGenre = bookApiRepository.getBookListByTitle("ascdgfhgh", 10);
        assertEquals(bookListByGenre.size(), 0);
    }

    @Test
    public void getBookListByTitle_foundBook_returnOneBookList() {
        Items items = new Items();
        com.librarium.entity.dto.Book book = new com.librarium.entity.dto.Book();
        VolumeInfo vi = new VolumeInfo();
        vi.setTitle("dawsefrdgh");
        book.setVolumeInfo(vi);
        items.setItems(Arrays.asList(book));
        Mockito.when(restTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(items, HttpStatus.OK));
        List<Book> bookListByGenre = bookApiRepository.getBookListByTitle("ascdgfhgh", 10);
        assertEquals(bookListByGenre.size(), 1);
    }

    @Test(expected = RestClientException.class)
    public void getBookListByTitle_ServiceException_ExceptionThrows() {
        Mockito.when(restTemplate.getForEntity(anyString(), any()))
                .thenThrow(new RestClientResponseException("adwawd", 405, "404", HttpHeaders.EMPTY,
                        new byte[0], Charset.defaultCharset()));
        bookApiRepository.getBookListByTitle("ascdgfhgh", 10);
    }

    @Test
    public void getBookListByIsbn_NotFound_returnEmptyList() {
        Mockito.when(restTemplate.getForEntity(anyString(), any()))
                .thenThrow(new RestClientResponseException("Not found", 404, "404", HttpHeaders.EMPTY,
                        new byte[0], Charset.defaultCharset()));
        List<Book> bookListByGenre = bookApiRepository.getBookListByIsbn("2134545", 10);
        assertEquals(bookListByGenre.size(), 0);
    }

    @Test
    public void getBookListByIsbn_foundBook_returnOneBookList() {
        Items items = new Items();
        com.librarium.entity.dto.Book book = new com.librarium.entity.dto.Book();
        VolumeInfo vi = new VolumeInfo();
        vi.setTitle("dawsefrdgh");
        book.setVolumeInfo(vi);
        items.setItems(Arrays.asList(book));
        Mockito.when(restTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(items, HttpStatus.OK));
        List<Book> bookListByGenre = bookApiRepository.getBookListByIsbn("23456", 10);
        assertEquals(bookListByGenre.size(), 1);
    }

    @Test(expected = RestClientException.class)
    public void getBookListByIsbn_ServiceException_returnEmptyList() {
        Mockito.when(restTemplate.getForEntity(anyString(), any()))
                .thenThrow(new RestClientResponseException("adwawd", 405, "404", HttpHeaders.EMPTY,
                        new byte[0], Charset.defaultCharset()));
        bookApiRepository.getBookListByIsbn("12345", 10);
    }

    @Test
    public void getBookListByGenre_NotFound_returnEmptyList() {
        Mockito.when(restTemplate.getForEntity(anyString(), any()))
                .thenThrow(new RestClientResponseException("Not found", 404, "404", HttpHeaders.EMPTY,
                        new byte[0], Charset.defaultCharset()));
        List<Book> bookListByGenre = bookApiRepository.getBookListByGenre("other", 10);
        assertEquals(bookListByGenre.size(), 0);
    }

    @Test
    public void getBookListByGenre_RusGenreFindBook_returnOneBookList() {
        Items items = new Items();
        com.librarium.entity.dto.Book book = new com.librarium.entity.dto.Book();
        VolumeInfo vi = new VolumeInfo();
        vi.setTitle("dawsefrdgh");
        book.setVolumeInfo(vi);
        items.setItems(Arrays.asList(book));
        Mockito.when(restTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(items, HttpStatus.OK));
        List<Book> bookListByGenre = bookApiRepository.getBookListByGenre("другое", 10);
        assertEquals(bookListByGenre.size(), 1);
    }

    @Test(expected = RestClientException.class)
    public void getBookListByGenre_ServiceException_ExceptionThrows() {
        Mockito.when(restTemplate.getForEntity(anyString(), any()))
                .thenThrow(new RestClientResponseException("adwawd", 405, "404", HttpHeaders.EMPTY,
                        new byte[0], Charset.defaultCharset()));
        bookApiRepository.getBookListByGenre("other", 10);
    }

    @Test(expected = RestClientException.class)
    public void getBookListByAuthor_ServiceException_ExceptionThrows() {
        Mockito.when(restTemplate.getForEntity(anyString(), any()))
                .thenThrow(new RestClientResponseException("adwawd", 405, "404", HttpHeaders.EMPTY,
                        new byte[0], Charset.defaultCharset()));
        bookApiRepository.getBookListByAuthor("1asdfghj", 10);
    }

    @Test
    public void getBookListByAuthor_NotFound_returnEmptyList() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(restTemplate.getForEntity(anyString(), any()))
                .thenThrow(new RestClientResponseException("Not found", 404, "404", HttpHeaders.EMPTY,
                        new byte[0], Charset.defaultCharset()));
        List<Book> bookListByGenre = bookApiRepository.getBookListByAuthor("wadsfrgt", 10);
        assertEquals(bookListByGenre.size(), 0);
    }

    @Test
    public void getBookListByAuthor_findBook_returnOneBookList() {
        Items items = new Items();
        com.librarium.entity.dto.Book book = new com.librarium.entity.dto.Book();
        VolumeInfo vi = new VolumeInfo();
        vi.setTitle("dawsefrdgh");
        book.setVolumeInfo(vi);
        items.setItems(Arrays.asList(book));
        Mockito.when(restTemplate.getForEntity(anyString(), any()))
                .thenReturn(new ResponseEntity<>(items, HttpStatus.OK));
        List<Book> bookListByGenre = bookApiRepository.getBookListByAuthor("asdfgh", 10);
        assertEquals(bookListByGenre.size(), 1);
    }

    @Test(expected = RestClientException.class)
    public void getBookListByGenresAndAuthors_ServiceExceptionAuthors_ExceptionThrows() {
        Mockito.when(restTemplate.getForEntity(Mockito.contains("authors"), any()))
                .thenThrow(new RestClientResponseException("adwawd", 405, "405", HttpHeaders.EMPTY,
                        new byte[0], Charset.defaultCharset()));
        bookApiRepository.findBooksByGenresAndAuthors(Arrays.asList("sadf"), Arrays.asList("sadfadw"));
    }

    @Test(expected = RestClientException.class)
    public void getBookListByGenresAndAuthors_ServiceExceptionGenres_ExceptionThrows() {
        Items items = new Items();
        com.librarium.entity.dto.Book book = new com.librarium.entity.dto.Book();
        VolumeInfo vi = new VolumeInfo();
        vi.setTitle("dawsefrdgh");
        book.setVolumeInfo(vi);
        items.setItems(Arrays.asList(book));
        Mockito.when(restTemplate.getForEntity(Mockito.contains("authors"), any()))
                .thenReturn(new ResponseEntity<>(items, HttpStatus.OK));
        Mockito.when(restTemplate.getForEntity(Mockito.contains("subject"), any()))
                .thenThrow(new RestClientResponseException("adwawd", 405, "405", HttpHeaders.EMPTY,
                        new byte[0], Charset.defaultCharset()));
        bookApiRepository.findBooksByGenresAndAuthors(Arrays.asList("sadf"), Arrays.asList("sadfadw"));
    }

    @Test
    public void getBookListByGenresAndAuthors_AllFound_returnList() {
        Items items = new Items();
        com.librarium.entity.dto.Book book = new com.librarium.entity.dto.Book();
        VolumeInfo vi = new VolumeInfo();
        vi.setTitle("dawsefrdgh");
        book.setVolumeInfo(vi);
        items.setItems(Arrays.asList(book));
        Mockito.when(restTemplate.getForEntity(Mockito.contains("authors"), any()))
                .thenReturn(new ResponseEntity<>(items, HttpStatus.OK));
        Mockito.when(restTemplate.getForEntity(Mockito.contains("subject"), any()))
                .thenReturn(new ResponseEntity<>(items, HttpStatus.OK));
        bookApiRepository.findBooksByGenresAndAuthors(Arrays.asList("sadf"), Arrays.asList("sadfadw"));
    }

}