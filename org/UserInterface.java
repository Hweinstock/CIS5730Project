
import java.util.List;
import java.util.Scanner;
// GIT
public class UserInterface {
	
	
	private DataManager dataManager;
	private Organization org;
	private Scanner in = new Scanner(System.in);
	
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
			}
			System.out.println("Enter 0 to create a new fund");
			int numFunds = org.getFunds().size();
			int option;
			while(true) {
				try {
					option = in.nextInt();
					in.nextLine();
					if(0 <= option && option <= numFunds) { // to check if input fund number is valid
						break;
					}
					System.out.println("There are only " + numFunds + " funds in the organization. \n "
							+ "So please enter a fund number between 1 and " + numFunds + " to see more information about the fund.");
				}
				catch(Exception e) {
					in.next(); // to advance to the next token
					System.out.println("Please enter a valid integer between 0 and " + numFunds);
				}
			}
			if (option == 0) {
				createFund(); 
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
		
		Fund fund = dataManager.createFund(org.getId(), name, description, target);
		org.getFunds().add(fund);

		
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
	
	
	public static void main(String[] args) {
		
		DataManager ds = new DataManager(new WebClient("localhost", 3001));
		
		String login = args[0];
		String password = args[1];
		
		try {
			Organization org = ds.attemptLogin(login, password);
			
			if (org == null) {
				System.out.println("Login failed. Incorrect username or password.");
			}
			else {

				UserInterface ui = new UserInterface(ds, org);
			
				ui.start();
			
			}
		} catch (Exception e) {
			System.out.println("Error in communicating with server.");
		}
		
	}

}
