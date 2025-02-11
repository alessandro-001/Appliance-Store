package Project.ApplianceItem;

import Project.Appliances.HomeAppliance;

/**
 * ----- APPLIANCE ITEM CLASS -----
 * @author Alessandro Frondini
 * ApplianceItem represent the individual item with additional parameters like Warranty Years, Brand and Model, 
 * associated with the same item in the appliance table inside the DB, this class provides new methods
 * to retrieve and modify the item properties.
 * Attributes:
 * - id: is the unique identifier in the applianceItem table in the DB.
 * - homeAppliance: is the associated HomeAppliance object to connect to.
 * - warrantyYears: duration in years of the appliance warranty.
 * - brand: the appliance brand (generated for assignment purposes only).
 * - model: model or name of the appliance (generated for assignment purposes only).
 */
public class ApplianceItem {

    private int id;
    private HomeAppliance homeAppliance; 
    private int warrantyYears;
    private String brand;
    private String model;

    /**
     * Basic Constructor
     * @param id (unique identifier).
     * @param homeAppliance (the object associated to).
     * @param warrantyYears (integer for the years).
     * @param brand (bran of item)
     * @param model (model of item)
     */
    public ApplianceItem(int id, HomeAppliance homeAppliance, int warrantyYears, String brand, String model) {
        this.id = id;
        this.homeAppliance = homeAppliance;
        this.warrantyYears = warrantyYears;
        this.brand = brand;
        this.model = model;
    } 
    
    /**
     * EXTRA method - overloaded with only some specific values
     */
    public ApplianceItem(int warrantyYears, String brand, String model) {
        this.warrantyYears = warrantyYears;
        this.brand = brand;
        this.model = model;
    }

    /** ----- GET & SET -----*/
    
    //GET/SET ID unique identifier
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    //GET/SET The associated HomeAppliance object
    public HomeAppliance getHomeAppliance() {
        return homeAppliance;
    }
    public void setHomeAppliance(HomeAppliance homeAppliance) {
        this.homeAppliance = homeAppliance;
    }

    //GET/SET Warranty years
    public int getWarrantyYears() {
        return warrantyYears;
    }
    public void setWarrantyYears(int warrantyYears) {
        this.warrantyYears = warrantyYears;
    }

    //GET/SET Appliance brand
    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }

    //GET/SET Appliance model/name
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    
    
    /**
     * @return a String representing the appliance item in the console.
     */
    @Override
    public String toString() {
        return "ApplianceItem ID = " + id +
               ", Warranty Years = " + warrantyYears +
               ", Brand = " + brand +
               ", Model = " + model;
    }
}

