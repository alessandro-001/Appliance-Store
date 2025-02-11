package Project.AppHandlers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.io.IOException;
import java.util.Map;
import com.sun.net.httpserver.HttpHandler;

import Project.Utils;
import Project.Appliances.HomeAppliance;
import Project.DAOs.HomeApplianceDAO;
import com.sun.net.httpserver.HttpExchange;

/**
 * ----- Process Deleted Product Handler -----
 * @author Alessandro Frondini
 * ProcessDeleteByIdHandler handles the process to delete an appliance from the DB by its ID
 * Implements HttpHandler to handle and validate POST requests, delete the product and respond with a confirmation page.
 * Features:
 * - Parse parameters from the HTTP body.
 * - Validates and maps the input data.
 * - Interacts with {@link Project.DAOs.HomeApplianceDAO} to delete the product from the database.
 * - Displays a confirmation message.
 * Dependencies:
 * - {@link Project.Utils}: utility methods to parse the data.
 * - {@link Project.Appliances.HomeAppliance}: represents HomeAppliance details.
 * - {@link Project.DAOs.HomeApplianceDAO}: handles the DB operations.
 * Exceptions:
 * - SQLException: DB interaction failures;
 * - NumberFormatException: when the data input is in the wrong format;
 * - IOException: unexpected I/O errors.
 */
public class ProcessDeleteByIdHandler implements HttpHandler {

	/**
	 * The handle reads the POST request, validate the ID and proceeds to remove from the DB, sending a message 
	 * after the operation is completed.
	 * @param he (HttpExchange object to encapsulate the HTTP request/response).
     * @throws IOException (if I/O errors occur during HTTP exchanges).
	 */
    @Override
    public void handle(HttpExchange he) throws IOException {
        System.out.println("You called --> ProcessDeleteByIdHandler");

        
        /*---- Read request ----*/
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(he.getRequestBody()));
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                requestBody.append(line);
            }
            
            
            Map<String, String> params = Utils.requestStringToMap(requestBody.toString());
            System.out.println("POST Params -> " + params);

            String productId = params.get("id");

            //Error handler
            if (productId == null || productId.isEmpty()) {
                he.sendResponseHeaders(400, -1); // 400 Bad Request
                return;
            }

            int id = Integer.parseInt(productId);

            
            /*---- Delete from DB ----*/ 
            HomeApplianceDAO applianceDAO = new HomeApplianceDAO();
            HomeAppliance appliance = applianceDAO.getProductById(id);
            
            if (appliance == null) {
                he.sendResponseHeaders(404, -1); //404 not Found
                return;
            }

           
            applianceDAO.deleteProductById(id); 

            /*---- DELETED APPLIANCE HTML ----*/
            String htmlResponse = "<html>"
			            		+ "<body>"
			            			+ "<h1> Product Deleted Successfully </h1>"
			            			+ "<a href=\"/\"> Back to List </a>"
			            		+ "</body>"
			            		+ "</html>";

            he.sendResponseHeaders(200, htmlResponse.getBytes().length);
            he.getResponseBody().write(htmlResponse.getBytes());
            

        /*---- Error catching blocks ----*/
        } catch (SQLException e) {
            System.err.println("SQL ERROR: " + e.getMessage());
            he.sendResponseHeaders(500, -1); //internal server error

        } catch (NumberFormatException e) {
            System.err.println("Invalid Input: " + e.getMessage());
            he.sendResponseHeaders(400, -1); //Bad Request

        } catch (Exception e) {
            System.err.println("Unexpected ERROR: " + e.getMessage());
            he.sendResponseHeaders(500, -1); //internal server error

        } finally {
            he.getResponseBody().close();
        }
    }
}


/* StuId: 14501682 */
