import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateOrderTest {

    private final User user = User.getRandomUser();
    List<String> ingredients = new ArrayList<>();


    public int randomNumber(){
        Random rand = new Random();
        int upperbound = 10;
        return rand.nextInt(upperbound);
    }

    @After
    public void tearDown(){
        UserClient.deleteUser(user);
    }

    @Test
    public void userCanCreateAnOrderWithIngredients(){
        String accessToken = UserClient.createUser(user).extract().path("accessToken");
        ingredients = OrderClient.getIngredients().extract().path("data._id");
        Ingredients orderIngredients = new Ingredients(ingredients.get(randomNumber()));
        ValidatableResponse createOrder = OrderClient.createOrder(orderIngredients, accessToken);
        int statusCode = createOrder.extract().statusCode();
        boolean result = createOrder.extract().path("success");
        int number = createOrder.extract().path("order.number");

        assertThat(statusCode, equalTo(SC_OK));
        assertThat(result, equalTo(true));
        assertThat(number, notNullValue());
    }

    @Test
    public void userCanCreateAnOrderWithoutLogin(){
        ingredients = OrderClient.getIngredients().extract().path("data._id");
        Ingredients orderIngredients = new Ingredients(ingredients.get(randomNumber()));
        ValidatableResponse createOrder = OrderClient.createOrder(orderIngredients, "");
        int statusCode = createOrder.extract().statusCode();
        boolean result = createOrder.extract().path("success");
        int number = createOrder.extract().path("order.number");

        assertThat(statusCode, equalTo(SC_OK));
        assertThat(number, notNullValue());
        assertThat(result, equalTo(true));
    }

    @Test
    public void userCantCreateAnOrderWithoutIngredients(){
        String accessToken = UserClient.createUser(user).extract().path("accessToken");
        Ingredients orderIngredients = new Ingredients("");
        ValidatableResponse createOrder = OrderClient.createOrder(orderIngredients, accessToken);
        int statusCode = createOrder.extract().statusCode();
        boolean result = createOrder.extract().path("success");
        String message = createOrder.extract().path("message");

        assertThat(statusCode, equalTo(SC_BAD_REQUEST));
        assertThat(message, equalTo("Ingredient ids must be provided"));
        assertThat(result, equalTo(false));
    }

    @Test
    public void userCantCreateAnOrderWithoutWrongIngredient(){
        String accessToken = UserClient.createUser(user).extract().path("accessToken");
        Ingredients orderIngredients = new Ingredients("Ingredient");
        ValidatableResponse createOrder = OrderClient.createOrder(orderIngredients, accessToken);
        int statusCode = createOrder.extract().statusCode();

        assertThat(statusCode, equalTo(SC_INTERNAL_SERVER_ERROR));
    }

}
