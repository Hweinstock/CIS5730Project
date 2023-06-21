import static org.junit.Assert.*;

import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.Test;

/*
 * This is tested more extensively in atttemptLogin and updateOrg, only a few isolation tests here. 
 */
public class DataManager_parseOrganization_Test {
    @Test(expected = IllegalArgumentException.class)
    public void testNullData() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));

        dm.parseOrganizationFromJSON(null);

    }

}
