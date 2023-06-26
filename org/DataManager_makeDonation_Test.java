import org.json.simple.JSONObject;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DataManager_makeDonation_Test {

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
    public void testSuccessfulDonation(){
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				JSONObject donation = new JSONObject();
				donation.put("fund", "0");
				donation.put("contributor","1");
				donation.put("amount", 10);
					JSONObject response = new JSONObject();
                response.put("status", "success");
					response.put("data", donation);
                return response.toJSONString();
				}


			});


			Donation donation = dm.makeDonation(new Fund("0", "testFund","testDescription",10),
					"testContributor",
					"1",
					10);

			assertNotNull(donation);
			assertEquals(donation.getFundId(), "0");
			assertEquals(donation.getAmount(), 10);
			assertEquals(donation.getContributorName(), "testContributor");
    }

    @Test(expected = IllegalStateException.class)
    public void testNullResponse(){
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return null;
			}

		});

		Donation donation = dm.makeDonation(new Fund("0", "testFund","testDescription",10),
				"testContributor",
				"1",
				10);

		}

		@Test(expected = IllegalArgumentException.class)
		public void testNullFund(){
			DataManager dm = new DataManager(new WebClient("localhost", 3001) {

				@Override
				public String makeRequest(String resource, Map<String, Object> queryParams) {
					return null;
				}

			});

			Donation donation = dm.makeDonation(null,
					"testContributor",
					"1",
					10);

		}

		@Test(expected = IllegalArgumentException.class)
		public void testNullContributorName(){
			DataManager dm = new DataManager(new WebClient("localhost", 3001) {

				@Override
				public String makeRequest(String resource, Map<String, Object> queryParams) {
					return null;
				}

			});

			Donation donation = dm.makeDonation(new Fund("0", "testFund","testDescription",10),
					null,
					"1",
					10);

		}

		@Test(expected = IllegalArgumentException.class)
		public void testNullContributorId(){
			DataManager dm = new DataManager(new WebClient("localhost", 3001) {

				@Override
				public String makeRequest(String resource, Map<String, Object> queryParams) {
					return null;
				}

			});

			Donation donation = dm.makeDonation(new Fund("0", "testFund","testDescription",10),
					"testContributor",
					null,
					10);

		}

		@Test(expected = IllegalArgumentException.class)
		public void testIllegalAmount(){
			DataManager dm = new DataManager(new WebClient("localhost", 3001) {

				@Override
				public String makeRequest(String resource, Map<String, Object> queryParams) {
					return null;
				}

			});

			Donation donation = dm.makeDonation(new Fund("0", "testFund","testDescription",10),
					"testContributor",
					"1",
					-10);

		}

		@Test(expected = IllegalStateException.class)
		public void testClientNull(){
			DataManager dm = new DataManager(null);

			Donation donation = dm.makeDonation(new Fund("0", "testFund","testDescription",10),
					"testContributor",
					"1",
					10);

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

			Donation donation = dm.makeDonation(new Fund("0", "testFund","testDescription",10),
					"testContributor",
					"1",
					10);

		}
}
