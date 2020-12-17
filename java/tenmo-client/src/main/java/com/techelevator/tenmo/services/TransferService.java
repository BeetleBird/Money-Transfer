package com.techelevator.tenmo.services;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.Balance;
import com.techelevator.tenmo.models.Transfer;

public class TransferService {

	public static String AUTH_TOKEN = "";
	private String BASE_URL;
	private RestTemplate restTemplate = new RestTemplate();

	public TransferService(String url) {
		this.BASE_URL = url;
	}

	public User[] listAllUsers() {
		User[] usersArray = null;
		try {
			usersArray = restTemplate.exchange(BASE_URL + "/get-users", HttpMethod.GET, makeAuthEntity(), User[].class)
					.getBody();
		} catch (RestClientResponseException ex) {
			System.out.println(ex.getRawStatusCode() + " " + ex.getStatusText());
		} catch (ResourceAccessException ex) {
			System.out.println(ex.getMessage());
		}
		return usersArray;

	}

	public Balance getBalance() {
		Balance balance = new Balance();

		try {
			balance = restTemplate.exchange(BASE_URL + "/get-balance", HttpMethod.GET, makeAuthEntity(), Balance.class)
					.getBody();

		} catch (RestClientResponseException ex) {
			System.out.println(ex.getRawStatusCode() + " " + ex.getStatusText());
		} catch (ResourceAccessException ex) {
			System.out.println(ex.getMessage());
		}

		return balance;
	}
	
	public Transfer addTransfer(Transfer transfer) {
		
		try {
			transfer = restTemplate.exchange(BASE_URL + "/transfer", HttpMethod.POST, makeTransferEntity(transfer),
					Transfer.class).getBody();
			
			System.out.println("Transfer Completed");

		} catch (RestClientResponseException ex) {
			System.out.println(ex.getRawStatusCode() + " " + ex.getStatusText());
		} catch (ResourceAccessException ex) {
			System.out.println(ex.getMessage());
		}
		
		return transfer;
	}
	
	private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.setBearerAuth(AUTH_TOKEN);
	    HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
	    return entity;
	  }

	private HttpEntity makeAuthEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(AUTH_TOKEN);
		HttpEntity entity = new HttpEntity<>(headers);
		return entity;
	}

}
