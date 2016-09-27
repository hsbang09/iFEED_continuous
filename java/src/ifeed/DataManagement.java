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
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.mongodb.client.FindIterable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static java.util.Arrays.asList;


/**
 *
 * @author Bang
 */
public class DataManagement {
    
    MongoClient mongoClient;
    MongoDatabase db;
    String dbName = "ifeed";
    String metaDataCollectionName = "metadata";
    String dataCollectionName = "data";
    
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
        db = mongoClient.getDatabase(dbName);
    }
    
    public void encodeMetadata(int nData,ArrayList<String> inputNames, ArrayList<String> outputNames){
        MongoCollection col = db.getCollection(metaDataCollectionName);
        col.insertOne(
                new Document()
                    .append("nData", nData)
                    .append("inputNames",inputNames)
                    .append("outputNames",outputNames)
        );
    }
    
    public void insertDocument(int id,ArrayList<String> inputs,ArrayList<String> outputs){
        MongoCollection col = db.getCollection(dataCollectionName);
        col.insertOne(
                new Document()
                    .append("id",id)
                    .append("inputs", inputs)
                    .append("outputs", outputs));
    }
}
