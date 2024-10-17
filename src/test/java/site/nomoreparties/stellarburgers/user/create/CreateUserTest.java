package site.nomoreparties.stellarburgers.user.create;

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

public class CreateUserTest {

    private UserActions userActions = new UserActions();
    private String accessToken;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = EnvConfig.BASE_URL;
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
    @DisplayName("Создание уникального пользователя")
    public void createNewUserAndCheckResponseTest() {
        Response response = userActions.createNewUser(FakerData.email, FakerData.password, FakerData.name);
        accessToken = response.jsonPath().getString("accessToken");

        response.then()
                .statusCode(HTTP_OK)
                .and().assertThat()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создать пользователя, который уже зарегистрирован")
    public void createAlreadyRegisteredUserTest() {
        Response response = userActions.createNewUser("test-data@yandex.ru", "password", "Username");

        response.then()
                .statusCode(HTTP_FORBIDDEN)
                .and().assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создать пользователя и не заполнить одно из обязательных полей")
    public void createUserWithEmptyFieldTest() {
        Response response = userActions.createNewUser(FakerData.email, FakerData.password, "");

        response.then()
                .statusCode(HTTP_FORBIDDEN)
                .and().assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
