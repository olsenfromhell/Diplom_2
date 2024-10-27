package site.nomoreparties.stellarburgers.user.login;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.nomoreparties.stellarburgers.EnvConfig;
import site.nomoreparties.stellarburgers.user.UserActions;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.Matchers.*;

public class LoginTest {

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = EnvConfig.BASE_URL;
    }

    UserActions createUser = new UserActions();

    @Test
    @DisplayName("Логин под существующим пользователем")
    public void loginWithExistingUserTest() {
        Response response = createUser.loginWithExistingUser("test-data@yandex.ru", "password", "Username");

        response.then()
                .statusCode(HTTP_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("accessToken", containsString("Bearer"));
    }

    @Test
    @DisplayName("Логин с неверным логином и паролем")
    public void loginWithIncorrectDataTest() {
        Response response = createUser.loginWithExistingUser("incorrect-data@test.ru", "test", "Username");

        response.then()
                .statusCode(HTTP_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));

    }
}
