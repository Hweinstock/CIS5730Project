import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class DataManager_createFund_Test {
	
	/*
	 * This is a test class for the DataManager.createFund method.
	 * Add more tests here for this method as needed.
	 * 
	 * When writing tests for other methods, be sure to put them into separate
	 * JUnit test classes.
	 */

	@Test
	public void testSuccessfulCreation() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\",\"data\":{\"_id\":\"12345\",\"name\":\"new fund\",\"description\":\"this is the new fund\",\"target\":10000,\"org\":\"5678\",\"donations\":[],\"__v\":0}}";

			}
			
		});
		
		
		Fund f = dm.createFund("12345", "new fund", "this is the new fund", 10000);
		
		assertNotNull(f);
		assertEquals("this is the new fund", f.getDescription());
		assertEquals("12345", f.getId());
		assertEquals("new fund", f.getName());
		assertEquals(10000, f.getTarget());
		
	}

	@Test 
	public void testFailingCreation() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\": \"failure\"}";
			}
		});

		Fund f = dm.createFund("54321", "failing new fund", "this is a failing new fund", 10000);
		assertNull(f);
	}

	@Test(expected = IllegalStateException.class)
	public void testErrorInCreation() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return null;
			}
		});

		Fund f = dm.createFund("54321", "failing new fund", "this is a failing new fund", 10000);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testNullClient() {

		DataManager dm = new DataManager(null);
		Fund f = dm.createFund("12345", "new fund", "this is the new fund", 10000);
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNullOrgId() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\",\"data\":{\"_id\":\"12345\",\"name\":\"new fund\",\"description\":\"this is the new fund\",\"target\":10000,\"org\":\"5678\",\"donations\":[],\"__v\":0}}";

			}
			
		});
		Fund f = dm.createFund(null, "new fund", "this is the new fund", 10000);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testStatusError() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\": \"error\"}";
			}
		});

		Fund f = dm.createFund("54321", "error new fund", "this is fund returns status: error", 10000);
	}

}