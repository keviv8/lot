package mainer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Cookie;
import io.restassured.response.Response;

import java.io.IOException;
import java.text.ParseException;

import static io.restassured.RestAssured.given;
/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException, ParseException {
        System.out.println("Hello World!");
        System.out.println(productDetails().getStatusCode());
        System.out.println(productDetails().getBody().asString());
        Response ci = cartItem();
        System.out.println(ci.getStatusCode());
        System.out.println(ci.getBody().asString());
    }

    public static Response productDetails() {
        Response prodDetails_response;
        prodDetails_response = given().when().get("https://api.takealot.com/rest/v-1-9-1/product-details/PLID68128803?platform=desktop&size=Single");
        return prodDetails_response;
    }

    public static Response guestLogin() {
        Response guestLogin_response;
        guestLogin_response = given().when().post("https://api.takealot.com/rest/v-1-9-1/guests/login");
        return guestLogin_response;
    }

    public static Response cartItem() throws IOException, ParseException {
        Response cartAdd_response;
        Response gLogin = guestLogin();
        System.out.println(gLogin.getDetailedCookies());
        Cookie gLogin_cookie_taid = gLogin.getDetailedCookie("taid");
        Cookie gLogin_cookie_tal_csrf = gLogin.getDetailedCookie("tal_csrf");
        Cookie gLogin_cookie_tausr = gLogin.getDetailedCookie("tausr");
        Cookie gLogin_cookie_cfduid = gLogin.getDetailedCookie("__cfduid");
        String csrf_id = readKeyValueStringFromJSON("csrf_token", gLogin.getBody().asString());
        String guest_id = readKeyValueStringFromJSON("guest_id", gLogin.getBody().asString());
        System.out.println("csrf_id " + csrf_id);
        String payload = "{\"products\":[{\"id\":92247807,\"quantity\":1,\"enhancedEcommerceAddToCart\":{\"ecommerce\":{\"add\":{\"products\":[{\"category\":\"Home & Kitchen/Homeware/Furniture/Bedroom Furniture/Beds\",\"name\":\"Postucare Limitless Mattress Only - 2 Comfort Levels\",\"dimension1\":\"Single\",\"price\":3399,\"variant\":null,\"id\":\"PLID68128803\",\"position\":0,\"brand\":\"Postucare\",\"quantity\":1}]},\"currencyCode\":\"ZAR\"},\"event\":\"addToCart\"}}]}";
        cartAdd_response = given().
                when().
                cookie(gLogin_cookie_taid).
                cookie(gLogin_cookie_tal_csrf).
                cookie(gLogin_cookie_tausr).
                cookie(gLogin_cookie_cfduid).
                header("x-csrf-token", csrf_id).
                body(payload).
                post("https://api.takealot.com/rest/v-1-9-1/customers/" + guest_id + "/cart/items");
        return cartAdd_response;
    }

    public static String readKeyValueStringFromJSON(String variablename, String json) throws IOException, ParseException {
        ObjectMapper om = new ObjectMapper();
//        JsonNode node = om.readTree(readJsonFromFile("C:\\Users\\keviv\\Documents\\Project Arena\\filezilla_rob\\RetrieveParty\\t_1571897769836\\response.json"));
        JsonNode node = om.readTree(json);
        JsonNode string = node.findValue(variablename);
//        System.out.println("Path " + node.findPath("responseObjects").get(0));
        return string.toString();
    }
}
