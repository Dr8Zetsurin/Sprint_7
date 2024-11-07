package org.example.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.model.Order;
import static io.restassured.RestAssured.given;
import static org.example.config.Config.*;

import static org.example.config.Config.BASE_URL;
import static org.example.config.Config.ORDER_PATH;

public class OrderClient {

    @Step("Создание заказа")
    public Response create(Order order) {
        return given()
                .header("Content-type", "application/json")
                // .header("Accept-Language", "ru-RU")
                .body(order)
                .when()
                .post(BASE_URL + ORDER_PATH);
    }

    @Step("Получение списка заказов")
    public Response getOrders() {
        return given()
                .when()
                .get(BASE_URL + ORDER_PATH);
    }

    @Step("Принять заказ")
    public Response accept(Integer orderId, String courierId) {
        return given()
                .header("Content-type", "application/json")
                .queryParam("courierId", courierId)
                .when()
                .put(BASE_URL + ORDER_PATH + "/accept/" + orderId);
    }

    @Step("Получить заказ по номеру")
    public Response getOrder(String trackId) {
        return given()
                .when()
                .get(BASE_URL + ORDER_PATH + "/track?t=" + trackId);
    }

    @Step("Отменить заказ")
    public Response cancel(int orderId) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .put(BASE_URL + ORDER_PATH + "/cancel?track=" + orderId);
    }
} 