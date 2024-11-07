package org.example.order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.client.OrderClient;
import org.junit.Test;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest {
    private OrderClient orderClient = new OrderClient();

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверяем, что в ответе возвращается список заказов")
    public void getOrdersList() {
        orderClient.getOrders()
                .then()
                .statusCode(200)
                .and()
                .body("orders", notNullValue());
    }
} 