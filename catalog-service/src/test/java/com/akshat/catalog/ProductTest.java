package com.akshat.catalog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductTest {

    @Test
    public void calculatePrice(){

        int price = 1000;
        int quantity = 2;

        int subtotal = price*quantity;

        assertEquals(2000,subtotal);

    }

}
