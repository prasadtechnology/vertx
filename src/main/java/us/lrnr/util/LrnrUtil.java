package us.lrnr.util;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import us.lrnr.constants.Types;

public class LrnrUtil {

	public static void getCredentials(Vertx vertx,RoutingContext routingContext){

		vertx.eventBus().send(Types.DAO_GET_CREDENTIALS, new JsonObject(), daoResponse -> {
			try{
				JsonObject respJson = (JsonObject) daoResponse.result().body();
				if (respJson.getInteger("status") == Types.STATUS_OK) {
					ResponseUtil.sendResponse(routingContext, Types.STATUS_OK, respJson);
				} else {
					ResponseUtil.sendResponse(routingContext, Types.STATUS_SERVER_SIDE_ERROR, respJson);
				}	
			}catch(Exception e){
				JsonObject obj = new JsonObject();
				obj.put("status", Types.STATUS_OK);
				obj.put("data", e.getMessage());
				ResponseUtil.sendResponse(routingContext, Types.STATUS_SERVER_SIDE_ERROR, obj);
			}
			
		});
	}

}
