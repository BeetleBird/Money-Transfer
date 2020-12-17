package com.techelevator.tenmo.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;

@Component
public class TransferSqlDAO implements TransferDAO {

	private JdbcTemplate jdbcTemplate;

	public TransferSqlDAO(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Balance getBalance(int userId) {

		String sql = "SELECT balance FROM accounts WHERE user_id = ?";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);

		Balance userBalance = new Balance();

		if (results.next()) {
			userBalance.setBalance(results.getDouble("balance"));
		}

		return userBalance;
	}

	@Override
	public Transfer makeTransfer(Transfer newTransfer) {
		int userId = newTransfer.getAccountFrom();
		double amount = newTransfer.getAmount();

		if (getBalance(userId).getBalance() < amount) {
			return null;
		}

		String sql = "insert into transfers(transfer_type_id, transfer_status_id, account_from, account_to, amount) "
				+ "values(?, ?, ?, ?, ?)";

		jdbcTemplate.update(sql, newTransfer.getTransferTypeID(), newTransfer.getTransferStatusID(),
				newTransfer.getAccountFrom(), newTransfer.getAccountTo(), newTransfer.getAmount());
		addAmount(newTransfer.getAccountTo(), newTransfer.getAmount());
		subtractAmount(newTransfer.getAccountFrom(), newTransfer.getAmount());
		return newTransfer;

	}

	@Override
	public List<Transfer> getTransfers() {
		List<Transfer> transfers = new ArrayList<>();
		String sql = "select * from transfers";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while (results.next()) {
			Transfer transfer = mapRowToTransfer(results);
			transfers.add(transfer);
		}

		return transfers;
	}

	private Transfer mapRowToTransfer(SqlRowSet rs) {
		Transfer transfer = new Transfer();

		transfer.setTransferID(rs.getInt("transfer_id"));
		transfer.setTransferTypeID(rs.getInt("transfer_type_id"));
		transfer.setTransferStatusID(rs.getInt("transfer_status_id"));
		transfer.setAccountFrom(rs.getInt("account_from"));
		transfer.setAccountTo(rs.getInt("account_to"));
		transfer.setAmount(rs.getDouble("amount"));

		return transfer;
	}

	@Override
	public void addAmount(int userId, double amount) {

		String sql = "UPDATE accounts SET balance = ?  WHERE user_id = ?";
		double currentAmount = getBalance(userId).getBalance();
		currentAmount += amount;
		jdbcTemplate.update(sql, currentAmount, userId);
	}

	@Override
	public void subtractAmount(int userId, double amount) {
		String sql = "UPDATE accounts SET balance = ? WHERE user_id = ?";
		double currentAmount = getBalance(userId).getBalance();
		currentAmount -= amount;
		jdbcTemplate.update(sql, currentAmount, userId);
	}

	@Override
	public List<Transfer> getTransfersById(int userId) {
		List<Transfer> transfers = new ArrayList<>();
		String sql = "select * from transfers WHERE account_from = ? OR account_to = ?";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
		while (results.next()) {
			Transfer transfer = mapRowToTransfer(results);
			transfers.add(transfer);
		}

		return transfers;

	}

}
