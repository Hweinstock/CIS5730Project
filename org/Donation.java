import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Donation implements Comparable<Donation> {
	
	private String fundId;
	private String contributorName;
	private long amount;
	private String date;
	
	public Donation(String fundId, String contributorName, long amount, String date) {
		this.fundId = fundId;
		this.contributorName = contributorName;
		this.amount = amount;
		this.date = date;
	}

	public String getFundId() {
		return fundId;
	}

	public String getContributorName() {
		return contributorName;
	}

	public long getAmount() {
		return amount;
	}

	public String getDate() {
		String[] split = date.split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2].split("T")[0];
		return monthNameFromNumber(month) + " " + day + ", " + year;
	}
	
	private String monthNameFromNumber(String monthNum) {
		return switch (monthNum) {
			case "01" -> "January";
			case "02" -> "February";
			case "03" -> "March";
			case "04" -> "April";
			case "05" -> "May";
			case "06" -> "June";
			case "07" -> "July";
			case "08" -> "August";
			case "09" -> "September";
			case "10" -> "October";
			case "11" -> "November";
			case "12" -> "December";
			default -> monthNum;
		};
	}

	@Override
	public int compareTo(Donation donation) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			return -format.parse(this.date).compareTo(format.parse(donation.date));
		} catch (ParseException e) {
			return 0;
		}
	}
}
