import java.util.Map;
import static org.junit.Assert.*;
import java.util.*;
import org.json.simple.JSONObject;
import org.junit.Test;

public class DataManager_attemptLogin_Test {

	// Mock object for TestDataManager to allow us to test attempLogin in isolation. 

	public String getContributorNameMock(String contributorId){
		if(contributorId == "testConstributorId"){
			return "testContributorName";
		} else {
			return null;
		}
	}

	public class TestDataManager extends DataManager {

		public TestDataManager(WebClient client) {
			super(client);
		}

		@Override
		public String getContributorName(String contributorId) {
			return getContributorNameMock(contributorId);
		}
	}

	@Test 
	public void testSuccessfullLogin() {
		TestDataManager dm = new TestDataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
				response.put("status", "success");

				JSONObject data = new JSONObject();
				data.put("_id", "testId");
				data.put("name", "testName");
				data.put("description", "testDescription");
				
				// Define list of funds
				List<JSONObject> funds = TestUtilities.getExampleFunds();
				data.put("funds", funds);
				response.put("data", data);
				
				return response.toJSONString();
			}
			
		});

		Organization org = dm.attemptLogin("username", "password");
		
		assertNotNull(org);
		assertEquals("testId", org.getId());
		assertEquals("testName", org.getName());
		assertEquals("testDescription", org.getDescription());

		List<Fund> testFunds = org.getFunds();
		List<JSONObject> expectedFunds = TestUtilities.getExampleFunds();
		assertEquals(expectedFunds.size(), testFunds.size());
		
		JSONObject currentExpectedFund;
		Fund currentActualFund;

		List<JSONObject> currentExpectedDonations;
		List<Donation> currentActualDonations;

		Donation currentActualDonation;
		JSONObject currentExpectedDonation;
		String currentExpectedContributorName;

		// Check that each associated fund is equal. 
		// Note that this requires strict ordering
		for(int fundIndex = 0; fundIndex < expectedFunds.size(); fundIndex ++) {
			currentActualFund = testFunds.get(fundIndex);
			currentExpectedFund = expectedFunds.get(fundIndex);
			
			// Check that properties of the funds line up. 
			assertEquals(currentExpectedFund.get("_id"), currentActualFund.getId());
			assertEquals(currentExpectedFund.get("name"), currentActualFund.getName());
			assertEquals(currentExpectedFund.get("description"), currentActualFund.getDescription());
			assertEquals(currentExpectedFund.get("target"), currentActualFund.getTarget());

			// Compare the donations associated with each fund. 
			//System.out.println(currentExpectedFund.get("donations").toString());
			currentActualDonations = currentActualFund.getDonations();
			currentExpectedDonations = (List<JSONObject>)currentExpectedFund.get("donations"); 
			
			assertEquals(currentExpectedDonations.size(), currentActualDonations.size());
			// Check that each associated donation is equal. 
			for (int donationIndex = 0; donationIndex < currentActualDonations.size(); donationIndex ++){
				currentActualDonation = currentActualFund.getDonations().get(donationIndex);
				currentExpectedDonation = currentExpectedDonations.get(donationIndex);

				assertEquals(currentExpectedFund.get("_id"), currentActualDonation.getFundId());
				currentExpectedContributorName = getContributorNameMock((String)currentExpectedDonation.get("contributor"));
				assertEquals(currentExpectedContributorName, currentActualDonation.getContributorName());
				assertEquals(currentExpectedDonation.get("amount"), currentActualDonation.getAmount());
				// don't check date anymore since its formatted by Donation class. 
				//assertEquals(currentExpectedDonation.get("date"), currentActualDonation.getDate());
			}	
		}

	}
	
	@Test
	public void testUnsuccessfullLogin() {
		TestDataManager dm = new TestDataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
				response.put("status", "login failed");
				return response.toJSONString();
			}
		});
		Organization org = dm.attemptLogin("username", "password");
		
		assertNull(org);
	}

	@Test(expected=IllegalStateException.class) 
	public void testErrorInLogin() {
		TestDataManager dm = new TestDataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject response = new JSONObject();
				response.put("status", "failure");
				return response.toJSONString();
			}
			
			
		});
		Organization org = dm.attemptLogin("username", "password"); 
	}

	@Test(expected=IllegalArgumentException.class) 
	public void testNullUsername() {
		TestDataManager dm = new TestDataManager(new WebClient("localhost", 3001));
		Organization org = dm.attemptLogin(null, "password"); 
	}

	@Test(expected=IllegalArgumentException.class) 
	public void testNullPassword() {
		TestDataManager dm = new TestDataManager(new WebClient("localhost", 3001));
		Organization org = dm.attemptLogin("username", null); 
	}

	@Test(expected=IllegalStateException.class) 
	public void testNullClient() {
		TestDataManager dm = new TestDataManager(null);
		Organization org = dm.attemptLogin("username", "password"); 
	}

}
