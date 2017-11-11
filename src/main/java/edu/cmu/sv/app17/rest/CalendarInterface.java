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
import edu.cmu.sv.app17.models.Calendar;
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

@Path("calendars")
public class CalendarInterface {
    private MongoCollection<Document> collection = null;
    private ObjectWriter ow;

    public CalendarInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("app17-5");
        collection = database.getCollection("cals");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll(@Context HttpHeaders headers, @DefaultValue("_id") @QueryParam("sort") String sortArg) {

        ArrayList<Calendar> calList = new ArrayList<Calendar>();

        BasicDBObject sortParams = new BasicDBObject();
        List<String> sortList = Arrays.asList(sortArg.split(","));
        sortList.forEach(sortItem -> {
            sortParams.put(sortItem,1);
        });

        try {
            Authorization.checkAdmin(headers);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            FindIterable<Document> results = collection.find().sort(sortParams);
            for (Document item : results) {
                String userId = item.getString("userId");
                Calendar cal = new Calendar(
                        userId,
                        sdf.format(item.getDate("date")),
                        item.getString("event")
                );
                cal.setId(item.getObjectId("_id").toString());
                calList.add(cal);
            }
            return new APPResponse(calList);

        } catch(APPUnauthorizedException e){
            throw new APPUnauthorizedException(70,"Not authorized.");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happens!");
        }
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getOne(@Context HttpHeaders headers,@PathParam("id") String id) {


        BasicDBObject query = new BasicDBObject();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            query.put("_id", new ObjectId(id));
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "Cal not found.");
            }
            Authorization.checkSelfandAdmin(headers,item.getString("userId"));
            Calendar cal = new Calendar(
                    item.getString("userId"),
                    sdf.format(item.getDate("date")),
                    item.getString("event")
            );
            cal.setId(item.getObjectId("_id").toString());
            return new APPResponse(cal);

        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(APPUnauthorizedException e){
            throw new APPUnauthorizedException(70,"Not authorized.");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happens!");
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
            Authorization.checkAdmin(headers);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));



            Document doc = new Document();
            if (json.has("userId"))
                doc.append("userId",json.getString("userId"));

            if(json.has("date")){
                String createdt = json.getString("date");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date parsedate = df.parse(createdt);
                doc.append("date",parsedate);
            }

            if (json.has("event"))
                doc.append("event",json.getString("event"));
            Document set = new Document("$set", doc);
            collection.updateOne(query,set);

        } catch(JSONException e) {
            System.out.println("Failed to update a cal");

        } catch(APPUnauthorizedException e){
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
            Authorization.checkAdmin(headers);

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
            if (!json.has("date"))
                throw new APPBadRequestException(55, "missing date");
            if (!json.has("event"))
                throw new APPBadRequestException(55, "missing event");

            String createdt = json.getString("date");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsedate = df.parse(createdt);


            Document doc = new Document("userId", json.getString("userId"))
                    .append("date", parsedate)
                    .append("event", json.getString("event"));
            collection.insertOne(doc);
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
