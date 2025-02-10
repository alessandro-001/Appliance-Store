package Project.AppHandlers;

import Project.AdminArea.SessionHandler;
import Project.Appliances.HomeAppliance;
//import Project.Basket.Basket;
import Project.DAOs.HomeApplianceDAO;


import java.io.OutputStream;
import java.io.OutputStreamWriter;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;
import java.io.BufferedWriter;
import java.io.IOException;


/**
 * ----- DISPLAY / LIST ALL PRODUCTS HANDLER -----
 * @author Alessandro Frondini - 14501682
 * Handles all the HTTP requests to display the products in the appliance table in the DB, it includes:
 * - Filtering by Category;
 * - Search product by ID;
 * - Sort price in ascending/descending order;
 * - Allows the user or admin to click on the product description to see more details (applianceItem);
 * - All CRUD operations for admin roles;
 * - Add to basket and checkout functions for users.
 * - Bootstrap CSS for styling the HTML response.
 */
public class ListAllProductsHandler implements HttpHandler {
	
	/**
	 * Handles the HTTP requests by rendering a table with the full list of appliances in the DB.
	 * The list can be further filtered by category or products can be searched by their ID.
	 * The actions column and buttons differs depending on the user role (admin/user).
	 * @param he (HttpExchange represents the request and response).
	 * @throws IOException (if an I/O error occurs).
	 */
	@Override
	public void handle(HttpExchange he) throws IOException {
		
		//status 200: OK
	    he.sendResponseHeaders(200, 0);
	    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(he.getResponseBody()));          
	    System.out.println("Received request ---> " + he.getRequestURI());

	    //GET user role from the session
	    String userRole = getSessionUserRole(he); // "user"; 
	    HomeApplianceDAO appliances = new HomeApplianceDAO();

	    //GET filter and search parameters
	    String categoryFilter = getCategoryFilter(he);
	    String searchQuery = getSearchQuery(he);
	    	    
	    //EXTRA sorting functions
	    String sortBy = getSortBy(he);  
        String sortOrder = getSortOrder(he); //asc || desc

	    
	    StringBuilder tableRows = new StringBuilder();
	    ArrayList<HomeAppliance> allAppliances;
	     
	    try {
	    	//Filter category when specified otherwise display all the products by default
	        if (categoryFilter != null && !categoryFilter.isEmpty()) {
	            allAppliances = appliances.filterProductsByCat(categoryFilter);
	        } else {
	            allAppliances = appliances.listAllProducts(sortBy, sortOrder);
	        }

	        System.out.println("User role entered ---> " + userRole);

	        //Table rows for appliances
	        for (HomeAppliance appliance : allAppliances) {
	            tableRows.append(
	                "<tr>" +
	                    "<td class=\"align-middle text-center\">" + appliance.getId() + "</td>" +
	                    "<td class=\"align-middle text-center\">" + appliance.getSku() + "</td>" +
	                    //EXTRA for ApplianceItem table display
	                    "<td class=\"align-middle text-center\">" +
	                    	"<a href=\"/applianceItem?id=" + appliance.getId() + "\">" + appliance.getDescription() + "</a>" +
	                    "</td>" +	                    
	                    "<td class=\"align-middle text-center\">" + appliance.getCategory() + "</td>" +
	                    "<td class=\"align-middle text-center\">" + appliance.getPrice() + "</td>");

	            if (userRole.equals("admin")) { //Shows the actions column only if the user is admin
	                tableRows.append(
	                		
	                	/*--- ADMIN only ACTIONS column ----*/
	                    "<td class=\"align-middle text-center\">" +
	                    	//Delete 
	                        "<form action=\"/deleteById\" method=\"get\" style=\"display:inline;\">" +
	                            "<input type=\"hidden\" name=\"id\" value=\"" + appliance.getId() + "\" />" +
	                            "<button type=\"submit\" class=\"btn btn-outline-danger btn-sm\"> " +
	                            	"<i class=\"fa fa-trash\" aria-hidden=\"true\"></i>\n  Delete" +
	                            "</button>" +
	                        "</form>" +
	                        "&nbsp;" +
	                        //Update
	                        "<form action=\"/updateById\" method=\"get\" style=\"display:inline;\">" +
	                            "<input type=\"hidden\" name=\"id\" value=\"" + appliance.getId() + "\" />" +
	                            "<button type=\"submit\" class=\"btn btn-outline-warning btn-sm\">  " +
	                            	"<i class=\"fa fa-wrench\" aria-hidden=\"true\"></i>\n Update" +
	                            "</button>" +
	                        "</form>" +
	                    "</td>");
	            } else {
	            	
	            	/*--- USERS only SHOPPING BASKET function ----*/
	            	//Button to add to basket
	                tableRows.append( "<td class=\"align-middle text-center\">" +
	                		//Users action column
	                        "<form action=\"/addToBasket\" method=\"post\" style=\"display:inline;\">" + 
		                        "<input type=\"hidden\" name=\"sku\" value=\"" + appliance.getSku() + "\" />" +//getSku of appliance
		                        "<button type=\"submit\" class=\"btn btn-outline-success btn-sm\">" +
		                        	//icon
		                            "<i class=\"fa fa-cart-plus\"></i> Add to Basket" +
		                        "</button>" +
			                "</form>" +
			             "</td>");
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    /*---- LIST of ALL APPLIANCES HTML ----*/
	    out.write(
	        "<html>" +
	            //Head
	            "<head> " +
	                "<title> List Of All Appliances </title> "+
	                //BootstrapCSS 
	                "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\" " +
	                "integrity=\"sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2\" " +
	                "crossorigin=\"anonymous\">" +
	                //FontAwesone icon
	                "<link href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css\" rel=\"stylesheet\">\n" +
	                "<style>" +
	                    "td, th { white-space: nowrap; text-align: center; }" +  
	                    "td.address { white-space: normal; text-align: left; }" +  
	                "</style>" +
	            "</head>" +
	            //Body    
	            "<body style=\"background-color: dark; text-align: center \" >" +
	            "<div class=\"container\">" +
	                "<div class=\"d-flex justify-content-between align-items-center mt-3\">" +
	                    "<h1 style=\"color: black; font-size: 40px;\"> List of All Appliances </h1>" +
	                    //Search form
	                    "<form action=\"/searchById\" method=\"get\" class=\"form-inline\">" +
	                        "<input type=\"text\" name=\"id\" class=\"form-control mr-2\" placeholder=\"Enter Product ID\" required>" +
	                        "<button type=\"submit\" class=\"btn btn-dark\"> Search </button>" +
	                    "</form>" +
	                    //Filter form 
	                    "<form action=\"/listAllProducts\" method=\"get\" class=\"form-inline mt-2\">" +
	                        "<select name=\"categoryFilter\" class=\"form-control mr-2\">" +
	                        "<option value=\"\" disabled selected> Choose a Category </option>"
	                        );

	                    //Category filter dropdown
	                    String[] categories = {"Kitchen", "Cooling", "Personal_Care"};	                    
	                    for (String category : categories) {
	                        out.write(
	                            "<option value=\"" + category + "\" " +
	                            (categoryFilter.equals(category) ? "selected" : "") + ">" + category + "</option>");
	                    }

	                    out.write(
	                        "</select>" +
	                        "<button type=\"submit\" class=\"btn btn-dark\">" +
	                        	"Filter " +
	                        "</button>" +
	                    "</form>" +
	                "</div>" +                    
	                //EXTRA sorting dropdown
	                "<form action=\"/listAllProducts\" method=\"get\" class=\"form-inline mt-2\">" +
	                	//sort by
		                "<select name=\"sortBy\" class=\"form-control mr-2\">" +
			                "<option value=\"price\" " + (sortBy.equals("price") ? "selected" : "") + "> Price </option>" +
			                "<option value=\"warranty\" " + (sortBy.equals("warranty") ? "selected" : "") + "> Warranty </option>" +
		                "</select>" +
			            //order by
		                "<select name=\"sortOrder\" class=\"form-control mr-2\">" +
		                	"<option value=\"asc\" " + (sortOrder.equals("asc") ? "selected" : "") + "> Ascending </option>" +
		                	"<option value=\"desc\" " + (sortOrder.equals("desc") ? "selected" : "") + "> Descending </option>" +
		                "</select>" +
		                "<button type=\"submit\" class=\"btn btn-dark\"> Sort </button>" +
	                "</form>" +
	                //Table
	                "<table class=\"table table-bordered table-striped table-dark mt-4\">" +
	                    "<thead class=\"thead-dark\">" +
	                        "<tr>" +
	                            "<th> ID </th>" +
	                            "<th> SKU </th>" +
	                            "<th> Description <br><small> (Click on name for more details) </small></th>" +
	                            "<th> Category </th>" +
	                            "<th> Price  </th>" +
	                            "<th> Actions </th>" +
	                        "</tr>" +
	                    "</thead>" +
	                    "<tbody>" +
	                        tableRows.toString() +
	                    "</tbody>" +
	                "</table>" +
	                //Back Button  
	                "<form action=\"/\" class=\"mt-4\">" +
	                "<div class=\"row mt-4\">" +
	                    "<div class=\"col text-left\">" +
	                        "<a href=\"" + (userRole.equals("user") ? "/login" : "/home") + "\" class=\"btn btn-primary\">" +
	                            (userRole.equals("user") ? "Logout" : "Back to Home") +
	                        "</a>" +
	                    "</div>"
	                );

	                /*---- USER checkout ----*/
	                if (userRole.equals("user")) {
	                    out.write(
	                        "<div class=\"col text-right\">" +
	                            "<a href=\"/basket\" class=\"btn btn-success\"> Checkout </a>" +
	                        "</div>"
	                    );
	                }
	                /*---- ADMIN add product ----*/
	                if (userRole.equals("admin")) {
	                    out.write(
	                        "<div class=\"col text-right\">" +
	                            "<a href=\"/addById\" class=\"btn btn-success\"> Add a Product </a>" +
	                        "</div>"
	                    );
	                }

	            out.write(
	                "</form>" +
	                "</body>" +
	                "</html>");

	    out.close();
	}

	
	
	/**
	 * Retrieve the user role from the HTTP request via:
	 * - Query Parameters;
	 * - Cookies;
	 * - Referer headers.
	 * @param he HttpExchange contains the HTTP request.
	 * @return the user role as a String, it defaults to 'user' if no role exist.
	 */
	private String getSessionUserRole(HttpExchange he) {
	    //GET role from query
	    String query = he.getRequestURI().getQuery();
	    if (query != null && query.contains("role=")) {
	        for (String param : query.split("&")) {
	            if (param.startsWith("role=")) {
	                System.out.println("Role from query: " + param.split("=")[1]);
	                return param.split("=")[1];
	            }
	        }
	    }

	    //GET role from cookies
	    if (he.getRequestHeaders().containsKey("Cookie")) {
	        String cookies = he.getRequestHeaders().getFirst("Cookie");
	        System.out.println("Cookies: " + cookies);
	        for (String cookie : cookies.split("; ")) {
	            if (cookie.startsWith("role=")) {
	                return cookie.split("=")[1];
	            }
	        }
	    }

	    //GET role from Referer 
	    String ref = he.getRequestHeaders().getFirst("Referer");
	    
	    if (ref != null && ref.contains("role=")) {
	        for (String param : ref.split("\\?")[1].split("&")) {
	            if (param.startsWith("role=")) {
	                System.out.println("Role from referer: " + param.split("=")[1]);
	                return param.split("=")[1];
	            }
	        }
	    }
	    //Back to DEFAULT
	    return "user"; 
		
	}
	

		
	/**
	 * VALIDATE the session with the sessionId in the cookies
	 * @param he the HttpExchange contains the HTTP request. 
	 * @return true is session is valis and false if not.
	 */
	private boolean validateSession(HttpExchange he) {
	    String cookieHeader = he.getRequestHeaders().getFirst("Cookie");
	    if (cookieHeader != null && cookieHeader.contains("sessionId=")) {
	        String sessionId = extractSessionIdFromCookie(cookieHeader);
	        Map<String, Object> role = SessionHandler.getSession(sessionId);
	        if (role != null) {
	            System.out.println("Session valid: " + sessionId);
	            return true;  //Valid session
	        }
	    }
	    
	    System.out.println("Session invalid or not found");
	    return false;  //Invalid session
	}

	
	/**
	 * EXTRACT session from cookies in the HTTP request
	 * @param cookieHeader (cookie header in HTTP response).
	 * @return the session ID as a String or null if not existent.
	 */
	private String extractSessionIdFromCookie(String cookieHeader) {
	    for (String cookie : cookieHeader.split(";")) {
	        String[] keyValue = cookie.trim().split("=");
	        if (keyValue.length == 2 && "sessionId".equals(keyValue[0])) {
	            return keyValue[1];
	        }
	    }
	    return null;
	}
	
	
	/**
	 * GET the categories for the filter
	 * @param he the HttpExchange contains the HTTP request.  
	 * @return the selected category as a String or empty String when no filter is active.
	 */
	private String getCategoryFilter(HttpExchange he) {
        String query = he.getRequestURI().getQuery();
        if (query != null && query.contains("categoryFilter=")) {
            for (String param : query.split("&")) {
                if (param.startsWith("categoryFilter=")) {
                    return param.split("=")[1];
                }
            }
        }
        return "";  //Default to no category filter
    }
	
	
	/**
	 * GET the search query from the request URI
	 * @param he the HttpExchange contains the HTTP request. 
	 * @return the search query as a String or empty String when there's no query.
	 */
    private String getSearchQuery(HttpExchange he) {
        String query = he.getRequestURI().getQuery();
        if (query != null && query.contains("searchQuery=")) {
            for (String param : query.split("&")) {
                if (param.startsWith("searchQuery=")) {
                    return param.split("=")[1];
                }
            }
        }
        return "";  //Default to no search query
    }
    
    
    /*---- EXTRA SORTING FEATURE ----*/
 
    /**
     * GET sortBy parameters (price, warranty) from query params
     * @param he the HttpExchange contains the HTTP request. 
     * @return the sorting attribute as a String or 'price' by default.
     */
    private String getSortBy(HttpExchange he) {
        String query = he.getRequestURI().getQuery();
        if (query != null && query.contains("sortBy=")) {
            for (String param : query.split("&")) {
                if (param.startsWith("sortBy=")) {
                    return param.split("=")[1];
                }
            }
        }
        return "price";  //Default to sort by price
    }

    /**
     * GET sortOrder parameters in ascending or descending order
     * @param he the HttpExchange contains the HTTP request. 
     * @return the sorting order a String or 'asc' by default.
     */
    private String getSortOrder(HttpExchange he) {
        String query = he.getRequestURI().getQuery();
        if (query != null && query.contains("sortOrder=")) {
            for (String param : query.split("&")) {
                if (param.startsWith("sortOrder=")) {
                    return param.split("=")[1];
                }
            }
        }
        return "asc";  //DEFAULT to ascending order
    }

}
