package Project.Tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Project.Customers.Address;
import Project.Customers.Customer;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests class for Customers.
 */
public class CustomerTest {
    
    private Customer customer;
    private Address address;

    @BeforeEach
    void setUp() {
        //Address 
        address = new Address("1 Watson street", "Block A", "Manchester", "UK", "M3 4EF");
        
        //Customer 
        customer = new Customer(1, "Technology Business", address, "0123456789");
    }

    @Test
    void testCustomer() {
        assertEquals(1, customer.getCustomerId(), "Customer ID is 1");
        assertEquals("Technology Business", customer.getBusinessName(), "Business name is 'Technology Business'");
        assertEquals(address, customer.getAddress(), "Address match the provided address");
        assertEquals("0123456789", customer.getTelephoneNumber(), "Phone number is '0123456789'");
    }

    @Test
    void testToString() {
        // Verify the toString method outputs the correct string
        String expected = "Customer ID 1, BusinessName = Technology Business, Address = " + address + ", TelephoneNumber = 0123456789";
        assertEquals(expected, customer.toString(), "toString method output don't match the value");
    }
}
