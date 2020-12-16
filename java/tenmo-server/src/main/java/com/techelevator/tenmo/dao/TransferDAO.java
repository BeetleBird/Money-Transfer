package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Balance;

public interface TransferDAO {
	
	public Balance getBalance(int userId);

}
