import com.library.model.Location;
import com.library.model.ELocationType;
import com.library.dao.LocationDao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class LocationTest {

    private LocationDao locationDao = new LocationDao();
    private Location provinceLocation;
    private Location retrievedLocation;
    private UUID provinceId;

    @Before
    public void setUp() {
        locationDao.deleteAllLocations();
        // Create province before each test
        provinceLocation = new Location();
        provinceLocation.setCode("14");
        provinceLocation.setName("EASTERN");
        provinceLocation.setType(ELocationType.PROVINCE);

        String result = locationDao.saveLocation(provinceLocation);
        assertEquals("Location saved Successfully", result);

        provinceId = provinceLocation.getId();
        assertNotNull(provinceId);
    }

    @Test
    public void testSaveProvinceLocation() {
        // Province is already created in setUp()
        System.out.println("Saved Province Location ID: " + provinceId);
        assertNotNull(provinceId);
    }

    @Test
    public void testGetLocationById() {
        retrievedLocation = locationDao.getLocationById(provinceId);
        assertNotNull(retrievedLocation);
        assertEquals("EASTERN", retrievedLocation.getName());
    }

    @Test
    public void testSaveDistrict() {
        Location parentProvince = locationDao.getLocationById(provinceId);

        Location district = new Location();
        district.setCode("19");
        district.setName("BURERA");
        district.setType(ELocationType.DISTRICT);
        district.setParentLocation(parentProvince);

        String result = locationDao.saveLocation(district);
        assertEquals("Location saved Successfully", result);
    }
}