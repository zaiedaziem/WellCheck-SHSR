package com.SmartHealthRemoteSystem.SHSR.Sensor;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    private static final String CONNECTION_STRING = "mongodb+srv://admin:admin@atlascluster.htlbqbu.mongodb.net/?retryWrites=true&w=majority&appName=AtlasCluster";
    private static MongoClient mongoClient = null;
    
    public static MongoDatabase connect() {
        mongoClient = MongoClients.create(CONNECTION_STRING);
        return mongoClient.getDatabase("Wellcheck2"); 
    }
    
    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }
}
