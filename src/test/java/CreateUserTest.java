import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateUserTest {

    private final User user = User.getRandomUser();

    @After
    public void tearDown(){
        UserClient.deleteUser(user);
    }

    @Test
    public void userCanBeCreatedWithValidCredentials(){
        ValidatableResponse createResponse = UserClient.createUser(new User(user.getEmail(), user.getPassword(), user.getName()));
        boolean result = createResponse.extract().path("success");
        int statusCode = createResponse.extract().statusCode();

        assertThat("Can't create user", statusCode, equalTo(SC_OK));
        assertThat(result,equalTo(true));
    }

    @Test
    public void userCantBeCreatedWithSameCredentials(){
        UserClient.createUser(new User("RandomEmail@praktikum.ru", user.getPassword(), user.getName()));
        ValidatableResponse createResponse = UserClient.createUser(new User("RandomEmail@praktikum.ru", user.getPassword(), user.getName()));
        boolean result = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");
        int statusCode = createResponse.extract().statusCode();

        assertThat("User created", statusCode, equalTo(SC_FORBIDDEN));
        assertThat(message, equalTo("User already exists"));
        assertThat(result,equalTo(false));
    }

    @Test
    public void userCantBeCreatedWithoutEmail(){
        ValidatableResponse createResponse = UserClient.createUser(new User("", user.getPassword(), user.getName()));
        boolean result = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");
        int statusCode = createResponse.extract().statusCode();

        assertThat("User created", statusCode, equalTo(SC_FORBIDDEN));
        assertThat(message, equalTo("Email, password and name are required fields"));
        assertThat(result,equalTo(false));
    }

    @Test
    public void userCantBeCreatedWithoutPassword(){
        ValidatableResponse createResponse = UserClient.createUser(new User(user.getEmail(), "", user.getName()));
        boolean result = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");
        int statusCode = createResponse.extract().statusCode();

        assertThat("User created", statusCode, equalTo(SC_FORBIDDEN));
        assertThat(message, equalTo("Email, password and name are required fields"));
        assertThat(result,equalTo(false));
    }

    @Test
    public void userCantBeCreatedWithoutName(){
        ValidatableResponse createResponse = UserClient.createUser(new User(user.getEmail(), user.getPassword(),""));
        boolean result = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");
        int statusCode = createResponse.extract().statusCode();

        assertThat("User created", statusCode, equalTo(SC_FORBIDDEN));
        assertThat(message, equalTo("Email, password and name are required fields"));
        assertThat(result,equalTo(false));
    }
}
