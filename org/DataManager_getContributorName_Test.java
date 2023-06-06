import static org.junit.Assert.*;

import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.Test;

public class DataManager_getContributorName_Test {
	
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

    @Test 
    public void testNullResposne(){
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return null;
			}
			
		});

        String name = dm.getContributorName("testId");
		
		assertNull(name);
    }
	

}
