package Project.Tests;

import Project.ApplianceItem.ApplianceItem;
import Project.Appliances.HomeAppliance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HomeApplianceTest {

    private HomeAppliance appliance;
    private ApplianceItem applianceItem;

    @BeforeEach
    public void setUp() {
        applianceItem = new ApplianceItem(1, null, 2, "FakeBrand", "Model3000"); 
        appliance = new HomeAppliance(1, "ABC123", "Washing Machine", "Laundry", 333, applianceItem);
    }

    @Test
    public void testHomeAppliance() {
        assertNotNull(appliance);
        assertEquals(1, appliance.getId());
        assertEquals("ABC123", appliance.getSku());
        assertEquals("Washing Machine", appliance.getDescription());
        assertEquals("Laundry", appliance.getCategory());
        assertEquals(333, appliance.getPrice());
        assertNotNull(appliance.getApplianceItem());
    }

    @Test
    public void testHomeApplianceNoArgs() {
        HomeAppliance emptyHomeAppliance = new HomeAppliance();
        assertNotNull(emptyHomeAppliance);
        assertEquals(0, emptyHomeAppliance.getId());
        assertNull(emptyHomeAppliance.getSku());
        assertNull(emptyHomeAppliance.getDescription());
        assertNull(emptyHomeAppliance.getCategory());
        assertEquals(0, emptyHomeAppliance.getPrice());
    }

    @Test
    public void testHomeApplianceWithoutApplianceItem() {
        HomeAppliance applianceWithoutItem = new HomeAppliance(2, "JKL890", "Refrigerator", "Kitchen", 999);
        assertNotNull(applianceWithoutItem);
        assertNull(applianceWithoutItem.getApplianceItem());
    }

    @Test
    public void testSettersAndGetters() {
        appliance.setId(2);
        appliance.setSku("DEF456");
        appliance.setDescription("Dishwasher");
        appliance.setCategory("Kitchen");
        appliance.setPrice(600);
        appliance.setApplianceItem(null);

        assertEquals(2, appliance.getId());
        assertEquals("DEF456", appliance.getSku());
        assertEquals("Dishwasher", appliance.getDescription());
        assertEquals("Kitchen", appliance.getCategory());
        assertEquals(600, appliance.getPrice());
        assertNull(appliance.getApplianceItem());
    }

    @Test
    public void testToString() {
        String expectedString = "Appliance ID = 1, SKU = ABC123, Description = Washing Machine, Category = Laundry, Price=333, ApplianceItem = " + applianceItem;
        assertEquals(expectedString, appliance.toString());

        HomeAppliance applianceWithoutItem = new HomeAppliance(3, "UFC025", "Microwave", "Kitchen", 90);
        expectedString = "Appliance ID = 3, SKU = UFC025, Description = Microwave, Category = Kitchen, Price=90, ApplianceItem = None";
        assertEquals(expectedString, applianceWithoutItem.toString());
    }
}
