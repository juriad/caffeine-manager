package cz.artique.test.caffeineManager.service;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

public class UserService {
	private SimpleJdbcInsert insertUser;

	public void setDataSource(DataSource dataSource) {
		insertUser = new SimpleJdbcInsert(dataSource).withTableName("users").usingColumns("login", "password", "email")
				.usingGeneratedKeyColumns("id");
	}

	public int registerUser(String login, String password, String email) throws DuplicateKeyException {
		Map<String, Object> parameters = new HashMap<String, Object>(3);
		parameters.put("login", login);

		// use BCrypt, which is proven to be good
		String hashedPass = BCrypt.hashpw(password, BCrypt.gensalt());
		parameters.put("password", hashedPass);
		parameters.put("email", email);
		return insertUser.executeAndReturnKey(parameters).intValue();
	}
}
