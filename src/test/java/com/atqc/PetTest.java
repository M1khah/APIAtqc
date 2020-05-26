package com.atqc;

import com.atqc.models.PetModel;
import com.github.javafaker.Faker;
import com.google.common.collect.ImmutableMap;
import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.*;

import static io.restassured.RestAssured.basePath;
import static io.restassured.RestAssured.given;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;


public class PetTest extends RestAPIBaseTest{

    Faker faker = new Faker();

    Map<String,Object> category = new HashMap<String,Object>() {{
        put("id", 0);
        put("name", "Maine");
    }};

    ArrayList<Map<String, ?>> tags = new ArrayList<Map<String, ?>>() {{
        Map<String, Object> tagMap = new HashMap<String, Object>() {{
            put("id", 3);
            put("name", "coon");
        }};
        add(tagMap);
    }};

    Map<String,Object> testPet = new HashMap<String,Object>() {{
        put("id", 0);
        put("category", category);
        put("name", "Behemoth");
        put("photoUrls", asList("photohub", "facebook"));
        put("tags", tags);
        put("status", "available");
    }};

    @Test(priority = 1)
    @Description("Create a pet with valid data")
    public void positivePostNewPet(){

       ValidatableResponse pet = given()
                .spec(REQUEST_SPEC)
                .body(testPet)
      .when()
                .post(basePath)
      .then()
                .statusCode(200)
                .body("category.name", equalTo("Maine"))
                .body("tags.name", hasItem("coon"))
                .body("photoUrls", hasItem("photohub"))
                .body("name", is("Behemoth"));
       pet.extract().jsonPath().getLong("id");
    }


    @Test(priority = 2)
    @Description("Get data of the pet")
    public void positiveGetPetData(long petID){
        given()
                .spec(REQUEST_SPEC)
       .when()
                .get("/pet/{petID}", petID)
       .then()
                .statusCode(200)
                .body("name", hasLength(6))
                .body("id", equalTo(123))
                .body("name", is(notNullValue()))
                .body("status", is(not(equalTo("qwerty"))));
    }

    @Test(dataProvider = "wrongId", priority = 3)
    @Description("Wrong GET requests")
    public void negativeGetPetsById(String id, int code) {

        given()
                .spec(REQUEST_SPEC)
        .when()
                .get("/{basePath}" + "{id}", basePath, id)
        .then()
                .statusCode(code);
    }

    @DataProvider(name = "wrongId")
    private Object[][] provider() {

        return new Object[][] {

                {"", 405},
                {"!@#$", 404},
                {"qww", 404},
                {"dead", 404}

        };
    }

    @Test(priority = 4)
    @Description("Create pet with no name passed")
    public void positiveCreatePetNoName(){
        given()
                .spec(REQUEST_SPEC)
                .body(PetModel.negativeCreatePetNoName())
        .when()
                .post(basePath)
        .then()
                .statusCode(200)
                .body("name", is(nullValue()));
    }

    @Test(priority = 5)
    @Description("Find pets by status")
    public void positiveFindPetsByStatus(){

        given()
                .spec(REQUEST_SPEC)
        .when()
                .get(basePath + "findByStatus?status=pending")
        .then()
                .statusCode(200)
                .body("status[0]", is("pending"));
    }

    @Test(priority = 6)
    @Description("Validate zero results for invalid status")
    public void positiveValkdateZeroResults(){
        given()
                .spec(REQUEST_SPEC)
        .when()
                .get(basePath + "findByStatus?status=qwerty")
        .then()
                .statusCode(200)
                .body("", Matchers.hasSize(0));

    }

    @Test(priority = 7)
    @Description("Validate error for create pet with no body")
    public void negativeCreatePetNoBody(){
        given()
                .spec(REQUEST_SPEC)
                .body("")
        .when()
                .post(basePath)
        .then()
                .statusCode(405)
                .body("type", equalTo("unknown"))
                .body("message", is("no data"));
    }

    @Test(priority = 8)
    @Description("Update pet with no body")
    public void negativeUpdatePet(){
        given()
                .spec(REQUEST_SPEC)
                .body("")
        .when()
                .put(basePath)
        .then()
                .statusCode(405)
                .body("type", equalTo("unknown"))
                .body("message", is("no data"));
    }

    @Test(priority = 9)
    @Description("Update pet wrong id") //Should be an error and negative test, but this API accepts anything))
    public void negativeUpdatePetWrongId(){
        given()
                .spec(REQUEST_SPEC)
                .body(PetModel.negativeCreatePetWrongId())
        .when()
                .put(basePath)
        .then()
                .statusCode(200);
    }

    ImmutableMap<String,?> becauseWeNeedGuava = ImmutableMap.<String, Object>builder()
            .put("name", faker.ancient().titan())
            .put("id",faker.number().randomDigit())
            .build();


    @Test(priority = 10)
    @Description("Create a pet with Guava")
    public void createPetWithGuava(){
        given()
                .spec(REQUEST_SPEC)
                .body(becauseWeNeedGuava)
        .when()
                .post(basePath)
        .then()
                .statusCode(200)
                .body("name", is(becauseWeNeedGuava.get("name")))
                .body("id", equalTo(becauseWeNeedGuava.get("id")));
    }
}
