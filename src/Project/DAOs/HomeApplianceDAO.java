package Project.DAOs;

import java.sql.*;
import java.util.Scanner;

import Project.ApplianceItem.ApplianceItem;
import Project.Appliances.HomeAppliance;

import java.util.ArrayList;

/**
 * ----- HOME APPLIANCE DAO -----
 * @author Alessandro Frondini
 * The DAO (Data Access Object) manages all the appliances related operations in the DB
 * CRUD Features:
 * - list all products in the DB
 * - search products by their ID
 * - retrieves a single product by its ID
 * - search products by their description
 * - filter products by category
 * - add products to the DB
 * - delete products by ID
 * - update products by ID
 * - use of applianceItem class for further CRUD operations
 * 
 */
public class HomeApplianceDAO {

	
    /*----- DATABASE CONNECTION -----*/
    private Connection connect() {
        Connection connection = null;

        try {
            String url = "jdbc:sqlite:store.sqlite";
            connection = DriverManager.getConnection(url);
            System.out.println("\nDatabase Connected Successfully! :)\n\n///---  Extracting Data from Appliance ---///\n");
            

        } catch (SQLException se) {
            System.err.println(se.getClass().getName() + se.getMessage());
        }
        return connection;
    }
    
    
    /*-------------------------------------------- CRUD operations --------------------------------------------*/ 
    
    
    /**
     * ----- 1. LIST ALL PRODUCTS in appliance table -----
     * @return a list of {@link HomeAppliance} objects with extra {@link ApplianceItem} details
     */
    public ArrayList<HomeAppliance> listAllProducts() {  
    	    	
        //EXTRA updated code including the applianceItem 
    	String sql = "SELECT "
                + "app.id AS applianceId, "
                + "app.sku, "
                + "app.description, "
                + "app.category, "
                + "app.price, "
                + "appIt.id AS applianceItemId, "
                + "appIt.warrantyYears, "
                + "appIt.brand, "
                + "appIt.model "
                + "FROM "
                + "appliance app "
                + "LEFT JOIN "
                + "applianceItem appIt "
                + "ON "
                + "app.id = appIt.applianceId";
        
        ArrayList<HomeAppliance> appliances = new ArrayList<>();
        
        try (Connection conn = connect();
        	 Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
        	
            System.out.println("You selected  --->  Listing all products\n");
            while (rs.next()) {
            	
            	//standard appliance table
            	HomeAppliance appliance = new HomeAppliance(
                        rs.getInt("applianceId"),
                        rs.getString("sku"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getInt("price")
                    );
            	
            	//EXTRA added data to appliance
            	int applianceItemId = rs.getInt("applianceItemId");
                if (applianceItemId != 0) { 
                    ApplianceItem applianceItem = new ApplianceItem(
                            applianceItemId,
                            appliance,
                            rs.getInt("warrantyYears"),
                            rs.getString("brand"),
                            rs.getString("model")
                    );
                    appliance.setApplianceItem(applianceItem);
                }               
            	appliances.add(appliance);           	
            }
            
            
            //Check for an empty DB
            if (appliances.isEmpty()) {
                System.out.println("No products in Database");
            }
	
	        } catch (SQLException se) {
	            System.err.println("ERROR in listing all products " + se.getMessage());
	            se.printStackTrace();
	        }
	        return appliances;

    }
    
    /**
     * ----- 1.1 overloaded LIST ALL PRODUCTS with sorting feature -----
     * List all products with the option of sorting them by price or warranty years, in ascending or descending order
     * @param sortBy ("price" or "warrantyYears")
     * @param sortOrder ("ASC" or "DESC")
     * @return a sorted list of {@link HomeAppliance} objects
     */
    public ArrayList<HomeAppliance> listAllProducts(String sortBy, String sortOrder) {
        String sql = "SELECT "
                + "app.id AS applianceId, "
                + "app.sku, "
                + "app.description, "
                + "app.category, "
                + "app.price, "
                + "appIt.id AS applianceItemId, "
                + "appIt.warrantyYears, "
                + "appIt.brand, "
                + "appIt.model "
                + "FROM "
                + "appliance app "
                + "LEFT JOIN "
                + "applianceItem appIt "
                + "ON "
                + "app.id = appIt.applianceId";
        
        //Sorting by price || warrantyYears
        if (sortBy != null && sortOrder != null) {
            if (sortBy.equals("price") || sortBy.equals("warrantyYears")) {
            	if (sortBy.equals("warrantyYears")) {
                    sql += " ORDER BY appIt.warrantyYears " + sortOrder; //Warranty is in applianceItem tab
                } else {
                    sql += " ORDER BY " + sortBy + " " + sortOrder; //price is in appliance tab
                }
            }
        }

        ArrayList<HomeAppliance> appliances = new ArrayList<>();
        
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("You selected  --->  Listing all products\n");
            while (rs.next()) {
                
                // Standard appliance table
                HomeAppliance appliance = new HomeAppliance(
                        rs.getInt("applianceId"),
                        rs.getString("sku"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getInt("price")
                    );
                
                // EXTRA added data to appliance
                int applianceItemId = rs.getInt("applianceItemId");
                if (applianceItemId != 0) { 
                    ApplianceItem applianceItem = new ApplianceItem(
                            applianceItemId,
                            appliance,
                            rs.getInt("warrantyYears"),
                            rs.getString("brand"),
                            rs.getString("model")
                    );
                    appliance.setApplianceItem(applianceItem);
                }               
                appliances.add(appliance);           
            }
            
            // Check for an empty DB
            if (appliances.isEmpty()) {
                System.out.println("No products in Database");
            }

        } catch (SQLException se) {
            System.err.println("ERROR in listing all products " + se.getMessage());
            se.printStackTrace();
        }
        return appliances;
    }

    
    
    /**
     * ----- 2. SEARCH PRODUCT by ID in appliance table -----
     * @param id (ID of the product to search)
     * @return a list containing the matching {@link HomeAppliance} object
     * @throws SQLException (if a database access error occurs)
     */   
    public ArrayList<HomeAppliance> searchProductById(int id) throws SQLException {

    	String sql = "SELECT * FROM appliance WHERE id = ?";
    	ArrayList<HomeAppliance> appliances = new ArrayList<>();
        
        try (Connection conn = connect(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
            	System.out.println("You selected ID  --->  " + id);
                if (rs.next()) {
                	HomeAppliance appliance = new HomeAppliance(
                            rs.getInt("id"),
                            rs.getString("sku"),
                            rs.getString("description"),
                            rs.getString("category"),
                            rs.getInt("price")
                    );
                    appliances.add(appliance);  
                }
            }
        } catch (SQLException se) {
            System.err.println("ERROR in searching product  " + se.getMessage());
            se.printStackTrace();
        }
        return appliances;
    } 
    
    /**
     * ----- 2.1 GET a single Product by ID -----
     * @param id (ID of the product to retrieve)
     * @return a {@link HomeAppliance} object containing the product info
     * @throws SQLException (if a database access error occurs)
     */
    public HomeAppliance getProductById(int id) throws SQLException {		
    	//EXTRA modified to connect the applianceItem
    	String sql = "SELECT app.id AS applianceId, "
    			   + "app.sku, "
    			   + "app.description, "
    			   + "app.category, "
    			   + "app.price, " 
    			   + "appIt.warrantyYears, "
                   + "appIt.brand, "
                   + "appIt.model " +
                   "FROM appliance app " +
                   "LEFT JOIN applianceItem appIt ON app.id = appIt.applianceId " +
                   "WHERE app.id = ?";

        HomeAppliance appliance = null;  

        try (Connection conn = connect(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                	ApplianceItem applianceItem = null;
                    if (rs.getString("brand") != null) {
                        applianceItem = new ApplianceItem(
                            rs.getInt("warrantyYears"),
                            rs.getString("brand"),
                            rs.getString("model")
                        );
                    }
      
                    appliance = new HomeAppliance(
                        rs.getInt("applianceId"),
                        rs.getString("sku"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getInt("price"),
                        applianceItem
                    );
                }
            }
            
        } catch (SQLException se) {
            System.err.println("ERROR in getting product by ID: " + se.getMessage());
            se.printStackTrace();
        }
        return appliance; 
    }
    
    
    /**
     * ----- 2.2 SEARCH PRODUCTS by DESCRIPTION -----
     * @param searchString (keyword to search for product descriptions)
     * @return a list of {@link HomeAppliance} objects that match the search
     */
    public ArrayList<HomeAppliance> searchProductsByDescription(String searchString) {
        String sql = "SELECT * FROM appliance WHERE description LIKE ?"; 
        ArrayList<HomeAppliance> appliances = new ArrayList<>();
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	
            pstmt.setString(1, "%" + searchString + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("You selected  --->  Searching products by description: " + searchString);
                while (rs.next()) {
                    HomeAppliance appliance = new HomeAppliance(
                            rs.getInt("id"),
                            rs.getString("sku"),
                            rs.getString("description"),
                            rs.getString("category"),
                            rs.getInt("price")
                    );
                    appliances.add(appliance);
                }
                if (appliances.isEmpty()) {
                    System.out.println("No products found matching the search criteria.");
                }
            }
        } catch (SQLException se) {
            System.err.println("ERROR in searching products by description " + se.getMessage());
            se.printStackTrace();
        }
        return appliances;
    }
    
    
    /**
     * ----- 2.3 FILTER PRODUCTS by CATEGORY -----
     * @param category (category to filter)
     * @return a list of {@link HomeAppliance} objects of the filtered category
     */    
    public ArrayList<HomeAppliance> filterProductsByCat(String category) {
	    String query = "SELECT * FROM appliance WHERE category = ?";
	    ArrayList<HomeAppliance> appliances = new ArrayList<>();
	    
	    try (Connection conn = this.connect();
	         PreparedStatement preStatement = conn.prepareStatement(query)) {
	    	
	        preStatement.setString(1, category);
	        ResultSet result = preStatement.executeQuery();
	        
	        while (result.next()) {
	        	HomeAppliance appliance = new HomeAppliance(
	        			result.getInt("id"),
	        	        result.getString("sku"),
	        	        result.getString("description"),
	        	        result.getString("category"),
	        	        result.getInt("price")
	        	    );
	        	    appliance.setId(result.getInt("id"));
	        	    appliances.add(appliance);
	        }
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	    }
	    return appliances;
	}
    
    
    /**
     * ----- 3. ADD NEW PRODUCTS to appliance table (with scanner) -----
     * @param scanner (receive user input for product details to fill the appliance table)
     */
    public void addProduct(Scanner scanner) {
    	
    	System.out.println("Enter new SKU: ");
        String sku = scanner.nextLine();
        
        System.out.println("Enter description: ");
        String description = scanner.nextLine(); 
        
        System.out.println("Enter category: ");
        String category = scanner.nextLine();
 
        System.out.println("Enter new price: ");
        int price = scanner.nextInt();
        
        scanner.nextLine();
        
        //add to table
    	String sql = "INSERT INTO appliance (sku, description, category, price) " + 
    				 "VALUES (?, ?, ?, ?)";
    	
    	try (Connection conn = connect();
    		 PreparedStatement pstmt = conn.prepareStatement(sql)) {
    		
    		pstmt.setString(1, sku);
            pstmt.setString(2, description);
            pstmt.setString(3, category);
            pstmt.setInt(4, price);
            
            
            int extraRows = pstmt.executeUpdate();  
            if (extraRows > 0) {
            	try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            		
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);  
                        System.out.println("The new product has been added successfully! :)");
                        System.out.println("New ID added: " + generatedId);
                    }
                }
            	
            } else {
                System.out.println("Failed to add the product :(");
            }
            
    	} catch (SQLException se) {
            System.err.println("ERROR adding product: " + se.getMessage());
            se.printStackTrace();
        }
    }
   
    
    
    /**
     * ----- 3.1 ADD NEW PRODUCTS // Overloaded  (with HomeAppliance) -----
     * @param appliance object containing the appliance item info
     * @throws SQLException (if HomeAppliance has an invalid ID or database access errors)
     */
    public void addProduct(HomeAppliance appliance) throws SQLException {
    	String sql = "INSERT INTO appliance (sku, description, category, price) VALUES (?, ?, ?, ?)";
    	
    	try (Connection conn = connect();
       		 PreparedStatement pstmt = conn.prepareStatement(sql)) {
    		 
            pstmt.setString(1, appliance.getSku());
            pstmt.setString(2, appliance.getDescription());
            pstmt.setString(3, appliance.getCategory());
            pstmt.setInt(4, appliance.getPrice());
            
            int extraRows = pstmt.executeUpdate();
            
            if (extraRows > 0) {
            	try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            		
            		if(generatedKeys.next()) {
            			appliance.setId(generatedKeys.getInt(1));
            			System.out.println("The new product"+ appliance.getId() +"has been added successfully! :)");
            		} else {
                        throw new SQLException("Failed to retrieve the appliance ID");
            		}
            	}
            	
            } else {
            	System.out.println("Failed to add the product :(");
            }
    		
    	} 
    	catch (SQLException se) {
            System.err.println("ERROR adding product: " + se.getMessage());
            se.printStackTrace();
        }
    }
    
    //EXTRA for applianceItem
    public void addApplianceItem(ApplianceItem applianceItem) throws SQLException {
        if (applianceItem.getHomeAppliance().getId() == 0) {
            throw new SQLException("Invalid. HomeAppliance must be saved first and have a valid ID.");
        }

        String sql = "INSERT INTO applianceItem (applianceId, warrantyYears, brand, model) VALUES (?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the parameters for the query
            pstmt.setInt(1, applianceItem.getHomeAppliance().getId()); // Link to HomeAppliance via ID
            pstmt.setInt(2, applianceItem.getWarrantyYears());
            pstmt.setString(3, applianceItem.getBrand());
            pstmt.setString(4, applianceItem.getModel());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("The new appliance item has been added successfully!");
            } else {
                System.out.println("Failed to add the appliance item.");
            }

        } catch (SQLException se) {
            System.err.println("ERROR adding appliance item: " + se.getMessage());
            throw se;
        }
    }


    
    
    /**
     * ----- 4. UPDATE PRODUCT by ID in appliance table -----
     * @return true (if the update was successful) or false otherwise.
     */
       
    //EXTRA revisited method including applianceItem 
    public boolean updateProductByID(int productId, String sku, String description, String category, int price, int warrantyYears, String brand, String model) {
        //appliance table
    	String sql = "UPDATE appliance " +
                     "SET sku = ?, description = ?, category = ?, price = ? " +
                     "WHERE id = ?";
    	//applianceItem table
        String sqlItem = "UPDATE applianceItem " +
                         "SET warrantyYears = ?, brand = ?, model = ? " +
                         "WHERE applianceId = ?"; 

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             PreparedStatement pstmtItem = conn.prepareStatement(sqlItem)) {

            //Update appliance table
            pstmt.setString(1, sku);
            pstmt.setString(2, description);
            pstmt.setString(3, category);
            pstmt.setInt(4, price);
            pstmt.setInt(5, productId);

            int rowsUpdatedAppliance = pstmt.executeUpdate();

            //Update applianceItem table
            pstmtItem.setInt(1, warrantyYears);
            pstmtItem.setString(2, brand);
            pstmtItem.setString(3, model);
            pstmtItem.setInt(4, productId); // use the same id for appliance and applianceItem

            int rowsUpdatedItem = pstmtItem.executeUpdate();

            return rowsUpdatedAppliance > 0 && rowsUpdatedItem > 0; // return true if both updates are successful
        } catch (SQLException se) {
            System.err.println("ERROR updating the product: " + se.getMessage());
            se.printStackTrace();
            return false;
        }
    }


    
    
    /**
     * ----- 5. DELETE PRODUCT by ID in appliance table -----
     * @param productId (ID of the product to delete)
     * @return true if the product was deleted or false otherwise
     * @throws SQLException (if database access errors occurs)
     */
    public boolean deleteProductById(int productId) throws SQLException {
    	//EXTRA for delete applianceItem too
        String deleteAppItemSql = "DELETE FROM applianceItem WHERE applianceId = ?";
    	String sql = "DELETE FROM appliance WHERE id = ?";
    	boolean deleted = false;
    	
    	try (Connection conn = connect();
    		 PreparedStatement pstmt1 = conn.prepareStatement(sql);
    		 PreparedStatement pstmt2 = conn.prepareStatement(deleteAppItemSql)) {
    		
    		//delete from first table
    		pstmt1.setInt(1, productId);		
    		int extraRows1 = pstmt1.executeUpdate();
    		if (extraRows1 > 0) {
    			deleted = true;
                System.out.println("Product with ID " + productId + " has been deleted from appliance :(");
            } 
    		
    		//delete from second table
    		pstmt2.setInt(1, productId);
            int extraRows2 = pstmt2.executeUpdate();
            if (extraRows2 > 0) {
                deleted = true;
                System.out.println("Product with ID " + productId + " has been deleted from applianceItem too!");
            } else {
                System.out.println("No product found with ID " + productId);
            }
            
    	} catch (SQLException se) {
            System.err.println("ERROR deleting product: " + se.getMessage());
            se.printStackTrace(); 
        } 
    	return deleted;
    }

    //not is use
	public HomeAppliance getApplianceBySku(String sku) {
		return null;
	}
    

}

