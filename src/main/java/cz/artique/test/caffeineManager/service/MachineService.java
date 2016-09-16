package cz.artique.test.caffeineManager.service;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

public class MachineService {
	private SimpleJdbcInsert insertMachine;

	public void setDataSource(DataSource dataSource) {
		insertMachine = new SimpleJdbcInsert(dataSource).withTableName("machines").usingColumns("name", "caffeine")
				.usingGeneratedKeyColumns("id");
	}

	public int registerMachine(String name, int caffeine) throws DuplicateKeyException {
		Map<String, Object> parameters = new HashMap<String, Object>(2);
		parameters.put("name", name);
		parameters.put("caffeine", caffeine);
		return insertMachine.executeAndReturnKey(parameters).intValue();
	}
}
