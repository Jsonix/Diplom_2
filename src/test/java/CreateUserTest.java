import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateUserTest {

    private UserClient userClient;
    private User user;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandomUser();
    }

    @After
    public void tearDown(){
        userClient.deleteUser(user);
    }

    @Test
    public void userCanBeCreatedWithValidCredentials(){
        ValidatableResponse createResponse = userClient.createUser(new User(user.getEmail(), user.getPassword(), user.getName()));
        boolean result = createResponse.extract().path("success");
        int statusCode = createResponse.extract().statusCode();

        assertThat("Can't create user", statusCode, equalTo(SC_OK));
        assertThat(result,equalTo(true));
    }

    @Test
    public void userCantBeCreatedWithSameCredentials(){
        userClient.createUser(new User("RandomEmail@praktikum.ru", user.getPassword(), user.getName()));
        ValidatableResponse createResponse = userClient.createUser(new User("RandomEmail@praktikum.ru", user.getPassword(), user.getName()));
        boolean result = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");
        int statusCode = createResponse.extract().statusCode();

        assertThat("User created", statusCode, equalTo(SC_FORBIDDEN));
        assertThat(message, equalTo("User already exists"));
        assertThat(result,equalTo(false));
    }

    @Test
    public void userCantBeCreatedWithoutEmail(){
        ValidatableResponse createResponse = userClient.createUser(new User("", user.getPassword(), user.getName()));
        boolean result = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");
        int statusCode = createResponse.extract().statusCode();

        assertThat("User created", statusCode, equalTo(SC_FORBIDDEN));
        assertThat(message, equalTo("Email, password and name are required fields"));
        assertThat(result,equalTo(false));
    }

    @Test
    public void userCantBeCreatedWithoutPassword(){
        ValidatableResponse createResponse = userClient.createUser(new User(user.getEmail(), "", user.getName()));
        boolean result = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");
        int statusCode = createResponse.extract().statusCode();

        assertThat("User created", statusCode, equalTo(SC_FORBIDDEN));
        assertThat(message, equalTo("Email, password and name are required fields"));
        assertThat(result,equalTo(false));
    }

    @Test
    public void userCantBeCreatedWithoutName(){
        ValidatableResponse createResponse = userClient.createUser(new User(user.getEmail(), user.getPassword(),""));
        boolean result = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");
        int statusCode = createResponse.extract().statusCode();

        assertThat("User created", statusCode, equalTo(SC_FORBIDDEN));
        assertThat(message, equalTo("Email, password and name are required fields"));
        assertThat(result,equalTo(false));
    }
}
