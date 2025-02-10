package Project.AppHandlers;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import Project.DAOs.HomeApplianceDAO;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * ----- DELETE BY ID HANDLER -----
 * @author Alessandro Frondini - 14501682
 * DeleteByIdHandler handles the HTTP requests to remove a product from the DB table appliance by its ID
 * It provides aHTML form to enter the product ID to remove.
 * - Bootstrap CSS for styling the HTML response.
 */
public class DeleteByIdHandler implements HttpHandler {

	/**
	 * - Handles the HTTP requests to delete a product.
	 * - Parse the product ID from the query params and utilises the relative DAO function (deleteProductById)
	 * to perform the removal.
	 * - If successful it generates a small HTML response displaying the removed ID.
	 */
    @Override
    public void handle(HttpExchange he) throws IOException {
    	System.out.println("You called -> DeleteByIdHandler");
    	
        he.sendResponseHeaders(200, 0);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));

        String query = he.getRequestURI().getQuery();
        String productId = null;

        //Parse the query to extract the ID
        if (query != null) {
            String[] queryParameters = query.split("&");
            for (String param : queryParameters) {
                String[] keyValue = param.split("=");
                if (keyValue[0].equals("id")) {
                    productId = keyValue[1];
                }
            }
        }
        
        //Deleted message constructor
        StringBuilder message = new StringBuilder();
        if (productId != null) {
            try {
                HomeApplianceDAO applianceDAO = new HomeApplianceDAO();
                boolean success = applianceDAO.deleteProductById(Integer.parseInt(productId));

                if (success) {
                    message.append("<h2> Product with ID " + productId + " has been deleted! </h2>");
                } else {
                    message.append("<h2> No product found :( Try Again" + productId + " </h2>");
                }
            } catch (Exception e) {
                e.printStackTrace();
                message.append("<h2> Error deleting product </h2>");
            }
        }

        /*---- DELETE APPLIANCE by ID HTML----*/
        out.write(
        		"<html>" +
        			    // Head
        			    "<head>" +
        			        "<title> Delete Product </title>" +
        			        "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\" " +
        			        "integrity=\"sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2\" " +
        			        "crossorigin=\"anonymous\">" +
        			    "</head>" +
        			    // Body
        			    "<body class=\"container\">" +
        			        "<h1 class=\"mt-5 text-center\"> Delete Product </h1>" +
        			        // Delete/enter ID form
        			        "<div class=\"d-flex justify-content-center mt-4\">" +
        			            "<form action=\"/deleteById\" method=\"get\" class=\"d-flex\">" +
        			                "<input type=\"text\" name=\"id\" class=\"form-control mr-2\" placeholder=\"Enter Product ID\" required>" +
        			                "<button type=\"submit\" class=\"btn btn-danger\">Delete</button>" +
        			            "</form>" +
        			        "</div>" +
        			        "<div>" +
        			            message.toString() +
        			        "</div>" +
        			        // Back home buttons
        			        "<div class=\"d-flex justify-content-center mt-4\">" +
        			            "<form action=\"/listAllProducts\" class=\"mr-3\">" +
        			                "<button type=\"submit\" class=\"btn btn-primary btn-lg\">Go Back</button>" +
        			            "</form>" +
        			        "</div>" +
        			    "</body>" +
        			"</html>"

        	);

        out.close();
    }
}

