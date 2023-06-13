
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
//GIT
public class DataManager {

	private final WebClient client;
	// proctected so that we man manually test/inspect in tests. 
	protected Map<String, String> contributorNameCache = new HashMap<String, String>();
	public DataManager(WebClient client) {
		this.client = client;
	}

	/**
	 * Attempt to log the user into an Organization account using the login and password.
	 * This method uses the /findOrgByLoginAndPassword endpoint in the API
	 * @return an Organization object if successful; null if unsuccessful
	 */
	public Organization attemptLogin(String login, String password) {

		try {
			Map<String, Object> map = new HashMap<>();
			map.put("login", login);
			map.put("password", password);
			String response = client.makeRequest("/findOrgByLoginAndPassword", map);

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String status = (String)json.get("status"); 
			
			if (status.equals("success")) {
				JSONObject data = (JSONObject)json.get("data");
				String fundId = (String)data.get("_id");
				String name = (String)data.get("name");
				String description = (String)data.get("description");
				Organization org = new Organization(fundId, name, description);

				JSONArray funds = (JSONArray)data.get("funds");
				Iterator it = funds.iterator();
				while(it.hasNext()){
					JSONObject fund = (JSONObject) it.next(); 
					fundId = (String)fund.get("_id");
					name = (String)fund.get("name");
					description = (String)fund.get("description");
					long target = (Long)fund.get("target");

					Fund newFund = new Fund(fundId, name, description, target);

					JSONArray donations = (JSONArray)fund.get("donations");
					List<Donation> donationList = new LinkedList<>();
					Iterator it2 = donations.iterator();
					while(it2.hasNext()){
						JSONObject donation = (JSONObject) it2.next();
						String contributorId = (String)donation.get("contributor");
						String contributorName = this.getContributorName(contributorId);
						long amount = (Long)donation.get("amount");
						String date = (String)donation.get("date");
						donationList.add(new Donation(fundId, contributorName, amount, date));
					}

					newFund.setDonations(donationList);

					org.addFund(newFund);

				}

				return org;
			}
			else if(status.equals("login failed")){
				return null;
			}
			else {
				throw new IllegalStateException("An error has occurred while communicating with the server.");
			}
		}
		catch (Exception e) {
//			e.printStackTrace(); // assuming we don't have to print this since we're displaying the error message in UserInterface.main 
			throw new IllegalStateException("An error has occurred while communicating with the server.");
		}
	}

	/**
	 * Look up the name of the contributor with the specified ID.
	 * This method uses the /findContributorNameById endpoint in the API.
	 * @return the name of the contributor on success; null if no contributor is found
	 */
	public String getContributorName(String id) {
		if(this.contributorNameCache.containsKey(id)){
			return this.contributorNameCache.get(id);
		}
		try {

			Map<String, Object> map = new HashMap<>();
			map.put("id", id);
			String response = client.makeRequest("/findContributorNameById", map);

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String status = (String)json.get("status");

			if (status.equals("success")) {
				String name = (String)json.get("data");
				this.contributorNameCache.put(id, name);
				return name;
			}
			else return null;


		}
		catch (Exception e) {
			return null;
		}	
	}

	/**
	 * This method creates a new fund in the database using the /createFund endpoint in the API
	 * @return a new Fund object if successful; null if unsuccessful
	 */
	public Fund createFund(String orgId, String name, String description, long target) {

		try {

			Map<String, Object> map = new HashMap<>();
			map.put("orgId", orgId);
			map.put("name", name);
			map.put("description", description);
			map.put("target", target);
			String response = client.makeRequest("/createFund", map);

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String status = (String)json.get("status");

			if (status.equals("success")) {
				JSONObject fund = (JSONObject)json.get("data");
				String fundId = (String)fund.get("_id");
				return new Fund(fundId, name, description, target);
			}
			else return null;

		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
	}


}
