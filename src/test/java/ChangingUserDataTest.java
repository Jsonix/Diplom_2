import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ChangingUserDataTest {

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
    public void userCanUpdateEmail(){
        String newEmail = User.getRandomEmail();
        User newUser = new User(newEmail, user.getPassword(), user.getName());
        ValidatableResponse changeResponse = UserClient.changeUserData(UserClient.getToken(user), newUser);
        boolean result = changeResponse.extract().path("success");
        String email = changeResponse.extract().path("user.email");
        int statusCode = changeResponse.extract().statusCode();

        assertThat(result,equalTo(true));
        assertThat(email,equalTo(newEmail.toLowerCase()));
        assertThat(statusCode,equalTo(SC_OK));
    }

    @Test
    public void userCanUpdatePassword(){
        String newPassword = User.getRandomData();
        User newUser = new User(user.getEmail(), newPassword, user.getName());
        ValidatableResponse changeResponse = UserClient.changeUserData(UserClient.getToken(user), newUser);
        boolean result = changeResponse.extract().path("success");
        int statusCode = changeResponse.extract().statusCode();

        assertThat(result,equalTo(true));
        assertThat(statusCode,equalTo(SC_OK));
    }

    @Test
    public void userCanUpdateName(){
        String newName = User.getRandomData();
        User newUser = new User(user.getEmail(), user.getPassword(), newName);
        ValidatableResponse changeResponse = UserClient.changeUserData(UserClient.getToken(user), newUser);
        boolean result = changeResponse.extract().path("success");
        String email = changeResponse.extract().path("user.name");
        int statusCode = changeResponse.extract().statusCode();

        assertThat(result,equalTo(true));
        assertThat(email,equalTo(newName));
        assertThat(statusCode,equalTo(SC_OK));
    }

    @Test
    public void userCantUpdateDataIfHeDidntLoggedIn(){
        ValidatableResponse changeResponse = UserClient.changeUserData("", user);
        boolean result = changeResponse.extract().path("success");
        String message = changeResponse.extract().path("message");
        int statusCode = changeResponse.extract().statusCode();

        assertThat(result,equalTo(false));
        assertThat(message,equalTo("You should be authorised"));
        assertThat(statusCode,equalTo(SC_UNAUTHORIZED));
    }

    @Test
    public void userCanUpdateEmailWhichAlreadyInUse(){
        User user2 = User.getRandomUser();
        UserClient.createUser(user2);

        String newEmail = user2.getEmail();
        User newUser = new User (newEmail, user.getPassword(), user.getName());

        ValidatableResponse changeResponse = UserClient.changeUserData(UserClient.getToken(user), newUser);
        String email = changeResponse.extract().path("user.email");
        boolean result = changeResponse.extract().path("success");
        String message = changeResponse.extract().path("message");
        int statusCode = changeResponse.extract().statusCode();

        assertThat(result,equalTo(false));
        assertThat(message,equalTo("User with such email already exists"));
        assertThat(statusCode,equalTo(SC_FORBIDDEN));
    }
}
