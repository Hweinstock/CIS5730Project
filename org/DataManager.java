
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

	public enum OrgCreationStatus {
		CREATED, 
		SERVER_ERROR, 
		DUPLICATE, 
		INVALID
	}

	private String nullWebClientErrorMsg = "WebClient is null";
	private String unableToParseRsp = "Unable to parse response from WebClient";
	private String failedConnectionErrorMsg = "Unable to connect with WebClient";
	private String webClientResponseErrorMsg = "Web Client responded with error";

	public OrgCreationStatus createOrg(String login, String password, String orgName, String orgDescription) {
		if(login == null || password == null || orgName == null || orgDescription == null){
			throw new IllegalArgumentException("Null Login or password attemped.");
		}

		if(login == "" || password == "" || orgName == "" || orgDescription == ""){
			return OrgCreationStatus.INVALID;
		}

		if(client == null){
			throw new IllegalStateException(nullWebClientErrorMsg);
		}

		if(this.doesLoginExist(login)){
			return OrgCreationStatus.DUPLICATE;
		}
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("login", login);
			map.put("password", password);
			map.put("name", orgName);
			map.put("description", orgDescription);
			String response = client.makeRequest("/createOrg", map);
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String status = (String) json.get("status");

			if (status.equals("success")){
				return OrgCreationStatus.CREATED;
			} else if(status.equals("failed")){
				return OrgCreationStatus.SERVER_ERROR;
			}
			else {
				throw new IllegalStateException(failedConnectionErrorMsg);
			}
		} 
		catch (Exception e) {
			return OrgCreationStatus.SERVER_ERROR;
		}
	}

	public Organization updateOrg(String orgId, String orgName, String orgDesc, Credentials credentials) {
		if(orgId == null || orgName == null || orgDesc == null || credentials == null || credentials.hasNullValue()) {
			throw new IllegalArgumentException("Null credentials, changes, or orgId.");
		}

		if(client == null){
			throw new IllegalStateException(nullWebClientErrorMsg);
		}

		try {
			Map<String, Object> map = new HashMap<>();
			map.put("id", orgId);
			map.put("login", credentials.login);
			map.put("password", credentials.password);
			map.put("name", orgName);
			map.put("description", orgDesc);

			String response = client.makeRequest("/updateOrg", map);
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String status = (String) json.get("status");

			if(status.equals("success")){
				JSONObject data = (JSONObject)json.get("data");
				Organization org = parseOrganizationFromJSON(data);
				return org;
			} else {
				throw new IllegalStateException(failedConnectionErrorMsg);
			}


		} catch (Exception e) {
			throw new IllegalStateException(failedConnectionErrorMsg);
		}
	}

	public boolean doesLoginExist(String login) {
		if(login == null) {
			throw new IllegalArgumentException("Null login attemped.");
		}

		if(client == null){
			throw new IllegalStateException(nullWebClientErrorMsg);
		}
		
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("login", login);
			String response = client.makeRequest("/doesLoginExist", map);
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String status = (String) json.get("status");
			if (status.equals("success")) {
				boolean result = (boolean) json.get("data");
				return result;
			} else {
				throw new IllegalStateException(failedConnectionErrorMsg);
			}

		} catch(Exception exception) {
			throw new IllegalStateException(failedConnectionErrorMsg);
		}
	}

	private Organization parseOrganizationFromJSON(JSONObject data){
		if(data == null){
			throw new IllegalArgumentException("Cannot parse null data.");
		}

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
	/**
	 * Attempt to log the user into an Organization account using the login and password.
	 * This method uses the /findOrgByLoginAndPassword endpoint in the API
	 * @return an Organization object if successful; null if unsuccessful
	 */
	public Organization attemptLogin(String login, String password) {

		if(login == null || password == null){
			throw new IllegalArgumentException("Null Login or password attemped.");
		}

		if(client == null){
			throw new IllegalStateException(nullWebClientErrorMsg);
		}

		try {
			Map<String, Object> map = new HashMap<>();
			map.put("login", login);
			map.put("password", password);
			String response = client.makeRequest("/findOrgByLoginAndPassword", map);

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String status = (String) json.get("status"); 
			
			if (status.equals("success")) {
				JSONObject data = (JSONObject)json.get("data");
				Organization org = this.parseOrganizationFromJSON(data);

				return org;
			}
			else if(status.equals("login failed")){
				return null;
			}
			else {
				throw new IllegalStateException(failedConnectionErrorMsg);
			}
		}
		catch (Exception e) {
//			e.printStackTrace(); // assuming we don't have to print this since we're displaying the error message in UserInterface.main 
			throw new IllegalStateException(failedConnectionErrorMsg);
		}
	}

	/**
	 * Look up the name of the contributor with the specified ID.
	 * This method uses the /findContributorNameById endpoint in the API.
	 * @return the name of the contributor on success; null if no contributor is found
	 */
	public String getContributorName(String id) {

		if(id == null){
			throw new IllegalArgumentException("Null id passed to getContributorName");
		}

		if(client == null){
			throw new IllegalStateException(nullWebClientErrorMsg);
		}

		if(this.contributorNameCache.containsKey(id)){
			return this.contributorNameCache.get(id);
		}
		try {

			Map<String, Object> map = new HashMap<>();
			map.put("id", id);
			String response = client.makeRequest("/findContributorNameById", map);

			if(response == null){
				throw new IllegalStateException(failedConnectionErrorMsg);
			}

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String status = (String)json.get("status");

			if (status.equals("success")) {
				String name = (String)json.get("data");
				this.contributorNameCache.put(id, name);
				return name;
			} else if (status.equals("error")) {
				throw new IllegalStateException(webClientResponseErrorMsg);
			}
			else return null;

		}
		catch (IllegalStateException e) {
			throw e;
		}
		catch (Exception e) {
			throw new IllegalStateException(unableToParseRsp);
		}	
	}

	/**
	 * This method creates a new fund in the database using the /createFund endpoint in the API
	 * @return a new Fund object if successful; null if unsuccessful
	 */
	public Fund createFund(String orgId, String name, String description, long target) {

		if(orgId == null || name == null || description == null)
		{
			throw new IllegalArgumentException("One of orgId, name, and description are null!");
		}
		
		if(client == null){
			throw new IllegalStateException(nullWebClientErrorMsg);
		}

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
			} else if (status.equals("error")){
				throw new IllegalStateException("webClientResponseErrorMsg");
			}
			else return null;

		}
		catch (Exception e) {
			throw new IllegalStateException(unableToParseRsp);
		}	
	}


}
