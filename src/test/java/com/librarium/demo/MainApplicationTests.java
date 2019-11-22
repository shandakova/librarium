package com.librarium.demo;

import com.librarium.entity.Book;
import com.librarium.entity.Lists;
import com.librarium.entity.Quote;
import com.librarium.repository.BookRepository;
import com.librarium.repository.ListsRepository;
import com.librarium.repository.QuoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@EnableJpaRepositories(basePackages = "com.librarium.repository")
class MainApplicationTests {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private QuoteRepository quoteRepository;
    @Autowired
    private ListsRepository listsRepository;

    @Test
    void saveBook() {
        long count = bookRepository.count();
        Book book = new Book("Name");
        book.setAuthor("Author");
        bookRepository.save(book);
        assertEquals(count + 1, bookRepository.count());
    }

    @Test
    void saveManyBooks() {
        long count = bookRepository.count();
        Book book = new Book("Name");
        Book book2 = new Book("Name2");
        bookRepository.saveAll(Arrays.asList(book, book2));
        assertEquals(count + 2, bookRepository.count());
    }

    @Test
    void findById() {
        Book book = new Book("Name");
        bookRepository.save(book);
        Optional<Book> optBook = Optional.of(bookRepository.findById(book.getId()).orElse(new Book("Not finded")));
        assertEquals("Name", optBook.get().getName());
    }

    @Test
    void findByNameLike() {
        List<Book> books = bookRepository.findByNameContainsIgnoreCase("cool book");
        int count = books.size();
        Book book = new Book("Cool Book 1");
        Book book2 = new Book("Cool Book 2");
        bookRepository.saveAll(Arrays.asList(book, book2));
        books = bookRepository.findByNameContainsIgnoreCase("cool book");
        assertEquals(count + 2, books.size());
    }

    @Test
    @Transactional
    void orphanRemovingQuote() {
        long count = quoteRepository.count();
        Book book = new Book("Name");
        bookRepository.save(book);
        Quote quote = new Quote();
        quote.setQuotation("Some Text");
        quote.setBook(book);
        book.addQuote(quote);
        bookRepository.save(book);
        assertEquals(1, book.getNumberQuotes());
        assertEquals(count + 1, quoteRepository.count());
        bookRepository.deleteAll();
        assertEquals(0, quoteRepository.count());
    }

    @Test
    @Transactional
    void removeQuote() {
        Long count = quoteRepository.count();
        Book book = new Book("Name");
        bookRepository.save(book);
        Quote quote = new Quote();
        quote.setQuotation("Some Text");
        quote.setBook(book);
        book.addQuote(quote);
        bookRepository.save(book);
        assertEquals(count + 1, quoteRepository.count());
        quote = book.getQuotes().iterator().next();
        book.getQuotes().remove(quote);
        bookRepository.save(book);
        quoteRepository.delete(quote);
        assertEquals(count, quoteRepository.count());
    }

    @Transactional
    @Test
    void addBookToList() {
        long countList = listsRepository.count();
        long countBook = bookRepository.count();
        Book book = new Book("Book1");
        bookRepository.save(book);
        Lists lists = new Lists();
        lists.getBooks().add(book);
        book.getLists().add(lists);
        listsRepository.save(lists);
        bookRepository.save(book);
        System.out.println(listsRepository.findAll().iterator().next().getBooks().size());
        assertEquals(countList + 1, bookRepository.count());
        assertEquals(countBook + 1, listsRepository.count());
    }

}
