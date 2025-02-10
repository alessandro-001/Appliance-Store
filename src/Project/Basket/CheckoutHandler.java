package Project.Basket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * @author Alessandro Frondini - 14501682
 * The CheckoutHandler calculated the final price of the items stored in the basket, it also provides
 * the option to clear the basket and start over. The checkout functionality is a mock for demonstration 
 * purposes only, hence it does not implements the payment logic.
 */
public class CheckoutHandler implements HttpHandler {

    //DB connection
    private Connection connect() {
        Connection connection = null;
        try {
            String url = "jdbc:sqlite:store.sqlite";
            connection = DriverManager.getConnection(url);
            System.out.println("\nDatabase Connected Successfully! :)");
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return connection;
    }
    
    
    /**
     * Empty the basket method
     */
    private void clearBasket() {
        try (Connection connection = connect()) {
            if (connection != null) {
                String deleteQuery = "DELETE FROM basket"; //delete all records from basket table
                try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
                    pstmt.executeUpdate(); 
                    System.out.println("Basket emptied successfully!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the HTTP requests to display the checkout page or to clear the basket and 
     * redirect to the {@link ListAllProductsHandler} page.
     * @param he (HTTP object containing the  details of the HTTP request/response)
     * @throws IOException (if an IO error occurs during handling)
     */ 
    @Override
    public void handle(HttpExchange he) throws IOException {
    	
    	String method = he.getRequestMethod();

        //x Continue Shopping button, redirect to the /listAllProducts page and 
    	//clear the basket table
        if ("POST".equals(method)) {
        	clearBasket();
            he.getResponseHeaders().set("Location", "/listAllProducts");  
            he.sendResponseHeaders(302, -1);  
            return;
        }
    	
    	
        he.sendResponseHeaders(200, 0);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));

        // Total price calculation for checkout
        double totalPrice = 0.0;
        try (Connection connection = connect()) {
            if (connection != null) {
                String query = "SELECT price FROM basket";
                try (PreparedStatement pstmt = connection.prepareStatement(query);
                     ResultSet rs = pstmt.executeQuery()) {

                    while (rs.next()) {
                        double price = rs.getDouble("price");
                        totalPrice += price;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //HTML Checkout page
        out.write(
            "<html>" +
            	//Head
                "<head>" +
                    "<title>Checkout</title>" +
                    "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\" " +
                    "integrity=\"sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2\" " +
                    "crossorigin=\"anonymous\">" +
                "</head>" +
                //Body
                "<body style=\"background-color: white; text-align: center\">" +
                "<div class=\"container\">" +
                    "<h1 class=\"mt-5\"> Final Checkout </h1>" +
                    "<h3 class=\"mt-3\"> Total Price: GBP " + String.format("%.2f", totalPrice) + "</h3>" +
                    "<p class=\"mt-3\"> The logic to allow payment is not implemented yet.</p>" +
                    "<p class=\"mt-3\"> Since this is a mock system, all prices, customers, and appliances, are invented for demonstration purposes only! </p>" +
                    "<p class=\"mt-3\"> Thank you for checking my system :) </p>" +
                    //Buttons
                    "</br><a href=\"/basket\" class=\"btn btn-secondary\"> Back to Basket </a> </br></br>" +   
                    "<form method=\"POST\">" +
                    "<p> or </p>" +
                    "<button type=\"submit\" class=\"btn btn-primary mt-2\"> Continue Shopping </button>" +
                    	"<p class=\"mt-80\" style=\"font-size: 0.8rem;\"> (Clear basket and start over) </p>\n" +
                    "</form>" +
                    //credit note
                    "<p class=\"mt-80\" style=\"font-size: 0.8rem;\"> Created by: Alessandro Frondini </p>\n" +
                "</div>" +
            "</body>" +
            "</html>"
        );
        out.close();
    }
}
