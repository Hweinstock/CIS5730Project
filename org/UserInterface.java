
import java.util.*;

public class UserInterface {
	
	
	private DataManager dataManager;
	private Organization org;
	private static Scanner in = new Scanner(System.in);
	private static Map<Integer, List<String>> aggregateContriMap = new HashMap<Integer, List<String>>();

	private enum WelcomeOption {
		LOGIN, 
		CREATE_ACCOUNT, 
		EXIT
	}
	
	public UserInterface(DataManager dataManager, Organization org) {
		this.dataManager = dataManager;
		this.org = org;
	} 
	
	public void start(Credentials credentials) { 
				
		while (true) {
			System.out.println("\n\n");
			System.out.println("-------------------");
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
			System.out.println("Enter 'account' to change Organization account details");
			System.out.println("Enter 'password' to change the current password");
			System.out.println("-------------------");

			int numFunds = org.getFunds().size();
			int option;
			
			String userInput = in.nextLine().trim();

			if (userInput.equals("logout")) {
				String[] strArr = new String[0];
				main(strArr);
				return;
			} else if (userInput.equals("account")) {
				System.out.println("You chose to change account info.");
				Organization newOrg = changeAccountInfo(org.getId(), org.getName(), org.getDescription(), credentials);
				if(newOrg != null){
					org = newOrg; 
				}
				start(credentials);
				return;
			} else if (userInput.equals("password")) {
				System.out.println("You chose to change the password.");
				Organization passwordUpdatedOrg = changePassword(org.getId(), credentials);
				if(passwordUpdatedOrg != null) {
					org = passwordUpdatedOrg;
				}
				start(credentials); // credentials contains the updated password
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

	public String promptForPassword(String dispMsg) {
		try {
//			System.out.print(dispMsg);
			while(true) {
				System.out.println(dispMsg);
				String passwordEntered = in.nextLine().trim();
				if(!passwordEntered.equals("")) {
					return passwordEntered;
				}
				System.out.println("The password field cannot be left blank. Please enter a value.\n");
			}
//			String passwordEntered = in.nextLine().trim();
//			return passwordEntered;
		} catch(Exception e) {
			System.out.println("Please enter a valid value for the password.");
			return null;
		}
		
	}

	public Organization changeAccountInfo(String orgId, String originalName, String originalDescription, Credentials credentials) {

		System.out.println("-------------------");
		System.out.println("Your current account information:");
		System.out.println("\t login: " + credentials.login);
		System.out.println("\t name: " + originalName);
		System.out.println("\t description: " + originalDescription);
		System.out.println("-------------------");

		String passwordEntered;
		Organization newOrg;
		while(true) {
			passwordEntered = promptForPassword("Please re-enter your password: ");
			if(passwordEntered != null){
				break;
			}
		}
		

		if(!credentials.isSamePassword(passwordEntered)){
			System.out.println("Password entered incorrectly, returning to main menu.");
			return null;
		} else {
			System.out.println("Successfully Authenticated:");
			
			System.out.println("Please enter the new name of the organization or leave blank to keep current name.");
			String newName = in.nextLine().trim();

			System.out.println("Please enter the new description of the organization or leave blank to keep current description.");
			String newDescription = in.nextLine().trim();
			
			if(newName.equals("")){
				newName = originalName;
			}

			if(newDescription.equals("")){
				newDescription = originalDescription;
			}

			if(newName == originalName && newDescription == originalDescription){
				System.out.println("No changes made, returning back to main menu.");
				return null;
			}

			try {
				newOrg = dataManager.updateOrg(orgId, newName, newDescription, credentials);
			}
			catch (Exception e) {
//				e.printStackTrace();
				System.out.println("Error: Unable to update org information. Returning to main menu");
				return null;
			}

			return newOrg;

		}
		
	}
	
	public Organization changePassword(String orgId, Credentials creds) {
		
		String currPasswordEntered;
		Organization updatedOrg;
		
		while(true) {
			currPasswordEntered = promptForPassword("Please enter your current password for authentication: ");
			if(currPasswordEntered != null) {
				break;
			}
		}
		
		if(currPasswordEntered.equals(creds.password)) {
			String newPasswordEnteredOnce;
			String newPasswordEnteredTwice;
			
//			while(true) {
//				newPasswordEnteredOnce = promptForPassword("Please enter your new password: ");
//				if(newPasswordEnteredOnce == null) {
//					return null; // if prompt throws exception
//				}
//				if(!newPasswordEnteredOnce.equals("")) {
//					break;
//				}
//				System.out.println("The password cannot be empty. Please try again.\n");
//			}
			newPasswordEnteredOnce = promptForPassword("Please enter your new password: ");
			if(newPasswordEnteredOnce == null) {
				return null; // if prompt throws exception
			}
			
//			while(true) {
//				newPasswordEnteredTwice = promptForPassword("Please re-confirm your new password: ");
//				if(newPasswordEnteredTwice == null) {
//					return null; // if prompt throws exception
//				}
//				if(!newPasswordEnteredTwice.equals("")) {
//					break;
//				}
//				System.out.println("The password confirmation cannot be empty. Please try again.\n");
//			}
			newPasswordEnteredTwice = promptForPassword("Please re-confirm your new password: ");
			if(newPasswordEnteredTwice == null) {
				return null; // if prompt throws exception
			}
			if(newPasswordEnteredOnce.equals(newPasswordEnteredTwice)) {
				creds.setPassword(newPasswordEnteredOnce);
				try {
					updatedOrg = dataManager.updateOrg(orgId, org.getName(), org.getDescription(), creds);
					System.out.println("Password updated successfully!");
					return updatedOrg; 
				}
				catch(Exception e) {
//					e.printStackTrace();
					System.out.println("Error: Unable to update the password due to a server error. Please re-attempt to change the password");
					creds.setPassword(currPasswordEntered); // reverting back to the original password
					changePassword(orgId, creds);
				}
			}
			else {
				System.out.println("The two passwords don't match. Returning to the main menu...");
				return null;
			}
		}
		else {
			System.out.println("Incorrect password entered. Returning to the main menu...");
			return null;
		}
		return null;
	}
	
	public void createFund() {
		
		String name;
		String description;
		
		while(true) {
			System.out.print("Enter the fund name: ");
			name = in.nextLine().trim();
			
			if(name != null && !name.equals("")) {
				break;
			}
			System.out.println("The fund name should not be null or empty.\n");
			
		}
		
		while(true) {
			System.out.print("Enter the fund description: ");
			description = in.nextLine().trim();
			
			if(description != null && !description.equals("")) {
				break;
			}
			System.out.println("The fund description should not be null or empty.\n");
			
		}
		
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
	
	public static WelcomeOption welcomeUser() {
		System.out.println("-------------------");
		System.out.println("Welcome to our app!");
		System.out.println("Please enter 0 to login, 1 to create a new account, and 2 to exit.");
		System.out.println("-------------------");
		String userInput;
		int optionChosen;
		while(true) {
			try {
				userInput = in.nextLine().trim();
				optionChosen = Integer.parseInt(userInput); 
				return WelcomeOption.values()[optionChosen];
			}
			catch(Exception e) {
				System.out.println("Please enter a valid option: 0, 1, or 2.");
			}
		}
	}

	
	public static Credentials promptForLogin(){
		try {
			System.out.print("Please enter the username: ");
			String login = in.nextLine().trim();

			System.out.print("Please enter the password: ");
			String password = in.nextLine().trim();
			return new Credentials(login, password);
		}
		catch(Exception e) {
			System.out.println("Please enter a valid value for the username and password.");
		}
		return null;
	}

	public static List<String> promptForNewOrg(){
		List<String> org = new ArrayList<String>();
		try {
			System.out.print("Please enter the organization name: ");
			String name = in.nextLine().trim();

			System.out.print("Please enter the organization description: ");
			String description = in.nextLine().trim();

			org.add(name);
			org.add(description);
			return org;
		}
		catch(Exception e) {
			System.out.println("Please enter a valid value for the new organization name and description.");
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

	private static Credentials createAccount(DataManager ds) {
		System.out.println("-------------------");
		System.out.println("\n Creating new organization:");
		System.out.println("-------------------");
		while(true) {
			try {
				List<String> orgInfo = promptForNewOrg();
				Credentials credentials = promptForLogin();
				DataManager.OrgCreationStatus status = ds.createOrg(credentials.login, credentials.password, orgInfo.get(0), orgInfo.get(1));
				
				switch(status){
					case CREATED:
						System.out.println("Your account has been created, you will now be logged in!");
						return credentials;
					case DUPLICATE:
						System.out.println("The username you entered already exists, please try a different one.");
						break;
					case INVALID:
						System.out.println("The information you entered is invalid. Please try again and make sure no entries are blank.");
						break;
					case SERVER_ERROR:
						System.out.println("Unable to communicate with server, please try again. ");
						break;
					default:
						throw new IllegalStateException("Unknown DataManager.OrgCreationStatus seen: " + status);
				}
			} catch (Exception e) {
				System.out.println("An error occurred in creating the new organization, please try again.");

			}
		}
	}

	public static void main(String[] args) {
		DataManager ds = new DataManager(new WebClient("localhost", 3001));
		
		WelcomeOption optionSelected = welcomeUser();
		Credentials credentials;
	
		switch ( optionSelected ) {
			case LOGIN:
				System.out.println("Login selected");
				credentials = promptForLogin();
				break;
			case CREATE_ACCOUNT:
				credentials = createAccount(ds);
				break;
			case EXIT:
				System.out.println("Exit selected");
				return;
			default:
				System.out.println("An unknown error occurred, please restart the application.");
				return;
		}
		
		try {
			Organization org = ds.attemptLogin(credentials.login, credentials.password);
			
			if (org == null) {
				System.out.println("Login failed. Incorrect username or password.");
			}
			else {

				UserInterface ui = new UserInterface(ds, org);
			
				ui.start(credentials);
			
			}
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("Error in communicating with server.");
		}
		
	}

}
