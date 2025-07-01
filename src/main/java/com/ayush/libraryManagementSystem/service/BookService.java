package com.ayush.libraryManagementSystem.service;

import com.ayush.libraryManagementSystem.model.User;
import com.ayush.libraryManagementSystem.repository.UserRepository;
import com.ayush.libraryManagementSystem.model.Book;
import com.ayush.libraryManagementSystem.repository.BookRepoository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepoository bookRepository;

    @Autowired
    private UserRepository userRepository;
    //injecting email service
    @Autowired
    private EmailService emailService;


    //  this is for search a book by title or author
    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword);
    }





    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public void deleteById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + id));

        if (book.isBorrowed()) {
            throw new IllegalArgumentException("Cannot delete a borrowed book");
        }

        bookRepository.delete(book);
    }
    public Book borrowBook(Long bookId, Long userId) {
        Book book = findById(bookId);
        User user = userRepository.findById(userId).orElse(null);

        if (book != null && !book.isBorrowed() && user != null) {
            book.setBorrowedBy(user);
            book.setBorrowed(true);
            return save(book);
        }
        // Handle errors (e.g., book not found, book already borrowed, user not found)
        return null;
    }

//    public Book returnBook(Long bookId) {
//        Book book = findById(bookId);
//        if (book != null && book.isBorrowed()) {
//            book.setBorrowedBy(null);
//            book.setBorrowed(false);
//            return save(book);
//        }
//        // Handle errors (e.g., book not found, book not borrowed)
//        return null;
//    }

    public Book returnBook(Long bookId) {
        Book book = findById(bookId);
        if (book != null && book.isBorrowed()) {
            // 👇 Save reference before setting to null
            User borrower = book.getBorrowedBy();

            // Set to null for update
            book.setBorrowedBy(null);
            book.setBorrowed(false);

            Book updated = save(book);

            // 👇 Restore borrower in returned object (just for controller use)
            updated.setBorrowedBy(borrower);

            return updated;
        }
        return null;
    }

}