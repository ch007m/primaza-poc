package io.halkyon;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import io.halkyon.model.Cluster;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ClustersEndpointTest {

    @Test
    public void testAddCluster(){
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json")
                .body("{\"name\": \"ocp4.11-node-1\", \"environment\": \"PROD\", \"url\": \"https://10.0.2.11:6443\" }")
                .when().post("/clusters")
                .then()
                .statusCode(201);

    }

    @Test
    public void testAddClusterViaHtmxForm(){
        // An htmx request will contain a HX-Request header and Content-Type: application/x-www-form-urlencoded
        given()
                .header("HX-Request", true)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body("{\"name\": \"ocp4.11-node-2\", \"environment\": \"TEST\", \"url\": \"https://10.0.2.12:6443\" }")
                .when().post("/clusters")
                .then()
                .statusCode(201);
    }

    @Test
    public void testClusterEntity() {
        final String path="/clusters";
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json")
                .body("{\"name\": \"ocp4.11-node-3\", \"environment\": \"PROD\", \"url\": \"https://10.0.2.11:6443\" }")
                .when().post("/clusters")
                .then()
                .statusCode(201);

        Cluster cluster = given()
                .when().get("/clusters/name/ocp4.11-node-3")
                .then()
                .statusCode(200)
                .extract().as(Cluster.class);

        //Delete the 'ocp4.11-node-3':
        given()
                .when().delete(path + "/" + cluster.id)
                .then()
                .statusCode(204);

        //List all, 'ocp4.11-node-3' should be missing now:
        given()
                .when().get(path)
                .then()
                .statusCode(200)
                .body(not(containsString("ocp4.11-node-3")));
    }

}