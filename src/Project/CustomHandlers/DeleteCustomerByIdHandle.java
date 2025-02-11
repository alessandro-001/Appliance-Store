package Project.CustomHandlers;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import Project.DAOs.CustomerDAO;
import Project.DAOs.HomeApplianceDAO;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * @author Alessandro Frondini
 * The DeleteCustomerByIdHandle allow the admin to input the customer id in a form field
 * to delete it from the customer able in the database, providing a feedback on successful
 * deletion or not.
 * Features:
 * - Process HTTP GET requests with the ID as parameter.
 * - Works with the {@link CustomerDAO} to perform deletion in the DB.
 * - Redirect to a HTML page to show the successful deletion.
 * - Offers the option to go back to the customer table in case the wrong customer was selected.
 */
public class DeleteCustomerByIdHandle implements HttpHandler {
	
	 
	/**
	 * The handle extracts the customer ID to be deleted and generates the HMTL page.
	 * @param he (HTTP object containing the  details of the HTTP request/response)
     * @throws IOException (if an IO error occurs during handling)	 
	 */
	@Override
    public void handle(HttpExchange he) throws IOException {
	 System.out.println("You called -> DeleteCustomerByIdHandle");
    	
        he.sendResponseHeaders(200, 0);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));
        
        String query = he.getRequestURI().getQuery();
        String customerId = null;
        
        if (query != null) {
            String[] queryParameters = query.split("&");
            for (String param : queryParameters) {
                String[] keyValue = param.split("=");
                if (keyValue[0].equals("id")) {
                    customerId = keyValue[1];
                }
            }
        }
        
        //Deleted message constructor
        StringBuilder message = new StringBuilder();
        
        if (customerId != null) {
            try {
                CustomerDAO customerDAO = new CustomerDAO();
                boolean success = customerDAO.deleteCustomerById(Integer.parseInt(customerId));

                if (success) {
                    message.append("<h2> Customer with ID " + customerId + " has been deleted! </h2>");
                } else {
                    message.append("<h2> No customer found :( Try Again" + customerId + " </h2>");
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                message.append("<h2> Error deleting customer </h2>");
            }
        }
        
        /**---- DELETE CUSTOMER by ID HTML ----*/
        out.write(
        	    "<html>" +
        	    // Head
        	    "<head>" +
        	        "<title> Delete Customer </title>" +
        	        "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\" " +
        	        "integrity=\"sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2\" " +
        	        "crossorigin=\"anonymous\">" +
        	    "</head>" +
        	    //Body
        	    "<body class=\"container\">" +
        	        "<h1 class=\"mt-5 text-center\"> Delete Customer </h1>" +
        	        //Delete/enter ID form
        	        "<div class=\"d-flex justify-content-center mt-4\">" +
        	            "<form action=\"/deleteCustomerById\" method=\"get\" class=\"d-flex\">" +
        	                "<input type=\"text\" name=\"id\" class=\"form-control mr-2\" placeholder=\"Enter Customer ID\" required>" +
        	                "<button type=\"submit\" class=\"btn btn-danger\"> Delete </button>" +
        	            "</form>" +
        	        "</div>" +
        	        "<div>" +
        	        	message.toString() +
        	        	"</div>" +
        	        //Back home button
        	        "<div class=\"d-flex justify-content-center mt-4\">" +
        	            "<form action=\"/listAllCustomers\">" +
        	                "<button type=\"submit\" class=\"btn btn-primary\"> Go Back </button>" +
        	            "</form>" +
        	        "</div>" +

        	    "</body>" +
        	    "</html>"
        	);

	  out.close();

	 }
}



















