package com.techelevator.tenmo.dao;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.model.Balance;

@Component
public class TransferSqlDAO implements TransferDAO{
	
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

}
