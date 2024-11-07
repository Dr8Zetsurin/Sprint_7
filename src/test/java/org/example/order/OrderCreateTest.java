package org.example.order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.client.OrderClient;
import org.example.model.Order;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreateTest {
    private OrderClient orderClient = new OrderClient();
    private final String[] color;
    private int orderId;

    public OrderCreateTest(String[] color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] getColors() {
        return new Object[][] {
            {new String[]{"BLACK"}},
            {new String[]{"GREY"}},
            {new String[]{"BLACK", "GREY"}},
            {new String[]{}}
        };
    }

    @Test
    @DisplayName("Создание заказа с разными цветами")
    @Description("Проверяем создание заказа с разными вариантами цвета самоката")
    public void createOrderWithDifferentColors() {
        Order order = new Order(
            "Тест",
            "Тестов",
            "Тестовая улица, 1",
            "Сокольники",
            "+7 800 355 35 35",
            5,
            "2024-03-20",
            "Тестовый комментарий",
            color
        );

        var response = orderClient.create(order)
                .then()
                .statusCode(201)
                .and()
                .body("track", notNullValue());
                
        orderId = response.extract().path("track");
    }

    @After
    public void tearDown() {
        if (orderId != 0) {
            orderClient.cancel(orderId);
        }
    }
} 