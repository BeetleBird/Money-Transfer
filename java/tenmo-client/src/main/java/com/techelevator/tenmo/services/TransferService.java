package com.techelevator.tenmo.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.Balance;

public class TransferService {
	
	public static String AUTH_TOKEN = "";
	private String BASE_URL;
	private RestTemplate restTemplate = new RestTemplate();
	
	public TransferService(String url) {
		this.BASE_URL = url;
	}
	
	public Balance getBalance() {
		Balance balance = new Balance();
		
		try {
			balance = restTemplate.exchange(BASE_URL + "/get-balance", HttpMethod.GET, 
					makeAuthEntity(), Balance.class).getBody();
			
		} catch(RestClientResponseException ex) {
			System.out.println(ex.getRawStatusCode() + " " + ex.getStatusText());
		} catch (ResourceAccessException ex) {
			System.out.println(ex.getMessage());
		}
		
		
		return balance;
	}
	
	private HttpEntity makeAuthEntity() {
		  HttpHeaders headers = new HttpHeaders();
		    headers.setBearerAuth(AUTH_TOKEN);
		    HttpEntity entity = new HttpEntity<>(headers);
		    return entity;
	  }

}
