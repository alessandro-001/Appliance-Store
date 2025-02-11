package Project;

import Project.DAOs.HomeApplianceDAO;

import Project.Appliances.HomeAppliance;
import Project.Customers.Address;
import Project.Customers.Customer;
import Project.DAOs.CustomerDAO;
import java.util.Scanner;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * ---- MENU CONSOLE ----
 * @author Alessandro Frondini
 * The MenuConsole class provides an interactive menu in the console interface 
 * where is possible to manage the home appliances and customers tables directly. 
 * The menu allows the admin to list, search, add, update, and delete products or customers.
 * 
 * This class is linked to {@link Project.DAOs.HomeApplianceDAO} and {@link Project.DAOs.CustomerDAO} to operate
 * on appliances and customers tables stored in the store.sqlite database. 
 * The console menu loop continues until the user chooses to exit (y/n).
 */

public class MenuConsole {

    private final HomeApplianceDAO haDao = new HomeApplianceDAO();
    private final CustomerDAO cDao = new CustomerDAO();

    public void displayMenu() throws SQLException {
        Scanner in = new Scanner(System.in);
        int selected;
        boolean continueMenu = true;
        
        do {
            System.out.println(" -----------------------------------------");
            System.out.println("|  Welcome to The Home Appliance Store :) |");
            System.out.println("|    Choose from the following options    |");
            System.out.println(" -----------------------------------------");
            
            System.out.println("[1] ---> List all Products");
            System.out.println("[2] ---> Search a Product ID");
            System.out.println("[3] ---> Add a new Product");
            System.out.println("[4] ---> Update a product by ID");
            System.out.println("[5] ---> Delete a product by ID \n");
            
            System.out.println("[6] ---> List all Customers");
            System.out.println("[7] ---> Search a Customer by ID");
            System.out.println("[8] ---> Add a new Customer");
            System.out.println("[9] ---> Update a Customer by ID");
            System.out.println("[10] ---> Delete a Customer by ID \n");
            
            System.out.println("[0] ---> Exit \n");
            System.out.print("Click here and enter a number:  ");
        

            selected = in.nextInt();
            in.nextLine();

            switch (selected) {
            
          //PRODUCTS/APPLIANCES 
            
            	/*----- List All Products(Appliances) -----*/
                case 1: 
                	ArrayList<HomeAppliance> products = haDao.listAllProducts();
                	for (HomeAppliance product : products) {
                        System.out.println(product);
                    }
                    break;
                
                /*----- Search Product by ID -----*/
                case 2:										
                	System.out.print("Enter the product ID to search: ");	
                    int productId = in.nextInt();
                    in.nextLine();
                    ArrayList<HomeAppliance> appliances = haDao.searchProductById(productId);
                    for (HomeAppliance appliance : appliances) {
                        System.out.println(appliance);
                        }
                    break; 
                      
				/*----- Add a New Product -----*/	
				case 3:
					haDao.addProduct(in);
					in.nextLine();
					break;
				
				/*----- Update Existing Product by ID -----*/
				case 4:
				    System.out.println("Enter the product ID to update: ");
				    int updateId = in.nextInt();
				    in.nextLine();
				    
				    System.out.print("Enter new SKU: ");
				    String sku = in.nextLine();

				    System.out.print("Enter new description: ");
				    String description = in.nextLine();

				    System.out.print("Please enter one of the following categories: ");
				    System.out.println("Kitchen, Laundry, Cooling, Personal Care, Entertainment, Cleaning, Heating");
				    String category = in.nextLine();

				    System.out.print("Enter new price: ");
				    int price = in.nextInt();
				    in.nextLine(); 
				    
				    //EXTRA for applianceItem
				    System.out.print("Enter Warranty Years: ");
				    int warrantyYears = in.nextInt();
				    in.nextLine(); 				    

				    System.out.print("Enter new Brand: ");
				    String brand = in.nextLine();
				    
				    System.out.print("Enter new Model: ");
				    String model = in.nextLine();
				    

				    boolean successProductUpdated = haDao.updateProductByID(updateId, sku, description, category, price, warrantyYears, brand, model);

				    //Display modified appliance
				    if (successProductUpdated) {
				        System.out.println("Product updated successfully!");
				    } else {
				        System.out.println("Product with ID " + updateId + " not found.");
				    }
				    break;

				
				/*----- Delete a product by ID -----*/
				case 5:
					System.out.print("Enter the ID of the product to delete: ");                         
					int deleteId = in.nextInt();
					in.nextLine();
					haDao.deleteProductById(deleteId);
					break;
					 
					
		  //CUSTOMERS
					
				/*----- List All Customers -----*/
                case 6: 
                	System.out.println("List all Customers");
                	ArrayList<Customer> customersList = cDao.listAllCustomers();
	            	for (Customer customer : customersList) {
	            		System.out.println(customer);
	            	    }
	            	    break;
                    
                /*----- Search Customers by ID -----*/
                case 7: 
                	System.out.print("Enter the Customer's ID here: ");
                	int customerId = in.nextInt();
                    in.nextLine();
                    ArrayList<Customer> customers = cDao.searchCustomerById(customerId);
                    for (Customer customer : customers) {
                        System.out.println(customer);
                        }
                    break;
                    
                /*----- Add new Customers -----*/
                case 8: 
                	System.out.println("Add new Customer");
                	cDao.addCustomer(in);
					in.nextLine();
                    break;
                    
                /*----- Update Customers by ID -----*/
                case 9: 
                    System.out.print("Enter the Customer ID to update: ");
                    int customUpdateId = in.nextInt();
                    in.nextLine(); // Consume newline character

                    System.out.print("Enter the new Business Name: ");
                    String businessName = in.nextLine();

                    System.out.println("Enter the new Address details:");
                    System.out.print("  Address Line 0: ");
                    String addressLine0 = in.nextLine();
                    
                    System.out.print("  Address Line 1: ");
                    String addressLine1 = in.nextLine();
                    
                    System.out.print("  Address Line 2: ");
                    String addressLine2 = in.nextLine();
                    
                    System.out.print("  Country: ");
                    String country = in.nextLine();
                    
                    System.out.print("  Post Code: ");
                    String postCode = in.nextLine();

                    Address address = new Address(addressLine0, addressLine1, addressLine2, country, postCode);

                    System.out.print("Enter the new Telephone Number: ");
                    String telephoneNumber = in.nextLine();

                    boolean successCustomUpdated = cDao.updateCustomerById(customUpdateId, businessName, address, telephoneNumber);

                    if (successCustomUpdated) {
                        System.out.println("Customer updated successfully!");
                    } else {
                        System.out.println("Customer with ID " + customUpdateId + " not found.");
                    }
                    break;

                    
                    /*----- Delete Customers by ID -----*/
                case 10: 
                	System.out.print("Enter the Customer ID to update: ");
                	int deleteCustomId = in.nextInt();
					in.nextLine();
					cDao.deleteCustomerById(deleteCustomId);
                    break;
                    	
                    
          /*----- EXIT program-----*/
                case 0:
                    System.out.println("Exiting the program.....BYE!");
                    break;

                default:
                    System.out.println("Sorry! This option is not implemented yet :(");
                    break;
            }
            if(selected != 0) {
            	System.out.print("\nDo you want to go back to the Console Menu? (y/n): ");
            	char choice = in.next().charAt(0);
            	
            	if (choice == 'n' || choice == 'N') {
                    System.out.println("\nExiting the program.....BYE!");
                    continueMenu = false;
                }
            }
            
        } while (continueMenu);
   
        in.close();
    }
    

    /**
     * This is entry point for the MenuConsole class, which initialises and displays the 
     * menu in the console to operate home appliances and customers.
     * 
     * @throws SQLException if there is a database access error.
     */
    /*-----  DISPLAY the CONSOLE MENU  -----*/  
    public static void main(String[] args) throws SQLException {
    	MenuConsole mc = new MenuConsole();
    	mc.displayMenu();
    }

}


















