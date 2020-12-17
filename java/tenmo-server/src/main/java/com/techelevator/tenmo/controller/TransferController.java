package com.techelevator.tenmo.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {
	
	@Autowired
	TransferDAO transferDAO;
	
	@Autowired
	UserDAO userDao;
	
	@RequestMapping(path="/get-balance", method=RequestMethod.GET)
	public Balance getBalance(Principal principal) {
		
		System.out.println("The following person is requesting a balance: " + principal.getName());
		
		int id = userDao.findIdByUsername(principal.getName());
		
		Balance balace = transferDAO.getBalance(id);
		
		return balace;
		
	}

	@RequestMapping(path="/get-users", method=RequestMethod.GET)
	public List<User> listAllUsers() {
		return userDao.findAll();
		
	}
	
	@RequestMapping(path="/get-transfers", method=RequestMethod.GET)
	public List<Transfer> getTransfers() {
		return transferDAO.getTransfers();
		
	}
	
	@ResponseStatus(HttpStatus.CREATED)
    @RequestMapping( path = "/transfer", method = RequestMethod.POST)
	public Transfer makeTransfer(@RequestBody Transfer transfer) {
		return transferDAO.makeTransfer(transfer);
	}
	
	
}
