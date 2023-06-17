
import java.util.*;

// GIT
public class UserInterface {
	
	
	private DataManager dataManager;
	private Organization org;
	private static Scanner in = new Scanner(System.in);
	private static Map<Integer, List<String>> aggregateContriMap = new HashMap<Integer, List<String>>();
	
	public UserInterface(DataManager dataManager, Organization org) {
		this.dataManager = dataManager;
		this.org = org;
	} 
	
	public void start() {
				
		while (true) {
			System.out.println("\n\n");
			if (org.getFunds().size() > 0) {
				System.out.println("There are " + org.getFunds().size() + " funds in this organization:");
			
				int count = 1;
				for (Fund f : org.getFunds()) {
					
					System.out.println(count + ": " + f.getName());
					
					count++;
				}
				System.out.println("Enter the fund number to see more information.");
				System.out.println("Enter " + count + " to see information for contributions to all funds.");
			}
			System.out.println("Enter 0 to create a new fund");
			System.out.println("Enter 'logout' to log out of the app");
			int numFunds = org.getFunds().size();
			int option;
			String userInput = in.nextLine().trim();

			if (userInput.equals("logout")) {
				String[] strArr = new String[0];
				main(strArr);
				return;
			}

			while(true) {
				try {
					option = Integer.parseInt(userInput);
					if(0 <= option && option <= numFunds+1) { // to check if input fund number is valid
						break;
					}
					System.out.println("There are only " + numFunds + " funds in the organization. \n "
							+ "So please enter a fund number between 1 and " + (numFunds+1) + " to see more information about the fund or all funds.");
					userInput = in.nextLine().trim();
				}
				catch(Exception e) {
//					in.next(); // to advance to the next token
					userInput = in.nextLine().trim();
					System.out.println("Please enter a valid integer between 0 and " + (numFunds+1));
				}
			}

			if (option == 0) {
				createFund(); 
			}
			else if (option == numFunds+1) {
				displayAllFunds();
			}
			else {
				displayFund(option);
			}
		}			
			
	}
	
	public void createFund() {
		
		System.out.print("Enter the fund name: ");
		String name = in.nextLine().trim();
		
		System.out.print("Enter the fund description: ");
		String description = in.nextLine().trim();

		long target;
		while(true) {
			try {
				System.out.print("Enter the fund target: ");
				target = in.nextInt();
				in.nextLine();
				if(target>0.0) { // not including 0 as a valid fund target. 
					break;
				}
				System.out.println("Fund target should be positive (greater than 0). Please enter a positive number.");
			}
			catch (Exception e) {
				in.next();
				System.out.println("Please enter a valid positive (greater than 0) number.");
			}
			
		}
		
		try {
			Fund fund = dataManager.createFund(org.getId(), name, description, target);
			org.getFunds().add(fund);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			createFund();
		}

		
	}
	
	
	public void displayFund(int fundNumber) {
		
		Fund fund = org.getFunds().get(fundNumber - 1);
		
		System.out.println("\n\n");
		System.out.println("Here is information about this fund:");
		System.out.println("Name: " + fund.getName());
		System.out.println("Description: " + fund.getDescription());
		System.out.println("Target: $" + fund.getTarget());

		long totalAmount = 0;
		List<Donation> donations = fund.getDonations();
		System.out.println("Number of donations: " + donations.size());

		if (donations.size() >= 1) {
			if(aggregateContriMap.containsKey(fundNumber)) {
				List<String> valueFromMap = aggregateContriMap.get(fundNumber);
				String displayStr = valueFromMap.get(0);
				System.out.println(displayStr);
			}
			else {
				displayContributions(donations, fundNumber);
				List<String> valueFromMap = aggregateContriMap.get(fundNumber);
				String displayStr = valueFromMap.get(0);
				System.out.println(displayStr);
			}
		}
		for (Donation donation : donations) {
			totalAmount += donation.getAmount();
			System.out.println("* " + donation.getContributorName() + ": $" + donation.getAmount() + " on " + donation.getDate());
		}
		double percent = ((double) totalAmount / fund.getTarget()) * 100;
		percent = (double) Math.round(percent * 100) / 100;
		System.out.println("Total donation amount: $" + totalAmount + " (" + percent + "% of target)");
	
		
		System.out.println("Press the Enter key to go back to the listing of funds");
		in.nextLine();
		
		
		
	}

	public void displayAllFunds() {
		List<Fund> funds = org.getFunds();
		List<Donation> donations = new LinkedList<>();
		Map<String, String> fundMap = new HashMap<>();

		for (Fund fund : funds) {
			fundMap.put(fund.getId(), fund.getName());
			donations.addAll(fund.getDonations());
		}

		Collections.sort(donations);

		System.out.println("\n\n");
		System.out.println("Here are the contributions to all funds:");
		for (Donation donation : donations) {
			System.out.println("* " + fundMap.get(donation.getFundId()) + ": $" + donation.getAmount() + " on " + donation.getDate());
		}

		System.out.println("Press the Enter key to go back to the listing of funds");
		in.nextLine();
	}
	
	
	public static List<String> userLogin(){
		List<String> loginCreds = new ArrayList<String>();
		try {
			System.out.print("Please enter the username: ");
			String username = in.nextLine().trim();

			System.out.print("Please enter the password: ");
			String password = in.nextLine().trim();
			loginCreds.add(username);
			loginCreds.add(password);
			return loginCreds;
		}
		catch(Exception e) {
			System.out.println("Please enter a valid value for the username and password.");
		}
		return null;
	}


	public static void displayContributions(List<Donation> donorList, int fundNumber) {
		long totAmount = 0;
		HashMap<String, Long> contrAmounts = new HashMap<String, Long>();
		HashMap<String, Integer> contrTimes = new HashMap<String, Integer>();
		List<String> mapValue = new ArrayList<String>();

		for(Donation donation : donorList) {
			String donorName = donation.getContributorName();
			Long donorAmount = donation.getAmount();
			totAmount += donorAmount;

			if(contrAmounts.containsKey(donorName)) {
				Long currAmount = contrAmounts.get(donorName);
				contrAmounts.put(donorName, currAmount+donorAmount);

				Integer numContributions = contrTimes.get(donorName);
				contrTimes.put(donorName, numContributions+1);

			}
			else {
				contrAmounts.put(donorName, donorAmount);
				contrTimes.put(donorName, 1);
			}
		}

		List<Map.Entry<String, Long>> contrEntries = new ArrayList<>(contrAmounts.entrySet());
		contrEntries.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

		String displayString = "\nAggregate Donations: \n";

		for (Map.Entry<String, Long> entry : contrEntries) {
			String donor = entry.getKey();
			Long amount = entry.getValue();
			Integer numContr = contrTimes.get(donor);

			displayString += "* " +  donor + " has made " + numContr + " donations totalling: $" + amount + "\n";
		}

		mapValue.add(displayString);
		mapValue.add(Long.toString(totAmount));

		aggregateContriMap.put(fundNumber, mapValue);

	}


	public static void main(String[] args) {
		DataManager ds = new DataManager(new WebClient("localhost", 3001));
		

		List<String> loginDetails = userLogin();
		String loginUser = loginDetails.get(0);
		String passwordUser = loginDetails.get(1);
		
		try {
			Organization org = ds.attemptLogin(loginUser, passwordUser);
			
			if (org == null) {
				System.out.println("Login failed. Incorrect username or password.");
			}
			else {

				UserInterface ui = new UserInterface(ds, org);
			
				ui.start();
			
			}
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("Error in communicating with server.");
		}
		
	}

}
