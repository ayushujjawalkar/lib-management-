package com.ayush.libraryManagementSystem.service;

import com.ayush.libraryManagementSystem.model.User;
import com.ayush.libraryManagementSystem.repository.UserRepository;
import com.ayush.libraryManagementSystem.model.Book;
import com.ayush.libraryManagementSystem.repository.BookRepoository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    public List<Book> searchBooks(String keyword)
    {
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
            // ✅ Set borrow date and due date (e.g., 14 days later)
            book.setBorrowDate(LocalDate.now());
            book.setDueDate(LocalDate.now().plusDays(14));
            return save(book);
        }
        // Handle errors (e.g., book not found, book already borrowed, user not found)
        return null;
    }


//    public Book returnBook(Long bookId) {
//        Book book = findById(bookId);
//        if (book != null && book.isBorrowed()) {
//            // 👇 Save reference before setting to null
//            User borrower = book.getBorrowedBy();
//
//            // Set to null for update
//            book.setBorrowedBy(null);
//            book.setBorrowed(false);
//
//            Book updated = save(book);
//
//            // 👇 Restore borrower in returned object (just for controller use)
//            updated.setBorrowedBy(borrower);
//
//            return updated;
//        }
//        return null;
//    }
    //new for check

    public Book returnBook(Long bookId) {
        Book book = findById(bookId);

        if (book != null && book.isBorrowed()) {
            User borrower = book.getBorrowedBy();

            // Calculate fine if overdue
            LocalDate today = LocalDate.now();
            LocalDate dueDate = book.getDueDate();
            long overdueDays = 0;
            double finePerDay = 5.0; // ₹5 fine per day (example)

            if (dueDate != null && today.isAfter(dueDate)) {
                overdueDays = ChronoUnit.DAYS.between(dueDate, today);
                double fine = overdueDays * finePerDay;
                book.setFine(fine); // Optional: store in DB
            } else {
                book.setFine(0); // No fine
            }

            book.setBorrowedBy(null);
            book.setBorrowed(false);
            book.setBorrowDate(null);
            book.setDueDate(null); // Clear due date on return

            Book updated = save(book);

            // Restore borrower for response (optional)
            updated.setBorrowedBy(borrower);

            return updated;
        }

        return null;
    }


}