package com.cashbook.cashbookMember;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cashbooks/{cashbookId}/members")
@RequiredArgsConstructor
@Validated
public class CashbookMemberController {

    private final CashbookMemberService cashbookMemberService;

    @PostMapping
    public ResponseEntity<CashbookMemberResponse> shareCashbook(
            @PathVariable Long cashbookId,
            @Valid @RequestBody ShareCashbookRequest request) {

        CashbookMemberResponse response =
                cashbookMemberService.shareCashbook(cashbookId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CashbookMemberResponse>> getMembers(
            @PathVariable Long cashbookId) {

        return ResponseEntity.ok(
                cashbookMemberService.getMembers(cashbookId)
        );
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<CashbookMemberResponse> updateMemberRole(
            @PathVariable Long cashbookId,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateCashbookMemberRoleRequest request) {

        CashbookMemberResponse response =
                cashbookMemberService.updateMemberRole(
                        cashbookId,
                        userId,
                        request.getMemberRole()
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long cashbookId,
            @PathVariable Long userId) {

        cashbookMemberService.removeMember(cashbookId, userId);

        return ResponseEntity.noContent().build();
    }
}
