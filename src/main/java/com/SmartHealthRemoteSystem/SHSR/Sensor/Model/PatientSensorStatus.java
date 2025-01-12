package com.SmartHealthRemoteSystem.SHSR.Sensor.Model;

public class PatientSensorStatus {
    private String uniqueKey;
    private String patientId;
    private String patientName;
    private String sensorId;

    public PatientSensorStatus(String uniqueKey, String patientId, String patientName, String sensorId) {
        this.uniqueKey = uniqueKey;
        this.patientId = patientId;
        this.patientName = patientName;
        this.sensorId = sensorId;
    }

    // Getters
    public String getUniqueKey() { return uniqueKey; }
    public String getPatientId() { return patientId; }
    public String getPatientName() { return patientName; }
    public String getSensorId() { return sensorId; }
}