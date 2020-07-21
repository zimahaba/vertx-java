package com.zimahaba.vertx.verticle;

import com.zimahaba.vertx.entity.Entity;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start()  {

    HttpServer server = vertx.createHttpServer();

    // Routes
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.get("/entity").handler(this::getEntities);
    router.post("/entity").handler(this::addEntity);

    server.requestHandler(router).listen(9000);
  }

  private Map<Integer, Entity> map = new HashMap<>();

  private void getEntities(RoutingContext context) {
    context.response().end(new JsonArray(map.values().stream().collect(Collectors.toList())).encodePrettily());
  }

  private void addEntity(RoutingContext context) {
    Entity entity = context.getBodyAsJson().mapTo(Entity.class);
    map.put(entity.getId(), entity);
    context.response().end(JsonObject.mapFrom(entity).encodePrettily());
  }
}
