package com.SmartHealthRemoteSystem.SHSR.Sensor.Model;

public class PatientSensorStatus {
    private String uniqueKey;
    private String patientId;
    private String patientName;
    private String sensorId;
    private String registeredHospital;

    public PatientSensorStatus(String uniqueKey, String patientId, String patientName, String sensorId, String registeredHospital) {
        this.uniqueKey = uniqueKey;
        this.patientId = patientId;
        this.patientName = patientName;
        this.sensorId = sensorId;
        this.registeredHospital = registeredHospital;
    }

    // Getters and setters
    public String getUniqueKey() { return uniqueKey; }
    public void setUniqueKey(String uniqueKey) { this.uniqueKey = uniqueKey; }
    
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    
    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    
    public String getRegisteredHospital() { return registeredHospital; }
    public void setRegisteredHospital(String registeredHospital) { this.registeredHospital = registeredHospital; }
}