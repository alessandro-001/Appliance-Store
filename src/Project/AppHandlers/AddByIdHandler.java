package Project.AppHandlers;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Random;
import java.util.function.IntSupplier;

/**
 * ----- ADD BY ID HANDLER -----
 * @author Alessandro Frondini - 14501682
 * AddByIdHandler handles the HTTP requests to add appliances in the DB table appliance by the ID
 * - Generate an HTML form to collect data (appliance details) and generate a random SKU at every call.
 * - Implements the HttpHandler interface.
 * - Bootstrap CSS for styling the HTML response.
 */
public class AddByIdHandler implements HttpHandler {
    
	/**
	 * The handle handles requests and generate a HTML form to add a new appliance.
	 * @param he HttpExchange contains the HTTP request and response.
	 * @throws IOException (if an I/O error occurs).
	 */
    public void handle(HttpExchange he) throws IOException {
        
        System.out.println("You called ---> AddByIdHandler");
        
        he.sendResponseHeaders(200, 0);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));
        
        /**
         * SKU prefix is the generated SKU prefixed in the input block.
         */
        String generatedSKU = generateRandomSKU("SKU");
        
        /*---- ADD APPLIANCE by ID HTML----*/
        out.write(
            "<html>" +
                //Head
                "<head>" +
                    "<title> Add a Product </title>" +
                    //Bootstrap
                    "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\">" +
                "</head>" +
                //Body
                "<h1 style=\"color: black; font-size: 36px; margin-bottom: 20px; text-align: center\"> Add a Product </h1>" +
                "<body style=\"background-color: white; text-align: left; padding: 20px;\">" +
                    //Form fields
                    "<div class=\"container\" style=\"max-width: 600px;\">" +
                    "<form action=\"/processAddById\" method=\"post\">" +
                        //Add SKU
                        "<div class=\"form-group\">" +
                            "<label for=\"sku\" class=\"mr-2\"> SKU: </label>" +
                            "<input type=\"text\" name=\"sku\" id=\"sku\" class=\"form-control mr-2\" placeholder=\"Enter Product SKU\" value=\"" + generatedSKU + "\" required>" +
                        "</div>" +
                        //Add description
                        "<div class=\"form-group\">" +
                            "<label for=\"description\" class=\"mr-2\"> Description: </label>" +
                            "<input type=\"text\" name=\"description\" id=\"description\" class=\"form-control mr-2\" placeholder=\"Enter Product Description\" required>" +
                        "</div>" +
                        //Add Category
                        "<div class=\"form-group\">" +
	                        "<label for=\"category\" class=\"mr-2\"> Category: </label>" +
	                        "<select name=\"category\" id=\"category\" class=\"form-control mr-2\" required>" +
	                            "<option value=\"\" disabled selected>Select Category</option>" +
	                            "<option value=\"Kitchen\">Kitchen</option>" +
	                            "<option value=\"Cooling\">Cooling</option>" +
	                            "<option value=\"Personal_Care\">Personal Care</option>" +
	                        "</select>" +
                        "</div>" +
                        //Add price
                        "<div class=\"form-group\">" +
                            "<label for=\"price\" class=\"mr-2\"> Price: </label>" +
                            "<input type=\"number\" name=\"price\" id=\"price\" class=\"form-control mr-2\" placeholder=\"Enter Product Price\" required>" +
                        "</div>" +
                            
                        //ApplianceItem parameters
	                    //Warranty Years
	                    "<div class=\"form-group\">" +
	                        "<label for=\"warrantyYears\" class=\"mr-2\"> Warranty Years: </label>" +
	                        "<input type=\"number\" name=\"warrantyYears\" id=\"warrantyYears\" class=\"form-control\" placeholder=\"Enter Warranty Years\" required>" +
	                    "</div>" +
	                    //Add Brand    
	                    "<div class=\"form-group\">" +
		                    "<label for=\"brand\" class=\"mr-2\"> Brand: </label>" +
		                    "<input type=\"text\" name=\"brand\" id=\"brand\" class=\"form-control\" placeholder=\"Enter Product Brand\" required>" +
		                "</div>" +
		                //Add Model    
		                "<div class=\"form-group\">" +
		                    "<label for=\"model\" class=\"mr-2\"> Model: </label>" +
		                    "<input type=\"text\" name=\"model\" id=\"model\" class=\"form-control\" placeholder=\"Enter Product Model\" required>" +
		                "</div>" +
                        // Buttons
                        "<div class=\"d-flex justify-content-between mt-4\">" +
                            "<button type=\"submit\" class=\"btn btn-success\"> Add Appliance </button>" +
                            "<a href=\"/listAllProducts\" class=\"btn btn-primary\"> Go Back </a>" +
                        "</div>" +
                    "</form>" +
                "</div>" +
                "</body>" +
            "</html>");
        
        out.close();
    }

    /**
     * --- SKU Randomiser ---
     * Generates a randomised SKU + number to be prefixed in the input block.
     * @param prefix (prefix the SKU).
     * @return a RNG SKU.
     */
    private String generateRandomSKU(String prefix) {
        Random random = new Random();
        IntSupplier randomSKU = () -> 19 + random.nextInt(981); // Max SKU 999 
        return prefix + String.format("%03d", randomSKU.getAsInt());
    }
}
