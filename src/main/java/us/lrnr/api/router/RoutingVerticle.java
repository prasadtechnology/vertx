package us.lrnr.api.router;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import us.lrnr.service.CredentialService;
import us.lrnr.util.LrnrUtil;

public class RoutingVerticle extends AbstractVerticle {

	private static final Logger logger = LoggerFactory.getLogger(RoutingVerticle.class);

	static JDBCClient sqlClient = null;

	@Override
	public void start(Future<Void> future) {

		Router router = Router.router(vertx);

		vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port", 8090),
				result -> {
					if (result.succeeded()) {
						future.complete();
					} else {
						future.fail(result.cause());
					}
				});

//		router.get("/api/whiskies").handler(this::getAll);
//		router.post("/api/whiskies").handler(this::addOne);
//		router.delete("/api/whiskies/:id").handler(this::deleteOne);
//
//		// Serve static resources from the /assets directory
//		router.route("/assets/*").handler(StaticHandler.create("assets"));
//
//		router.route().handler(BodyHandler.create());

		// Bind "/" to our hello message - so we are still compatible.
		router.route("/credentials").handler(this::getCredentials);
	}

	private  void getCredentials(RoutingContext routingContext) {
		LrnrUtil.getCredentials(vertx, routingContext);
	}

//	private void getAll(RoutingContext routingContext) {
//		routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
//				.end(Json.encodePrettily(products.values()));
//	}
//
//	private void addOne(RoutingContext routingContext) {
//		final Whisky whisky = Json.decodeValue(routingContext.getBodyAsString(), Whisky.class);
//		products.put(whisky.getId(), whisky);
//		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
//				.end(Json.encodePrettily(whisky));
//	}
//
//	private void deleteOne(RoutingContext routingContext) {
//		String id = routingContext.request().getParam("id");
//		if (id == null) {
//			routingContext.response().setStatusCode(400).end();
//		} else {
//			Integer idAsInteger = Integer.valueOf(id);
//			products.remove(idAsInteger);
//		}
//		routingContext.response().setStatusCode(204).end();
//	}

}