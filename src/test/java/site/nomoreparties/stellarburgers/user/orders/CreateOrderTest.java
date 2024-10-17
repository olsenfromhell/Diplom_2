package site.nomoreparties.stellarburgers.user.orders;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.nomoreparties.stellarburgers.EnvConfig;
import site.nomoreparties.stellarburgers.FakerData;
import site.nomoreparties.stellarburgers.user.UserActions;

import java.util.Arrays;
import java.util.List;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTest {

    private UserActions userActions = new UserActions();
    private String accessToken;

    private String[] ids = {"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa70"};

    List<String> ingredientIds = Arrays.asList(ids);


    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = EnvConfig.BASE_URL;

        // создаем пользователя и забираем токен
        Response response = userActions.createNewUser(FakerData.email, FakerData.password, FakerData.name);
        accessToken = response.jsonPath().getString("accessToken");
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null) {
            Response response = UserActions.deleteUser(accessToken);
            response.then()
                    .statusCode(HTTP_ACCEPTED)
                    .and().assertThat()
                    .body("success", equalTo(true));
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    public void createOrderWithAuthTest() {
        Response response = userActions.createOrderWithAuth(accessToken, ingredientIds);

        response.then()
                .statusCode(HTTP_OK)
                .and().assertThat()
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutAuthTest() {
        Response response = userActions.createOrderWithoutAuth(ingredientIds);

        response.then()
                .statusCode(HTTP_OK)
                .and().assertThat()
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с невалидным ингредиентом")
    public void createOrderWithInvalidIngredientTest() {
        Response response = userActions.createOrderWithAuth( accessToken, Arrays.asList("invalid_id"));

        response.then()
                .statusCode(HTTP_INTERNAL_ERROR);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        Response response = userActions.createOrderWithAuth(accessToken, null);

        response.then()
                .statusCode(HTTP_BAD_REQUEST)
                .and().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }
}
