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

import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.*;

public class GetUserOrdersTest {

    private UserActions userActions = new UserActions();
    private String accessToken;

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
    @DisplayName("Получение заказов для авторизованного пользователя")
    public void getUserOrdersWithAuthTest() {
        Response response = userActions.getUserOrders(accessToken);

        response.then()
                .statusCode(HTTP_OK)
                .and().assertThat()
                .body("success", equalTo(true))
                .body("orders", notNullValue())
                .body("total", greaterThan(0))
                .body("totalToday", greaterThan(0));
    }

    @Test
    @DisplayName("Получение заказов для неавторизованного пользователя")
    public void getUserOrdersWithoutAuthTest() {
        Response response = userActions.getUserOrdersWithoutAuth();

        response.then()
                .statusCode(HTTP_UNAUTHORIZED)
                .and().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
