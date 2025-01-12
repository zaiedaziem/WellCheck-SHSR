package com.SmartHealthRemoteSystem.SHSR.Sensor.Service;

import com.SmartHealthRemoteSystem.SHSR.Sensor.MongoDBConnection;
import com.SmartHealthRemoteSystem.SHSR.Sensor.RegistrationResult;
import com.SmartHealthRemoteSystem.SHSR.Sensor.Model.PatientSensorStatus;
import java.time.Instant;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SensorRegistrationHandler {
    private static final Logger logger = LoggerFactory.getLogger(SensorRegistrationHandler.class);

    public RegistrationResult registerSensor(String patientID, String patientName, String sensorID, String uniqueKey) {
        MongoDatabase database = MongoDBConnection.connect();
        try {
            // First check if the unique key exists and hasn't been used (sensorId is null)
            MongoCollection<Document> sensorCollection = database.getCollection("Sensor");
            Document keyDoc = sensorCollection.find(
                new Document("key", uniqueKey)
                    .append("sensorId", null)
            ).first();
            
            if (keyDoc == null) {
                logger.error("Invalid or already used key: {}", uniqueKey);
                return new RegistrationResult(false, "Invalid or already used unique key");
            }

            // Update the key record with sensorId
            sensorCollection.updateOne(
                new Document("key", uniqueKey),
                new Document("$set", new Document("sensorId", sensorID))
            );

            MongoCollection<Document> patientCollection = database.getCollection("Patient");
            Document patient = patientCollection.find(new Document("_id", patientID)).first();
            
            if (patient == null) {
                logger.error("No patient found for PatientID: {}", patientID);
                return new RegistrationResult(false, "Patient not found");
            }
    
            // Update patient with sensor ID
            patientCollection.updateOne(
                new Document("_id", patientID),
                new Document("$set", new Document(patientID + ".sensorDataId", sensorID))
            );
    
            // Create sensor data document
            MongoCollection<Document> sensorDataCollection = database.getCollection("SensorData");
            Document sensorDataNested = new Document()
                .append("Heart_Rate", 0)
                .append("bodyTemperature", 0)
                .append("ecgReading", 0)
                .append("oxygenReading", 0)
                .append("sensorDataId", sensorID)
                .append("timestamp", Instant.now().toString())
                .append("uniqueKey", uniqueKey);

            Document sensorDocument = new Document()
                .append(sensorID, sensorDataNested);
    
            sensorDataCollection.insertOne(sensorDocument);
            return new RegistrationResult(true, null);
        } finally {
            MongoDBConnection.close();
        }
    }

    public List<PatientSensorStatus> getAllPatientSensorStatus() {
        List<PatientSensorStatus> statusList = new ArrayList<>();
        MongoDatabase database = null;
    
        try {
            database = MongoDBConnection.connect();
            MongoCollection<Document> sensorCollection = database.getCollection("Sensor");
            MongoCollection<Document> patientCollection = database.getCollection("Patient");
            FindIterable<Document> sensors = sensorCollection.find();
    
            for (Document sensorDoc : sensors) {
                String uniqueKey = sensorDoc.getString("key");
                String sensorId = sensorDoc.getString("sensorId");
    
                if (sensorId == null) {
                    // Key is available
                    statusList.add(new PatientSensorStatus(
                        uniqueKey,
                        "",  // patientId
                        "N/A", // patientName
                        null  // sensorId
                    ));
                } else {
                    // Find patient with matching sensorId
                    FindIterable<Document> patients = patientCollection.find();
                    String patientName = "N/A";
                    String patientId = "";
                    
                    // Iterate through patients to find matching sensorId
                    for (Document patient : patients) {
                        patientId = patient.getString("_id");
                        Document patientInfo = (Document) patient.get(patientId);
                        
                        if (patientInfo != null && sensorId.equals(patientInfo.getString("sensorDataId"))) {
                            patientName = patientInfo.getString("name");
                            break;
                        }
                    }
                
                    statusList.add(new PatientSensorStatus(
                        uniqueKey,
                        patientId,
                        patientName,
                        sensorId
                    ));
                }
            }
        } finally {
            if (database != null) {
                MongoDBConnection.close();
            }
        }
    
        return statusList;
    }
}
