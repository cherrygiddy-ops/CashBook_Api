package com.cashbook.transactions;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDto> create(
            @Valid @RequestBody TransactionRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDto>> getAll() {

        return ResponseEntity.ok(
                transactionService.getAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                transactionService.getById(id)
        );
    }

    @GetMapping("/cashbook/{cashbookId}")
    public ResponseEntity<List<TransactionResponseDto>> getByCashbook(
            @PathVariable Long cashbookId) {

        return ResponseEntity.ok(
                transactionService.getByCashbook(cashbookId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequestDto request) {

        return ResponseEntity.ok(
                transactionService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        transactionService.delete(id);

        return ResponseEntity.noContent().build();
    }
}