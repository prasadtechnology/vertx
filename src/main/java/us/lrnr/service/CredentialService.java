package us.lrnr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.RoutingContext;
import us.lrnr.constants.Types;

public class CredentialService {
	
	private static final Logger logger = LoggerFactory.getLogger(CredentialService.class);

	 public static void getCredentials(Message message,JDBCClient sqlClient){
		
	JsonObject messageRes = new JsonObject();
		 
		sqlClient.getConnection(res -> {
			if (res.succeeded()) {

				SQLConnection connection = res.result();

				logger.info("got the connection ======>");
				String sql = "SELECT * from credentials where is_active = 1 order by lms_type";
	
				connection.query(sql, result -> {
					
					if (result.succeeded()) {
						
						ResultSet resultSet = result.result();
						logger.info("got the result set ======>");
						String resultSetResponse = resultSet.getRows().toString();
						connection.close();
						
						messageRes.put("status", Types.STATUS_OK);
						messageRes.put("data", resultSetResponse);
						
						message.reply(messageRes);
						
					} else {
						
						connection.close();
						
						messageRes.put("status", Types.STATUS_SERVER_SIDE_ERROR);
						messageRes.put("data", "something went wrong while executing the query....");
						
						logger.info("failed to execute the query ========>");
						
						message.reply(messageRes);
					}
				});

			} else {
				messageRes.put("status", Types.STATUS_SERVER_SIDE_ERROR);
				messageRes.put("data", "un able to -get the connection ....");
				message.reply(messageRes);
			}
		});
		
	}

	 public static void getCredential(Message message,JDBCClient sqlClient){
		
	JsonObject reqJson = (JsonObject)message.body();
	JsonObject messageRes = new JsonObject();
		 
		sqlClient.getConnection(res -> {
			if (res.succeeded()) {

				SQLConnection connection = res.result();

				logger.info("got the connection ======>");
				String sql = "SELECT * from credentials where id=:id ";
				sql = sql.replaceAll(":id", reqJson.getString("id"));
	
				connection.query(sql, result -> {
					
					if (result.succeeded()) {
						
						ResultSet resultSet = result.result();
						logger.info("got the result set ======>");
						String resultSetResponse = resultSet.getRows().toString();
						connection.close();
						
						messageRes.put("status", Types.STATUS_OK);
						messageRes.put("data", resultSetResponse);
						
						message.reply(messageRes);
						
					} else {
						
						connection.close();
						
						messageRes.put("status", Types.STATUS_SERVER_SIDE_ERROR);
						messageRes.put("data", "something went wrong while executing the query....");
						
						logger.info("failed to execute the query ========>");
						
						message.reply(messageRes);
					}
				});

			} else {
				messageRes.put("status", Types.STATUS_SERVER_SIDE_ERROR);
				messageRes.put("data", "un able to -get the connection ....");
				message.reply(messageRes);
			}
		});
		
	}
}
