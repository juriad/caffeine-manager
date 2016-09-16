package cz.artique.test.caffeineManager.resource;

import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import cz.artique.test.caffeineManager.service.CoffeeService;

@Component
@Path("/coffee")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CoffeeResource extends Resource {

	@Autowired
	private CoffeeService coffeeService;

	@GET
	@Path("/buy/{userId}/{machineId}")
	public Response get(@PathParam("userId") int userId, @PathParam("machineId") int machineId) {
		try {
			coffeeService.buyCoffee(userId, machineId);
			return ok();
		} catch (DataIntegrityViolationException e) {
			return nonExistentErr("userId", "machineId");
		}
	}

	@PUT
	@Path("/buy/{userId}/{machineId}")
	public Response put(@PathParam("userId") int userId, @PathParam("machineId") int machineId, JsonObject object) {
		try {
			String timestampStr = object.getString("timestamp");
			DateTime timestamp = ISODateTimeFormat.dateTimeParser().parseDateTime(timestampStr);
			coffeeService.buyCoffee(userId, machineId, timestamp);
			return ok();
		} catch (IllegalArgumentException e) {
			return typeErr("timestamp", "ISO-8601 encoded date");
		} catch (NullPointerException e) {
			return wrongInputErr("{timestamp: date as string}");
		} catch (DataIntegrityViolationException e) {
			return nonExistentErr("userId", "machineId");
		}
	}
}
