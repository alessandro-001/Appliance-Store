package Project.AppHandlers;

import Project.DAOs.HomeApplianceDAO;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

/**
 * @author Alessandro Frondini - 14501682
 * UpdateByIdHandler handles HTTP requests for updating home appliances in the database by their ID.
 * Features:
 * - Displays a form to update the product details, including warranty years, brand and model.
 * - Parse and validate the user input.
 * - Interacts with the {@link HomeApplianceDAO} to update the records in the DB.
 * - Returns a HTML page to confirm the successful item update.
 * - Bootstrap CSS for styling the HTML response.
 * Dependencies:
 * - {@link HomeApplianceDAO}: For database operations.
 * - JavaIO to handle HTTP response.
 * - Java Stream API to parse form data.
 */
public class UpdateByIdHandler implements HttpHandler {
	
	//DAO instance to interact with DB
    private final HomeApplianceDAO aDAO;

    /**
     * Constructor
     * @param applianceDAO (used for database operations).
     */
    public UpdateByIdHandler(HomeApplianceDAO applianceDAO) {
        this.aDAO = applianceDAO;
    }

    /**
     * Handle for HTTP requests, depending on the method:
     * - GET: display update form;
     * - POST: process form submission;
     */
    @Override
    public void handle(HttpExchange he) throws IOException {
        String method = he.getRequestMethod();

        if ("GET".equalsIgnoreCase(method)) {
            showForm(he);
        } else if ("POST".equalsIgnoreCase(method)) {
            //Form sub handle
            handleFormSubmission(he);
        }
    }

    /**
     * ----- Update Form HTML -----
     * @param he (contain HTTP response to write to).
     * @throws IOException (if an I/O error occurs). 
     */
    private void showForm(HttpExchange he) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));
        he.sendResponseHeaders(200, 0);

        out.write(
            "<html>" +
            	//Head
            	"<head>"
	          + "<title> Update Product </title>"+
	            //Bootstrap CSS
	            "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\">" +
	            "</head>" +
	            //Body
                "<body style=\"background-color: white; text-align: center\">" +
                	//Update form
                    "<h1 style=\"color: black; font-size: 36px;\"> Update a Product by ID </h1>" +
                    "<form method=\"POST\" action=\"/updateById\">" +
                        "<label for=\"id\">Product ID:</label><br>" +
                        "<input type=\"text\" id=\"id\" name=\"id\" required>"
                        + "<br><br>" +                       
                        "<label for=\"sku\">SKU:</label><br>" +
                        "<input type=\"text\" id=\"sku\" name=\"sku\" required>"
                        + "<br><br>" +
                        "<label for=\"description\"> Description: </label><br>" +
                        "<input type=\"text\" id=\"description\" name=\"description\" required>"
                        + "<br><br>" +
                        "<label for=\"category\"> Category: </label><br>" +
                        "<select id=\"category\" name=\"category\" required>" +
                            "<option value=\"Kitchen\">Kitchen</option>" +
                            "<option value=\"Cooling\">Cooling</option>" +
                            "<option value=\"Personal_Care\">Personal Care</option>" +
                        "</select>" +
                        "<br><br>" +
                        "<label for=\"price\"> New Price: </label><br>" +
                        "<input type=\"number\" id=\"price\" name=\"price\" required>" 
                        + "<br><br>" +
                        //Extra for applianceITem
                        "<label for=\"price\"> Warranty Years: </label><br>" +
                        "<input type=\"number\" id=\"warrantyYears\" name=\"warrantyYears\" required>" 
                        + "<br><br>" +
                        "<label for=\"price\"> Brand: </label><br>" +
                        "<input type=\"text\" id=\"brand\" name=\"brand\" required>" 
                        + "<br><br>" +
                        "<label for=\"price\"> Model: </label><br>" +
                        "<input type=\"text\" id=\"model\" name=\"model\" required><br><br>" +
                        
                        //Buttons
                        "<div class=\"form-group d-flex justify-content-center w-100\">" +
	                        "<div class=\"mx-2\">" +
	                            "<button type=\"submit\" class=\"btn btn-success\"> Update </button>" +
	                        "</div>" +
	                        "<div class=\"mx-2\">" +
	                            "<form action=\"/listAllProducts\" method=\"get\">" +
	                                "<button type=\"submit\" class=\"btn btn-secondary\"> Go Back </button>" +
	                            "</form>" +
	                        "</div>" +
                        "</div>"+
					
                    "</form>" +
                "</body>" +
	            "</html>"
	        );
        
        out.close();
    }

    /**
     * ----- Form submission handler -----
     * Handles the form submission and update the table in the DB.
     * @param he (contain HTTP response to write to).
     * @return a Map of key-value pairs representing form fields and values.
     * @throw IOException if an error occurs while parsing/processing the form data.
     */
    private void handleFormSubmission(HttpExchange he) throws IOException {
        // Parse form
        Map<String, String> formData = parseForm(he);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));

        try {
            // Debugging: Print the form data to verify input
            System.out.println("Form Data: " + formData);

            int id = Integer.parseInt(formData.get("id"));
            String sku = formData.get("sku");
            String description = formData.get("description");
            String category = formData.get("category");
            int price = Integer.parseInt(formData.get("price"));
            int warrantyYears = Integer.parseInt(formData.get("warrantyYears"));
            String brand = formData.get("brand");
            String model = formData.get("model");

            //DAO method
            boolean success = aDAO.updateProductByID(id, sku, description, category, price, warrantyYears, brand, model);

            //Display in console if update was successful
            System.out.println("Update Success: " + success);

            //HTML response
            String response = 
                "<html>" +
                    "<head>" +
                        "<title> Product Update </title>" +
                        "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\">" +
                    "</head>" +
                    "<body style=\"background-color: white; text-align: center; padding: 20px;\">" +
                        "<h1 style=\"color: black;\"> Product Updated </h1>" +
                        "<div class=\"container mt-4\">" +
                            (success
                                ? "<p style=\"color: green; font-size: 20px;\"> Product was updated successfully :) </p>"
                                : "<p style=\"color: red; font-size: 20px;\"> Product with ID " + id + " not found</p>") +
                            "<table class=\"table table-bordered mt-4\" style=\"width: 60%; margin: auto;\">" +
                                "<thead class=\"thead-dark\">" +
                                    "<tr>" +
                                        "<th>ID</th>" +
                                        "<th>SKU</th>" +
                                        "<th>Description</th>" +
                                        "<th>Category</th>" +
                                        "<th>Price</th>" +
                                        "<th>Warranty Years</th>" +
                                        "<th>Brand</th>" +
                                        "<th>Model</th>" +
                                    "</tr>" +
                                "</thead>" +
                                "<tbody>" +
                                    "<tr>" +
                                        "<td>" + id + "</td>" +
                                        "<td>" + sku + "</td>" +
                                        "<td>" + description + "</td>" +
                                        "<td>" + category + "</td>" +
                                        "<td>" + price + "</td>" +
                                        "<td>" + warrantyYears + "</td>" +
                                        "<td>" + brand + "</td>" +
                                        "<td>" + model + "</td>" +
                                    "</tr>" +
                                "</tbody>" +
                            "</table>" +
                        "</div>" +
                        "<div class=\"mt-4\">" +
                            "<form action=\"/listAllProducts\" method=\"get\">" +
                                "<button type=\"submit\" class=\"btn btn-secondary\"> Go Back </button>" +
                            "</form>" +
                        "</div>" +
                    "</body>" +
                "</html>";

            he.sendResponseHeaders(200, response.getBytes().length);
            out.write(response);

        } catch (Exception e) {
            String errorPage = 
                "<html>" +
                    "<head>" +
                        "<title> Error </title>" +
                    "</head>" +
                    "<body style=\"background-color: white; text-align: center;\">" +
                        "<h1 style=\"color: red;\"> Error Updating the Product </h1>" +
                        "<p>" + e.getMessage() + "</p>" +
                        "<div class=\"mt-4\">" +
                            "<form action=\"/updateById\" method=\"get\">" +
                                "<button type=\"submit\" class=\"btn btn-secondary\">Try Again</button>" +
                            "</form>" +
                        "</div>" +
                    "</body>" +
                "</html>";

            he.sendResponseHeaders(500, errorPage.getBytes().length);
            out.write(errorPage);
        } finally {
            out.close();
        }
    }


    /**
     * ----- Data parsing for the form -----
     * Parses the HTTP request body to extract the form data.
     * @param he (HttpExchange containing the HTTP request with the form data).
     * @return a Map of key-value pairs representing form fields and values.
     * @throws IOException if an error occurs during parsing the request body.
     */
    private Map<String, String> parseForm(HttpExchange he) throws IOException {
        //Read request body 
        byte[] requestBody = new byte[he.getRequestBody().available()];
        he.getRequestBody().read(requestBody);
        
        //Convert to a String
        String body = new String(requestBody, StandardCharsets.UTF_8);

        return Arrays.stream(body.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }

}

