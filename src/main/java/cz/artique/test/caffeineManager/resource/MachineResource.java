package cz.artique.test.caffeineManager.resource;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import cz.artique.test.caffeineManager.service.MachineService;

@Component
@Path("/machine")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MachineResource extends Resource {

	@Autowired
	private MachineService machineService;

	@POST
	public Response put(JsonObject object) {
		try {
			int id = machineService.registerMachine(object.getString("name"), object.getInt("caffeine"));
			JsonObjectBuilder json = Json.createObjectBuilder();
			json.add("id", id);
			return ok(json.build());
		} catch (NullPointerException | ClassCastException e) {
			return wrongInputErr("{name: string, caffeine: int}");
		} catch (DuplicateKeyException e) {
			return duplicateErr("machine", "name");
		}
	}
}
