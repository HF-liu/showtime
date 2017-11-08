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
import edu.cmu.sv.app17.models.Cast;
import edu.cmu.sv.app17.models.Review;
import edu.cmu.sv.app17.models.Show;
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

@Path("shows")
public class ShowsInterface {
    private MongoCollection<Document> showCollection;
    private MongoCollection<Document> reviewCollection;
    private MongoCollection<Document> castCollection;
    private ObjectWriter ow;


    public ShowsInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("app17-5");

        this.showCollection = database.getCollection("shows");
        this.reviewCollection = database.getCollection("reviews");
        this.castCollection = database.getCollection("casts");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll() {

        ArrayList<Show> showList = new ArrayList<Show>();
        try {
            FindIterable<Document> results = showCollection.find();
            if (results == null) {
                return new APPResponse(showList);
            }
            for (Document item : results) {
                Show show = new Show(
                        item.getString("showName"),
                        item.getString("channelID"),
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
            throw new APPNotFoundException(0,"There are no shows.");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }

    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getOne(@PathParam("id") String id) {
        BasicDBObject query = new BasicDBObject();
        try {
            query.put("_id", new ObjectId(id));
            Document item = showCollection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "Show not found.");
            }
            Show show = new Show(
                    item.getString("showName"),
                    item.getString("channelID"),
                    item.getString("intro"),
                    item.getString("showCategory"),
                    item.getString("showphoto"),
                    item.getInteger("showRating")
            );
            show.setId(item.getObjectId("_id").toString());
            return new APPResponse(show);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No such show");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }

    }

    @GET
    @Path("{id}/reviews")
    @Produces({MediaType.APPLICATION_JSON})
    public APPListResponse getReviewsForShow(@Context HttpHeaders headers, @PathParam("id") String id,
                                             @DefaultValue("_id") @QueryParam("sort") String sortArg,
                                             @DefaultValue("20") @QueryParam("count") int count,
                                             @DefaultValue("0") @QueryParam("offset") int offset
    ) {

        ArrayList<Review> reviewList = new ArrayList<Review>();

        try {
            //checkAuthentication(headers,id);
            BasicDBObject query = new BasicDBObject();
            query.put("showId", id);
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
                        item.getString("showId"),
                        sdf.format(item.getDate("createDate")),
                        item.getString("reviewTopic"),
                        item.getString("reviewContent")
                );
                review.setId(item.getObjectId("_id").toString());
                reviewList.add(review);
            }
            return new APPListResponse(reviewList,resultCount,offset,reviewList.size());

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No such reviews");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }

    }

    @GET
    @Path("{id}/casts")
    @Produces({MediaType.APPLICATION_JSON})
    public APPListResponse getCastsForShow(@Context HttpHeaders headers, @PathParam("id") String id,
                                             @DefaultValue("_id") @QueryParam("sort") String sortArg,
                                             @DefaultValue("20") @QueryParam("count") int count,
                                             @DefaultValue("0") @QueryParam("offset") int offset
    ) {

        ArrayList<Cast> castList = new ArrayList<Cast>();

        try {
            //checkAuthentication(headers,id);
            BasicDBObject query = new BasicDBObject();
            query.put("showId", id);
            long resultCount = castCollection.count(query);

            BasicDBObject sortParams = new BasicDBObject();
            List<String> sortList = Arrays.asList(sortArg.split(","));
            sortList.forEach(sortItem -> {
                sortParams.put(sortItem,1);
            });

            FindIterable<Document> results = castCollection.find(query).sort(sortParams).skip(offset).limit(count);

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
            return new APPListResponse(castList,resultCount,offset,castList.size());

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No such casts");
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
        } catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }

        try {

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            Document doc = new Document();
            if (json.has("showName"))
                doc.append("showName", json.getString("showName"));
            if (json.has("channelId"))
                doc.append("channelId", json.getString("channelId"));
            if (json.has("intro"))
                doc.append("intro", json.getString("intro"));
            if (json.has("showCategory"))
                doc.append("showCategory", json.getString("showCategory"));
            if (json.has("showphoto"))
                doc.append("showphoto", json.getString("showphoto"));
            if (json.has("showRating"))
                doc.append("showRating", json.getInt("showRating"));
            Document set = new Document("$set", doc);
            showCollection.updateOne(query, set);

        } catch (JSONException e) {
            System.out.println("Failed to edit showInfo");

        }
        return new APPResponse();
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse createshow(Object obj) {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(obj));
            if (!json.has("showName"))
                throw new APPBadRequestException(55, "missing showName");
            if (!json.has("channelId"))
                throw new APPBadRequestException(55, "missing channelId");
            if (!json.has("intro"))
                throw new APPBadRequestException(55, "missing intro");
            if (!json.has("showCategory"))
                throw new APPBadRequestException(55, "missing showCategory");
            if (!json.has("showphoto"))
                throw new APPBadRequestException(55, "missing showphoto");
            if (!json.has("showRating"))
                throw new APPBadRequestException(55, "missing showRating");

            Document doc = new Document("showName", json.getString("showName"))
                    .append("channelId", json.getString("channelId"))
                    .append("intro", json.getString("intro"))
                    .append("showCategory", json.getString("showCategory"))
                    .append("showphoto", json.getString("showphoto"))
                    .append("showRating", json.getInt("showRating"));
            showCollection.insertOne(doc);
            return new APPResponse(obj);
        } catch (JSONException e) {
            throw new APPBadRequestException(33, e.getMessage());
        } catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }
        catch (Exception e) {
            throw new APPInternalServerException(0, e.getMessage());
        }
    }

    @POST
    @Path("{id}/reviews")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse createReview(@PathParam("id") String id, Object request) throws ParseException {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));

            if (!json.has("userId"))
                throw new APPBadRequestException(55, "missing userId");
            if (!json.has("createDate"))
                throw new APPBadRequestException(55, "missing createDate");
            if (!json.has("reviewTopic"))
                throw new APPBadRequestException(55, "missing reviewTopic");
            if (!json.has("reviewContent"))
                throw new APPBadRequestException(55, "missing reviewContent");

            String createdt = json.getString("createDate");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsedate = df.parse(createdt);

            Document doc = new Document("userId", json.getString("userId"))
                    .append("createDate", parsedate)
                    .append("reviewTopic", json.getString("reviewTopic"))
                    .append("reviewContent", json.getString("reviewContent"))
                    .append("showId", id);
            reviewCollection.insertOne(doc);
            return new APPResponse(request);
        } catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        } catch (JSONException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }
    }

    @POST
    @Path("{id}/casts")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse createCast(@PathParam("id") String id, Object request) throws ParseException {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
            if (!json.has("castName"))
                throw new APPBadRequestException(55, "missing castName");
            if (!json.has("roles"))
                throw new APPBadRequestException(55, "missing roles");
            if (!json.has("castPhoto"))
                throw new APPBadRequestException(55, "missing castPhoto");

            Document doc = new Document("castName", json.getString("castName"))
                    .append("roles", json.getString("roles"))
                    .append("castPhoto", json.getString("castPhoto"))
                    .append("showId", id);
            castCollection.insertOne(doc);
            return new APPResponse(request);
        } catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        } catch (JSONException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }
    }

    @DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse delete(@PathParam("id") String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        DeleteResult deleteResult = showCollection.deleteOne(query);
        if (deleteResult.getDeletedCount() < 1)
            throw new APPNotFoundException(66,"Could not delete");

        return new APPResponse();
    }
}
