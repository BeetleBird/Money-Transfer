package com.techelevator.tenmo.model;

public class Transfer {
	
	private int transferID;
	private int transferTypeID;
	private int transferStatusID;
	private int accountFrom;
	private int accountTo;
	private double amount;
	
	public Transfer() {}
	
	public Transfer(int transferID, int transferTypeID, int transferStatusID, int accountFrom, int accountTo,
			double amount) {

		this.transferID = transferID;
		this.transferTypeID = transferTypeID;
		this.transferStatusID = transferStatusID;
		this.accountFrom = accountFrom;
		this.accountTo = accountTo;
		this.amount = amount;
	}
	public int getTransferID() {
		return transferID;
	}
	public void setTransferID(int transferID) {
		this.transferID = transferID;
	}
	public int getTransferTypeID() {
		return transferTypeID;
	}
	public void setTransferTypeID(int transferTypeID) {
		this.transferTypeID = transferTypeID;
	}
	public int getTransferStatusID() {
		return transferStatusID;
	}
	public void setTransferStatusID(int transferStatusID) {
		this.transferStatusID = transferStatusID;
	}
	public int getAccountFrom() {
		return accountFrom;
	}
	public void setAccountFrom(int accountFrom) {
		this.accountFrom = accountFrom;
	}
	public int getAccountTo() {
		return accountTo;
	}
	public void setAccountTo(int accountTo) {
		this.accountTo = accountTo;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	
}
