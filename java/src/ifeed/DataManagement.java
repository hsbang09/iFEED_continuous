/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ifeed;

//import hdf.hdf5lib.*;
//import hdf.hdf5lib.HDF5Constants;
//import org.slf4j.*;

import com.mongodb.MongoClient;
import com.mongodb.DB;

/**
 *
 * @author Bang
 */
public class DataManagement {
    
    
    public DataManagement(){
                
        try{            
            
            
            
            MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
            
            DB db = mongoClient.getDB( "test" );
            
            
            
        //////////////////////////////////////////////////////////// HDF test code

//            final String FILE = "C:\\Users\\Bang\\Documents\\file.h5";
//            int file_id = -1;    // file identifier 
//            int status = -1;
//            
//            HDF5Constants hdf5const = new HDF5Constants();
//            
//            String name = FILE;
//            int flags = hdf5const.H5F_ACC_TRUNC;
//            int create_id = hdf5const.H5P_DEFAULT;
//            int access_id = hdf5const.H5P_DEFAULT;
//
//            // Create a new file using default file properties.
//            file_id = H5.H5Fcreate (name, flags, create_id, access_id);
//
//            System.out.println ("\nThe file name is: " + name);
//            System.out.println ("The file ID is: " + file_id);
//
//            status = H5.H5Fclose (file_id);

        /////////////////////////////////////////////////////////   

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
   
    }
    
    
    
    
    
}
