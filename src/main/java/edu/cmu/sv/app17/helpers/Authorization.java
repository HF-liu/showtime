package edu.cmu.sv.app17.helpers;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.cmu.sv.app17.exceptions.APPUnauthorizedException;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.ws.rs.core.HttpHeaders;
import java.util.List;

public class Authorization {
    public static MongoClient mongoClient = new MongoClient();
    public static MongoDatabase database = mongoClient.getDatabase("app17-5");
    public static MongoCollection<Document> userCollection = database.getCollection("users");
    public static MongoCollection<Document> adminCollection = database.getCollection("admins");


    public static void checkSelfandAdmin(HttpHeaders headers, String id) throws Exception{

        List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null)
            throw new APPUnauthorizedException(70,"No Authorization Headers");
        if (id == null)
            throw new APPUnauthorizedException(70,"No id found");
        String token = authHeaders.get(0);
        String clearToken = APPCrypt.decrypt(token);
        BasicDBObject query = new BasicDBObject();
        query.put("userId", clearToken);
        Document item = adminCollection.find(query).first();
        if ((id.compareTo(clearToken) != 0) && (item == null)) {
            throw new APPUnauthorizedException(71,"Invalid token. Please try getting a new token");
        }
    }

    public static void checkAdmin(HttpHeaders headers) throws Exception{
        List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null)
            throw new APPUnauthorizedException(70,"No Authorization Headers");
        String token = authHeaders.get(0);
        String clearToken = APPCrypt.decrypt(token);
        BasicDBObject query = new BasicDBObject();
        query.put("userId", clearToken);
        Document item = adminCollection.find(query).first();
        if (item == null) {
            throw new APPUnauthorizedException(71,"You are not admin");
        }
    }

    public static void checkUser(HttpHeaders headers) throws Exception{
        List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null)
            throw new APPUnauthorizedException(70,"No Authorization Headers");
        String token = authHeaders.get(0);
        String clearToken = APPCrypt.decrypt(token);
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(clearToken));
        Document item = userCollection.find(query).first();
        if (item == null) {
            throw new APPUnauthorizedException(71,"You are not a user.");
        }
    }


}
