package edu.cmu.sv.app17.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import edu.cmu.sv.app17.exceptions.APPBadRequestException;
import edu.cmu.sv.app17.exceptions.APPInternalServerException;
import edu.cmu.sv.app17.exceptions.APPNotFoundException;
import edu.cmu.sv.app17.exceptions.APPUnauthorizedException;
import edu.cmu.sv.app17.helpers.*;
import edu.cmu.sv.app17.models.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;


import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Path("favs")

public class FavInterface {
    private MongoCollection<Document> userCollection;
    private MongoCollection<Document> adminCollection;
    private MongoCollection<Document> favCollection;
    private ObjectWriter ow;

    public FavInterface(){
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("app17-5");

        this.userCollection = database.getCollection("users");
        this.adminCollection = database.getCollection("admins");
        this.favCollection = database.getCollection("favs");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll(@Context HttpHeaders headers) {

        ArrayList<Fav> favList = new ArrayList<>();
        try {
            Authorization.checkAdmin(headers);
            FindIterable<Document> results = favCollection.find();
            if (results == null) {
                return new APPResponse(favList);
            }
            for (Document item : results) {
                Fav fav = new Fav(
                        item.getString("userId"),
                        item.getString("showId")
                );
                fav.setId(item.getObjectId("_id").toString());
                favList.add(fav);
            }
            return new APPResponse(favList);
        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"There are no users.");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        } catch(APPUnauthorizedException e){
            throw new APPUnauthorizedException(70,"Not authorized.");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }

    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getOne(@Context HttpHeaders headers,@PathParam("id") String id) {


        BasicDBObject query = new BasicDBObject();

        try {

            query.put("_id", new ObjectId(id));
            Document item = favCollection.find(query).first();
            Authorization.checkSelfandAdmin(headers,item.getString("userId"));
            Fav fav = new Fav(
                    item.getString("userId"),
                    item.getString("showId")
            );
            fav.setId(item.getObjectId("_id").toString());
            return new APPResponse(fav);

        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        } catch(APPUnauthorizedException e){
            throw new APPUnauthorizedException(70,"Not authorized.");
        }   catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }

    }

    @DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete(@Context HttpHeaders headers,
                              @PathParam("id") String id) {
        try {

            Authorization.checkAdmin(headers);

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            DeleteResult deleteResult = favCollection.deleteOne(query);
            if (deleteResult.getDeletedCount() < 1)
                throw new APPNotFoundException(66, "Could not delete");
        }catch(APPUnauthorizedException e){
            throw new APPUnauthorizedException(70,"Not authorized.");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happens!");
        }


        return new APPResponse();
    }

}
