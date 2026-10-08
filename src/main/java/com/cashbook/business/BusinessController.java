package com.cashbook.business;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @PostMapping
    public ResponseEntity<BusinessResponseDTO> createBusiness(
            @Valid @RequestBody BusinessRequestDto request
    ) {

        BusinessResponseDTO response =
                businessService.createBusiness(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<BusinessResponseDTO>> getMyBusinesses() {

        return ResponseEntity.ok(
                businessService.getMyBusinesses()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessResponseDTO> getBusiness(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                businessService.getBusiness(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessResponseDTO> updateBusiness(
            @PathVariable Long id,
            @Valid @RequestBody BusinessRequestDto request
    ) {

        return ResponseEntity.ok(
                businessService.updateBusiness(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBusiness(
            @PathVariable Long id
    ) {

        businessService.deleteBusiness(id);

        return ResponseEntity.noContent().build();
    }
}
