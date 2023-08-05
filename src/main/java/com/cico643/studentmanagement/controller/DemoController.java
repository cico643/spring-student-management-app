package com.cico643.studentmanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/demo-controller")
public class DemoController {

    @GetMapping
    public ResponseEntity<String> secureEndpointGET() {
        return ResponseEntity.ok("Hello from secured endpoint GET");
    }

    @PostMapping
    public ResponseEntity<String> secureEndpointPost() {
        return ResponseEntity.ok("Hello from secured endpoint POST");
    }

    @DeleteMapping
    public ResponseEntity<String> secureEndpointDelete() {
        return ResponseEntity.ok("Hello from secured endpoint DELETE");
    }

    @PutMapping
    public ResponseEntity<String> secureEndpointPut() {
        return ResponseEntity.ok("Hello from secured endpoint PUT");
    }


}
