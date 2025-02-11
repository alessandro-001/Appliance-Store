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
 * @author Alessandro Frondini
 * the BasketHandler class is responsible to handle the basket functionality. 
 * Features:
 * - Connect to SQLite DB;
 * - Display HTML Basket items table, with the option to remove them from the basket with POST requests.
 * - Display the total price of the items stored in the basket;
 */
public class BasketHandler implements HttpHandler {
    
    /**
     * DB connection
     * @return connection object
     */
    private Connection connect() {
        Connection connection = null;
        try {
            String url = "jdbc:sqlite:store.sqlite";
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return connection;
    }
    
    
    /**
     * Local method to remove items from the basket
     * @param itemId (the Id of the item to be removed)
     */
    private void removeItem(int itemId) {
        try (Connection connection = connect()) {
            if (connection != null) {
                String deleteQuery = "DELETE FROM basket WHERE id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
                	
                    pstmt.setInt(1, itemId);
                    pstmt.executeUpdate(); 
                    System.out.println("Item with ID " + itemId + " was removed from basket!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The handle manages viewing the items, removing and calculating the total price of the items stored in the basket.
     * @param he (HTTP object containing the  details of the HTTP request/response)
     * @throws IOException (if an IO error occurs during handling)
     */
    @Override
    public void handle(HttpExchange he) throws IOException {
    	
    	String method = he.getRequestMethod();
        if ("POST".equals(method)) {
            String queryString = he.getRequestURI().getQuery();
            if (queryString != null && queryString.startsWith("removeItemId=")) {
                String[] params = queryString.split("=");
                if (params.length == 2) {
                    try {
                        int itemId = Integer.parseInt(params[1]); //Get the ID from the query
                        removeItem(itemId); //Remove the item from the basket
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        he.sendResponseHeaders(200, 0);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));

        //Local query handling
        StringBuilder tableRows = new StringBuilder();
        double totalPrice = 0.0; // Variable to store the total price

        try (Connection connection = connect()) {
            if (connection != null) {
                String query = "SELECT * FROM basket";
                try (PreparedStatement pstmt = connection.prepareStatement(query);
                     ResultSet rs = pstmt.executeQuery()) {

                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String sku = rs.getString("sku");
                        String description = rs.getString("description");
                        String category = rs.getString("category");
                        double price = rs.getDouble("price");

                        //Total price addition
                        totalPrice += price;
                        
                        tableRows.append(
                                "<tr>" +
                                    "<td class=\"align-middle text-center\">" + id + "</td>" +
                                    "<td class=\"align-middle text-center\">" + sku + "</td>" +
                                    "<td class=\"align-middle text-center\">" + description + "</td>" +
                                    "<td class=\"align-middle text-center\">" + category + "</td>" +
                                    "<td class=\"align-middle text-center\">" + price + "</td>" +
                                    "<td class=\"align-middle text-center\">" +
                                        "<form method=\"POST\" action=\"/basket?removeItemId=" + id + "\">" +
                                            "<button type=\"submit\" class=\"btn btn-outline-danger\">Remove</button>" +
                                        "</form>" +
                                    "</td>" +
                                "</tr>"
                            );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Shopping basket HTML
        out.write(
        	    "<html>" +
        	        "<head>" +
        	            "<title>Shopping Basket</title>" +
        	            "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\" " +
        	            "integrity=\"sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2\" " +
        	            "crossorigin=\"anonymous\">" +
        	        "</head>" +
        	        "<body style=\"background-color: white; text-align: center\">" +
        	            "<div class=\"container\">" +
        	                "<h1 class=\"mt-5\">Your Shopping Basket</h1>" +
        	                "<table class=\"table table-bordered table-striped table-dark mt-4\">" +
        	                    "<thead class=\"thead-dark\">" +
        	                        "<tr>" +
        	                            "<th> ID </th>" +
        	                            "<th> SKU </th>" +
        	                            "<th> Description </th>" +
        	                            "<th> Category </th>" +
        	                            "<th> Price </th>" +
        	                        "</tr>" +
        	                    "</thead>" +
        	                    "<tbody>" +
        	                        tableRows.toString() +
        	                    "</tbody>" +
        	                "</table>" +
        	                // Display total price
        	                "<h3 class=\"mt-3\">Total Price: (GBP) " + String.format("%.2f", totalPrice) + "</h3>" +

        	                // Create a row for the buttons
        	                "<div class=\"row mt-3\">" +
        	                    // Left side button - Go Back
        	                    "<div class=\"col-6 text-left\">" +
        	                        "<a href=\"/listAllProducts\" class=\"btn btn-primary\"> Go Back </a>" +
        	                    "</div>" +
        	                    // Right side button - Checkout
        	                    "<div class=\"col-6 text-right\">" +
        	                        "<a href=\"/checkout\" class=\"btn btn-success\"> Proceed to Checkout </a>" +
        	                    "</div>" +
        	                "</div>" +
        	            "</div>" +
        	        "</body>" +
        	    "</html>"
        	);

        out.close();
    }
}
