package us.lrnr.util;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class ResponseUtil {

	public static void sendResponse(RoutingContext routingContext, int statusCode, JsonObject responseJson) {
		routingContext.response().setStatusCode(statusCode).putHeader("content-type", "application/json")
				.end(responseJson.encodePrettily());
	}
}
