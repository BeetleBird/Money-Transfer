package com.techelevator.tenmo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Balance;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.TransferService;
import com.techelevator.view.ConsoleService;

public class App {

	private static final String API_BASE_URL = "http://localhost:8080/";

	private static final String MENU_OPTION_EXIT = "Exit";
	private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN,
			MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS,
			MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS,
			MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };

	private AuthenticatedUser currentUser;
	private ConsoleService console;
	private AuthenticationService authenticationService;
	private TransferService transferService;

	public static void main(String[] args) {
		App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL),
				new TransferService(API_BASE_URL));
		app.run();
	}

	public App(ConsoleService console, AuthenticationService authenticationService, TransferService transferService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.transferService = transferService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");

		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while (true) {
			String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		Balance balance = transferService.getBalance();
		System.out.println("Your current account balance is: $" + balance.getBalance());

	}

	private void viewTransferHistory() {
		Transfer[] transfers = transferService.listTransfersById();

		List<Integer> list = printTransfers(transfers);

		System.out.println("Please enter transfer ID to view details (0 to cancel): ");
		Scanner scanner = new Scanner(System.in);
		String inputString = scanner.nextLine();

		try {
			int userInput = Integer.parseInt(inputString);
			if (userInput == 0) {
				return;
			}
			if (list.contains(userInput)) {
				for (Transfer transfer : transfers) {
					if (transfer.getTransferID() == userInput) {
						transferDetails(transfer);
						return;
					}
				}
			} else {
				System.out.println("Invalid Id Try Again ");
			}
		} catch (NumberFormatException ex) {

		}

	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub

	}

	private void sendBucks() {
		User[] users = transferService.listAllUsers();
		List<Integer> ids = printUsers(users);
		System.out.println();
		System.out.print("Enter ID of user you are sending to (0 to cancel): ");
		Scanner scanner = new Scanner(System.in);
		String inputString = scanner.nextLine();
		System.out.println();

		try {
			int receiverID = Integer.parseInt(inputString);
			if (receiverID == 0) {
				return;
			}
			if (ids.contains(receiverID)) {

				System.out.print("Enter amount: ");
				inputString = scanner.nextLine();
				System.out.println();
				double amount = Double.parseDouble(inputString);

				if (transferService.getBalance().getBalance() >= amount) {

					Transfer transfer = new Transfer();
					transfer.setAccountFrom(currentUser.getUser().getId());
					transfer.setAccountTo(receiverID);
					transfer.setAmount(amount);
					transfer.setTransferStatusID(2);
					transfer.setTransferTypeID(2);

					transferService.addTransfer(transfer);

				} else {
					System.out.println("You don't have enough money to send this amount");
				}

			} else {
				System.out.println("This is not a valid ID");
			}

		} catch (NumberFormatException ex) {
			System.out.println("Please enter a number");
		}
	}

	private void requestBucks() {
		// TODO Auto-generated method stub

	}

	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while (!isAuthenticated()) {
			String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
		while (!isRegistered) // will keep looping until user is registered
		{
			UserCredentials credentials = collectUserCredentials();
			try {
				authenticationService.register(credentials);
				isRegistered = true;
				System.out.println("Registration successful. You can now login.");
			} catch (AuthenticationServiceException e) {
				System.out.println("REGISTRATION ERROR: " + e.getMessage());
				System.out.println("Please attempt to register again.");
			}
		}
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) // will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
			try {
				currentUser = authenticationService.login(credentials);
				String token = currentUser.getToken();
				TransferService.AUTH_TOKEN = token;
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: " + e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}

	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}

	public List<Integer> printUsers(User[] userArray) {
		// List to keep track of valid User ID's
		List<Integer> ids = new ArrayList<Integer>();

		if (userArray != null) {

			System.out.println("--------------------------------------------");
			System.out.println("Users");
			System.out.println("ID         Name");
			System.out.println("--------------------------------------------");
			for (User userArr : userArray) {
				if (userArr.getId() != currentUser.getUser().getId()) {
					System.out.println(userArr.toString());
					ids.add(userArr.getId());
				}
			}
		}

		return ids;
	}

	public List<Integer> printTransfers(Transfer[] transferArray) {
		// List to keep track of valid Transfer ID's
		List<Integer> ids = new ArrayList<Integer>();

		if (transferArray != null) {

			System.out.println("--------------------------------------------");
			System.out.println("Transfers");
			System.out.println("ID          From/To                 Amount");
			System.out.println("--------------------------------------------");
			for (Transfer transfer : transferArray) {
				String fromTo;
				String name;
				if (currentUser.getUser().getId() == transfer.getAccountFrom()) {
					fromTo = "To:   ";
					name = transfer.getUsernameTo();
				} else {
					fromTo = "From: ";
					name = transfer.getUsernameFrom();
				}
				System.out.println(transfer.getTransferID() + "          " + fromTo + name + "                 "
						+ transfer.getAmount());

				ids.add(transfer.getTransferID());
			}
		}

		return ids;
	}

	public void transferDetails(Transfer transfer) {
		System.out.println("--------------------------");
		System.out.println("Transfer Details");
		System.out.println("--------------------------");
		System.out.println("Id: " + transfer.getTransferID());
		System.out.println("From: " + transfer.getUsernameFrom());
		System.out.println("To: " + transfer.getUsernameTo());
		System.out.println("Type: Send");
		System.out.println("Status: Approved");
		System.out.println("Amount: $" + transfer.getAmount());

	}

}
