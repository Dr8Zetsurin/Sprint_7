package org.example.courier;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.client.CourierClient;
import org.example.model.Courier;
import org.example.utils.DataGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest {
    private CourierClient courierClient = new CourierClient();
    private int courierId;
    private Courier courier;

    @Before
    public void setUp() {
        courier = new Courier(
            DataGenerator.generateLogin(),
            DataGenerator.generatePassword(),
            DataGenerator.generateFirstName()
        );
        courierClient.create(courier);
        
        var loginResponse = courierClient.login(courier);
        courierId = loginResponse.then().extract().path("id");
    }

    @Test
    @DisplayName("Успешный логин курьера")
    @Description("Проверяем, что курьер может успешно авторизоваться")
    public void courierCanLogin() {
        courierClient.login(courier)
                .then()
                .statusCode(200)
                .and()
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Проверяем, что система возвращает ошибку при неверном пароле")
    public void loginWithWrongPassword() {
        Courier wrongCourier = new Courier(courier.getLogin(), "wrongpass", courier.getFirstName());
        
        courierClient.login(wrongCourier)
                .then()
                .statusCode(404)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин несуществующего пользователя")
    @Description("Проверяем, что система возвращает ошибку при попытке входа несуществующего пользователя")
    public void loginNonexistentCourier() {
        Courier nonexistentCourier = new Courier(
            DataGenerator.generateLogin(),
            DataGenerator.generatePassword(),
            DataGenerator.generateFirstName()
        );
        
        courierClient.login(nonexistentCourier)
                .then()
                .statusCode(404)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @After
    public void tearDown() {
        if (courierId != 0) {
            courierClient.delete(courierId);
        }
    }
} 