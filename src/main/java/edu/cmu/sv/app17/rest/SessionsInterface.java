package edu.cmu.sv.app17.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.cmu.sv.app17.exceptions.APPBadRequestException;
import edu.cmu.sv.app17.exceptions.APPInternalServerException;
import edu.cmu.sv.app17.exceptions.APPNotFoundException;
import edu.cmu.sv.app17.helpers.APPCrypt;
import edu.cmu.sv.app17.helpers.APPResponse;
import edu.cmu.sv.app17.helpers.Authorization;
import edu.cmu.sv.app17.models.User;
import edu.cmu.sv.app17.models.Session;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("sessions")

public class SessionsInterface {

    private MongoCollection<Document> userCollection;
    private MongoCollection<Document> reviewCollection;
    private ObjectWriter ow;


    public SessionsInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("app17-5");

        this.userCollection = database.getCollection("users");
        this.reviewCollection = database.getCollection("reviews");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }


    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse create( Object request) {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
            if (!json.has("email"))
                throw new APPBadRequestException(55, "missing emailAddress");
            if (!json.has("password"))
                throw new APPBadRequestException(55, "missing password");
            BasicDBObject query = new BasicDBObject();
            query.put("email", json.getString("email"));
            query.put("password", APPCrypt.encrypt(json.getString("password")));

            Document item = userCollection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No user found matching credentials");
            }
            User user = new User(
                    item.getString("userName"),
                    item.getString("email"),
                    item.getString("phone")
            );
            user.setId(item.getObjectId("_id").toString());
            return new APPResponse(new Session(user));
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }
        catch (APPBadRequestException e) {
            throw e;
        }
        catch (APPNotFoundException e) {
            throw e;
        }
        catch (Exception e) {
            throw new APPInternalServerException(0, e.getMessage());
        }
    }
}



