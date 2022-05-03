import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class OrderListTest {

    private UserClient userClient;
    private User user;
    List<String> ingredients = new ArrayList<>();
    public String orderIngredients;
    String accessToken;
    Ingredients createOrder;

    public int randomNumber(){
        Random rand = new Random();
        int upperbound = 10;
        return rand.nextInt(upperbound);
    }

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandomUser();
        ingredients = OrderClient.getIngredients().extract().path("data._id");
        orderIngredients = ingredients.get(randomNumber());
        accessToken = UserClient.createUser(user).extract().path("accessToken");
        createOrder = new Ingredients(orderIngredients);
    }

    @After
    public void tearDown(){
        userClient.deleteUser(user);
    }

    @Test
    public void userCanGetListOfHisOrders(){
         ValidatableResponse getListOrders = OrderClient.getUserOrders(accessToken);
         int statusCode = getListOrders.extract().statusCode();
         boolean result = getListOrders.extract().path("success");

         assertThat(statusCode, equalTo(SC_OK));
         assertThat(result, equalTo(true));
    }

    @Test
    public void userCantGetListOfHisOrdersWithoutLogin(){
        ValidatableResponse getListOrders = OrderClient.getUserOrders("");
        int statusCode = getListOrders.extract().statusCode();
        boolean result = getListOrders.extract().path("success");
        String message = getListOrders.extract().path("message");

        assertThat(statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat(result, equalTo(false));
        assertThat(message, equalTo("You should be authorised"));
    }
}
