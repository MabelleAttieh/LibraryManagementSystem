package application;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.*;

public class LibraryApp extends Application {

    private Connection connection;

    @Override
    public void start(Stage primaryStage) {
        connection = DatabaseConnection.connect();

        // UI Components
        Label titleLabel = new Label("Book Title:");
        TextField titleField = new TextField();
        
        Label authorLabel = new Label("Author:");
        TextField authorField = new TextField();
        
        Label yearLabel = new Label("Published Year:");
        TextField yearField = new TextField();
        
        Button addButton = new Button("Add Book");
        Button updateButton = new Button("Update Book");
        Button deleteButton = new Button("Delete Book");
        Button showBooksButton = new Button("Show All Books");
        Button dropTableButton = new Button("Drop Table");
        Button limitButton = new Button("Limit Results");
        Button joinButton = new Button("Join Members and Books");

        // Action Listeners
        addButton.setOnAction(e -> {
            String title = titleField.getText();
            String author = authorField.getText();
            int year = Integer.parseInt(yearField.getText());
            insertBook(title, author, year);
        });

        updateButton.setOnAction(e -> {
            String title = titleField.getText();
            String author = authorField.getText();
            int year = Integer.parseInt(yearField.getText());
            updateBook(title, author, year);
        });

        deleteButton.setOnAction(e -> {
            String title = titleField.getText();
            deleteBook(title);
        });

        showBooksButton.setOnAction(e -> showAllBooks());
        dropTableButton.setOnAction(e -> dropBooksTable());
        limitButton.setOnAction(e -> showLimitedBooks(2));
        joinButton.setOnAction(e -> joinBooksAndMembers());

        // GridPane Layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        // Add Components to GridPane
        grid.add(titleLabel, 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(authorLabel, 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(yearLabel, 0, 2);
        grid.add(yearField, 1, 2);
        grid.add(addButton, 1, 3);
        grid.add(updateButton, 1, 4);
        grid.add(deleteButton, 1, 5);
        grid.add(showBooksButton, 1, 6);
        grid.add(dropTableButton, 1, 7);
        grid.add(limitButton, 1, 8);
        grid.add(joinButton, 1, 9);

        // Set up the scene and stage
        Scene scene = new Scene(grid, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Library Management System");
        primaryStage.show();
    }

    // Insert a book into the database
    private void insertBook(String title, String author, int year) {
        String sql = "INSERT INTO books (title, author, published_year) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setInt(3, year);
            pstmt.executeUpdate();
            System.out.println("Book added: " + title);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Update a book in the database
    private void updateBook(String title, String author, int year) {
        String sql = "UPDATE books SET author = ?, published_year = ? WHERE title = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, author);
            pstmt.setInt(2, year);
            pstmt.setString(3, title);
            pstmt.executeUpdate();
            System.out.println("Book updated: " + title);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Delete a book from the database
    private void deleteBook(String title) {
        String sql = "DELETE FROM books WHERE title = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.executeUpdate();
            System.out.println("Book deleted: " + title);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Show all books
    private void showAllBooks() {
        String sql = "SELECT * FROM books";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
                        rs.getString("title") + "\t" +
                        rs.getString("author") + "\t" +
                        rs.getInt("published_year"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void dropBooksTable() {
        String disableFKChecks = "SET FOREIGN_KEY_CHECKS = 0";
        String dropBooksSql = "DROP TABLE IF EXISTS books";
        String enableFKChecks = "SET FOREIGN_KEY_CHECKS = 1";

        try (Statement stmt = connection.createStatement()) {
            // Disable foreign key checks
            stmt.executeUpdate(disableFKChecks);

            // Now, drop the books table
            stmt.executeUpdate(dropBooksSql);

            // Enable foreign key checks again
            stmt.executeUpdate(enableFKChecks);

            System.out.println("Table 'books' dropped.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

 // Show limited number of books
    private void showLimitedBooks(int limit) {
        String sql = "SELECT * FROM books LIMIT ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
                        rs.getString("title") + "\t" +
                        rs.getString("author") + "\t" +
                        rs.getInt("published_year"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

 // Join books and members tables and show data
    private void joinBooksAndMembers() {
        String sql = "SELECT members.name, books.title FROM members " +
                "JOIN borrowed_books ON members.id = borrowed_books.member_id " +
                "JOIN books ON books.id = borrowed_books.book_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getString("name") + "\t" + rs.getString("title"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}