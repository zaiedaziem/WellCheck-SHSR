package com.SmartHealthRemoteSystem.SHSR.Sensor.Service;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import org.springframework.stereotype.Service;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import com.SmartHealthRemoteSystem.SHSR.Sensor.MongoDBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UniqueKeyGenerator {
    private static final Logger logger = LoggerFactory.getLogger(UniqueKeyGenerator.class);
    
    public String generateAndStore() {
        String uniqueKey = UUID.randomUUID().toString();
        MongoDatabase database = null;
        
        try {
            database = MongoDBConnection.connect();
            MongoCollection<Document> keyCollection = database.getCollection("Sensor");
            
            // Check for duplicate keys
            Document existingKey = keyCollection.find(new Document("key", uniqueKey)).first();
            if (existingKey != null) {
                // In the unlikely event of a collision, generate a new key
                return generateAndStore();
            }
            
            Document keyDoc = new Document()
                .append("key", uniqueKey)
                .append("sensorId", null)
                .append("created", System.currentTimeMillis());
            
            keyCollection.insertOne(keyDoc);
            logger.info("Generated new unique key: {}", uniqueKey);
            return uniqueKey;
        } catch (Exception e) {
            logger.error("Error generating unique key: {}", e.getMessage());
            throw new RuntimeException("Failed to generate unique key", e);
        } finally {
            if (database != null) {
                MongoDBConnection.close();
            }
        }
    }
    
    public List<Document> getAllKeys() {
        List<Document> keys = new ArrayList<>();
        MongoDatabase database = null;
        
        try {
            database = MongoDBConnection.connect();
            MongoCollection<Document> keyCollection = database.getCollection("Sensor");
            FindIterable<Document> documents = keyCollection.find();
            
            for (Document doc : documents) {
                keys.add(doc);
            }
            return keys;
        } finally {
            if (database != null) {
                MongoDBConnection.close();
            }
        }
    }
    
    public boolean isKeyAvailable(String key) {
        MongoDatabase database = null;
        
        try {
            database = MongoDBConnection.connect();
            MongoCollection<Document> keyCollection = database.getCollection("Sensor");
            Document keyDoc = keyCollection.find(
                new Document("key", key)
                .append("sensorId", null)
            ).first();
            
            return keyDoc != null;
        } finally {
            if (database != null) {
                MongoDBConnection.close();
            }
        }
    }
    
    public void deleteKey(String key) {
        MongoDatabase database = null;
        
        try {
            database = MongoDBConnection.connect();
            MongoCollection<Document> keyCollection = database.getCollection("Sensor");
            keyCollection.deleteOne(new Document("key", key));
            logger.info("Deleted key: {}", key);
        } catch (Exception e) {
            logger.error("Error deleting key {}: {}", key, e.getMessage());
            throw new RuntimeException("Failed to delete key", e);
        } finally {
            if (database != null) {
                MongoDBConnection.close();
            }
        }
    }
}