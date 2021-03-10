package com.redhat.bobbycar.funqy.cloudevent;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class FunqyTest {

    @Test
    public void testZoneChangeEvent() {

        String payload = "{\n" +
                "  \"carId\": \"219873-asdjh-239847\",\n" +
                "  \"previousZoneId\": \"previous-zone-11\",\n" +
                "  \"nextZoneId\": \"next-zone-34\"\n" +
                "}";

        System.out.println(payload);

        RestAssured.given().contentType("application/json")
                .header("ce-id", UUID.randomUUID().toString())
                .header("ce-type", "zoneChange")
                .header("ce-source", "Kafka:Topic:bobbycar-zonechange")
                .body(payload)
                .post("/")
                .then().statusCode(204);
    }
}
