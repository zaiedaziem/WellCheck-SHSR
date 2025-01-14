package com.SmartHealthRemoteSystem.SHSR.Sensor.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.SmartHealthRemoteSystem.SHSR.Sensor.Service.UniqueKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class UniqueKeyController {
    
    @Autowired
    private UniqueKeyGenerator keyGenerator;

    @GetMapping("/generateUniqueKey")
    public String generateUniqueKeyPage() {
        return "generateUniqueKeyPage";
    }

    @PostMapping("/api/uniqueKey/generate")
    public String generateUniqueKey(Model model) {
        try {
            String uniqueKey = keyGenerator.generateAndStore();
            model.addAttribute("uniqueKey", uniqueKey);
            model.addAttribute("message", "Key generated successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e.getMessage());
        }
        return "generateUniqueKeyPage";
    }
}