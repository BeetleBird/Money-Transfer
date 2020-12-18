package com.techelevator.tenmo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.TransferSqlDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.dao.UserSqlDAO;
import com.techelevator.tenmo.model.Transfer;

class TransferDAOIntegrationTest {
	
	private static SingleConnectionDataSource dataSource;
	private TransferDAO transferDAO;
	private UserDAO userDao;
	
	private static int TEST_ID1;
	private static int TEST_ID2;
		

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/tenmo");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		dataSource.setAutoCommit(false);

	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		dataSource.destroy();
	}

	@BeforeEach
	void setUp() throws Exception {
		
		transferDAO = new TransferSqlDAO(dataSource);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		userDao = new UserSqlDAO(jdbcTemplate);
		
		userDao.create("testUser1", "password");
		TEST_ID1 = userDao.findIdByUsername("testUser1");
		userDao.create("testUser2", "password");
		TEST_ID2 = userDao.findIdByUsername("testUser2");
	}

	@AfterEach
	void tearDown() throws Exception {
		dataSource.getConnection().rollback();
	}

	@Test
	void test_user_has_a_balance_of_1000() {
		
		double actualResult = transferDAO.getBalance(TEST_ID1).getBalance();
		assertEquals(1000, actualResult, 0);
	}
	
	@Test
	void make_transfer_increases_size_of_transfer_list() {
		
		int expectedSize = transferDAO.getTransfers().size() + 1;
		Transfer transfer = new Transfer();
		transfer.setAccountFrom(TEST_ID1);
		transfer.setAccountTo(TEST_ID2);
		transfer.setAmount(100);
		transfer.setTransferStatusID(2);
		transfer.setTransferTypeID(2);
		
		transferDAO.makeTransfer(transfer);
		int actualSize = transferDAO.getTransfers().size();
		
		assertEquals(expectedSize, actualSize);
		
	}
	
	@Test
	void user_cannot_send_more_money_than_they_have() {
		Transfer transfer = new Transfer();
		transfer.setAccountFrom(TEST_ID1);
		transfer.setAccountTo(TEST_ID2);
		transfer.setAmount(100000000);
		transfer.setTransferStatusID(2);
		transfer.setTransferTypeID(2);
		
		transfer = transferDAO.makeTransfer(transfer);
		
		assertNull(transfer);
	}
	
	@Test
	void transfer_saves_new_balances_to_users() {
		Transfer transfer = new Transfer();
		transfer.setAccountFrom(TEST_ID1);
		transfer.setAccountTo(TEST_ID2);
		transfer.setAmount(200);
		transfer.setTransferStatusID(2);
		transfer.setTransferTypeID(2);
		
		transferDAO.makeTransfer(transfer);
		
		double expectedAmountUser1 = 800;
		double actualAmountUser1 = transferDAO.getBalance(TEST_ID1).getBalance();
		
		double expectedAmountUser2 = 1200;
		double actualAmountUser2 = transferDAO.getBalance(TEST_ID2).getBalance();
		
		assertEquals(expectedAmountUser1, actualAmountUser1);
		assertEquals(expectedAmountUser2, actualAmountUser2);
	}
	

}
