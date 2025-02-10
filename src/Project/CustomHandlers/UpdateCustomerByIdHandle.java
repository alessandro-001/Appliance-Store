package Project.CustomHandlers;

import Project.DAOs.CustomerDAO;
import Project.DAOs.HomeApplianceDAO;
import Project.Customers.Address;

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
 * ---- UPDATE CUSTOMERS -----
 * @author Alessandro Frondini - 14501682
 * The UpdateCustomerByIdHandle manages the updating of customer details by their ID
 * Features:
 * - Display an HTML form to update the customer details
 * - Supports GET and POST methods:
 * 	- GET: Display form to update details
 * 	- POST: process form and update the DB
 */
public class UpdateCustomerByIdHandle implements HttpHandler {
	private final CustomerDAO cDao;
	
	/**
	 * Builds a new UpdateCustomerByIdHandle with a specific CustomerDAO
	 * @param customerDAO (to access and manages customer data)
	 */
	public UpdateCustomerByIdHandle(CustomerDAO customerDAO) {
		this.cDao = customerDAO;
	}
	
	/**
	 * This handle the HTTP requests and determine the appropriate method
     * @param he (HTTP object containing the  details of the HTTP request/response)
     * @throws IOException (if an IO error occurs during handling)
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
	
	//Update Form HTML
    private void showForm(HttpExchange he) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));
        he.sendResponseHeaders(200, 0);
        

        out.write(
            "<html>" +
            	//Head
            	"<head>"
	          + "<title> Update Customer </title>"+
	            //Bootstrap
	            "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\">" +
	            "</head>" +
	            //Body
                "<body style=\"background-color: white; text-align: center\">" +
                	//Update form
                    "<h1 style=\"color: black; font-size: 36px;\"> Update a Customer by ID </h1>" +
                    "<form method=\"POST\" action=\"/updateCustomerById\">" +
                        "<label for=\"id\"> Customer ID: </label><br>" +
                        "<input type=\"text\" id=\"customerId\" name=\"customerId\" reuired>"
                        + "<br>" +                       
                        "<label for=\"sku\"> Business Name: </label><br>" +
                        "<input type=\"text\" id=\"businessName\" name=\"businessName\" required>"
                        + "<br>" +
                        "<label for=\"description\"> Telephone Number: </label><br>" +
                        "<input type=\"text\" id=\"telephoneNumber\" name=\"telephoneNumber\" required>"
                        + "<br>" +
                        "<label for=\"category\"> AddressLine 0: </label><br>" +
                        "<input type=\"text\" id=\"addressLine0\" name=\"addressLine0\" required>"
                        + "<br>" +
                        "<label for=\"price\"> AddressLine 1: </label><br>" +
                        "<input type=\"text\" id=\"addressLine1\" name=\"addressLine1\" required><br><br>" 
                        + "<br>" +
                        "<label for=\"category\"> AddressLine 2: </label><br>" +
                        "<input type=\"text\" id=\"addressLine2\" name=\"addressLine2\" required>"
                        + "<br>" +
                        "<label for=\"category\"> Country </label><br>" +
                        "<input type=\"text\" id=\"country\" name=\"country\" required>"
                        + "<br>" +
                        "<label for=\"category\"> Postcode: </label><br>" +
                        "<input type=\"text\" id=\"postCode\" name=\"postCode\" required>" +
              
                        //Buttons
                        "<div class=\"form-group d-flex justify-content-center w-100\">" +
	                        "<div class=\"mx-2\">" +
	                            "<button type=\"submit\" class=\"btn btn-success\"> Update </button>" +
	                        "</div>" +
	                        "<div class=\"mx-2\">" +
	                            "<form action=\"/listAllCustomers\" method=\"get\">" +
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
     * --- Form submission handler ---
     * Handle the form submission to update the customer details in the DB and generates a HTML response
     * @param he (HTTP object containing the  details of the HTTP request/response)
     * @throws IOException (if an IO error occurs during handling)
     */
    private void handleFormSubmission(HttpExchange he) throws IOException {
        //Parse form
        Map<String, String> formData = parseForm(he);

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));

        try {
            int customerId = Integer.parseInt(formData.get("customerId"));
            String businessName = formData.get("businessName");
            String telephoneNumber = formData.get("telephoneNumber");
            String addressLine0 = formData.get("addressLine0");
            String addressLine1 = formData.get("addressLine1");
            String addressLine2 = formData.get("addressLine2");
            String country = formData.get("country");
            String postCode = formData.get("postCode");

            Address address = new Address(addressLine0, addressLine1, addressLine2, country, postCode);

            //DAO method
            boolean success = cDao.updateCustomerById(customerId, businessName, address, telephoneNumber);

            //HTML response
            String response = 
                "<html>" +
                    // Head section
                    "<head>" +
                        "<title> Customer Update </title>" +
                        "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\">" +
                    "</head>" +
                    // Body section
                    "<body style=\"background-color: white; text-align: center; padding: 20px;\">" +
                        "<h1 style=\"color: black;\"> Customer Updated </h1>" +
                        "<div class=\"container mt-4\">" +
                            (success
                                ? "<p style=\"color: green; font-size: 20px;\"> The Customer with ID " + customerId + " was updated successfully :)</p>"
                                : "<p style=\"color: red; font-size: 20px;\"> Customer with ID " + customerId + " not found</p>") +
                            (success
                                ? "<table class=\"table table-bordered mt-4\" style=\"width: 60%; margin: auto;\">" +
                                    "<thead class=\"thead-dark\">" +
                                        "<tr>" +
                                            "<th>Customer ID</th><th>Business Name</th><th>Telephone</th><th>Address</th>" +
                                        "</tr>" +
                                    "</thead>" +
                                    "<tbody>" +
                                        "<tr>" +
                                            "<td>" + customerId + "</td>" +
                                            "<td>" + businessName + "</td>" +
                                            "<td>" + telephoneNumber + "</td>" +
                                            "<td>" + addressLine0 + ", " + addressLine1 + ", " + addressLine2 + ", " +
                                                country + ", " + postCode + "</td>" +
                                        "</tr>" +
                                    "</tbody>" +
                                  "</table>"
                                : "") +
                        "</div>" +
                        "<div class=\"mt-4\">" +
                            "<form action=\"/listAllCustomers\" method=\"get\">" +
                                "<button type=\"submit\" class=\"btn btn-secondary\">Go Back</button>" +
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
                        "<title>Error</title>" +
                        "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\">" +
                    "</head>" +
                    "<body style=\"background-color: white; text-align: center;\">" +
                        "<h1 style=\"color: red;\"> Error Updating Customer </h1>" +
                        "<p>" + e.getMessage() + "</p>" +
                        "<div class=\"mt-4\">" +
                            "<form action=\"/updateCustomer\" method=\"get\">" +
                                "<button type=\"submit\" class=\"btn btn-secondary\"> Try Again </button>" +
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
     * --- Data parsing for the form ---
     * @param he (HTTP object containing the  details of the HTTP request/response)
     * @return a map of form data key value pairs
     * @throws IOException (if an IO error occurs during handling)
     */
    private Map<String, String> parseForm(HttpExchange he) throws IOException {
        // Read request body 
        byte[] requestBody = new byte[he.getRequestBody().available()];
        he.getRequestBody().read(requestBody);
        
        //Convert to string
        String body = new String(requestBody, StandardCharsets.UTF_8);

        return Arrays.stream(body.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }
}

