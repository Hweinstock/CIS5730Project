import java.util.Map;
import static org.junit.Assert.*;
import java.util.*;

import org.json.simple.JSONObject;
import org.junit.Test;

public class DataManager_createAccount_Test {

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
		});

		boolean result = dm.createAccount("username", "password");
		
		assertTrue(result);
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
		});

		boolean result = dm.createAccount("username", "password");
		
		assertFalse(result);
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
			
			
		});
		boolean result = dm.createAccount("username", "password");
	}
}
