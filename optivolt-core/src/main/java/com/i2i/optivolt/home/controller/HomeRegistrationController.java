package com.i2i.optivolt.home.controller;

import com.i2i.optivolt.home.dto.HomeRegistrationRequest;
import com.i2i.optivolt.home.entity.Home;
import com.i2i.optivolt.home.service.HomeRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/homes")
@RequiredArgsConstructor
@Tag(name = "Home Registration", description = "Endpoints for registering new homes")
public class HomeRegistrationController {

    private final HomeRegistrationService homeRegistrationService;

    @PostMapping
    @Operation(summary = "Register a new home and its appliances")
    public ResponseEntity<Home> registerHome(@RequestBody HomeRegistrationRequest request) {
        Home home = homeRegistrationService.registerHome(request);
        return new ResponseEntity<>(home, HttpStatus.CREATED);
    }
}
