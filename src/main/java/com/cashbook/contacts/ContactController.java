package com.cashbook.contacts;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<ContactResponseDto> create(
            @Valid @RequestBody ContactRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(contactService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ContactResponseDto>> getAll() {

        return ResponseEntity.ok(
                contactService.getAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponseDto> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                contactService.getById(id)
        );
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<ContactResponseDto>> getByBusiness(
            @PathVariable Long businessId) {

        return ResponseEntity.ok(
                contactService.getByBusiness(businessId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ContactRequestDto request) {

        return ResponseEntity.ok(
                contactService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        contactService.delete(id);

        return ResponseEntity.noContent().build();
    }
}