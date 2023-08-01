import java.sql.*;
import java.util.Scanner;

public class Manager {

    public void managerDetails() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/event";
            String user = "root";
            String password = "AJay@2706";

            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                System.out.println("Welcome to Event Management System (Manager)");
                System.out.println("==============================================");

                while (true) {
                    System.out.println("\nOptions:");
                    System.out.println("1. View Booking Requests");
                    System.out.println("2. Accept Booking Request");
                    System.out.println("3. Reject Booking Request");
                    System.out.println("4. Exit");

                    Scanner scanner = new Scanner(System.in);
                    String choice = scanner.nextLine();

                    switch (choice) {
                        case "1":
                            viewBookingRequests(connection);
                            break;
                        case "2":
                            acceptBookingRequest(connection, scanner);
                            break;
                        case "3":
                            rejectBookingRequest(connection, scanner);
                            break;
                        case "4":
                            System.out.println("Exiting the manager application.");
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

    private static void viewBookingRequests(Connection connection) throws SQLException {
        String query = "SELECT * FROM orders";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (!resultSet.isBeforeFirst()) {
                System.out.println("No booking requests found.");
                return;
            }

            System.out.println("Booking Requests:");
            while (resultSet.next()) {
                int orderId = resultSet.getInt("event_id"); // Replace 'orderId' with the actual column name
                String eventName = resultSet.getString("event_name"); // Replace 'eventName' with the actual column name
                String eventDate = resultSet.getString("event_date"); // Replace 'eventDate' with the actual column name
                String eventLocation = resultSet.getString("event_location"); // Replace 'eventLocation' with the actual column name

                System.out.println(orderId + " - " + eventName + " - " + eventDate + " - " + eventLocation);
            }
        }
    }




    private static void acceptBookingRequest(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter the ID of the booking request you want to accept: ");
        int requestId;
        try {
            requestId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid integer ID.");
            return; // Exit the method to avoid further processing with invalid input
        }

        String updateQuery = "UPDATE orders SET status = 'Accepted' WHERE event_id = ?";
        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
            updateStmt.setInt(1, requestId);

            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Booking request ID " + requestId + " has been accepted.");
            } else {
                System.out.println("Booking request ID " + requestId + " not found, or it is already accepted.");
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while updating the request. Please try again.");
            e.printStackTrace();
        }
    }


    private static void rejectBookingRequest(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter the ID of the booking request you want to reject: ");
        int requestId = Integer.parseInt(scanner.nextLine());

        String updateQuery = "UPDATE orders SET status = 'Rejected' WHERE event_id = ?";
        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
            updateStmt.setInt(1, requestId);

            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Booking request ID " + requestId + " has been rejected.");
            } else {
                System.out.println("Failed to update the request. Please try again.");
            }
        }
    }
}