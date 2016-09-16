package cz.artique.test.caffeineManager.service;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

public class CoffeeService {
	private SimpleJdbcInsert insertCoffee;

	public void setDataSource(DataSource dataSource) {
		insertCoffee = new SimpleJdbcInsert(dataSource).withTableName("coffee").usingColumns("userId", "machineId",
				"timestamp");
	}

	public void buyCoffee(int userId, int machineId) throws DataIntegrityViolationException {
		buyCoffee(userId, machineId, DateTime.now());
	}

	public void buyCoffee(int userId, int machineId, DateTime timestamp) throws DataIntegrityViolationException {
		Map<String, Object> parameters = new HashMap<String, Object>(3);
		parameters.put("userId", userId);
		parameters.put("machineId", machineId);
		parameters.put("timestamp", timestamp.toDate());
		insertCoffee.execute(parameters);
	}
}
