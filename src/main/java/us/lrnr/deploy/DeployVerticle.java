package us.lrnr.deploy;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import us.lrnr.api.router.RoutingVerticle;
import us.lrnr.dao.LrnrDaoVerticle;

public class DeployVerticle extends AbstractVerticle {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DeployVerticle.class);
	
	@Override
	public void start(Future<Void> startFuture) throws IOException {
		JsonObject configJson = vertx.getOrCreateContext().config();
		LOGGER.info("************* configJson ************** : "+configJson.encodePrettily());
		DeploymentOptions dOptions = new DeploymentOptions().setConfig(configJson);
		
		vertx.deployVerticle(new LrnrDaoVerticle(),dOptions,dbRes ->{
			if(dbRes.succeeded()) {
				LOGGER.info("database verticle deployed with deployment id :" + dbRes.result());
					vertx.deployVerticle(new RoutingVerticle(),dOptions,apiRes -> {
						if(apiRes.succeeded()) {
							LOGGER.info("LrnrAuthoringAPI verticle deployed with deployment id :" + apiRes.result());
							startFuture.complete();
						}
						else {
							startFuture.fail(dbRes.cause());
						}
					});
			}
			else {
				startFuture.fail(dbRes.cause());
			}
			
		});
		
	}

}
