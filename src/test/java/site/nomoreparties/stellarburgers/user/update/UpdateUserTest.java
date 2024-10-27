package site.nomoreparties.stellarburgers.user.update;

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
import static org.hamcrest.Matchers.equalTo;

public class UpdateUserTest {

    private UserActions userActions = new UserActions();
    private String accessToken;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = EnvConfig.BASE_URL;

        // Создаем нового пользователя и получаем accessToken
        Response response = userActions.createNewUser(FakerData.email, FakerData.password, FakerData.name);
        accessToken = response.jsonPath().getString("accessToken");
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null) {
            Response response = userActions.deleteUser(accessToken);
            response.then()
                    .statusCode(HTTP_ACCEPTED)
                    .and().assertThat()
                    .body("success", equalTo(true));
        }
    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией")
    public void updateUserWithAuthTest() {
        String newEmail = "test-user-for-api-test@test.com";
        String newName = "username";

        Response response = userActions.updateUserWithAuth(accessToken, newEmail, newName);

        response.then()
                .statusCode(HTTP_OK)
                .and().assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail))
                .body("user.name", equalTo(newName));
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    public void updateUserWithoutAuthTest() {
        String newEmail = "test-user-for-api-test@test.com";
        String newName = "username";

        Response response = userActions.updateUserWithoutAuth(newEmail, newName);

        response.then()
                .statusCode(HTTP_UNAUTHORIZED)
                .and().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
