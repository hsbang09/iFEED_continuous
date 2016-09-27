/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ifeed;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;



/**
 *
 * @author Bang
 */
public class DataManagement {
    
    MongoClient mongoClient;
    DB db;
    MongoDatabase Mdb;
    String dbName = "ifeed";
    String metaDataCollectionName = "metadata";
    String dataCollectionName = "data";
    private static DataManagement instance = null;

    
    public static DataManagement getInstance() {
        if (instance == null) {
            instance = new DataManagement();
        }
        return instance;
    }

    
    
    public DataManagement(){
                
        try{            
//            MongoClientURI mongoClientURI = new MongoClientURI("mongodb://ifeedadmin:qkdgustmd@ds041586.mlab.com:41586/ifeed");
            mongoClient = new MongoClient( "localhost" , 27017 );
//            mongoClient = new MongoClient(mongoClientURI);

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    
    
    public void createNewDB(){
//        db = mongoClient.getDatabase(dbName);
        db = mongoClient.getDB(dbName);
        Mdb = mongoClient.getDatabase(dbName);
        if(db.collectionExists(metaDataCollectionName)){
            db.getCollection(metaDataCollectionName).drop();
        }
        if(db.collectionExists(dataCollectionName)){
            db.getCollection(dataCollectionName).drop();
        }
    }
    
    public void encodeMetadata(int nData,ArrayList<String> inputNames, ArrayList<String> outputNames){
        MongoCollection col = Mdb.getCollection(metaDataCollectionName);
        col.insertOne(
                new Document()
                    .append("nData", nData)
                    .append("inputNames",inputNames)
                    .append("outputNames",outputNames)
        );
    }
    
    public void insertDocument(int id,ArrayList<String> inputs,ArrayList<String> outputs){
        MongoCollection col = Mdb.getCollection(dataCollectionName);
        col.insertOne(
                new Document()
                    .append("id",id)
                    .append("inputs", inputs)
                    .append("outputs", outputs));
    }
    
    public void queryArchitecture(int id){
        
        DBCollection col = db.getCollection(dataCollectionName);
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("id", id);
        col.find(whereQuery);
        DBCursor cursor = col.find(whereQuery);
        DBObject doc = cursor.one();
        
        System.out.println(doc.get("inputs").getClass().toString());
        System.out.println(doc.get("inputs").toString());
        
    }
}
