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


@Path("channels")
public class ChannelInterface {
    private MongoCollection<Document> userCollection;
    private MongoCollection<Document> reviewCollection;
    private MongoCollection<Document> channelCollection;
    private MongoCollection<Document> adminCollection;
    private MongoCollection<Document> showCollection;
    private ObjectWriter ow;


    public ChannelInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("app17-5");

        this.userCollection = database.getCollection("users");
        this.reviewCollection = database.getCollection("reviews");
        this.adminCollection = database.getCollection("admins");
        this.channelCollection = database.getCollection("channels");
        this.showCollection = database.getCollection("shows");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll(@Context HttpHeaders headers) {

        ArrayList<Channel> channelList = new ArrayList<Channel>();
        try {
            Authorization.checkUser(headers);
            FindIterable<Document> results = channelCollection.find();
            if (results == null) {
                return new APPResponse(channelList);
            }
            for (Document item : results) {
                Channel channel = new Channel(
                        item.getString("channelName"),
                        item.getString("channelLogo")
                );
                channel.setId(item.getObjectId("_id").toString());
                channelList.add(channel);
            }
            return new APPResponse(channelList);
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
            Authorization.checkUser(headers);
            query.put("_id", new ObjectId(id));
            Document item = channelCollection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "Channel not found.");
            }
            Channel channel = new Channel(
                    item.getString("channelName"),
                    item.getString("channelLogo")
            );
            channel.setId(item.getObjectId("_id").toString());
            return new APPResponse(channel);

        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        } catch(APPUnauthorizedException e){
            throw new APPUnauthorizedException(70,"Not authorized.");
        }   catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }


    }

    @GET
    @Path("{id}/shows")
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getFavsForUser(@Context HttpHeaders headers, @PathParam("id") String id) {

        ArrayList<Show> showList = new ArrayList<>();
        BasicDBObject query = new BasicDBObject();

        try {
            Authorization.checkUser(headers);
            query.put("channelId", id);
            FindIterable<Document> results = showCollection.find(query);
            if (results == null) {
                throw new APPNotFoundException(0, "Shows not found.");
            }
            for(Document item: results){
                Show show = new Show(
                        item.getString("showName"),
                        item.getString("channelId"),
                        item.getString("intro"),
                        item.getString("showCategory"),
                        item.getString("showphoto"),
                        item.getInteger("showRating")
                );
                show.setId(item.getObjectId("_id").toString());
                showList.add(show);
            }
            return new APPResponse(showList);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No such fav");
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
            Authorization.checkAdmin(headers);

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));
            Document item = channelCollection.find(query).first();


            Document doc = new Document();
            if (json.has("channelName"))
                doc.append("channelName",json.getString("channelName"));
            if (json.has("channelLogo"))
                doc.append("channelLogo",json.getString("channelLogo"));

            Document set = new Document("$set", doc);
            channelCollection.updateOne(query,set);

        } catch(JSONException e) {
            System.out.println("Failed to update a document");

        }catch(APPUnauthorizedException e){
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
            if (!json.has("channelName"))
                throw new APPBadRequestException(55, "missing channelName");
            if (!json.has("ChannelLogo"))
                throw new APPBadRequestException(55, "missing ChannelLogo");


            Document doc = new Document("channelName", json.getString("channelName"))
                    .append("channelLogo", json.getString("channelLogo"));
            channelCollection.insertOne(doc);
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
