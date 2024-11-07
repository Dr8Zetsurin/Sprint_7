package org.example.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.model.Courier;
import static io.restassured.RestAssured.given;
import static org.example.config.Config.*;
import java.util.HashMap;

import static org.example.config.Config.BASE_URL;
import static org.example.config.Config.COURIER_PATH;

public class CourierClient {
    
    @Step("Создание курьера")
    public Response create(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                // .header("Accept-Language", "ru-RU")
                .body(courier)
                .when()
                .post(BASE_URL + COURIER_PATH);
    }

    @Step("Логин курьера")
    public Response login(Courier courier) {
        var credentials = new HashMap<String, String>();
        credentials.put("login", courier.getLogin());
        credentials.put("password", courier.getPassword());
        
        return given()
                .header("Content-type", "application/json")
                // .header("Accept-Language", "ru-RU")
                .body(credentials)
                .when()
                .post(BASE_URL + COURIER_PATH + "/login");
    }

    @Step("Удаление курьера")
    public Response delete(int courierId) {
        return given()
                .when()
                .delete(BASE_URL + COURIER_PATH + "/" + courierId);
    }
} 