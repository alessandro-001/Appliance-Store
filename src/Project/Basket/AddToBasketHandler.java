package Project.Basket;

import com.sun.net.httpserver.HttpHandler;

import Project.AdminArea.SessionHandler;
import Project.Appliances.HomeAppliance;

import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import Project.Basket.*;
import java.util.Map;

/**
 * @author Alessandro Frondini
 * The AddToBasketHandler handles the HTTP POST requests to add an item in the shopping basket.
 * Is connected to an SQLite DB to get the appliance by it SKU and inserts it into the basket table
 * before being processed or deleted by the user later in the application.
 */
public class AddToBasketHandler implements HttpHandler {
	
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
	 * Handle POST requests by extracting the SKU from the request body, validate it and
	 * adds it to the basket table in the DB.
	 * @param he (HttpExchange represents the request and response).
	 * @throws IOException (if an I/O error occurs).
	 */
	@Override
    public void handle(HttpExchange exchange) throws IOException {
		if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
			//Request body reader
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String requestBody = br.readLine();
            String sku = getQueryParameter(requestBody, "sku");
            
            if (sku == null || sku.isEmpty()) {
                sendResponse(exchange, 400, "SKU not provided!");
                return;
            }
            
            
            //Handle the query locally
            try (Connection conn = connect()) {
            	String selectSql = "SELECT id, sku, description, category, price FROM appliance WHERE sku = ?";
                PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                selectStmt.setString(1, sku);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    // Insert the appliance details into the basket table
                    String insertSql = "INSERT INTO basket (sku, description, category, price) VALUES (?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(insertSql);
                    pstmt.setString(1, rs.getString("sku"));
                    pstmt.setString(2, rs.getString("description"));
                    pstmt.setString(3, rs.getString("category"));
                    pstmt.setDouble(4, rs.getDouble("price"));
                    pstmt.executeUpdate();
                    
                    System.out.println("Item added to basket successfully! :)");
                    redirectToPage(exchange, exchange.getRequestURI().toString());
                    //sendResponse(exchange, 200, "Item added to basket successfully! :)");
                    
                } else {
                	System.out.println("Appliance not found! :O");
                    sendResponse(exchange, 404, "Appliance not found! :O");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Internal server error!");
            }
        }
		

	}
	
	
	 /**
	  * Page redirector to a specific URL
	  * @param exchange (HTTP request and response)
	  * @param redirectUrl (URL to redirect to)
	  * @throws IOException (if an I/O error occurs).
	  */
	 private void redirectToPage(HttpExchange exchange, String redirectUrl) throws IOException {
		    exchange.getResponseHeaders().set("Location", redirectUrl);
		    exchange.sendResponseHeaders(302, -1); // 302 Found for redirection
		    exchange.close();
		}
	 
	 
	 /**
	  * Send status code response with message
	  * @param exchange (HTTP request and response)
	  * @param statusCode (HTTP status code)
	  * @param message (response message)
	  * @throws IOException (if an I/O error occurs)
	  */
	 private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
	        exchange.sendResponseHeaders(statusCode, message.getBytes().length);
	        exchange.getResponseBody().write(message.getBytes());
	        exchange.close();
	    }
	 
	 /**
	  * GET the query params
	  * @param query (query String)
	  * @param paramName (name of the parameter to extract)
	  * @return (parameter value or null if not found)
	  */
	 private String getQueryParameter(String query, String paramName) {
		 if (query != null) {
			 for (String pair : query.split("&")) {
				 String[] keyValue = pair.split("=");
				 if (keyValue.length == 2 && keyValue[0].equals(paramName)) {
					 return keyValue[1];
				 }
			 }
		 }
		 return null;
	 }
}
