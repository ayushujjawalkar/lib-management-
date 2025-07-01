package com.ayush.libraryManagementSystem.controller;

import com.ayush.libraryManagementSystem.service.BookService;
import com.ayush.libraryManagementSystem.model.Book;
import com.ayush.libraryManagementSystem.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController
{
    @Autowired
    private BookService bookService;

    @GetMapping("/all")
    public List<Book> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public Book getBook(@PathVariable Long id)
    {
        return bookService.findById(id);
    }

    @PostMapping("/addBook")
    public Book addBook(@RequestBody Book book) {
        return bookService.save(book);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book book)
    {
        // Additional logic to ensure you're updating the correct book
        return bookService.save(book);
    }

     //this is for search a book by title or author
    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam("keyword") String keyword) {
        return bookService.searchBooks(keyword);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteById(id);
            return ResponseEntity.ok("Book deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid book ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting book: " + e.getMessage());
        }
    }

@Autowired
private EmailService emailService;
                     //borrow book with email notifications
    @PostMapping("/{bookId}/borrow/{userId}")
    public ResponseEntity<Book> borrowBook(@PathVariable Long bookId, @PathVariable Long userId) {
        Book borrowedBook = bookService.borrowBook(bookId, userId);

        if (borrowedBook != null) {
            // Send email to the user
            if (borrowedBook.getBorrowedBy() != null && borrowedBook.getBorrowedBy().getEmail() != null) {
                String toEmail = borrowedBook.getBorrowedBy().getEmail();
                String subject = "Book Borrowed: " + borrowedBook.getTitle();
                String message = "Dear " + borrowedBook.getBorrowedBy().getName() + ",\n\n" +
                        "You have successfully borrowed the book: \"" + borrowedBook.getTitle() + "\" by " + borrowedBook.getAuthor() + ".\n\n" +
                        "Please remember to return it on time.\n\n" +
                        "Regards,\nLibrary Team";

                emailService.sendEmail(toEmail, subject, message);
            }

            return ResponseEntity.ok(borrowedBook);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

      // return book with email notification but email is not working
    @PostMapping("/{bookId}/return")
    public ResponseEntity<Book> returnBook(@PathVariable Long bookId) {
        Book returnedBook = bookService.returnBook(bookId);

        if (returnedBook != null) {
            if (returnedBook.getBorrowedBy() != null && returnedBook.getBorrowedBy().getEmail() != null) {
                String toEmail = returnedBook.getBorrowedBy().getEmail();
                String subject = "📕 Book Returned Confirmation";
                String message = "Dear " + returnedBook.getBorrowedBy().getName() + ",\n\n" +
                        "You have successfully returned the book \"" + returnedBook.getTitle() + "\" by " + returnedBook.getAuthor() + ".\n" +
                        "Thank you for using our Library Management System!\n\n" +
                        "Regards,\nLibrary Team";

                try {
                    emailService.sendEmail(toEmail, subject, message);
                    System.out.println("Return confirmation email sent to " + toEmail);
                } catch (Exception e) {
                    System.err.println("Failed to send return email: " + e.getMessage());
                }
            }
            return ResponseEntity.ok(returnedBook);
        } else {
            return ResponseEntity.badRequest().build(); // or a more descriptive error response
        }
    }

    //filter book  by status
    @GetMapping("/filter")
    public List<Book> filterBooks(@RequestParam String status) {
        List<Book> allBooks = bookService.findAll();

        if ("borrowed".equalsIgnoreCase(status)) {
            return allBooks.stream()
                    .filter(Book::isBorrowed)
                    .toList();
        } else if ("available".equalsIgnoreCase(status)) {
            return allBooks.stream()
                    .filter(book -> !book.isBorrowed())
                    .toList();
        }

        return allBooks;
    }
}
