import org.json.simple.JSONObject;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DataManager_contributorIdByName_Test {
	
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
    public void testSuccess(){
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
                response.put("status", "success");
				response.put("id", "123");
                return response.toJSONString();
			}
            
			
		});
		
		
		String id = dm.contributorIdByName("name");
		
		assertNotNull(id);
		assertEquals(id, "123");
    }

    @Test (expected = IllegalStateException.class)
    public void testNameNotExists(){
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
                response.put("status", "not found");
                return response.toJSONString();
			}
			
		});

		String id = dm.contributorIdByName("name");
    }

    @Test(expected = IllegalStateException.class)
    public void testNullResponse(){
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return null;
			}
			
		});

		String id = dm.contributorIdByName("name");
		
    }

	@Test(expected = IllegalArgumentException.class)
	public void testNullName(){
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return null;
			}

		});

		String id = dm.contributorIdByName(null);

	}

	@Test(expected = IllegalStateException.class)
	public void testClientNull(){
		DataManager dm = new DataManager(null);

		String id = dm.contributorIdByName("name");

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

		String id = dm.contributorIdByName("name");

	}
}
