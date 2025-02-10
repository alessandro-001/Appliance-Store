package Project.Appliances;

import Project.ApplianceItem.ApplianceItem;

/**
 * @author Alessandro Frondini - 14501682
 * The HomeAppliance class represent the appliance element in the appliance table in the DB.
 * Includes the optional associated applianceItem class.
 * 
 */
public class HomeAppliance {
	private int id;
	private String sku;
	private String description;
	private String category;
	private int price;
	
	//EXTRA applianceItem
	private ApplianceItem applianceItem;
	
		
	/**---- Constructor Overloading ----*/
	
	/** No-argument default empty constructor */
    public HomeAppliance() {
    }
	
    
    /**
     * EXTRA constructor with applianceItem    
     * @param id (unique identifier for the appliance)
     * @param sku (stock keeping unit)
     * @param description (appliance description)
     * @param category (category of the appliance)
     * @param price (price of the appliance)
     * @param applianceItem (associated ApplianceItem)
     */
    public HomeAppliance(int id, String sku, String description, String category, int price, ApplianceItem applianceItem) {
    	this.id = id;
    	this.sku = sku;
    	this.description = description;
    	this.category = category;
    	this.price = price;
    	this.applianceItem = applianceItem;
    }
	
    
    /**
     * Base Constructor without an associated ApplianceItem.
     * 
     * @param id           the unique identifier for the appliance
     * @param sku          the stock keeping unit
     * @param description  the description of the appliance
     * @param category     the category of the appliance
     * @param price        the price of the appliance
     */
	public HomeAppliance(int id, String sku, String description, String category, int price) {
        this.id = id;
        this.sku = sku;
        this.description = description;
        this.category = category;
        this.price = price;
    }
	
	
	
	/** ----- GET & SET -----*/
	
	//product ID
	public int getId() {
		return id;
	}
	public void setId(int id) {
        this.id = id;
    }
	
	//product SKU
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
        this.sku = sku;
    }
	
	//product DESCRIPTION
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
        this.description = description;
    }
	
	//product CATEGORY
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
        this.category = category;
    }

	//product PRICE
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
        this.price = price;
    }
	

    //EXTRA for applianceItem
	public ApplianceItem getApplianceItem() {
		return applianceItem;
		
	}
	public void setApplianceItem(ApplianceItem applianceItem) {
		this.applianceItem = applianceItem;
		
	}
    

	/**
	 * @return a string representation of the appliance.
	 */
	@Override	
    public String toString() {
        return  "Appliance ID = " + id +
                ", SKU = " + sku +
                ", Description = " + description + 
                ", Category = " + category + 
                ", Price=" + price +
                
                (applianceItem != null ? ", ApplianceItem = " + applianceItem : ", ApplianceItem = None"); 
	}	
}
