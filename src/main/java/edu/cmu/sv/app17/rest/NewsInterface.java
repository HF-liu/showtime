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


@Path("news")

public class NewsInterface {

    private MongoCollection<Document> userCollection;
    private MongoCollection<Document> adminCollection;
    private MongoCollection<Document> newsCollection;
    private ObjectWriter ow;

    public NewsInterface(){
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("app17-5");

        this.userCollection = database.getCollection("users");
        this.adminCollection = database.getCollection("admins");
        this.newsCollection = database.getCollection("news");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll(@Context HttpHeaders headers, @DefaultValue("_id") @QueryParam("sort") String sortArg) {

        ArrayList<News> newsList = new ArrayList<>();

        BasicDBObject sortParams = new BasicDBObject();
        List<String> sortList = Arrays.asList(sortArg.split(","));
        sortList.forEach(sortItem -> {
            sortParams.put(sortItem,1);
        });

        try {
            Authorization.checkUser(headers);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            FindIterable<Document> results = newsCollection.find().sort(sortParams);
            for (Document item : results) {
                String source = item.getString("source");
                News news = new News(
                        source,
                        sdf.format(item.getDate("date")),
                        item.getString("title"),
                        item.getString("content")
                );
                news.setId(item.getObjectId("_id").toString());
                newsList.add(news);
            }
            return new APPResponse(newsList);

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
            Document item = newsCollection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "News not found.");
            }
            News news = new News(
                    item.getString("source"),
                    sdf.format(item.getDate("date")),
                    item.getString("title"),
                    item.getString("content")
            );
            news.setId(item.getObjectId("_id").toString());
            return new APPResponse(news);

        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        } catch(APPUnauthorizedException e){
            throw new APPUnauthorizedException(70,"Not authorized.");
        }   catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }

    }


}
