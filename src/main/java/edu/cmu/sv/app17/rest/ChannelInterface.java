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
    private ObjectWriter ow;


    public ChannelInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("app17-5");

        this.userCollection = database.getCollection("users");
        this.reviewCollection = database.getCollection("reviews");
        this.adminCollection = database.getCollection("admins");
        this.channelCollection = database.getCollection("channels");
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








}
