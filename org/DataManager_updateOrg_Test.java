import static org.junit.Assert.*;

import java.util.*;
import java.util.Map;
import org.json.simple.JSONObject;
import org.junit.Test;

public class DataManager_updateOrg_Test {

    public class TestDataManager extends DataManager {

		public TestDataManager(WebClient client) {
			super(client);
		}

		@Override
		public String getContributorName(String contributorId) {
			return "testContributorName";
		}
	}

    @Test
    public void testSuccessfulUpdate() {
        TestDataManager dm = new TestDataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
				response.put("status", "success");

				JSONObject data = new JSONObject();
				data.put("_id", "testId");
				data.put("name", queryParams.get("name"));
				data.put("description",  queryParams.get("description"));
				
				// Define list of funds
				List<JSONObject> funds = TestUtilities.getExampleFunds();
				data.put("funds", funds);
				response.put("data", data);
				
				return response.toJSONString();
			}
			
		});

        Organization org = dm.updateOrg("testId", "testName2", "testDescription", new Credentials("login", "password"));
        assertEquals("testName2", org.getName());
        assertEquals("testDescription", org.getDescription());
    }

    @Test(expected = IllegalStateException.class)
    public void testNullResponse() {
        TestDataManager dm = new TestDataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return null;
			}
			
		});

        Organization org = dm.updateOrg("testId", "testName2", "testDescription", new Credentials("login", "password"));
    }

    @Test(expected = IllegalStateException.class)
    public void testFailedResponse() {
        TestDataManager dm = new TestDataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
				response.put("status", "failure");
                return response.toJSONString();
			}
			
		});

        Organization org = dm.updateOrg("testId", "testName2", "testDescription", new Credentials("login", "password"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullId() {
        TestDataManager dm = new TestDataManager(new WebClient("localhost", 3001));

        Organization org = dm.updateOrg(null, "testName2", "testDescription", new Credentials("login", "password"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullName() {
        TestDataManager dm = new TestDataManager(new WebClient("localhost", 3001));

        Organization org = dm.updateOrg("testId", null, "testDescription", new Credentials("login", "password"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullDescription() {
        TestDataManager dm = new TestDataManager(new WebClient("localhost", 3001));

        Organization org = dm.updateOrg("testId", "testName2", null, new Credentials("login", "password"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCredentials() {
        TestDataManager dm = new TestDataManager(new WebClient("localhost", 3001));

        Organization org = dm.updateOrg("testId", "testName2", "testDescription", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPartiallyNullCredentials() {
        TestDataManager dm = new TestDataManager(new WebClient("localhost", 3001));

        Organization org = dm.updateOrg("testId", "testName2", "testDescription", new Credentials("login", null));
    }
    
    @Test(expected = IllegalStateException.class)
    public void testNullClient() {
    	TestDataManager dm = new TestDataManager(null);
    	Organization org = dm.updateOrg("testId", "testName1", "testDescription", new Credentials("login", "password"));
    }
}
