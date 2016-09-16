package cz.artique.test.caffeineManager.resource;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.ws.rs.core.Response;

public class Resource {
	private void makeList(StringBuilder sb, String... columns) {
		for (int i = 0; i < columns.length; i++) {
			sb.append(columns[i]);
			if (i < columns.length - 2) {
				sb.append(", ");
			} else if (i == columns.length - 2) {
				sb.append(" or ");
			}
		}
	}

	public Response ok() {
		return ok(null);
	}

	public Response ok(JsonValue obj) {
		return Response.ok().entity(obj).build();
	}

	public Response err(int code, JsonObject json) {
		return Response.status(code).entity(json).build();
	}

	public Response duplicateErr(String object, String... attributes) {
		JsonObjectBuilder json = Json.createObjectBuilder();
		json.add("error_code", 1);

		StringBuilder sb = new StringBuilder("A ");
		sb.append(object);
		sb.append(" with such ");
		makeList(sb, attributes);
		sb.append(" already exists.");

		json.add("error_text", sb.toString());
		return err(400, json.build());
	}

	public Response wrongInputErr(String details) {
		JsonObjectBuilder json = Json.createObjectBuilder();
		json.add("error_code", 2);
		json.add("error_text", "The input must be JSON of the following form: " + details);
		return err(400, json.build());
	}

	public Response nonExistentErr(String... attributes) {
		JsonObjectBuilder json = Json.createObjectBuilder();
		json.add("error_code", 3);

		StringBuilder sb = new StringBuilder("A referenced object by ");
		makeList(sb, attributes);
		sb.append(" does not exists.");

		json.add("error_text", sb.toString());
		return err(400, json.build());
	}

	public Response emailErr() {
		JsonObjectBuilder json = Json.createObjectBuilder();
		json.add("error_code", 4);
		json.add("error_text", "The argument is not a valid email.");
		return err(400, json.build());
	}

	public Response intTypeErr() {
		JsonObjectBuilder json = Json.createObjectBuilder();
		json.add("error_code", 5);
		json.add("error_text", "The parameter must be an integer");
		return err(400, json.build());
	}

	public Response typeErr(String attribute, String type) {
		JsonObjectBuilder json = Json.createObjectBuilder();
		json.add("error_code", 6);
		json.add("error_text", "The attribute " + attribute + " must be a " + type + ".");
		return err(400, json.build());
	}
}
