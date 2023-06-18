import java.util.Map;
import static org.junit.Assert.*;
import java.util.*;

import org.json.simple.JSONObject;
import org.junit.Test;

public class DataManager_createOrg_Test {

	// Mock object for TestDataManager to allow us to test attempLogin in isolation. 
	
	
    @Test
	public void testSuccessfulCreation() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
				response.put("status", "success");
				return response.toJSONString();
			}

		}) {
			@Override
			public boolean doesLoginExist(String login) { return false; }
		};

		DataManager.OrgCreationStatus status = dm.createOrg("username", "password", "orgName", "orgDesc");
		
		assertEquals(DataManager.OrgCreationStatus.CREATED, status);
	}

	@Test
	public void testUnsuccessfullCreation() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
				response.put("status", "failed");
				return response.toJSONString();
			}
		}) {
			@Override
			public boolean doesLoginExist(String login) { return false; }
		};

		DataManager.OrgCreationStatus status = dm.createOrg("username", "password", "orgName", "orgDesc");
		assertEquals(DataManager.OrgCreationStatus.SERVER_ERROR, status);
	}

	@Test
	public void testLoginAlreadyExists() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
				response.put("status", "failed");
				return response.toJSONString();
			}
		}) {

			@Override
			public boolean doesLoginExist(String login) { return true; }
		};

		DataManager.OrgCreationStatus status = dm.createOrg("username", "password", "orgName", "orgDesc");
		
		assertEquals(DataManager.OrgCreationStatus.DUPLICATE, status);
	}

	@Test(expected=IllegalStateException.class) 
	public void testErrorInCreation() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
				response.put("status", "failure");
				return response.toJSONString();
			}
			
		}) {
			@Override
			public boolean doesLoginExist(String login) { return false; }
		};
		DataManager.OrgCreationStatus status = dm.createOrg("username", "password", "orgName", "orgDesc");
	}

	@Test(expected=IllegalArgumentException.class) 
	public void testNullUsername() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001));
		DataManager.OrgCreationStatus status = dm.createOrg(null, "password", "orgName", "orgDesc"); 
	}

	@Test(expected=IllegalArgumentException.class) 
	public void testNullPassword() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001));
		DataManager.OrgCreationStatus status = dm.createOrg("username", null, "orgName", "orgDesc"); 
	}

	@Test(expected=IllegalStateException.class) 
	public void testNullClient() {
		DataManager dm = new DataManager(null);
		DataManager.OrgCreationStatus status = dm.createOrg("username", "password", "orgName", "orgDesc"); 
	}

	@Test(expected=IllegalArgumentException.class) 
	public void testNullOrgName() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001));
		DataManager.OrgCreationStatus status = dm.createOrg("username", "password", null, "orgDesc"); 
	}

	@Test(expected=IllegalArgumentException.class) 
	public void testNullOrgDesc() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001));
		DataManager.OrgCreationStatus status = dm.createOrg("username", "password", "orgName", null); 
	}

	@Test
	public void testEmptyName() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001));
		DataManager.OrgCreationStatus status = dm.createOrg("", "password", "orgName", "orgDesc"); 
		assertEquals(DataManager.OrgCreationStatus.INVALID, status);
	}

	@Test
	public void testEmptyPassword() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001));
		DataManager.OrgCreationStatus status = dm.createOrg("username", "", "orgName", "orgDesc");
		assertEquals(DataManager.OrgCreationStatus.INVALID, status);
	}

	@Test
	public void testEmptyOrgName() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001));
		DataManager.OrgCreationStatus status = dm.createOrg("username", "password", "", "orgDesc"); 
		assertEquals(DataManager.OrgCreationStatus.INVALID, status);
	}

	@Test
	public void testEmptyOrgDesc() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001));
		DataManager.OrgCreationStatus status = dm.createOrg("username", "password", "orgName", ""); 
		assertEquals(DataManager.OrgCreationStatus.INVALID, status);
	}
}
