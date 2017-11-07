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
import edu.cmu.sv.app17.helpers.APPCrypt;
import edu.cmu.sv.app17.helpers.APPListResponse;
import edu.cmu.sv.app17.helpers.APPResponse;
import edu.cmu.sv.app17.helpers.PATCH;
import edu.cmu.sv.app17.models.Fav;
import edu.cmu.sv.app17.models.Review;
import edu.cmu.sv.app17.models.User;
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

@Path("users")
public class UserInterface {
    private MongoCollection<Document> collection;
    private MongoCollection<Document> reviewCollection;
    private MongoCollection<Document> favCollection;
    private MongoCollection<Document> adminCollection;
    private ObjectWriter ow;


    public UserInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("app17-5");

        this.collection = database.getCollection("users");
        this.reviewCollection = database.getCollection("reviews");
        this.favCollection = database.getCollection("favs");
        this.adminCollection = database.getCollection("admins");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll() {

        ArrayList<User> userList = new ArrayList<User>();

        FindIterable<Document> results = collection.find();
        if (results == null) {
            return new APPResponse(userList);
        }
        for (Document item : results) {
            User user = new User(
                    item.getString("userName"),
                    item.getString("email"),
                    item.getString("password")
            );
            user.setId(item.getObjectId("_id").toString());
            userList.add(user);
        }
        return new APPResponse(userList);
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getOne(@PathParam("id") String id) {
        BasicDBObject query = new BasicDBObject();
        try {
            query.put("_id", new ObjectId(id));
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "User not found.");
            }
            User user = new User(
                    item.getString("userName"),
                    item.getString("email"),
                    item.getString("password")
            );
            user.setId(item.getObjectId("_id").toString());
            return new APPResponse(user);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No such user");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }


    }

    @GET
    @Path("{id}/reviews")
    @Produces({MediaType.APPLICATION_JSON})
    public APPListResponse getReviewsForUser(@Context HttpHeaders headers, @PathParam("id") String id,
                                             @DefaultValue("_id") @QueryParam("sort") String sortArg,
                                             @DefaultValue("20") @QueryParam("count") int count,
                                             @DefaultValue("0") @QueryParam("offset") int offset
    ) {

        ArrayList<Review> reviewList = new ArrayList<Review>();

        try {
            //checkAuthentication(headers,id);
            BasicDBObject query = new BasicDBObject();
            query.put("userId", id);
            long resultCount = reviewCollection.count(query);

            BasicDBObject sortParams = new BasicDBObject();
            List<String> sortList = Arrays.asList(sortArg.split(","));
            sortList.forEach(sortItem -> {
                sortParams.put(sortItem,1);
            });

            FindIterable<Document> results = reviewCollection.find(query).sort(sortParams).skip(offset).limit(count);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
            return new APPListResponse(reviewList,resultCount,offset,reviewList.size());

        } catch(Exception e) {
            System.out.println("EXCEPTION!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }

    }


    @POST
    @Path("{id}/reviews")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse create(@PathParam("id") String id, Object request) throws ParseException {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }
        if (!json.has("showId"))
            throw new APPBadRequestException(55, "missing showId");
        if (!json.has("createDate"))
            throw new APPBadRequestException(55, "missing createDate");
        if (!json.has("reviewTopic"))
            throw new APPBadRequestException(55, "missing reviewTopic");
        if (!json.has("reviewContent"))
            throw new APPBadRequestException(55, "missing reviewContent");

        String createdt = json.getString("createDate");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parsedate = df.parse(createdt);

        Document doc = new Document("showId", json.getString("showId"))
                .append("createDate", parsedate)
                .append("reviewTopic", json.getString("reviewTopic"))
                .append("reviewContent", json.getString("reviewContent"))
                .append("userId", id);
        reviewCollection.insertOne(doc);
        return new APPResponse();
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse createuser(Object obj) {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }
        if (!json.has("userName"))
            throw new APPBadRequestException(55, "missing userName");
        if (!json.has("email"))
            throw new APPBadRequestException(55, "missing email");
        if (!json.has("password"))
            throw new APPBadRequestException(55, "missing password");

        Document doc = new Document("userName", json.getString("userName"))
                .append("email", json.getString("email"))
                .append("password", json.getString("password"));
            collection.insertOne(doc);
        return new APPResponse();
    }

    @PATCH
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse update(@PathParam("id") String id, Object request) {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }

        try {

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            Document doc = new Document();
            if (json.has("userName"))
                doc.append("userName", json.getString("userName"));
            if (json.has("email"))
                doc.append("email", json.getString("email"));
            if (json.has("password"))
                doc.append("password", json.getString("password"));
            Document set = new Document("$set", doc);
            collection.updateOne(query, set);

        } catch (JSONException e) {
            System.out.println("Failed to create a document");

        }
        return new APPResponse();
    }

    @GET
    @Path("{id}/favs")
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getFavsForUser(@Context HttpHeaders headers, @PathParam("id") String id) {

        ArrayList<Fav> favList = new ArrayList<>();
        BasicDBObject query = new BasicDBObject();

        try {
            query.put("userId", id);
            FindIterable<Document> results = favCollection.find(query);
            if (results == null) {
                throw new APPNotFoundException(0, "Favs not found.");
            }
            for(Document item: results){
                Fav fav = new Fav(
                        item.getString("userId"),
                        item.getString("showId")
                );
                fav.setId(item.getObjectId("_id").toString());
                favList.add(fav);
            }
            return new APPResponse(favList);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No such fav");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }

    }

    @POST
    @Path("{id}/favs")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse createfav(@PathParam("id") String id, Object request) throws ParseException {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }
        if (!json.has("showId"))
            throw new APPBadRequestException(55, "missing showId");

        Document doc = new Document("showId", json.getString("showId"))
                .append("userId", id);
        favCollection.insertOne(doc);
        return new APPResponse();
    }



//    @DELETE
//    @Path("{id}")
//    @Produces({ MediaType.APPLICATION_JSON})
//    public APPResponse delete(@PathParam("id") String id) {
//        BasicDBObject query = new BasicDBObject();
//        query.put("_id", new ObjectId(id));
//
//        DeleteResult deleteResult = collection.deleteOne(query);
//        if (deleteResult.getDeletedCount() < 1)
//            throw new APPNotFoundException(66,"Could not delete");
//
//        return new APPResponse();
//    }

    void checkAuthentication(HttpHeaders headers,String id) throws Exception{
        List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null)
            throw new APPUnauthorizedException(70,"No Authorization Headers");
        String token = authHeaders.get(0);
        String clearToken = APPCrypt.decrypt(token);
        if (id.compareTo(clearToken) != 0) {
            throw new APPUnauthorizedException(71,"Invalid token. Please try getting a new token");
        }
    }

//    void checkAdmin(String id) throws Exception{
//        BasicDBObject query = new BasicDBObject();
//        query.put("userId", new ObjectId(id));
//        Document item = collection.find(query).first();
//        if (item == null) {
//            throw new APPNotFoundException(0, "You are not admin.");
//        }
//    }

}
