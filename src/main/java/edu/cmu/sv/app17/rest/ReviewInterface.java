package edu.cmu.sv.app17.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.MongoSocketOpenException;
import edu.cmu.sv.app17.exceptions.APPBadRequestException;
import edu.cmu.sv.app17.exceptions.APPInternalServerException;
import edu.cmu.sv.app17.exceptions.APPNotFoundException;
import edu.cmu.sv.app17.exceptions.APPUnauthorizedException;
import edu.cmu.sv.app17.helpers.APPResponse;
import edu.cmu.sv.app17.helpers.Authorization;
import edu.cmu.sv.app17.helpers.PATCH;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import edu.cmu.sv.app17.models.Review;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

@Path("reviews")
public class ReviewInterface {

    private MongoCollection<Document> collection = null;
    private ObjectWriter ow;

    public ReviewInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("app17-5");
        collection = database.getCollection("reviews");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll(@Context HttpHeaders headers, @DefaultValue("_id") @QueryParam("sort") String sortArg) {

        ArrayList<Review> reviewList = new ArrayList<Review>();

        BasicDBObject sortParams = new BasicDBObject();
        List<String> sortList = Arrays.asList(sortArg.split(","));
        sortList.forEach(sortItem -> {
            sortParams.put(sortItem,1);
        });

        try {
            Authorization.checkUser(headers);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            FindIterable<Document> results = collection.find().sort(sortParams);
            for (Document item : results) {
                String showId = item.getString("showId");
                Review review = new Review(
                        showId,
                        item.getString("userId"),
                        sdf.format(item.getDate("createDate")),
                        item.getString("reviewTopic"),
                        item.getString("reviewContent")
                );
                review.setId(item.getObjectId("_id").toString());
                reviewList.add(review);
            }
            return new APPResponse(reviewList);

        } catch(APPUnauthorizedException e){
            throw new APPUnauthorizedException(70,"Not authorized.");
        }  catch(Exception e) {
            System.out.println("EXCEPTION!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }
    }


    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getOne(@Context HttpHeaders headers,@PathParam("id") String id) {


        BasicDBObject query = new BasicDBObject();

        try {
            Authorization.checkUser(headers);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            query.put("_id", new ObjectId(id));
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "Review not found.");
            }
            Review review = new Review(
                    item.getString("showId"),
                    item.getString("userId"),
                    sdf.format(item.getDate("createDate")),
                    item.getString("reviewTopic"),
                    item.getString("reviewContent")
            );
            review.setId(item.getObjectId("_id").toString());
            return new APPResponse(review);

        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        } catch(APPUnauthorizedException e){
            throw new APPUnauthorizedException(70,"Not authorized.");
        }   catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }


    }




    @PATCH
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse update(@Context HttpHeaders headers,@PathParam("id") String id, Object request) throws ParseException {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }

        try {

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));
            Document item = collection.find(query).first();
            Authorization.checkSelfandAdmin(headers,item.getString("userId"));


            Document doc = new Document();
            if (json.has("showId"))
                doc.append("showId",json.getString("showId"));
            if (json.has("userId"))
                doc.append("userId",json.getString("userId"));

            if(json.has("createDate")){
                String createdt = json.getString("createDate");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date parsedate = df.parse(createdt);
                doc.append("createDate",parsedate);
            }

            if (json.has("reviewTopic"))
                doc.append("reviewTopic",json.getString("reviewTopic"));
            if (json.has("reviewContent"))
                doc.append("reviewContent",json.getString("reviewContent"));
            Document set = new Document("$set", doc);
            collection.updateOne(query,set);

        } catch(JSONException e) {
            System.out.println("Failed to update a document");

        }catch(APPUnauthorizedException e){
            throw new APPUnauthorizedException(70,"Not authorized.");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happens!");
        }
        return new APPResponse();
    }


    @DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete(@Context HttpHeaders headers,@PathParam("id") String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        try {
            Document item = collection.find(query).first();
            Authorization.checkSelfandAdmin(headers,item.getString("userId"));
            DeleteResult deleteResult = collection.deleteOne(query);
            if (deleteResult.getDeletedCount() < 1)
                throw new APPNotFoundException(66, "Could not delete");
        } catch(APPUnauthorizedException e){
            throw new APPUnauthorizedException(70,"Not authorized.");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happens!");
        }

        return new APPResponse();
    }
}
