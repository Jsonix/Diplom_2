import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class LoginTest {

    private final User user = User.getRandomUser();

    @Before
    public void setUp() {
        UserClient.createUser(user);
    }

    @After
    public void tearDown(){
        UserClient.deleteUser(user);
    }

    @Test
    public void userCanLoginWithExistingCredentials(){
        ValidatableResponse loginResponse = UserClient.login(new User(user.getEmail(), user.getPassword(), user.getName()));
        boolean result = loginResponse.extract().path("success");
        int statusCode = loginResponse.extract().statusCode();

        assertThat("Can't login", statusCode, equalTo(SC_OK));
        assertThat(result,equalTo(true));
    }

    @Test
    public void userCantLoginWithNonExistingEmail(){
        ValidatableResponse loginResponse = UserClient.login(new User("NonExistingEmail@praktikum.ru", user.getPassword(), user.getName()));
        boolean result = loginResponse.extract().path("success");
        String message = loginResponse.extract().path("message");
        int statusCode = loginResponse.extract().statusCode();

        assertThat("", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat(message, equalTo("email or password are incorrect"));
        assertThat(result,equalTo(false));
    }

    @Test
    public void userCantLoginWithNonExistingPassword(){
        ValidatableResponse loginResponse = UserClient.login(new User(user.getEmail(), "NonExistingPassword", user.getName()));
        boolean result = loginResponse.extract().path("success");
        String message = loginResponse.extract().path("message");
        int statusCode = loginResponse.extract().statusCode();

        assertThat("", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat(message, equalTo("email or password are incorrect"));
        assertThat(result,equalTo(false));
    }

}
