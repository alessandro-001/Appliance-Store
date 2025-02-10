package Project.Customers;

/**
 * @author Alessandro Frondini - 14501682
 * The Address class store information for the customer addresses, stored in the address table in the DB.
 */
public class Address {
	private String addressLine0;
    private String addressLine1;
    private String addressLine2;
    private String country;
    private String postCode;

    /**
     * Constructor 
     * @param addressLine0 (the street name).
     * @param addressLine1 (additional address information)
     * @param addressLine2 (further address details)
     * @param country (the country of the address)
     * @param postCode (postal code of the address)
     */
    public Address(String addressLine0, String addressLine1, String addressLine2, String country, String postCode) {
        this.addressLine0 = addressLine0;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.country = country;
        this.postCode = postCode;
    }

    /**----- GET Address parameters -----*/
    
    /**
     * @return the Street
     */
    public String getAddressLine0() {
        return addressLine0;
    }
    
    /**
     * @return address line 1
     */
    public String getAddressLine1() {
        return addressLine1;
    }
    
    /**
     * @return address line 2
     */
    public String getAddressLine2() {
        return addressLine2;
    }
    
    /**
     * @return country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @return post code
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * @return a formatted String with the full address details.
     */
    @Override
    public String toString() {
        return 	addressLine0 + ", " + 
        		addressLine1 + ", " + 
        		addressLine2 + ", " + 
        		country + ", " + 
        		postCode;
    }
}
