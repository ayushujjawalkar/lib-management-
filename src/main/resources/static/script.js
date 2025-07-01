const baseUrl = "http://localhost:8080";

// Load books and users on page load
document.addEventListener('DOMContentLoaded', () => {
    loadBooks();
    loadUsers();
});

// Event delegation for dynamic elements
document.addEventListener('click', function(e) {
    // Handle delete button clicks
    if (e.target.classList.contains('delete-btn')) {
        const bookId = e.target.getAttribute('data-id');

        // Debugging logs
        console.log('Delete button clicked');
        console.log('Button element:', e.target);
        console.log('All data attributes:', e.target.dataset);
        console.log('Extracted bookId:', bookId);

        if (!bookId) {
            console.error('No data-id attribute found on button!');
            alert('Error: Could not find book ID');
            return;
        }

        deleteBook(bookId);
    }
});
// Add user with validation and error handling
//async function addUser() {
//    try {
//        const name = document.getElementById('userName').value.trim();
//        const email = document.getElementById('userEmail').value.trim();
//
//        if (!name || !email) {
//            throw new Error('Name and email are required');
//        }
//
//        const response = await fetch(`${baseUrl}/api/users`, {
//            method: 'POST',
//            headers: { 'Content-Type': 'application/json' },
//            body: JSON.stringify({ name, email })
//        });
//
//        if (!response.ok) {
//            const error = await response.json();
//            throw new Error(error.message || 'Failed to add user');
//        }
//
//        await loadUsers();
//        // Clear form fields after successful addition
//        document.getElementById('userName').value = '';
//        document.getElementById('userEmail').value = '';
//    } catch (error) {
//        console.error('Error adding user:', error);
//        alert(error.message);
//    }
//}


async function addUser() {
    try {
        const name = document.getElementById('userName').value.trim();
        const email = document.getElementById('userEmail').value.trim();

        // 🛑 Check if fields are empty
        if (!name || !email) {
            throw new Error('Name and email are required');
        }

        // ✅ Email validation using regex
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            throw new Error('Please enter a valid email address (e.g., user@example.com)');
        }

        const response = await fetch(`${baseUrl}/api/users`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to add user');
        }

        await loadUsers();

        // Clear form fields after successful addition
        document.getElementById('userName').value = '';
        document.getElementById('userEmail').value = '';
        alert('User added successfully!');
    } catch (error) {
        console.error('Error adding user:', error);
        alert(error.message);
    }
}

// Add book with validation and error handling
async function addBook() {
    try {
        const title = document.getElementById('bookTitle').value.trim();
        const author = document.getElementById('bookAuthor').value.trim();

        if (!title || !author) {
            throw new Error('Title and author are required');
        }

        const response = await fetch(`${baseUrl}/api/books/addBook`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ title, author })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to add book');
        }

        await loadBooks();
        // Clear form fields after successful addition
        document.getElementById('bookTitle').value = '';
        document.getElementById('bookAuthor').value = '';
    } catch (error) {
        console.error('Error adding book:', error);
        alert(error.message);
    }
}

// Load users with error handling
async function loadUsers() {
    try {
        const response = await fetch(`${baseUrl}/api/users`);

        if (!response.ok) {
            throw new Error(`Failed to load users. Status: ${response.status}`);
        }

        const users = await response.json();
        document.getElementById('userList').innerHTML = users.map(user => `
            <div class="user">
                <strong>${user.name}</strong> (${user.email}) - ID: ${user.id}
            </div>
        `).join('');
    } catch (error) {
        console.error('Error loading users:', error);
        document.getElementById('userList').innerHTML = '<div class="error">Failed to load users</div>';
    }
}

// Load books with error handling
async function loadBooks() {
    try {
        const response = await fetch(`${baseUrl}/api/books/all`);
        const books = await response.json();
        console.log("Books data received:", books); // Debug log

        document.getElementById("bookList").innerHTML = books.map(book => `
            <div class="book">
                <strong>${book.title}</strong> by ${book.author}
                <br>Book ID: ${book.id}
                <br>Status: ${book.borrowed ? 'Borrowed by User ' + (book.borrowedBy?.id ?? '?') : 'Available'}
                <br>
                <button class="delete-btn" data-id="${book.id}">Delete</button>
            </div>
        `).join('');
    } catch (error) {
        console.error('Error loading books:', error);
    }
}
// Delete book with improved error handling
async function deleteBook(bookId) {
    try {
        // Validate bookId
        if (!bookId || bookId === "undefined") {
            throw new Error("Invalid book ID: " + bookId);
        }

        // Convert to number
        const numericId = Number(bookId);
        if (isNaN(numericId)) {
            throw new Error(`Book ID must be a number (received: ${bookId})`);
        }

        console.log("Attempting to delete book with ID:", numericId);

        const response = await fetch(`${baseUrl}/api/books/${numericId}`, {
            method: 'DELETE',
            headers: { 'Accept': 'application/json' }
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || "Failed to delete book");
        }

        await loadBooks();
        alert('Book deleted successfully!');
    } catch (error) {
        console.error("Delete error:", error);
        alert(error.message);
    }
}
// Borrow book with error handling
async function borrowBook() {
    try {
        const bookId = document.getElementById('borrowBookId').value.trim();
        const userId = document.getElementById('borrowUserId').value.trim();

        if (!bookId || !userId) {
            throw new Error('Both book ID and user ID are required');
        }

        const response = await fetch(`${baseUrl}/api/books/${bookId}/borrow/${userId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to borrow book');
        }

        await loadBooks();
    } catch (error) {
        console.error('Error borrowing book:', error);
        alert(error.message);
    }
}

// Return book with error handling
async function returnBook()
 {
    try {
        const bookId = document.getElementById('borrowBookId').value.trim();

        if (!bookId) {
            throw new Error('Book ID is required');
        }

        const response = await fetch(`${baseUrl}/api/books/${bookId}/return`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to return book');
        }

        await loadBooks();
    } catch (error) {
        console.error('Error returning book:', error);
        alert(error.message);
    }
}

//new for search book with author or title
// 🔍 Search books by title/author
async function searchBooks() {
    const keyword = document.getElementById("searchInput").value.trim();
    if (!keyword) {
        loadBooks(); // reload all if search is empty
        return;
    }

    try {
        const res = await fetch(`${baseUrl}/api/books/search?keyword=${encodeURIComponent(keyword)}`);
        const books = await res.json();
        renderBooks(books);
    } catch (error) {
        console.error("Search failed:", error);
    }
}


// 🧩 Reusable render function
function renderBooks(books)
{
    document.getElementById("bookList").innerHTML = books.map(book => `
        <div class="book">
            <strong>${book.title}</strong> by ${book.author}
            <br>Book ID: ${book.id}
            <br>Status: ${book.borrowed ? 'Borrowed by User ' + (book.borrowedBy?.id ?? '?') : 'Available'}
            <br>
            <button class="delete-btn" data-id="${book.id}">Delete</button>
        </div>
    `).join('');
}

//filtering book function
async function filterBooks() {
    const filter = document.getElementById("statusFilter").value;

    try {
        let res;
        if (filter === "all") {
            res = await fetch(`${baseUrl}/api/books/all`);
        } else {
            res = await fetch(`${baseUrl}/api/books/filter?status=${filter}`);
        }

        const books = await res.json();
        renderBooks(books);
    } catch (error) {
        console.error("Filter request failed:", error);
    }
}
