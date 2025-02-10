package Project.Tests;

import Project.ApplianceItem.ApplianceItem;
import Project.Appliances.HomeAppliance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ApplianceItemTest {

    private ApplianceItem applianceItem;
    private HomeAppliance appliance;

    @BeforeEach
    public void setUp() {
        appliance = new HomeAppliance(1, "ABC123", "Washing Machine", "Laundry", 500);
        applianceItem = new ApplianceItem(1, appliance, 2, "Samsong", "Modelo99");
    }

    @Test
    public void testApplianceItem() {
        assertNotNull(applianceItem);
        assertEquals(1, applianceItem.getId());
        assertEquals(2, applianceItem.getWarrantyYears());
        assertEquals("Samsong", applianceItem.getBrand());
        assertEquals("Modelo99", applianceItem.getModel());
        assertNotNull(applianceItem.getHomeAppliance());
    }

    
    
    @Test
    public void testApplianceItemWithoutHomeAppliance() {
        ApplianceItem applianceItemWithoutHomeAppliance = new ApplianceItem(2, "Samsong", "Modelo99");
        assertNotNull(applianceItemWithoutHomeAppliance);
        assertNull(applianceItemWithoutHomeAppliance.getHomeAppliance());
    }

    @Test
    public void testSetsAndGets() {
        applianceItem.setId(2);
        applianceItem.setWarrantyYears(3);
        applianceItem.setBrand("Samsong");
        applianceItem.setModel("Modelo99");
        applianceItem.setHomeAppliance(appliance);

        assertEquals(2, applianceItem.getId());
        assertEquals(3, applianceItem.getWarrantyYears());
        assertEquals("Samsong", applianceItem.getBrand());
        assertEquals("Modelo99", applianceItem.getModel());
        assertEquals(appliance, applianceItem.getHomeAppliance());
    }
    

    @Test
    public void testToString() {
        String expectedString = "ApplianceItem ID = 1, Warranty Years = 2, Brand = Samsong, Model = Modelo99";
        assertEquals(expectedString, applianceItem.toString());

        ApplianceItem applianceItemWithoutBrand = new ApplianceItem(3, null, 1, "Phelipes", "Modello88");
        expectedString = "ApplianceItem ID = 3, Warranty Years = 1, Brand = Phelipes, Model = Modello88";
        assertEquals(expectedString, applianceItemWithoutBrand.toString());
    }
}
