package Project.DAOs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import Project.Appliances.HomeAppliance;
import Project.Customers.Address;
import Project.Customers.Customer;

/**
 * ----- CUSTOMERS DAO -----
 * 
 * @author Alessandro Frondini - 14501682
 * The DAO (Data Access Object) manages all the customer related operations in the DB
 * CRUD Features:
 * - list all customers from the customer table in the DB
 * - search customers by their ID
 * - retrieves single customer details by their ID
 * - add customers to the DB
 * - update customer details
 * - delete customers
 */
public class CustomerDAO {
	
	/*----- DATABASE CONNECTION -----*/
    private Connection connect() {
        Connection connection = null;

        try {
            String url = "jdbc:sqlite:store.sqlite";
            connection = DriverManager.getConnection(url);
            System.out.println("\nDatabase Connected Successfully! :)\n\n///---  Extracting Data from Customer ---///\n");       

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return connection;
    }
    
    
    
    /*-------------------------------------------- CRUD operations --------------------------------------------*/ 

    

    /**
     * ----- 6. LIST ALL CUSTOMERS in customer table + Joined ADDRESSES -----
     * Retrieves a list of all the customer in the customer table in the DB
     * @return the list of all customers
     */
    public ArrayList<Customer> listAllCustomers() {
        String sql = "SELECT " +
						"customer_table.customerID, "
						+ "customer_table.businessName, "
						+ "customer_table.telephoneNumber, " +
						"address_table.addressLine0, "
						+ "address_table.addressLine1, "
						+ "address_table.addressLine2, "
					 	+ "address_table.country, "
					 	+ "address_table.postCode " +    
                	 "FROM customer customer_table " +
                	 "JOIN address address_table ON customer_table.addressID = address_table.addressID";  
        
        ArrayList<Customer> customers = new ArrayList<>();
        
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             
            System.out.println("You selected ---> Listing all customers\n");
            while (rs.next()) {
            	//Address table
            	Address address = new Address(
                		rs.getString("addressLine0"), 
                		rs.getString("addressLine1"), 
                		rs.getString("addressLine2"), 
                		rs.getString("country"),
                		rs.getString("postCode")
                );
            	//Customer table
                Customer customer = new Customer(
                    rs.getInt("customerID"),
                    rs.getString("businessName"),
                    address,
                    rs.getString("telephoneNumber")
                );
                
                customers.add(customer);
            }

            // Check if empty
            if (customers.isEmpty()) {
                System.out.println("No customers in the database.");
            }

        } catch (SQLException se) {
            System.err.println("ERROR in listing all customers: " + se.getMessage());
            se.printStackTrace();
        }
        
        return customers;
    }

    
    
    /**
     * ----- 7. SEARCH CUSTOMER by ID -----
     * Search a customer by ID from the DB and retrieves all the details including the JOINED
     * Address table.
     * @param id ( customer ID)
     * @return a list of all the customers with the matching ID
     * @throws SQLException when DB errors occurs
     */   
    public ArrayList<Customer> searchCustomerById(int id) throws SQLException {
        String sql = "SELECT " +
		        		"customer_table.customerID, "
		        		+ "customer_table.businessName, "
		        		+ "customer_table.telephoneNumber, " +
		                "address_table.addressLine0, "
		                + "address_table.addressLine1, "
		                + "address_table.addressLine2, "
		                + "address_table.country, "
		                + "address_table.postCode " +
                     "FROM customer customer_table " +
                     "JOIN address address_table ON customer_table.addressID = address_table.addressID " +
                     "WHERE customer_table.customerID = ?";
        ArrayList<Customer> customers = new ArrayList<>();
        
        try (Connection conn = connect(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("You selected Customer ID  --->  " + id + "\n");
                if (rs.next()) {
                	
                    Address address = new Address(
                        rs.getString("addressLine0"),
                        rs.getString("addressLine1"),
                        rs.getString("addressLine2"),
                        rs.getString("country"),
                        rs.getString("postCode")
                    );
                    Customer customer = new Customer(
                        rs.getInt("customerID"),
                        rs.getString("businessName"),
                        address,
                        rs.getString("telephoneNumber")
                    );

                    customers.add(customer);
                }
            }
            
            
        } catch (SQLException se) {
            System.err.println("ERROR in searching the customer: " + se.getMessage());
            se.printStackTrace();
        }
        return customers;
    }
    
     
    /**
     * ----- GET a single Customer -----
     * Retrieves a customer details by their ID
     * @param id (the customer ID)
     * @return the customer object with the related details
     * @throws SQLException when DB errors occurs
     */
    public Customer getCustomerId(int id) throws SQLException {
        String sql = "SELECT * FROM customer WHERE customerID = ?";
        Customer customer = null;  

        try (Connection conn = connect(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {

                    Address address = new Address(
                        rs.getString("addressLine0"),
                        rs.getString("addressLine1"),
                        rs.getString("addressLine2"),
                        rs.getString("country"),
                        rs.getString("postCode")
                    );
                    Customer custom = new Customer(
                        rs.getInt("customerID"),
                        rs.getString("businessName"),
                        address,
                        rs.getString("telephoneNumber")
                    );
                        
                }
            }
            
        } catch (SQLException se) {
            System.err.println("ERROR in getting customer by ID: " + se.getMessage());
            se.printStackTrace();
        }
        return customer; 
    }
      
    /**
     * ----- 8. ADD NEW CUSTOMERS to customer table (Console) -----
     * Add a new customer to the DB via console commands and use of Java Scanner
     * @param scanner (is the scanner used to get the input from the console user)
     */
    public void addCustomer(Scanner scanner) {
    	
        System.out.println("Enter new business name: ");
        String businessName = scanner.nextLine();
        
        System.out.println("Enter new telephone number: ");
        String telephoneNumber = scanner.nextLine();

        System.out.println("Enter new street: ");
        String addressLine0 = scanner.nextLine();
        
        System.out.println("Enter new address line 1: ");
        String addressLine1 = scanner.nextLine();
        
        System.out.println("Enter new address line 2: ");
        String addressLine2 = scanner.nextLine();
        
        System.out.println("Enter the country: ");
        String country = scanner.nextLine();
        
        System.out.println("Enter the postcode: ");
        String postCode = scanner.nextLine();

        //Insert the address into the table
        String addressSql = "INSERT INTO address ("
        						+ "addressLine0, "
        						+ "addressLine1, "
        						+ "addressLine2, "
        						+ "country, postCode) VALUES (?, ?, ?, ?, ?)";
        //Insert customer details
        String customerSql = "INSERT INTO customer ("
        						+ "businessName, "
        						+ "telephoneNumber, "
        						+ "addressID) VALUES (?, ?, ?)";
        
        try (Connection conn = connect(); 
             PreparedStatement addressPstmt = conn.prepareStatement(addressSql, Statement.RETURN_GENERATED_KEYS); 
             PreparedStatement customerPstmt = conn.prepareStatement(customerSql)) {

            
        	addressPstmt.setString(1, addressLine0);
        	addressPstmt.setString(2, addressLine1);
        	addressPstmt.setString(3, addressLine2);
        	addressPstmt.setString(4, country);
        	addressPstmt.setString(5, postCode);
            int addressRows = addressPstmt.executeUpdate();
            
            if (addressRows > 0) {
                try (ResultSet generatedKeys = addressPstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int addressId = generatedKeys.getInt(1);
                        
                        //insert into customer table
                        customerPstmt.setString(1, businessName);
                        customerPstmt.setString(2, telephoneNumber);
                        customerPstmt.setInt(3, addressId); //FK
                        
                        int cstExtraRows = customerPstmt.executeUpdate(); 
                        if (cstExtraRows > 0) {  //Rows check
                            System.out.println("The new customer has been added successfully! :)");
                        } else {
                            System.out.println("Failed to add the customer  :( ");
                        }
                    }
                }
            } else {
                System.out.println("Failed to add the address   :(");
            }

        } catch (SQLException e) {
            System.err.println("ERROR in adding customer:  " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    
    /**
     * ----- 8.1 ADD NEW CUSTOMER (Overloaded - web page) -----
     * Add a new customer from a customer object, used for the web page function.
     * @param customer (is the customer object containing the details)
     * @throws SQLException (if database access errors occurs)
     */
    public void addCustomer(Customer customer) throws SQLException {
        //Insert the address into the table
        String addressSql = "INSERT INTO address ("
        						+ "addressLine0, "
        						+ "addressLine1, "
        						+ "addressLine2, "
        						+ "country, "
        						+ "postCode) VALUES (?, ?, ?, ?, ?)";
        //Insert customer details
        String customerSql = "INSERT INTO customer ("
        						+ "businessName, "
        						+ "telephoneNumber, "
        						+ "addressID) VALUES (?, ?, ?)";
        
        try (Connection conn = connect();
             PreparedStatement addressPstmt = conn.prepareStatement(addressSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement customerPstmt = conn.prepareStatement(customerSql)) {
             
            Address address = customer.getAddress();
            addressPstmt.setString(1, address.getAddressLine0());
            addressPstmt.setString(2, address.getAddressLine1());
            addressPstmt.setString(3, address.getAddressLine2());
            addressPstmt.setString(4, address.getCountry());
            addressPstmt.setString(5, address.getPostCode());
            int addressRows = addressPstmt.executeUpdate();
            
            if (addressRows > 0) {
                try (ResultSet generatedKeys = addressPstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int addressId = generatedKeys.getInt(1);
                        
                        // insert into customer 
                        customerPstmt.setString(1, customer.getBusinessName());
                        customerPstmt.setString(2, customer.getTelephoneNumber());
                        customerPstmt.setInt(3, addressId); //FK
                        
                        int customerRows = customerPstmt.executeUpdate();
                        if (customerRows > 0) {
                            System.out.println("The new customer has been added successfully! :)");
                        } else {
                            System.out.println("Failed to add the customer :(");
                        }
                    }
                }
            } else {
                System.out.println("Failed to add the address :(");
            }

        } catch (SQLException e) {
            System.err.println("ERROR in adding customer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    /**
     * ----- 9 UPDATE EXISTIING CUSTOMERs details-----
     * @param customerId (is the ID of the customer to be updated)
     * @param businessName (new business name)
     * @param address (new address)
     * @param telephoneNumber (new telephone number)
     * @throws SQLException (if database access errors occurs)
     */
    public boolean updateCustomerById(int customerId, String businessName, Address address, String telephoneNumber) {
        //Update customer
    	String customerSql = "UPDATE customer "
                           + "SET businessName = ?, telephoneNumber = ? "
                           + "WHERE customerID = ?";
        //Update address
        String addressSql = "UPDATE address "
                          + "SET addressLine0 = ?, addressLine1 = ?, addressLine2 = ?, country = ?, postCode = ? "
                          + "WHERE addressID = (SELECT addressID FROM customer WHERE customerID = ?)";
        
        try (Connection conn = connect()) {
            //customer table
            try (PreparedStatement customerStmt = conn.prepareStatement(customerSql)) {
                customerStmt.setString(1, businessName);
                customerStmt.setString(2, telephoneNumber);
                customerStmt.setInt(3, customerId);
                customerStmt.executeUpdate();
            }

            //address table
            try (PreparedStatement addressStmt = conn.prepareStatement(addressSql)) {
                addressStmt.setString(1, address.getAddressLine0());
                addressStmt.setString(2, address.getAddressLine1());
                addressStmt.setString(3, address.getAddressLine2());
                addressStmt.setString(4, address.getCountry());
                addressStmt.setString(5, address.getPostCode());
                addressStmt.setInt(6, customerId);
                addressStmt.executeUpdate();
            }
            
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            return false;
        }
    }

    
    
    /**
     * ----- 9 DELETE EXISTIING CUSTOMERs from database -----
     * @param customerId (ID of the customer to delete)
     * @return true (if the customer ID was successfully deleted) or false (if no customer ID was found)
     * @throws SQLException (if database access error occurs)
     */
    public boolean deleteCustomerById(int customerId) throws SQLException {
    	//Delete customer
    	String sql = "DELETE FROM customer WHERE customerID = ?";
    	boolean deleted = false;
    	
    	try (Connection conn = connect();
       		 PreparedStatement pstmt = conn.prepareStatement(sql)) {
    		
    		pstmt.setInt(1, customerId);
    		int extraRows = pstmt.executeUpdate();
    		
    		if (extraRows > 0) {
    			deleted = true;
                System.out.println("Customer with ID " + customerId + " has been deleted :(");
            } else {
                System.out.println("No customer found with ID " + customerId);
            }
    		
    	} catch (SQLException se) {
            System.err.println("ERROR deleting customer: " + se.getMessage());
            se.printStackTrace(); 
        } 
    	
    	return deleted;
    }


}


