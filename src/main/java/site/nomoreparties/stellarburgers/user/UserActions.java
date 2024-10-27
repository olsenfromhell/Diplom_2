package site.nomoreparties.stellarburgers.user;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import site.nomoreparties.stellarburgers.EnvConfig;
import site.nomoreparties.stellarburgers.user.create.CreateUserPOJO;
import site.nomoreparties.stellarburgers.user.orders.CreateOrderPOJO;
import site.nomoreparties.stellarburgers.user.update.UpdateUserPOJO;

import java.util.List;

import static io.restassured.RestAssured.given;


public class UserActions {

    @Step("Create new user")
    public Response createNewUser(String email, String password, String name) {
        CreateUserPOJO newUser = new CreateUserPOJO(email, password, name);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(newUser)
                        .when()
                        .post(EnvConfig.USER_CREATE_ENDPOINT);

        return response;
    }

    @Step("Login with existing user")
    public Response loginWithExistingUser(String email, String password, String name) {
        CreateUserPOJO newUser = new CreateUserPOJO(email, password, name);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(newUser)
                        .when()
                        .post(EnvConfig.USER_LOGIN_ENDPOINT);

        return response;
    }

    @Step("Update user data with authorization")
    public Response updateUserWithAuth(String accessToken, String email, String name) {
        UpdateUserPOJO updatedUser = new UpdateUserPOJO(email, name);

        return given()
                .contentType(ContentType.JSON)
                .header("authorization", accessToken)
                .body(updatedUser)
                .when()
                .patch(EnvConfig.USER_DATA_ENDPOINT);
    }

    @Step("Update user data without authorization")
    public Response updateUserWithoutAuth(String email, String name) {
        UpdateUserPOJO updatedUser = new UpdateUserPOJO(email, name);

        return given()
                .contentType(ContentType.JSON)
                .body(updatedUser)
                .when()
                .patch(EnvConfig.USER_DATA_ENDPOINT);
    }

    @Step("Delete user")
    public static Response deleteUser(String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .header("authorization", accessToken)
                .when()
                .delete(EnvConfig.USER_DATA_ENDPOINT);
    }

    @Step("Create order with authorization")
    public Response createOrderWithAuth(String accessToken, List<String> ingredientIds) {
        CreateOrderPOJO order = new CreateOrderPOJO(ingredientIds);

        return given()
                .contentType(ContentType.JSON)
                .header("authorization", accessToken)
                .body(order)
                .when()
                .post(EnvConfig.USER_ORDERS_ENDPOINT);
    }

    @Step("Create order without authorization")
    public Response createOrderWithoutAuth(List<String> ingredientIds) {
        CreateOrderPOJO order = new CreateOrderPOJO(ingredientIds);

        return given()
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post(EnvConfig.USER_ORDERS_ENDPOINT);
    }

    @Step("Get user orders with authorization")
    public Response getUserOrders(String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .header("authorization", accessToken)
                .when()
                .get(EnvConfig.USER_ORDERS_ENDPOINT);
    }

    @Step("Get user orders without authorization")
    public Response getUserOrdersWithoutAuth() {
        return given()
                .contentType(ContentType.JSON)
                .when()
                .get(EnvConfig.USER_ORDERS_ENDPOINT);
    }

}
