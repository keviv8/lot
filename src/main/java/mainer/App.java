package mainer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Cookie;
import io.restassured.response.Response;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Properties;

import static io.restassured.RestAssured.given;
/**
 * Hello world!
 */
public class App {
    private static Properties prop = new Properties();

    public static void main(String[] args) throws IOException, ParseException {
        readProps();
        Response pd = productDetails();
        Response ci = cartItem();
        System.out.println(pd.getStatusCode());
        System.out.println(pd.getBody().asString());
        System.out.println(ci.getStatusCode());
        System.out.println(ci.getBody().asString());
    }

    public static Response productDetails() {
        Response prodDetails_response;
        prodDetails_response = given().when().get(prop.getProperty("product.details").replace("$product_id", "PLID68128803"));
        return prodDetails_response;
    }

    public static Response guestLogin() {
        Response guestLogin_response;
        guestLogin_response = given().when().post(prop.getProperty("guest.login"));
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
        String payloader = prop.getProperty("cartitem.payload");
        cartAdd_response = given().
                when().
                cookie(gLogin_cookie_taid).
                cookie(gLogin_cookie_tal_csrf).
                cookie(gLogin_cookie_tausr).
                cookie(gLogin_cookie_cfduid).
                header("x-csrf-token", csrf_id).
                body(payloader).
                post(prop.getProperty("add.cart").replace("$guest_id", guest_id));
        return cartAdd_response;
    }

    public static void readProps() {
        try (InputStream input = new FileInputStream("dot.properties")) {
            prop = new Properties();
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readKeyValueStringFromJSON(String variablename, String json) throws IOException, ParseException {
        ObjectMapper om = new ObjectMapper();
        JsonNode node = om.readTree(json);
        JsonNode string = node.findValue(variablename);
        return string.toString();
    }
}
