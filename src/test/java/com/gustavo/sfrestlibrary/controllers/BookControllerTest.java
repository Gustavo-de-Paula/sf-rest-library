package com.gustavo.sfrestlibrary.controllers;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.atlassian.oai.validator.whitelist.ValidationErrorsWhitelist;
import com.gustavo.sfrestlibrary.model.BookGenre;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.atlassian.oai.validator.whitelist.rule.WhitelistRules.messageHasKey;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@ActiveProfiles("test")
@Import(BookControllerTest.TestConfig.class)
@ComponentScan(basePackages = "com.gustavo.sfrestlibrary")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    @LocalServerPort
    Integer localPort;

    OpenApiValidationFilter apiValidationFilter = new OpenApiValidationFilter(OpenApiInteractionValidator
            .createForSpecificationUrl("openapi.yml")
            .withWhitelist(ValidationErrorsWhitelist.create()
                    .withRule("Ignore date format",
                            messageHasKey("validation.response.body.schema.format.date-time")))
            .build());

    public static class TestConfig {
        @Bean
        public SecurityFilterChain filterChain (HttpSecurity httpSecurity) throws Exception {
            httpSecurity.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                    .anyRequest().permitAll());

            httpSecurity.csrf(AbstractHttpConfigurer::disable);

            return httpSecurity.build();
        }
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = localPort;
    }

    @Test
    void testCreateBook() throws JSONException {
        given().contentType(ContentType.JSON)
                .body(createTestBook().toString())
                .when().post(BookController.BOOK_PATH)
                .then().assertThat()
                .statusCode(201);
    }

    @Test
    void listAllBooks() {
        given().contentType(ContentType.JSON)
                .when().filter(apiValidationFilter)
                .get(BookController.BOOK_PATH)
                .then().assertThat()
                .statusCode(200);
    }

    @Test
    void getBookById() {
        given().contentType(ContentType.JSON)
                .when().filter(apiValidationFilter)
                .get(BookController.BOOK_PATH_ID, getNeuromancerId())
                .then().assertThat()
                .statusCode(200)
                .body("bookId", equalTo(getNeuromancerId()))
                .body("bookName", equalTo("Neuromancer"))
                .body("bookIsbn", equalTo(1111111111111L))
                .body("bookGenre", equalTo(BookGenre.SCIFI.toString()))
                .body("bookVersion", equalTo(1));

//        Response response = given().contentType(ContentType.JSON)
//                .when().filter(apiValidationFilter)
//                .get(BookController.BOOK_PATH_ID, getNeuromancerId())
//                .then().extract().response();
//
//        Assertions.assertEquals(200, response.statusCode());
//        Assertions.assertEquals(getNeuromancerId(), response.jsonPath().getString("bookId"));
//        Assertions.assertEquals("The Color Out of Space", response.jsonPath().getString("bookName"));
//        Assertions.assertEquals(2222222222222L, response.jsonPath().getLong("bookIsbn"));
//        Assertions.assertEquals(BookGenre.HORROR.toString(), response.jsonPath().getString("bookGenre"));
//        Assertions.assertEquals(2, response.jsonPath().getInt("bookVersion"));
    }

    @Test
    void getBookByWrongIdReturnsNotFound() {
        given().contentType(ContentType.JSON)
                .get(BookController.BOOK_PATH_ID, UUID.randomUUID())
                .then().assertThat()
                .statusCode(404);
    }

    @Test
    void getBookByInvalidIdParamReturnsBadRequest() {
        given().contentType(ContentType.JSON)
                .get(BookController.BOOK_PATH_ID, "1")
                .then().assertThat()
                .statusCode(400);
    }

    @Test
    void getBookByName() {
        given().contentType(ContentType.JSON)
                .param("bookName", "neuromancer")
                .when().filter(apiValidationFilter)
                .get(BookController.BOOK_PATH)
                .then().assertThat()
                .statusCode(200)
                .body("content[0].bookId", equalTo(getNeuromancerId()))
                .body("content[0].bookName", equalTo("Neuromancer"))
                .body("content[0].bookIsbn", equalTo(1111111111111L))
                .body("content[0].bookGenre", equalTo(BookGenre.SCIFI.toString()))
                .body("content[0].bookVersion", equalTo(1));

//        Response response = given().contentType(ContentType.JSON)
//                .param("bookName", "neuromancer")
//                .when().filter(apiValidationFilter)
//                .get(BookController.BOOK_PATH)
//                .then().extract().response();
//
//        Assertions.assertEquals(200, response.statusCode());
//        Assertions.assertEquals("Neuromancer", response.jsonPath().getString("content[0].bookName"));
//        Assertions.assertEquals(1111111111111L, response.jsonPath().getLong("content[0].bookIsbn"));
//        Assertions.assertEquals(BookGenre.SCIFI.toString(), response.jsonPath().getString("content[0].bookGenre"));
//        Assertions.assertEquals(1, response.jsonPath().getInt("content[0].bookVersion"));
    }

    @Test
    void getBookByWrongNameReturnsEmptyCollection() {
        given().contentType(ContentType.JSON)
                .param("bookName", "kord of the rings: the two towers")
                .when().filter(apiValidationFilter)
                .get(BookController.BOOK_PATH)
                .then().assertThat()
                .statusCode(200).body("content", hasSize(0));
    }

    @Test
    void getBookByGenre() {
        Response response = given().contentType(ContentType.JSON)
                .param("bookGenre", BookGenre.SCIFI.toString())
                .when().filter(apiValidationFilter)
                .get(BookController.BOOK_PATH)
                .then().extract().response();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("Neuromancer", response.jsonPath().getString("content[0].bookName"));
        Assertions.assertEquals(1111111111111L, response.jsonPath().getLong("content[0].bookIsbn"));
        Assertions.assertEquals(BookGenre.SCIFI.toString(), response.jsonPath().getString("content[0].bookGenre"));
        Assertions.assertEquals(1, response.jsonPath().getInt("content[0].bookVersion"));
    }

    @Test
    void getBookByInvalidGenreParamReturnsBadRequest() {
        given().contentType(ContentType.JSON)
                .param("bookGenre", "horror")
                .get(BookController.BOOK_PATH)
                .then().assertThat()
                .statusCode(400);
    }

    @Test
    void updateBookById() throws JSONException {
        JSONObject requestBody = createTestBook();

        requestBody.put("bookName", "Test Book Updated");

        given().contentType(ContentType.JSON)
                .body(requestBody.toString())
                .put(BookController.BOOK_PATH_ID, getTestBookId())
                .then().assertThat()
                .statusCode(204);
    }

    @Test
    void updateBookWithInvalidIdReturnsNotFound() throws JSONException {
        JSONObject requestBody = createTestBook();

        given().contentType(ContentType.JSON)
                .body(requestBody.toString())
                .put(BookController.BOOK_PATH_ID, UUID.randomUUID())
                .then().assertThat()
                .statusCode(404);
    }

    @Test
    void updateBookWithInvalidParamReturnsBadRequest() throws JSONException {
        JSONObject requestBody = createTestBook();

        requestBody.put("bookVersion", "test");

        given().contentType(ContentType.JSON)
                .body(requestBody.toString())
                .put(BookController.BOOK_PATH_ID, getTestBookId())
                .then().assertThat()
                .statusCode(400);
    }

    @Test
    void updateBookWithMissingParamReturnsInternalServerError() throws JSONException {
        JSONObject requestBody = new JSONObject();

        requestBody.put("bookIsbn", 5555555555555L);
        requestBody.put("bookName", "Test Book Update");
        requestBody.put("bookVersion", 0);

        given().contentType(ContentType.JSON)
                .body(requestBody.toString())
                .put(BookController.BOOK_PATH_ID, getTestBookId())
                .then().assertThat()
                .statusCode(500);
    }

    @Test
    void patchBookById() throws JSONException {
        JSONObject requestBody = new JSONObject();

        requestBody.put("bookName", "Test Book Patch");

        given().contentType(ContentType.JSON)
                .body(requestBody.toString())
                .patch(BookController.BOOK_PATH_ID, getTestBookId())
                .then().assertThat()
                .statusCode(204);
    }

    @Test
    void patchBookWithInvalidIdReturnsNotFound() {
        JSONObject requestBody = new JSONObject();

        given().contentType(ContentType.JSON)
                .body(requestBody.toString())
                .patch(BookController.BOOK_PATH_ID, UUID.randomUUID())
                .then().assertThat()
                .statusCode(404);
    }

    @Test
    void patchBookWithInvalidParamReturnsBadRequest() throws JSONException {
        JSONObject requestBody = new JSONObject();

        requestBody.put("bookGenre", 1234);

        given().contentType(ContentType.JSON)
                .body(requestBody.toString())
                .patch(BookController.BOOK_PATH_ID, UUID.randomUUID())
                .then().assertThat()
                .statusCode(400);
    }

    @Test
    void deleteBookById() {
       given().contentType(ContentType.JSON)
                .delete(BookController.BOOK_PATH_ID, getNeuromancerId())
                .then().assertThat()
                .statusCode(204);
    }

    @Test
    void deleteBookByWrongIdReturnsNotFound() {
        given().contentType(ContentType.JSON)
                .delete(BookController.BOOK_PATH_ID, UUID.randomUUID())
                .then().assertThat()
                .statusCode(404);
    }

    @Test
    void deleteBookByInvalidIdParamReturnsBadRequest() {
        given().contentType(ContentType.JSON)
                .delete(BookController.BOOK_PATH_ID, "1")
                .then().assertThat()
                .statusCode(400);
    }

    public JSONObject createTestBook() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("bookId", UUID.randomUUID());
        jsonObject.put("bookIsbn", 5555555555555L);
        jsonObject.put("bookName", "Test Book");
        jsonObject.put("bookGenre", BookGenre.SCIFI);
        jsonObject.put("bookVersion", 1);
        jsonObject.put("bookCreationDate", LocalDateTime.now());
        jsonObject.put("bookUpdateDate", LocalDateTime.now());

        return jsonObject;
    }

    public String getNeuromancerId() {
        return given().contentType(ContentType.JSON)
                .param("bookName", "neuromancer")
                .when().filter(apiValidationFilter)
                .get(BookController.BOOK_PATH).jsonPath().get("content[0].bookId");
    }

    public String getTestBookId() {
        return given().contentType(ContentType.JSON)
                .param("bookName", "test book")
                .when().filter(apiValidationFilter)
                .get(BookController.BOOK_PATH).jsonPath().get("content[0].bookId");
    }
}
