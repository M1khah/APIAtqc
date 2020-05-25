package com.atqc;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeSuite;

import static com.atqc.framework.Config.restApiBaseUri;

public class RestAPIBaseTest {

    static final RequestSpecification REQUEST_SPEC =
            new RequestSpecBuilder()
                    .setContentType("application/json")
                    .setBaseUri(restApiBaseUri)
                    .build();

    @BeforeSuite
    public void addFilters() {

        RestAssured.filters(
                new AllureRestAssured(),
                new RequestLoggingFilter(),
                new ResponseLoggingFilter()
        );

    }
}
