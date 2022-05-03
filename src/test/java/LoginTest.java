import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class LoginTest {

    private UserClient userClient;
    private User user;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandomUser();
        userClient.createUser(user);
    }

    @After
    public void tearDown(){
        userClient.deleteUser(user);
    }

    @Test
    public void userCanLoginWithExistingCredentials(){
        ValidatableResponse loginResponse = userClient.login(new User(user.getEmail(), user.getPassword(), user.getName()));
        boolean result = loginResponse.extract().path("success");
        int statusCode = loginResponse.extract().statusCode();

        assertThat("Can't login", statusCode, equalTo(SC_OK));
        assertThat(result,equalTo(true));
    }

    @Test
    public void userCantLoginWithNonExistingEmail(){
        ValidatableResponse loginResponse = userClient.login(new User("NonExistingEmail@praktikum.ru", user.getPassword(), user.getName()));
        boolean result = loginResponse.extract().path("success");
        String message = loginResponse.extract().path("message");
        int statusCode = loginResponse.extract().statusCode();

        assertThat("", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat(message, equalTo("email or password are incorrect"));
        assertThat(result,equalTo(false));
    }

    @Test
    public void userCantLoginWithNonExistingPassword(){
        ValidatableResponse loginResponse = userClient.login(new User(user.getEmail(), "NonExistingPassword", user.getName()));
        boolean result = loginResponse.extract().path("success");
        String message = loginResponse.extract().path("message");
        int statusCode = loginResponse.extract().statusCode();

        assertThat("", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat(message, equalTo("email or password are incorrect"));
        assertThat(result,equalTo(false));
    }

}
