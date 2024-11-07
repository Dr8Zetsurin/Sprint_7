package org.example.order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.client.OrderClient;
import org.example.model.Order;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.After;

public class OrderGetTest {
    private OrderClient orderClient = new OrderClient();
    private int trackId;
    private int orderId;

    @Before
    public void setUp() {
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

        var response = orderClient.create(order);
        trackId = response.then().extract().path("track");
        
        var orderResponse = orderClient.getOrder(String.valueOf(trackId));
        orderId = orderResponse.then().extract().path("order.id");
        if (orderId == 0) {
            throw new RuntimeException("Не удалось получить id заказа из ответа");
        }
    }

    @Test
    @DisplayName("Получение заказа по номеру")
    @Description("Проверяем успешное получение заказа по его номеру")
    public void getOrderByTrackNumber() {
        orderClient.getOrder(String.valueOf(trackId))
                .then()
                .statusCode(200)
                .and()
                .body("order", notNullValue());
    }

    @Test
    @DisplayName("Получение заказа без номера")
    @Description("Проверяем, что запрос без номера заказа возвращает ошибку")
    public void getOrderWithoutTrackNumber() {
        orderClient.getOrder("")
                .then()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Получение несуществующего заказа")
    @Description("Проверяем, что запрос с несуществующим номером заказа возвращает ошибку")
    public void getNonexistentOrder() {
        orderClient.getOrder("999999")
                .then()
                .statusCode(404)
                .and()
                .body("message", equalTo("Заказ не найден"));
    }

    @After
    public void tearDown() {
        if (orderId != 0) {
            orderClient.cancel(orderId);
        }
    }
} 