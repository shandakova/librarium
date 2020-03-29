package com.librarium.service;

import com.librarium.entity.dto.Book;
import com.librarium.entity.dto.VolumeInfo;
import com.librarium.repository.BookApiRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
public class BookServiceTest {

    @TestConfiguration
    static class TestContextConfiguration {
        @Bean
        public BookService bookService() {
            return new BookService();
        }
    }


    @Autowired
    BookService bookService;
    @MockBean
    private BookApiRepository bookApiRepository;

    @Test
    public void getRecommendation_emptyBookList_returnEmptyResults() {
        Mockito.when(bookApiRepository.findBooksByGenresAndAuthors(new ArrayList<>(), new ArrayList<>()))
                .thenCallRealMethod();
        List<Book> books = bookService.getRecommendation(Collections.emptyList());
        assertEquals(0, books.size());
    }

    @Test
    public void getRecommendation_OneBookInList_returnResults() {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            VolumeInfo info = new VolumeInfo();
            info.setAuthors(Collections.singletonList("author"));
            info.setTitle("bookName");
            info.setCategories(Collections.singletonList("not_in_base"));
            info.setPublishedDate("1998");
            HashMap<String, String> identifiers = new HashMap<>();
            identifiers.put("type", "ISBN");
            identifiers.put("identifier", String.valueOf(i));
            info.setIndustryIdentifiers(Collections.singletonList(identifiers));
            books.add(new Book(info));
        }
        com.librarium.entity.Book book = new com.librarium.entity.Book("asf");
        book.setGenre("other");
        book.setAuthor("sdfg, sadfg");
        book.setISBN("0876");
        Mockito.when(bookApiRepository.findBooksByGenresAndAuthors(any(),
                any()))
                .thenReturn(books);
        List<Book> booksRes = bookService.getRecommendation(Collections.singletonList(book));
        assertEquals(20, booksRes.size());
    }

    @Test
    public void getRecommendation_ManyBookInListWithRepeatGenresAndAuthors_returnResults() {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            VolumeInfo info = new VolumeInfo();
            info.setAuthors(Collections.singletonList("author"));
            info.setTitle("bookName");
            info.setCategories(Collections.singletonList("not_in_base"));
            info.setPublishedDate("1998");
            HashMap<String, String> identifiers = new HashMap<>();
            identifiers.put("type", "ISBN");
            identifiers.put("identifier", String.valueOf(i));
            info.setIndustryIdentifiers(Collections.singletonList(identifiers));
            books.add(new Book(info));
        }
        List<com.librarium.entity.Book> searchBooks = new ArrayList<>();
        List<String> genres = Arrays.asList("другое", "готовка", "образование", "библия", "искусство", "драма", "другое");
        genres.forEach(genre -> {
                    com.librarium.entity.Book book = new com.librarium.entity.Book("asf");
                    book.setGenre(genre);
                    book.setAuthor("author" + genre);
                    book.setISBN("0876");
                    searchBooks.add(book);
                }
        );
        Mockito.when(bookApiRepository.findBooksByGenresAndAuthors(any(),
                any()))
                .thenReturn(books);
        List<Book> booksRes = bookService.getRecommendation(searchBooks);
        assertEquals(20, booksRes.size());
    }

    @Test
    public void castBookFromDtoBook_nullDataIdGenres_convertBook() {
        String author = "author";
        String bookName = "name";
        VolumeInfo info = new VolumeInfo();
        info.setAuthors(Collections.singletonList(author));
        info.setTitle(bookName);
        Book dtoBook = new Book();
        dtoBook.setVolumeInfo(info);
        com.librarium.entity.Book book = bookService.castBookFromDtoBook(dtoBook);
        assertEquals(book.getName(), bookName);
        assertEquals(book.getAuthor(), author);
    }

    @Test
    public void castBookFromDtoBook_AllFields_convertBook() {
        String author = "author";
        String bookName = "name";
        String number = "2345678";
        String data = "1998";
        VolumeInfo info = new VolumeInfo();
        info.setAuthors(Collections.singletonList(author));
        info.setTitle(bookName);
        info.setCategories(Collections.singletonList("not_in_base"));
        info.setPublishedDate(data);
        HashMap<String, String> identifiers = new HashMap<>();
        identifiers.put("type", "ISBN");
        identifiers.put("identifier", number);
        info.setIndustryIdentifiers(Collections.singletonList(identifiers));
        Book dtoBook = new Book();
        dtoBook.setVolumeInfo(info);
        com.librarium.entity.Book book = bookService.castBookFromDtoBook(dtoBook);
        assertEquals(bookName, book.getName());
        assertEquals(author, book.getAuthor());
        assertEquals(number, book.getISBN());
        assertEquals("другое", book.getGenre());
        assertEquals(Integer.parseInt(data), book.getYear());
    }

    @Test
    public void getEngGenre_genreNotInBase_returnOther() {
        String actualGenre = bookService.getEngGenre("жанр не из базы");
        assertEquals("other", actualGenre);
    }

    @Test
    public void getEngGenre_genreInBase_returnValueFromBase() {
        String actualGenre = bookService.getEngGenre("компьютеры");
        assertEquals("computers", actualGenre);
    }
}