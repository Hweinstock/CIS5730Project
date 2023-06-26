import org.json.simple.JSONObject;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class DataManager_verifyContributorByName_Test {
	
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
    public void testNameExists(){
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
                response.put("status", "success");
                return response.toJSONString();
			}
            
			
		});
		
		
		boolean exists = dm.verifyContributorByName("name");
		
		assertNotNull(exists);
		assertEquals(exists, true);
    }

    @Test 
    public void testNameNotExists(){
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
                response.put("status", "not found");
                return response.toJSONString();
			}
			
		});

        boolean exists = dm.verifyContributorByName("name");
		
		assertNotNull(exists);
		assertEquals(exists, false);
    }

    @Test(expected = IllegalStateException.class)
    public void testNullResponse(){
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return null;
			}
			
		});

        boolean name = dm.verifyContributorByName("name");
		
    }

	@Test(expected = IllegalArgumentException.class)
	public void testNullName(){
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return null;
			}

		});

		boolean name = dm.verifyContributorByName(null);

	}

	@Test(expected = IllegalStateException.class)
	public void testClientNull(){
		DataManager dm = new DataManager(null);

		boolean name = dm.verifyContributorByName("name");

	}

	@Test(expected = IllegalStateException.class)
	public void testErrorResponst(){
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
				response.put("status", "error");
				response.put("data", "error data");
				return response.toJSONString();
			}

		});

		boolean name = dm.verifyContributorByName("name");

	}
}
