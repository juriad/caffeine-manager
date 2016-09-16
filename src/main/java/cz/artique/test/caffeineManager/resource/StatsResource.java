package cz.artique.test.caffeineManager.resource;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.artique.test.caffeineManager.service.HistoryEntry;
import cz.artique.test.caffeineManager.service.StatsService;

@Component
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/stats")
public class StatsResource extends Resource {

	@Autowired
	private StatsService statsService;

	@GET
	@Path("/coffee")
	public Response global() {
		return byBoth(null, null);
	}

	@GET
	@Path("/coffee/machine/{machineId}")
	public Response byMachine(@PathParam("machineId") int machineId) {
		return byBoth(null, machineId);
	}

	@GET
	@Path("/coffee/user/{userId}")
	public Response byUser(@PathParam("userId") int userId) {
		return byBoth(userId, null);
	}

	public Response byBoth(Integer userId, Integer machineId) {
		List<HistoryEntry> history = statsService.getHistory(userId, machineId);
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

		JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
		JsonObjectBuilder innerObjectBuilder = Json.createObjectBuilder();
		for (HistoryEntry he : history) {
			innerObjectBuilder.add("login", he.getUserLogin());
			innerObjectBuilder.add("id", he.getUserId());
			objectBuilder.add("user", innerObjectBuilder.build());

			innerObjectBuilder.add("name", he.getMachineName());
			innerObjectBuilder.add("id", he.getMachineId());
			objectBuilder.add("machine", innerObjectBuilder.build());

			objectBuilder.add("timestamp", ISODateTimeFormat.dateTime().print(he.getTimestamp()));
			arrayBuilder.add(objectBuilder.build());
		}

		return ok(arrayBuilder.build());
	}

	@GET
	@Path("/level/user/{userId}")
	public Response level(@PathParam("userId") int userId) {
		DateTime from = DateTime.now().minusHours(24);
		Duration duration = new Duration(60 * 60 * 1000);
		List<Double> levels = statsService.getLevels(userId, from, duration);

		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (Double level : levels) {
			arrayBuilder.add(level);
		}
		return ok(arrayBuilder.build());
	}
}
