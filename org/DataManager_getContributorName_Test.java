import static org.junit.Assert.*;

import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.Test;

public class DataManager_getContributorName_Test {
	
	class TestDataManager extends DataManager {
		public TestDataManager(WebClient webClient){
			super(webClient);
		}
		
		public void addToCache(String key, String value) {
			this.contributorNameCache.put(key, value);
		}

		public Map<String, String> inspectCache() {
			return this.contributorNameCache;
		}
	}

    @Test 
    public void testSuccessfullId(){
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
                response.put("status", "success");
                response.put("data", "testName");
                return response.toJSONString();
			}
            
			
		});
		
		
		String name = dm.getContributorName("testId");
		
		assertNotNull(name);
		assertEquals("testName", name);
    }

    @Test 
    public void testFailingId(){
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
                response.put("status", "failure");
                return response.toJSONString();
			}
			
		});

        String name = dm.getContributorName("testId");
		
		assertNull(name);
    }

    @Test(expected = IllegalStateException.class)
    public void testNullResposne(){
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return null;
			}
			
		});

        String name = dm.getContributorName("testId");
		
    }

	@Test 
	public void testCacheResponse(){


		TestDataManager dm = new TestDataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return null;
			}

		});
		dm.addToCache("testId", "testName");
		String name = dm.getContributorName("testId");
		assertEquals("testName", name);
	}

	@Test 
	public void testCacheOnRequest(){

		TestDataManager dm = new TestDataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
                response.put("status", "success");
                response.put("data", "testName");
                return response.toJSONString();
			}

		});
		String name = dm.getContributorName("testId");
		Map<String, String> cache = dm.inspectCache();
		
		assertTrue(cache.containsKey("testId"));
		assertTrue(cache.get("testId") == name);


	}
	
	@Test(expected = IllegalStateException.class)
	public void testNullClient() {

		DataManager dm = new DataManager(null);
		String name = dm.getContributorName("12345");
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNullOrgId() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\",\"data\":{\"_id\":\"12345\",\"name\":\"new fund\",\"description\":\"this is the new fund\",\"target\":10000,\"org\":\"5678\",\"donations\":[],\"__v\":0}}";

			}
			
		});
		String name = dm.getContributorName(null);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testStatusError() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\": \"error\"}";
			}
		});

		String name = dm.getContributorName("54321");
	}
	
	@Test(expected=IllegalStateException.class)
	public void testNonParseableResp() {

		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				throw new IllegalArgumentException();
			}
		});

		String name = dm.getContributorName("54321");
	}
	

}
