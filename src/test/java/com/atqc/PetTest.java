package com.atqc;

import com.atqc.models.PetModel;
import com.github.javafaker.Faker;
import com.google.common.collect.ImmutableMap;
import io.qameta.allure.Description;
import org.hamcrest.Matchers;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.*;
import static com.atqc.models.PetModel.positiveUpdate;
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
       given()
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
    }


    @Test(priority = 2)
    @Description("Get data of the pet")
    public void positiveGetPetData(){

        PetModel pet = getPet();
        int petId = Math.toIntExact(pet.getId());

        given()
                .spec(REQUEST_SPEC)
       .when()
                .get("/{petId}", petId)
       .then()
                .statusCode(200)
                .body("id", is(petId))
                .body("name", is(pet.getName()))
                .body("photoUrls", is(notNullValue()));
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
    public void negativeUpdatePetNoBody(){
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
    @Description("Update pet negative id") //Should be an error and negative test, but this API accepts anything))
    public void negativeUpdatePetWrongId(){
        given()
                .spec(REQUEST_SPEC)
                .body(PetModel.negativeCreatePetWrongId())
        .when()
                .put(basePath)
        .then()
                .statusCode(200);
    }

    @Test(priority = 10)
    @Description("Update created Pet")
    public void positiveUpdateCreatedPet(){

        PetModel updatedPet = positiveUpdate();
        int updatedPetId = Math.toIntExact(updatedPet.getId());

        given()
                .spec(REQUEST_SPEC)
                .body(updatedPet)
        .when()
                .put(basePath)
        .then()
                .statusCode(200)
                .body("id", is(updatedPetId))
                .body("name", is(updatedPet.getName()));
    }

    ImmutableMap<String,?> becauseWeNeedGuava = ImmutableMap.<String, Object>builder()
            .put("name", faker.ancient().titan())
            .put("id",faker.number().randomDigit())
            .build();

    @Test(priority = 11)
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

    ImmutableMap<String,?> negativeUpdateStringId = ImmutableMap.<String, Object>builder()
            .put("name", faker.ancient().titan())
            .put("id",faker.chuckNorris().fact())
            .build();

    @Test(priority = 12)
    @Description("Update pet with String id")
    public void negativeUpdateStringId(){
        given()
                .spec(REQUEST_SPEC)
                .body(negativeUpdateStringId)
        .when()
                .put(basePath)
        .then()
                .statusCode(500)
                .body("message", is("something bad happened"));
    }
}
