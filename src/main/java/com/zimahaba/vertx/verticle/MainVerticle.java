package com.zimahaba.vertx.verticle;

import com.zimahaba.vertx.entity.Entity;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {

  private MongoClient mongo;

  @Override
  public void start()  {

    HttpServer server = vertx.createHttpServer();

    JsonObject mongoConfig = config().put("db_name", "test").put("connection_string", "mongodb://172.20.0.2:27017");
    mongo = MongoClient.create(vertx, mongoConfig);

    // Routes
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.get("/entity").handler(this::getEntities);
    router.post("/entity").handler(this::addEntity);

    server.requestHandler(router).listen(9000);
  }

  private void getEntities(RoutingContext context) {

    JsonArray entityList = new JsonArray();

    JsonObject query = new JsonObject();
    mongo.find("entity", query, res -> {
      if (res.succeeded()) {
        for (JsonObject jsonObject: res.result()) {
          entityList.add(jsonObject);
        }
        context.response().end(entityList.encodePrettily());
      } else {
        res.cause().printStackTrace();
      }
    });
  }

  private void addEntity(RoutingContext context) {
    Entity entity = context.getBodyAsJson().mapTo(Entity.class);

    JsonObject jsonEntity = JsonObject.mapFrom(entity);
    mongo.save("entity", jsonEntity, res -> {
      if (res.succeeded()) {
        String id = res.result();
        System.out.println("Saved entity with id " + id);
      } else {
        res.cause().printStackTrace();
      }
    });

    context.response().end(jsonEntity.encodePrettily());
  }
}
