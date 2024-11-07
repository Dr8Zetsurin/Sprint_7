package org.example.order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.client.CourierClient;
import org.example.client.OrderClient;
import org.example.model.Courier;
import org.example.model.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.equalTo;

public class OrderAcceptTest {
    private OrderClient orderClient = new OrderClient();
    private CourierClient courierClient = new CourierClient();
    private int courierId;
    private int orderId;

    @Before
    public void setUp() {
        // Создаем курьера
        Courier courier = new Courier("testcourier" + System.currentTimeMillis(), "1234", "test");
        var createResponse = courierClient.create(courier);
        if (createResponse.statusCode() != 201) {
            throw new RuntimeException("Failed to create courier: " + createResponse.body().asString());
        }
        
        // Логин курьера
        var loginResponse = courierClient.login(courier);
        if (loginResponse.statusCode() != 200) {
            throw new RuntimeException("Failed to login courier: " + loginResponse.body().asString());
        }
        
        courierId = loginResponse.then()
                .extract()
                .path("id");
                
        if (courierId == 0) {
            throw new RuntimeException("Courier ID is zero in response");
        }

        // Создаем заказ и делаем несколько попыток получить его статус
        Order order = new Order(
            "Тест",
            "Тестов",
            "Тестовая улица, 1",
            "Сокольники",
            "+7 800 355 35 35",
            5,
            "2024-03-20",
            "Тестовый комментарий",
            new String[]{"BLACK"}
        );

        var createOrderResponse = orderClient.create(order);
        if (createOrderResponse.statusCode() != 201) {
            throw new RuntimeException("Failed to create order: " + createOrderResponse.body().asString());
        }

        // Получаем track номер
        int trackId = createOrderResponse.then()
                .extract()
                .path("track");

        // Получаем заказ по track номеру
        var orderResponse = orderClient.getOrder(String.valueOf(trackId));
        if (orderResponse.statusCode() != 200) {
            throw new RuntimeException("Failed to get order: " + orderResponse.body().asString());
        }

        orderId = orderResponse.then()
                .extract()
                .path("order.id");

        if (orderId == 0) {
            throw new RuntimeException("Order ID is zero in response");
        }

        // Ждем, пока заказ станет доступным
        int maxAttempts = 3;
        int currentAttempt = 0;
        boolean orderAvailable = false;

        while (currentAttempt < maxAttempts && !orderAvailable) {
            try {
                Thread.sleep(1000); // Ждем секунду между попытками
                var orderStatus = orderClient.getOrder(String.valueOf(trackId))
                        .then()
                        .extract()
                        .statusCode();
                if (orderStatus == 200) {
                    orderAvailable = true;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for order to be available", e);
            }
            currentAttempt++;
        }

        if (!orderAvailable) {
            throw new RuntimeException("Order did not become available after " + maxAttempts + " attempts");
        }
    }

    @Test
    @DisplayName("Успешное принятие заказа")
    @Description("Проверяем успешное принятие заказа курьером")
    public void acceptOrderSuccessfully() {
        orderClient.accept(orderId, String.valueOf(courierId))
                .then()
                .statusCode(200)
                .and()
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Принятие заказа без id курьера")
    @Description("Проверяем, что нельзя принять заказ без указания id курьера")
    public void acceptOrderWithoutCourierId() {
        orderClient.accept(orderId, "")
                .then()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Принятие заказа c неверным id курьера")
    @Description("Проверяем, что нельзя принять заказ с несуществующим id курьера")
    public void acceptOrderWithWrongCourierId() {
        orderClient.accept(orderId, "999999")
                .then()
                .statusCode(404)
                .and()
                .body("message", equalTo("Курьера с таким id не существует"));
    }

    @After
    public void tearDown() {
        if (courierId != 0) {
            courierClient.delete(courierId);
        }
    }
} 