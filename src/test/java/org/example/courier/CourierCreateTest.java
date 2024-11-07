package org.example.courier;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.client.CourierClient;
import org.example.model.Courier;
import org.example.utils.DataGenerator;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class CourierCreateTest {
    private CourierClient courierClient = new CourierClient();
    private int courierId;

    @Test
    @DisplayName("Создание курьера с валидными данными")
    @Description("Проверяем успешное создание курьера с корректными данными")
    public void courierCanBeCreatedWithValidData() {
        Courier courier = new Courier(
            DataGenerator.generateLogin(),
            DataGenerator.generatePassword(),
            DataGenerator.generateFirstName()
        );
        
        courierClient.create(courier)
                .then()
                .statusCode(201)
                .and()
                .body("ok", equalTo(true));

        Integer id = courierClient.login(courier)
                .then()
                .extract()
                .path("id");
        if (id != null) {
            courierId = id.intValue();
        } else {
            throw new RuntimeException("Не удалось получить id курьера из ответа");
        }
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Description("Проверяем, что нельзя создать курьера с логином, который уже существует")
    public void cannotCreateTwoIdenticalCouriers() {
        String login = DataGenerator.generateLogin();
        Courier courier = new Courier(
            login,
            DataGenerator.generatePassword(),
            DataGenerator.generateFirstName()
        );
        
        courierClient.create(courier)
                .then()
                .statusCode(201);

        courierClient.create(courier)
                .then()
                .statusCode(409)
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));

        courierId = courierClient.login(courier)
                .then()
                .extract()
                .path("id");
    }

    @Test
    @DisplayName("Создание курьера без обязательного поля логин")
    @Description("Проверяем, что нельзя создать курьера без логина")
    public void cannotCreateCourierWithoutLogin() {
        Courier courier = new Courier(null, "1234", "testname");
        
        courierClient.create(courier)
                .then()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @After
    public void tearDown() {
        if (courierId != 0) {
            courierClient.delete(courierId);
        }
    }
} 