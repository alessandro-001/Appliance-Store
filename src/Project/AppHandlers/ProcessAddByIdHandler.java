package Project.AppHandlers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.io.IOException;
import java.util.Map;
import com.sun.net.httpserver.HttpHandler;

import Project.Utils;
import Project.ApplianceItem.ApplianceItem;
import Project.Appliances.HomeAppliance;
import Project.DAOs.HomeApplianceDAO;
import com.sun.net.httpserver.HttpExchange;


/**
 * ----- Process & Display added Products Handler -----
 * @author Alessandro Frondini - 14501682
 * ProcessAddByIdHandler processes the data passed from the {@link Project.AppHandlers.AddByIdHandler} form
 * and adds it associated details in the appliance table in the DB, generating a HTML response to give the user a confirmation.
 * Features:
 * - Parse parameters from HTTP body;
 * - Validate and maps the input data;
 * - Interacts with {@link Project.DAOs.HomeApplianceDAO} to add in the DB.
 * - Display a confirmation message.
 * - Bootstrap CSS for styling the HTML response.
 * Dependencies:
 * - {@link Project.Utils}: utility methods to parse data;
 * - {@link Project.ApplianceItem.ApplianceItem}: represent applianceItem data;
 * - {@link Project.Appliances.HomeAppliance}: represent Home Appliance details;
 * - {@link Project.DAOs.HomeApplianceDAO}: DB operations.
 * Exceptions:
 * - SQLException: DB interaction failures;
 * - NumberFormatException: when the data input is in the wrong format;
 * - IOException: unexpected I/O errors.
 */
public class ProcessAddByIdHandler implements HttpHandler {
	
	/**
	 * Handle HTTP exchange with POST requests to add products;
	 * @param he (HttpExchange is needed to encapsulate the HTTP request/response);
	 * @throws IOException (if I/O errors occurs while handling HTTP exchanges).
	 */
    public void handle(HttpExchange he) throws IOException {
    	System.out.println("You called --> ProcessAddByIdHandler");
    
    /*---- Read request -----*/
    try {
        BufferedReader in = new BufferedReader(new InputStreamReader(he.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        
        while ((line = in.readLine()) != null) {
            requestBody.append(line);
        }

        
        Map<String, String> params = Utils.requestStringToMap(requestBody.toString());
        System.out.println("POST Params -> " + params);
        
        String sku = params.get("sku");
        String description = params.get("description");
        String category = params.get("category");
        int price = Integer.parseInt(params.get("price"));
        //EXTRA for applianceItem
        int warrantyYears = Integer.parseInt(params.get("warrantyYears"));
        String brand = params.get("brand");
        String model = params.get("model");

     
        /*---- Add appliances to DB ----*/         
        HomeAppliance appliance = new HomeAppliance(0, sku, description, category, price);
        ApplianceItem applianceItem = new ApplianceItem(0, appliance, warrantyYears, brand, model);

        HomeApplianceDAO appliancesDAO = new HomeApplianceDAO();
        appliancesDAO.addProduct(appliance);
        appliancesDAO.addApplianceItem(applianceItem);
        
        
        
        /*---- DISPLAY ADDED APPLIANCE HTML ----*/
        String htmlResponse = 
        		"<html>" +
        		//Head
	            "<head>" +
    	            "<title> Product Added </title>" +
    	            "<meta charset=\"UTF-8\">" +
    	          //Bootstrap CSS
    	            "<link rel=\"stylesheet\"   href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\"  "
    	            + "integrity=\"sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2\"  "
    	            + "crossorigin=\"anonymous\"  >" +
	            "</head>" +
    	        //Body    
	            "<body>" +
    	            "<div class=\"container\">" +
    	            	"<h1>Product Added Successfully</h1>" +
    	            	//Table
        	            "<table class=\"table table-bordered\">" +
            	            "<tr><th> SKU </th><td>" + sku + "</td></tr>" +
            	            "<tr><th> Description </th><td>" + description + "</td></tr>" +
            	            "<tr><th> Category </th><td>" + category + "</td></tr>" +
            	            "<tr><th> Price </th><td>£" + price + "</td></tr>" +
            	            //extra for appliance item
            	            "<tr><th> Warranty Years </th><td>" + warrantyYears + "</td></tr>" +
                            "<tr><th> Brand </th><td>" + brand + "</td></tr>" +
                            "<tr><th> Model </th><td>" + model + "</td></tr>" +
        	            "</table>" +
            	        //Buttons
            	        "<div class=\"row mt-4\">" +
	                        "<div class=\"col text-left\">" +
	                            "<a href=\"/home\" class=\"btn btn-primary\"> Back to Home </a>" +
	                        "</div>" +
	                        "<div class=\"col text-right\">" +
	                            "<a href=\"/listAllProducts\" class=\"btn btn-primary\"> All Products </a>" +
                        "</div>" +
                    "</div>" +
    	            "</div>" +
	            "</body>" +        
	            "</html>";
 
        //Successful response
        he.sendResponseHeaders(200, htmlResponse.getBytes().length);
        he.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");

        he.getResponseBody().write(htmlResponse.getBytes());
    
    /*---- Error catching blocks ----*/
    } catch (SQLException e) {
        System.err.println("SQL ERROR: " + e.getMessage());
        he.sendResponseHeaders(500, -1); //500 Internal Server Error

    } catch (NumberFormatException e) {
        System.err.println("Invalid Input: " + e.getMessage());
        he.sendResponseHeaders(400, -1); //400 Bad Request

    } catch (Exception e) {
        System.err.println("Unexpected ERROR: " + e.getMessage());
        he.sendResponseHeaders(500, -1); //500 Internal Server Error

    } finally {
        he.getResponseBody().close();
    }
    
   }
}





