import java.util.*;
import org.json.simple.JSONObject;

public class TestUtilities {
    public static JSONObject getExampleDonation(){
		JSONObject donation = new JSONObject();
		donation.put("contributor", "testContributorId");
		donation.put("amount", Long.valueOf(100));
		donation.put("date", "11-06-2010");
		return donation;
	}
	public static List<JSONObject> getExampleDonations(){
		List<JSONObject> donations = new LinkedList<JSONObject>();
		JSONObject donation = getExampleDonation();
		donations.add(donation);
		return donations;
	}

	public static List<JSONObject> getExampleFunds(){
		List<JSONObject> funds = new LinkedList<JSONObject>();
		JSONObject fund = new JSONObject();
		fund.put("_id", "testFundId");
		fund.put("name", "testFundName");
		fund.put("description", "testFundDescription");
		fund.put("target", Long.valueOf(1000));	

		List<JSONObject> donations = getExampleDonations();
		fund.put("donations", donations);
		funds.add(fund);
		return funds;
	}
}
