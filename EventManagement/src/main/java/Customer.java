import java.sql.*;
import java.util.Scanner;
interface EventDetails {
    void listEvents(Connection connection) throws SQLException;
    void bookEvent(Connection connection, Scanner scanner) throws SQLException;
}
abstract class AbstractEvent {
    public abstract void listEvents(Connection connection) throws SQLException;
    public abstract void bookEvent(Connection connection, Scanner scanner) throws SQLException;
}
public class Customer extends AbstractEvent {

    @Override
    public void listEvents(Connection connection) throws SQLException {
        String query = "SELECT * FROM events";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("Event List:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");

                System.out.println(id + " - " + name);
            }
        }
    }

    @Override
    public void bookEvent(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter the ID of the event you want to book: ");
        int eventId = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter the Event Location: ");
        String eventLocation = scanner.nextLine();
        System.out.println("Enter the Event Date: ");
        String eventDate = scanner.nextLine();
        String checkEventQuery = "SELECT * FROM events WHERE id = ?";
        try (PreparedStatement checkEventStmt = connection.prepareStatement(checkEventQuery)) {
            checkEventStmt.setInt(1, eventId);
            ResultSet eventResult = checkEventStmt.executeQuery();

            if (eventResult.next()) {
                String eventName = eventResult.getString("name");

                System.out.println("Event Details:");
                System.out.println("Event ID: " + eventId);
                System.out.println("Event Name: " + eventName);
                System.out.println("Event Location: " + eventLocation);
                System.out.println("Event Date: " + eventDate);

                System.out.println("Confirm booking? (Y/N): ");
                String confirmation = scanner.nextLine().trim().toLowerCase();

                if (confirmation.equals("y")) {
                    String insertOrderQuery = "INSERT INTO orders (event_id, event_name, event_date, event_location) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertOrderStmt = connection.prepareStatement(insertOrderQuery)) {
                        insertOrderStmt.setInt(1, eventId);
                        insertOrderStmt.setString(2, eventName);
                        insertOrderStmt.setString(3, eventDate);
                        insertOrderStmt.setString(4, eventLocation);

                        int rowsAffected = insertOrderStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Your booking request for event '" + eventName + "' has been sent to the manager.");
                        } else {
                            System.out.println("Failed to place the order. Please try again.");
                        }
                    }
                } else {
                    System.out.println("Booking canceled.");
                }
            } else {
                System.out.println("Event with ID " + eventId + " not found.");
            }
        }
    }

    public void eventdetails() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/event";
            String user = "root";
            String password = "AJay@2706";

            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                System.out.println("Welcome to Event Management System (Customer)");
                System.out.println("==============================================");

                while (true) {
                    System.out.println("\nOptions:");
                    System.out.println("1. View Events");
                    System.out.println("2. Book Event");
                    System.out.println("3. Exit");
                    System.out.println("Enter your choice");
                    Scanner scanner = new Scanner(System.in);
                    String choice = scanner.nextLine();

                    switch (choice) {
                        case "1":
                            listEvents(connection);
                            break;
                        case "2":
                            bookEvent(connection, scanner);
                            break;
                        case "3":
                            System.out.println("Exiting the customer application.");
                            return;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Additional methods specific to Customer class, if any, can be added here.

    
}