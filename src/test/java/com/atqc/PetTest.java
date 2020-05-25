package com.atqc;

import com.atqc.models.PetModel;
import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.*;
import static io.restassured.RestAssured.given;
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
        put("photoUrls", Arrays.asList("photohub", "facebook"));
        put("tags", tags);
        put("status", "available");
    }};

    @Test(priority = 1)
    @Description("Create a pet with valid data")
    public void positivePostNewPet(){

       PetModel createdValidPet = given()
                .spec(REQUEST_SPEC)
                .body(testPet)
      .when()
                .post("/pet")
      .then()
                .statusCode(200)
                .body("category.name", equalTo("Maine"))
                .body("tags.name", hasItem("coon"))
                .body("photoUrls", hasItem("photohub"))
                .body("name", is("Behemoth"))
                .body("photoUrls", hasSize(2))
                .extract().as(PetModel.class);

        System.out.println(createdValidPet.getId());
    }

    @Test(priority = 2)
    @Description("Get data of the pet")
    public void positiveGetPetData(){
        given()
                .spec(REQUEST_SPEC)
       .when()
                .get("/pet/123")
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
                .get("/pet/{id}", id)
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
}
