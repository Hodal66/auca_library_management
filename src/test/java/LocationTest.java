import com.library.model.Location;
import com.library.model.ELocationType;
import com.library.dao.LocationDao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class LocationTest {

    private LocationDao locationDao = new LocationDao();
    private Location provinceLocation;
    private Location retrievedLocation;

    /**
     * Run this test first to create a province and get its ID.
     * Copy the printed UUID and use it in the next tests.
     */
    @Test
    public void testSaveProvinceLocation() {
        provinceLocation = new Location();
        provinceLocation.setCode("14");
        provinceLocation.setName("EASTERN");
        provinceLocation.setType(ELocationType.PROVINCE);

        String result = locationDao.saveLocation(provinceLocation);
        assertEquals("Location saved Successfully", result);

        // Print the generated ID for use in other tests
        System.out.println("Saved Province Location ID: " + provinceLocation.getId());
        assertNotNull(provinceLocation.getId());
    }

    /**
     * After running testSaveProvinceLocation, copy the printed ID and paste it below.
     * Example: UUID.fromString("paste-your-id-here")
     */
    // @Test
    // public void testGetLocationById() {
    //     // Replace with the actual ID printed from testSaveProvinceLocation
    //     retrievedLocation = locationDao.getLocationById(UUID.fromString("paste-your-id-here"));
    //     assertNotNull(retrievedLocation);
    //     assertEquals("EASTERN", retrievedLocation.getName());
    // }

    /**
     * Run this after testGetLocationById to save a district under the province.
     */
    // @Test
    // public void testSaveDistrict() {
    //     // Replace with the actual ID printed from testSaveProvinceLocation
    //     Location parentProvince = locationDao.getLocationById(UUID.fromString("paste-your-id-here"));

    //     Location district = new Location();
    //     district.setCode("18");
    //     district.setName("GATENGA");
    //     district.setType(ELocationType.DISTRICT);
    //     district.setParentLocation(parentProvince);

    //     String result = locationDao.saveLocation(district);
    //     assertEquals("Location saved Successfully", result);
    // }

}