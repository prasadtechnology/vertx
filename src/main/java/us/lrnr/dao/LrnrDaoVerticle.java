package us.lrnr.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import us.lrnr.constants.Types;
import us.lrnr.service.CredentialService;

public class LrnrDaoVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(LrnrDaoVerticle.class);


	public void start(Future<Void> startFuture) {
		
		JsonObject mySQLClientConfig = new JsonObject()
				.put("url", config().getString("url"))
				.put("driver_class", config().getString("driver_class"))
				.put("user", config().getString("user"))
				.put("password", config().getString("password"))
				.put("queryTimeout", config().getInteger("queryTimeout"));

		JDBCClient  sqlClient = JDBCClient.createShared(vertx, mySQLClientConfig);
		 
		LOGGER.info("deployment of LrnrDaoVerticle is started .....");

		vertx.eventBus().consumer(Types.DAO_GET_CREDENTIALS, message -> {
			LOGGER.info("entered LrnrDaoVerticle : " + message.body());
			CredentialService.getCredentials(message, sqlClient);
			
		});

		vertx.eventBus().consumer(Types.DAO_GET_CREDENTIAL, message -> {
			LOGGER.info("entered LrnrDaoVerticle : " + message.body());
			CredentialService.getCredential(message, sqlClient);
			
		});
		
		startFuture.complete();
	}
}
