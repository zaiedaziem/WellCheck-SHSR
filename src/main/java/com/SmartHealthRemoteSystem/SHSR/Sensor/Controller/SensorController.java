package com.SmartHealthRemoteSystem.SHSR.Sensor.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;
import com.SmartHealthRemoteSystem.SHSR.Sensor.RegistrationResult;
import com.SmartHealthRemoteSystem.SHSR.Sensor.Model.PatientSensorStatus;
import com.SmartHealthRemoteSystem.SHSR.Sensor.Service.SensorRegistrationHandler;
import java.util.List;
import java.util.Map;

@Controller
public class SensorController {
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private SensorRegistrationHandler sensorRegistrationHandler;

    @GetMapping("/register")
    public String showRegistrationPage(Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        Patient patient = patientService.getPatient(myUserDetails.getUsername());
        model.addAttribute("patient", patient);
        return "registrationPage";
    }

    @PostMapping("/api/sensors/register")
    public String registerSensor(@RequestParam Map<String, String> request, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        Patient patient = patientService.getPatient(myUserDetails.getUsername());
        
        if (patient.getSensorDataId() != null && !patient.getSensorDataId().isEmpty()) {
            model.addAttribute("errorMessage", 
                "You already have a registered sensor (ID: " + patient.getSensorDataId() + ")");
            model.addAttribute("patient", patient);
            return "registrationPage";
        }
        
        RegistrationResult result = sensorRegistrationHandler.registerSensor(
            patient.getUserId(), 
            patient.getName(), 
            request.get("sensorID"), 
            request.get("uniqueKey")
        );
        
        if (result.isSuccess()) {
            model.addAttribute("successMessage", "Sensor registered successfully!");
        } else {
            model.addAttribute("errorMessage", result.getErrorMessage());
        }
        
        model.addAttribute("patient", patient);
        return "registrationPage";
    }

    @GetMapping("/admin/sensor-status")
    public String viewSensorStatus(Model model) {
        List<PatientSensorStatus> sensorStatuses = sensorRegistrationHandler.getAllPatientSensorStatus();
        model.addAttribute("sensorStatuses", sensorStatuses);
        return "sensorStatusPage";
    }
}