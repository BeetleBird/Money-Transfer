package com.techelevator.tenmo.dao;

import java.util.List;

import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;

public interface TransferDAO {
	
	public Balance getBalance(int userId);
	
	public Transfer makeTransfer(Transfer newTransfer);
	
	public List<Transfer> getTransfers();

	public void addAmount(int userId, double amount); 
	
	public void subtractAmount(int userId, double amount);
	
	public List<Transfer> getTransfersById(int userId);
	
}
