package edu.cmu.sv.app17.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.MongoSocketOpenException;
import edu.cmu.sv.app17.exceptions.APPBadRequestException;
import edu.cmu.sv.app17.exceptions.APPInternalServerException;
import edu.cmu.sv.app17.exceptions.APPNotFoundException;
import edu.cmu.sv.app17.helpers.APPResponse;
import edu.cmu.sv.app17.helpers.PATCH;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import edu.cmu.sv.app17.models.Cast;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

@Path("casts")
public class CastsInterface {

    private MongoCollection<Document> collection = null;
    private ObjectWriter ow;

    public CastsInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("app17-5");
        collection = database.getCollection("casts");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }


    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll(@DefaultValue("_id") @QueryParam("sort") String sortArg) {

        ArrayList<Cast> castList = new ArrayList<Cast>();

        BasicDBObject sortParams = new BasicDBObject();
        List<String> sortList = Arrays.asList(sortArg.split(","));
        sortList.forEach(sortItem -> {
            sortParams.put(sortItem,1);
        });

        try {
            FindIterable<Document> results = collection.find().sort(sortParams);
            for (Document item : results) {
                String showId = item.getString("showId");
                Cast cast = new Cast(
                        showId,
                        item.getString("castName"),
                        item.getString("roles"),
                        item.getString("castPhoto")
                );
                cast.setId(item.getObjectId("_id").toString());
                castList.add(cast);
            }
            return new APPResponse(castList);

        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }

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
                throw new APPNotFoundException(0, "Sorry, no such cast.");
            }
            Cast cast = new Cast(
                    item.getString("showId"),
                    item.getString("castName"),
                    item.getString("roles"),
                    item.getString("castPhoto")
            );
            cast.setId(item.getObjectId("_id").toString());
            return new APPResponse(cast);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No such cast");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }

    }

    @PATCH
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse update(@PathParam("id") String id, Object request) {
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

            Document doc = new Document();
            if (json.has("showId"))
                doc.append("showId",json.getString("showId"));
            if (json.has("castName"))
                doc.append("castName",json.getString("castName"));
            if (json.has("roles"))
                doc.append("roles",json.getString("roles"));
            if (json.has("castPhoto"))
                doc.append("castPhoto",json.getString("castPhoto"));
            Document set = new Document("$set", doc);
            collection.updateOne(query,set);

        } catch(JSONException e) {
            System.out.println("Failed to create a document");

        }
        return new APPResponse();
    }


    @DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete(@PathParam("id") String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        DeleteResult deleteResult = collection.deleteOne(query);
        if (deleteResult.getDeletedCount() < 1)
            throw new APPNotFoundException(66,"Could not delete");

        return new APPResponse();
    }
}
