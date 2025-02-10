package Project.Customers;

/**
 * @author Alessandro Frondini - 14501682
 * The Customer class represent the customer with its attributes. This class provides the
 * methods to retrieve the ID, business name, address, and telephone number.
 */
public class Customer {
	 private int customerId;
	 private String businessName;
	 private Address address;
	 private String telephoneNumber;
	 
	/**
	 * Constructor
     * @param customerId (the unique ID of the customer)
     * @param businessName (the name of the customer's business)
     * @param address  (the Address object representing the customer's address)
     * @param telephoneNumber (the customer's telephone number)
	 */
	public Customer(int customerId, String businessName, Address address, String telephoneNumber) {
	       this.customerId = customerId;
	       this.businessName = businessName;
	       this.address = address;
	       this.telephoneNumber = telephoneNumber;
	}
  
	/**----- GET Customer parameters-----*/
	
	/**
	 * @return Customer ID
	 */
	public int getCustomerId() {
		return customerId;
	}
	
	/**
	 * @return Business Name
	 */
	public String getBusinessName() {
		return businessName;
	}
	
	/**
	 * @return Customer Address object connected to this class
	 */
	public Address getAddress() {
		return address;
	}
	
	/**
	 * @return the phone number
	 */
	public String getTelephoneNumber() {
		return telephoneNumber;
	}
	
	/**
	 * @return a string containing all the customer's details.
	 */
	@Override	
	public String toString() {
	    return  "Customer ID " + customerId +
	            ", BusinessName = " + businessName +
	            ", Address = " + address +
	            ", TelephoneNumber = " + telephoneNumber; 
	}	
}

