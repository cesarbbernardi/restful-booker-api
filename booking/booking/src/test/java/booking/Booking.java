package booking;

/*
This package implements some API tests for the Restful Booker Demo API.
*/

import java.io.IOException;
import org.testng.annotations.Test;
import org.testng.SkipException;
import static io.restassured.RestAssured.*;

public class Booking {
    String uri = "https://restful-booker.herokuapp.com/";
    String authToken = null; // stores the authtoken for a few tests
    String skipMessage = "API ping check failed. Skipping test"; // message to be displayed in case of a skip
    Integer bookingId = null; // stores a newly generated POSTed booking, to be used in some other tests
    boolean pingCheck = false; // stores the API status after a ping

    @Test (priority = 0)
    public void pingCheck() {   // pings the API to see if its up and running. If it fails, all other tests are skipped.
        given()
            .contentType("application/json")
            .log().all()
        .when()
            .get(uri+"ping")
        .then()
            .log().all()    
            .statusCode(201)
        ;
        pingCheck = true;  
    }

    @Test (priority=1)
    public void auth_getToken() throws IOException { // gets the token for authentication, to be used with the PUT and DELETE commands
        String jsonBody = Functions.readJSON("db/auth_credentials.json"); 

        if(!pingCheck) {
            throw new SkipException("API ping check failed. Skipping test");
        }
        authToken = 
            given()
                .contentType("application/json")
                .log().all()
                .body(jsonBody)
            .when()
                .post(uri+"auth")
            .then()
                .log().all()
                .statusCode(200)
                .extract().response().path("token")
            ;
    }

    @Test (priority = 2)
    public void postBooking() throws IOException {  // POSTs a new booking
        String jsonBody = Functions.readJSON("db/booking1.json");
            

        if(!pingCheck) {
            throw new SkipException("API ping check failed. Skipping test");
        }
            bookingId = 
            given()
                .contentType("application/json")
                .log().all()
                .body(jsonBody)
            .when()
                .post(uri+"booking")
            .then()
                .log().all()
                .statusCode(200)
                .extract().response().path("bookingid")
            ;
            
        }

        @Test (priority = 3)
        public void getBooking_specific() throws IOException { // searches for a pet using the id from the JSON file
            
            if(!pingCheck) {
                throw new SkipException("API ping check failed. Skipping test");
            }
                given()
                    .contentType("application/json")
                    .log().all()   
                .when()
                    .get(uri+"booking/"+bookingId)
                .then()
                    .log().all()
                    .statusCode(200)
                ;
            }

    @Test (priority = 4)
    public void putBooking() throws IOException {   // fully updates a POSTed booking using PUT. Requires auth token to work
        String jsonBody = Functions.readJSON("db/booking1updt.json");
        
        if(!pingCheck) {
            throw new SkipException("API ping check failed. Skipping test");
        }

            given()
                .cookie("token",authToken)
                /*.auth().oauth2(auth_getToken())
                command that should've been used normally.
                However, this website uses the auth tokens as cookies for whatever reason */
                .contentType("application/json")
                .log().all()
                .body(jsonBody)
            .when()
                .put(uri+"booking/"+bookingId)
            .then()
                .log().all()
                .statusCode(200)
            ;
        }

        @Test (priority = 5)
        public void patchBooking() throws IOException { // partially updates a POSTed booking using PATCH. Requires auth token to work
            String jsonBody = Functions.readJSON("db/booking2updt.json");
            
            if(!pingCheck) {
                throw new SkipException("API ping check failed. Skipping test");
            }

                given()
                    .cookie("token",authToken)
                    /*.auth().oauth2(auth_getToken())
                    command that should've been used normally.
                    However, this website uses the auth tokens as cookies for whatever reason */
                    .contentType("application/json")
                    .log().all()
                    .body(jsonBody)
                .when()
                    .patch(uri+"booking/"+bookingId)
                .then()
                    .log().all()
                    .statusCode(200)
                ;
            }

        @Test (priority = 6)
        public void getBooking_all() throws IOException { // returns a list of all booking IDs
            if(!pingCheck) {
            throw new SkipException("API ping check failed. Skipping test");
            }

                given()
                    .contentType("application/json")
                    .log().all()   
                .when()
                    .get(uri+"booking")
                .then()
                    .log().all()
                    .statusCode(200)
                ;
            }

        @Test (priority = 7)
        public void deleteBooking() {   // removes a POSTed booking using DELETE. Requires auth token to work
                        
            if(!pingCheck) {
                throw new SkipException("API ping check failed. Skipping test");
            }
    
                given()
                    .cookie("token",authToken)
                    /*.auth().oauth2(auth_getToken())
                    command that should've been used normally.
                    However, this website uses the auth tokens as cookies for whatever reason */
                    .contentType("application/json")
                    .log().all()
                .when()
                    .delete(uri+"booking/"+bookingId)
                .then()
                    .log().all()
                    .statusCode(201)
                ;

                given() // tries to access the deleted booking once more. A 404 error is expected.
                    .contentType("application/json")
                    .log().all()
                .when()
                    .get(uri+"booking"+bookingId)
                .then()
                    .log().all()
                    .statusCode(404)
                ;
            }

}