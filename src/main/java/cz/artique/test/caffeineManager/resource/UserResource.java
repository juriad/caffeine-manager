package cz.artique.test.caffeineManager.resource;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import cz.artique.test.caffeineManager.service.UserService;

@Component
@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource extends Resource {

	@Autowired
	private UserService userService;

	private final EmailValidator emailValidator = EmailValidator.getInstance();

	/**
	 * Only the email is being checked.
	 * 
	 * @param object
	 * @return
	 */
	@PUT
	@Path("/request")
	public Response put(JsonObject object) {
		try {
			// we validate the email
			String email = object.getString("email");
			if (!emailValidator.isValid(email)) {
				return emailErr();
			}

			int id = userService.registerUser(object.getString("login"), object.getString("password"),
					object.getString("email"));
			JsonObjectBuilder json = Json.createObjectBuilder();
			json.add("id", id);
			return ok(json.build());
		} catch (NullPointerException | ClassCastException e) {
			return wrongInputErr("{login: string, password: string, email: string}");
		} catch (DuplicateKeyException e) {
			return duplicateErr("user", "login", "email");
		}
	}
}
