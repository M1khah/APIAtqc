package com.atqc;

import io.qameta.allure.Description;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.atqc.framework.Config.restApiBaseUri;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserTest extends RestAPIBaseTest {

    @Test
    @Description("Create User with valid data")
    public void positivePostNewUser() {

        given()
                .contentType("application/json")
                .baseUri(restApiBaseUri)
                .header("Access-Token", "dfhdh=validtoken=gfsdfdfh")
                .body("{\n" +
                        "  \"id\": 0,\n" +
                        "  \"username\": \"Molly2\",\n" +
                        "  \"firstName\": \"string\",\n" +
                        "  \"lastName\": \"string\",\n" +
                        "  \"email\": \"string\",\n" +
                        "  \"password\": \"string\",\n" +
                        "  \"phone\": \"string\",\n" +
                        "  \"userStatus\": 0\n" +
                        "}")
        .when()
                .post("/user")
        .then()
                .statusCode(200)
                .body("code", is(200))
                .body("type", is("unknown"))
                .body("message", notNullValue());

    }

    @Test
    @Description("Get User by valid username")
    public void positiveGetUserByUsername() {

        String userName = "Molly2";

        String lastName = given()
                .contentType("application/json")
                .baseUri("https://petstore.swagger.io/v2")
                .header("Access-Token", "dfhdh=validtoken=gfsdfdfh")
        .when()
                .get("/user/{username}", userName)
        .then()
                .statusCode(200)
                .body("username", equalTo(userName))
                .body("firstName", is(not(emptyString())))
                .extract().path("lastName");

        System.out.println(lastName);

    }

    @Test(dataProvider = "responseErrors")
    @Description("Get user by invalid username")
    public void negativeGetUserByInvalidUsername(String username, int code, String message) {
        given()
                .contentType("application/json")
                .baseUri("https://petstore.swagger.io/v2")
                .header("Access-Token", "dfhdh=validtoken=gfsdfdfh")
        .when()
                .get("/user/{username}", username)
        .then()
                .statusCode(code)
                .body("code", is(1))
                .body("type", is("error"))
                .body("message", equalTo(message));

    }

    @DataProvider(name = "responseErrors")
    private Object[][] provider() {

        return new Object[][] {

                {"rdfhdh", 404, "User not found"},
                {"!2$%*", 404, "User not found"}

        };
    }

}
