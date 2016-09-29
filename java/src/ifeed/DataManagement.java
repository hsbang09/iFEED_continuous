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
import com.mongodb.BasicDBList;
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
    String dbName = "ifeed";
    String metaDataCollectionName = "metadata";
    String dataCollectionName = "data";
    String candidateFeatureCollectionName = "candidate_features";
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
        DB db = mongoClient.getDB(dbName);
        MongoDatabase Mdb = mongoClient.getDatabase(dbName);
        if(db.collectionExists(metaDataCollectionName)){
            db.getCollection(metaDataCollectionName).drop();
        }
        if(db.collectionExists(dataCollectionName)){
            db.getCollection(dataCollectionName).drop();
        }
        if(db.collectionExists(candidateFeatureCollectionName)){
            db.getCollection(candidateFeatureCollectionName).drop();
        }
    }
    
    public void encodeMetadata(int nData,ArrayList<String> inputNames, ArrayList<String> outputNames){
        MongoDatabase Mdb = mongoClient.getDatabase(dbName);
        MongoCollection col = Mdb.getCollection(metaDataCollectionName);
        col.insertOne(
                new Document()
                    .append("nData", nData)
                    .append("inputNames",inputNames)
                    .append("outputNames",outputNames)
        );
    }
    public void insertDocument_data(int id,ArrayList<String> inputs,ArrayList<String> outputs){
        MongoDatabase Mdb = mongoClient.getDatabase(dbName);
        MongoCollection col = Mdb.getCollection(dataCollectionName);
        col.insertOne(
                new Document()
                    .append("id",id)
                    .append("inputs", inputs)
                    .append("outputs", outputs));
    }
    public void insertDocument_candidateFeatures(int featureID, String name, String expression){
        MongoDatabase Mdb = mongoClient.getDatabase(dbName);
        MongoCollection col = Mdb.getCollection(candidateFeatureCollectionName);
        col.insertOne(
                new Document()
                    .append("id", featureID)
                    .append("name",name)
                    .append("expression",expression)
        );
    }
    
    
    
    public ArrayList<String>[] queryAllCandidateFeatures(){
        
        ArrayList<String>[] candidateFeatures = new ArrayList[2];
        ArrayList<String> cfn = new ArrayList<>(); // candidate feature names
        ArrayList<String> cfe = new ArrayList<>(); // candidate feature expressions
        
        DB db = mongoClient.getDB(dbName);
        DBCollection col = db.getCollection(candidateFeatureCollectionName);
        DBCursor cursor = col.find();
        
        while(cursor.hasNext()){
            DBObject doc = cursor.next();
            cfn.add((String) doc.get("name"));
            cfe.add((String) doc.get("expression"));
        }
        candidateFeatures[0] = cfn;
        candidateFeatures[1] = cfe;
        return candidateFeatures;
    }
    public ArrayList<Architecture> queryAllArchitecture(){
        DB db = mongoClient.getDB(dbName);
        DBCollection col = db.getCollection(dataCollectionName);
        DBCursor cursor = col.find();
        ArrayList<Architecture> allArch = new ArrayList<>();
        while(cursor.hasNext()){
            DBObject doc = cursor.next();
            int id = (int) doc.get("id");
            ArrayList<String> inputs = (ArrayList<String>) doc.get("inputs");
            ArrayList<String> outputs = (ArrayList<String>) doc.get("outputs");
            allArch.add(new Architecture(id,inputs,outputs));
        }
        return allArch;
    }
    
    public Architecture queryArchitecture(int id){
        DB db = mongoClient.getDB(dbName);
        DBCollection col = db.getCollection(dataCollectionName);
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("id", id);
        DBCursor cursor = col.find(whereQuery);
        DBObject doc = cursor.one();
        ArrayList<String> inputs = (ArrayList<String>) doc.get("inputs");
        ArrayList<String> outputs = (ArrayList<String>) doc.get("outputs");
        return new Architecture(id,inputs,outputs);
    }
    
    public ArrayList<String> queryInputNames(){
        DB db = mongoClient.getDB(dbName);
        DBCollection col = db.getCollection(metaDataCollectionName);
        DBCursor cursor = col.find();
        DBObject doc = cursor.one();
        ArrayList<String> inputNames = (ArrayList<String>) doc.get("inputNames");
        return inputNames;
    }
    
    
}
