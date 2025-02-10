package Project.Basket;

/**
 * ----- BASKET -----
 * @author Alessandro Frondini - 14501682
 * The Basket class represent the singular item information stored in the basket.
 */
public class Basket {
    private int id;
    private String sku;
    private String description;
    private String category;
    private double price;

    /**
     * Basket Constructor
     * @param id (unique identifier for the item)
     * @param sku (stock keeping unit)
     * @param description (description of the item)
     * @param category (the item category)
     * @param price ((the item price)
     */
    public Basket(int id, String sku, String description, String category, double price) {
        this.id = id;
        this.sku = sku;
        this.description = description;
        this.category = category;
        this.price = price;
    }

    /**----- GET & SET -----*/
    /**
     * GET item ID
     * @return the item ID
     */
    public int getId() {
        return id;
    }
    /**
     * SET item ID
     * @param id (the item ID)
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * GET sku
     * @return item SKU
     */
    public String getSku() {
        return sku;
    }
    /**
     * SET sku
     * @param sku (the item SKU)
     */
    public void setSku(String sku) {
        this.sku = sku;
    }

    /**
     * GET description
     * @return item description
     */
    public String getDescription() {
        return description;
    }
    /**
     * SET description
     * @param description of the item
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * GET item category
     * @return the item category
     */
    public String getCategory() {
        return category;
    }
    /**
     * SET item category
     * @param category of the item
     */
    public void setCategory(String category) {
        this.category = category;
    }
    
    /**
     * GET price
     * @return item price
     */
    public double getPrice() {
        return price;
    }
    /**
     * SET price
     * @param price of the item
     */
    public void setPrice(double price) {
        this.price = price;
    }

    
    /**
     * @return the String representation of the item stored in the basket.
     */
    @Override
    public String toString() {
        return "Basket [id=" + id + ", sku=" + sku + ", description=" + description + ", category=" + category
                + ", price=" + price + "]";
    }
}
