package io.vertx.blog.first;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.rxjava.ext.asyncsql.MySQLClient;

public class MyFirstVerticle extends AbstractVerticle {

	private static final Logger logger = LoggerFactory.getLogger(MyFirstVerticle.class);
	// Store our product
	private Map<String, Whisky> products = new LinkedHashMap();

	// Create some product
	private void createSomeData() {
		Whisky bowmore = new Whisky("Bowmore 15 Years Laimrig", "Scotland, Islay");
		products.put("1", bowmore);
		Whisky talisker = new Whisky("Talisker 57° North", "Scotland, Island");
		products.put("2", talisker);
	}

	@Override
	public void start(Future<Void> fut) {

		JsonObject mySQLClientConfig = new JsonObject()
				.put("url", "jdbc:mysql://localhost:3306/lrnr_dev_01?autoReconnect=true")
				.put("driver_class", "com.mysql.jdbc.Driver")
				.put("user", "vikranth").put("password", "vikranth")
				.put("queryTimeout", 10000);

		JDBCClient sqlClient = JDBCClient.createShared(vertx, mySQLClientConfig);

		

		createSomeData();

		// Create a router object.
		Router router = Router.router(vertx);

		// Create the HTTP server and pass the "accept" method to the request
		// handler.
		vertx.createHttpServer().requestHandler(router::accept).listen(
				// Retrieve the port from the configuration,
				// default to 8080.
				config().getInteger("http.port", 8090), result -> {
					if (result.succeeded()) {
						fut.complete();
					} else {
						fut.fail(result.cause());
					}
				});

		router.get("/api/whiskies").handler(this::getAll);
		router.post("/api/whiskies").handler(this::addOne);
		router.delete("/api/whiskies/:id").handler(this::deleteOne);

		// Serve static resources from the /assets directory
		router.route("/assets/*").handler(StaticHandler.create("assets"));

		router.route().handler(BodyHandler.create());

		// Bind "/" to our hello message - so we are still compatible.
		router.route("/").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			
			sqlClient.getConnection(res -> {
				if (res.succeeded()) {

					SQLConnection connection = res.result();

					logger.info("got the connection ======>");

					connection.query("SELECT * from credentials", result -> {
						if (result.succeeded()) {
							// Get the result set
							ResultSet resultSet = result.result();
							logger.info("got the result set ======>");
							response.putHeader("content-type", "application/json").end(resultSet.toJson().toString());
						} else {
							// Failed!
							logger.info("failed to execute the query ========>");
						}
					});

				} else {
					// Failed to get connection - deal with it
				}
			});
			
			
		});
	}

	private void getAll(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(products.values()));
	}

	private void addOne(RoutingContext routingContext) {
		final Whisky whisky = Json.decodeValue(routingContext.getBodyAsString(), Whisky.class);
		products.put(whisky.getId(), whisky);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(whisky));
	}

	private void deleteOne(RoutingContext routingContext) {
		String id = routingContext.request().getParam("id");
		if (id == null) {
			routingContext.response().setStatusCode(400).end();
		} else {
			Integer idAsInteger = Integer.valueOf(id);
			products.remove(idAsInteger);
		}
		routingContext.response().setStatusCode(204).end();
	}

}