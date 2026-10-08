package com.cashbook.cashbook;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cashbooks")
@RequiredArgsConstructor
public class CashbookController {

    private final CashbookService cashbookService;

    @PostMapping
    public ResponseEntity<CashbookResponseDto> create(
            @Valid @RequestBody CashbookRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cashbookService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<CashbookResponseDto>> getAll() {

        return ResponseEntity.ok(
                cashbookService.getAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CashbookResponseDto> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                cashbookService.getById(id)
        );
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<CashbookResponseDto>> getByBusiness(
            @PathVariable Long businessId) {

        return ResponseEntity.ok(
                cashbookService.getByBusiness(businessId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CashbookResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody CashbookRequestDto request) {

        return ResponseEntity.ok(
                cashbookService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        cashbookService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
