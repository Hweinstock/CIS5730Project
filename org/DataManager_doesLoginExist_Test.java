import java.util.Map;
import static org.junit.Assert.*;
import java.util.*;

import org.json.simple.JSONObject;
import org.junit.Test;

public class DataManager_doesLoginExist_Test {

	// Mock object for TestDataManager to allow us to test attempLogin in isolation. 


	@Test 
	public void testDoesExist() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
				response.put("status", "success");
                response.put("data", true);
				
				return response.toJSONString();
			}
			
		});

		boolean result = dm.doesLoginExist("login");
        assertTrue(result);
	}

    @Test 
	public void testDoesNotExist() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
				response.put("status", "success");
                response.put("data", false);
				
				return response.toJSONString();
			}
			
		});

		boolean result = dm.doesLoginExist("login");
        assertFalse(result);
	}
	
	@Test(expected=IllegalStateException.class) 
	public void testUnsuccessfull() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
				response.put("status", "failure");
				return response.toJSONString();
			}
		});
		boolean result = dm.doesLoginExist("login");
	}

	@Test(expected=IllegalStateException.class) 
	public void testErrorInCheck() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
				response.put("status", "error");
				return response.toJSONString();
			}
			
		});
		boolean result = dm.doesLoginExist("login");
	}

	@Test(expected=IllegalArgumentException.class) 
	public void testNullLogin() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001));
		boolean result = dm.doesLoginExist(null); 
	}

	@Test(expected=IllegalStateException.class) 
	public void testNullClient() {
		DataManager dm = new DataManager(null);
		boolean result = dm.doesLoginExist("login"); 
	}

}
