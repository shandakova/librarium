package com.librarium.gui;

import com.librarium.entity.Book;
import com.librarium.repository.BookRepository;
import com.librarium.repository.CommentRepository;
import com.librarium.repository.ListsRepository;
import com.librarium.repository.QuoteRepository;
import javafx.fxml.Initializable;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class BookInformationController implements Initializable {
    private BookRepository bookRepository;
    private CommentRepository commentRepository;
    private QuoteRepository quoteRepository;
    private ListsRepository listsRepository;
    private Book book;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initData(BookRepository bookRepository, CommentRepository commentRepository, QuoteRepository quoteRepository, ListsRepository listsRepository, Book book) {
        this.book = book;
        this.bookRepository = bookRepository;
        this.commentRepository = commentRepository;
        this.listsRepository = listsRepository;
        this.quoteRepository = quoteRepository;
    }
}
