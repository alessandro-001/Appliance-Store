package Project;

import Project.AdminArea.RegisterUserHandler;

import Project.AppHandlers.AddByIdHandler;
import Project.AppHandlers.DeleteByIdHandler;
import Project.AppHandlers.ListAllProductsHandler;
import Project.AppHandlers.ProcessAddByIdHandler;
import Project.AppHandlers.ProcessDeleteByIdHandler;
import Project.AppHandlers.SearchByIdHandler;
import Project.AppHandlers.UpdateByIdHandler;
import Project.ApplianceItem.ApplianceItemHandler;

import Project.Basket.AddToBasketHandler;
import Project.Basket.BasketHandler;
import Project.Basket.CheckoutHandler;

import Project.CustomHandlers.AddByCustomerIdHandle;
import Project.CustomHandlers.DeleteCustomerByIdHandle;
import Project.CustomHandlers.ListAllCustomerHandle;

import Project.DAOs.CustomerDAO;
import Project.DAOs.HomeApplianceDAO;

import Project.CustomHandlers.ProcessAddCustomerByIdHandle;
import Project.CustomHandlers.ProcessDeleteCustomerByIdHandle;
import Project.CustomHandlers.SearchByCustomerIdHandle;
import Project.CustomHandlers.UpdateCustomerByIdHandle;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.IOException;

import Project.Handlers.LoginHandler;
import Project.Handlers.RootHandler;

/**
 * ----- MAIN -----
 * @author Alessandro Frondini
 * The Main class is responsible for initialising the HTTP server and handling all the requests 
 * to home appliances and the customers tables and all the management operations.
 * It contains all the endpoints of the system to login, register, manage product and customers.
 * In addition is shuts down the server.
 */


public class Main {
	
	/*---- The server listening PORT ----*/
    static final private int PORT = 8080;
    
    public static void main(String[] args) throws IOException {
        System.out.println("The Server is Loading......");
        
        //Data Access Objects (DAO)
        HomeApplianceDAO applianceDAO = new HomeApplianceDAO();
        CustomerDAO customerDAO = new CustomerDAO();
        
        //Updates handlers
        UpdateByIdHandler updateHandler = new UpdateByIdHandler(applianceDAO);
        UpdateCustomerByIdHandle updateCustomerHandler = new UpdateCustomerByIdHandle(customerDAO);

        //HTTP Server initialiser
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        /*----- Login Page -----*/
        server.createContext("/login", new LoginHandler());
        server.createContext("/register", new RegisterUserHandler());
        
        /*----- Home Page -----*/
        server.createContext("/home", new RootHandler()); 
        
        /*----- END POINTS Appliances -----*/
        server.createContext("/listAllProducts", new ListAllProductsHandler());
        server.createContext("/searchById", new SearchByIdHandler());
        server.createContext("/addById", new AddByIdHandler());
        server.createContext("/processAddById", new ProcessAddByIdHandler());
        server.createContext("/deleteById", new DeleteByIdHandler());
        server.createContext("/processDeleteById", new ProcessDeleteByIdHandler());
        server.createContext("/updateById", new UpdateByIdHandler(applianceDAO));
        //EXTRA appliance item
        server.createContext("/applianceItem", new ApplianceItemHandler());


        /*----- END POINTS Customers -----*/
        server.createContext("/listAllCustomers", new ListAllCustomerHandle());
        server.createContext("/addCustomer", new AddByCustomerIdHandle());
        server.createContext("/processAddCustomerById", new ProcessAddCustomerByIdHandle());
        server.createContext("/searchByCustomerId", new SearchByCustomerIdHandle());
        server.createContext("/deleteCustomerById", new DeleteCustomerByIdHandle());
        server.createContext("/processDeleteCustomerById", new ProcessDeleteCustomerByIdHandle());
        server.createContext("/updateCustomerById", new UpdateCustomerByIdHandle(customerDAO));
       
        
        /*----- END POINTS Basket -----*/
        server.createContext("/basket",new BasketHandler());
        server.createContext("/addToBasket", new AddToBasketHandler());
        server.createContext("/checkout", new CheckoutHandler());
        
        /*----- SERVER start -----*/
        server.setExecutor(null);
        server.start();
        System.out.println("The server is listening on port: " + PORT);
       
        
        /*----- SERVER Shutdown -----*/
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down the server........bye!");
            server.stop(0);
        }));
    }
}


