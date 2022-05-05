import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class UserClient extends RestClient{


    public static final String REGISTER_PATH = "api/auth/register";
    public static final String LOGIN_PATH = "api/auth/login";
    public static final String USER_DATA_PATH = "api/auth/user";


    public static ValidatableResponse createUser(User user){
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(REGISTER_PATH)
                .then();
    }


    public static ValidatableResponse login(User user){
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(LOGIN_PATH)
                .then();
    }

    public static ValidatableResponse getUserData(String token){
        return given()
                .header("Authorization", token)
                .spec(getBaseSpec())
                .when()
                .get(USER_DATA_PATH)
                .then();
    }

    public static ValidatableResponse changeUserData(String token, User user){
        return given()
                .header("Authorization", token)
                .spec(getBaseSpec())
                .when()
                .body(user)
                .patch(USER_DATA_PATH)
                .then();
    }

    public static void deleteUser(String token){
        given()
                .header("Authorization", token)
                .spec(getBaseSpec()).when()
                .delete(USER_DATA_PATH)
                .then()
                .assertThat().statusCode(202);
    }

    public static String getToken(User user){
        return login(user).extract().path("accessToken");
    }

    public static void deleteUser(User user) {
        if (getToken(user) != null) {
            deleteUser(getToken(user));
        }
    }
}