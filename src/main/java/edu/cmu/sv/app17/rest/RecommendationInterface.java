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
import com.sun.org.apache.regexp.internal.RE;
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

@Path("recommendations")

public class RecommendationInterface {
    private MongoCollection<Document> userCollection;
    private MongoCollection<Document> adminCollection;
    private MongoCollection<Document> recCollection;
    private ObjectWriter ow;

    public RecommendationInterface(){
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("app17-5");

        this.userCollection = database.getCollection("users");
        this.adminCollection = database.getCollection("admins");
        this.recCollection = database.getCollection("recs");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll(@Context HttpHeaders headers) {

        ArrayList<Recommendation> recList = new ArrayList<>();
        try {
            Authorization.checkAdmin(headers);
            FindIterable<Document> results = recCollection.find();
            if (results == null) {
                return new APPResponse(recList);
            }
            for (Document item : results) {
                Recommendation rec = new Recommendation(
                        item.getString("userId"),
                        item.getString("showId")
                );
                rec.setId(item.getObjectId("_id").toString());
                recList.add(rec);
            }
            return new APPResponse(recList);
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

            query.put("userId", id);
            Document item = recCollection.find(query).first();
            Authorization.checkSelfandAdmin(headers,item.getString("userId"));
            Recommendation rec = new Recommendation(
                    item.getString("userId"),
                    item.getString("showId")
            );
            rec.setId(item.getObjectId("_id").toString());
            return new APPResponse(rec);

        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        } catch(APPUnauthorizedException e){
            throw new APPUnauthorizedException(70,"Not authorized.");
        }   catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }

    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse createshow(@Context HttpHeaders headers,
                                  Object obj) {
        JSONObject json = null;
        try {
            Authorization.checkAdmin(headers);
            json = new JSONObject(ow.writeValueAsString(obj));
            if (!json.has("userId"))
                throw new APPBadRequestException(55, "missing userId");
            if (!json.has("showId"))
                throw new APPBadRequestException(55, "missing showId");


            Document doc = new Document("userId", json.getString("userId"))
                    .append("showId", json.getString("showId"));
            recCollection.insertOne(doc);
            return new APPResponse(obj);
        } catch (JSONException e) {
            throw new APPBadRequestException(33, e.getMessage());
        } catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        } catch(APPUnauthorizedException e){
            throw new APPUnauthorizedException(70,"Not authorized.");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happens!");
        }
    }

}
