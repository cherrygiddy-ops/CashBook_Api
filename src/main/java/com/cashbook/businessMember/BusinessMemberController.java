package com.cashbook.businessMember;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business-members")
@RequiredArgsConstructor
public class BusinessMemberController {

    private final BusinessMemberService businessMemberService;

    @PostMapping
    public ResponseEntity<BusinessMemberResponseDto> addMember(
            @Valid @RequestBody BusinessMemberRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(businessMemberService.addMember(request));
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<BusinessMemberResponseDto>> getMembers(
            @PathVariable Long businessId) {

        return ResponseEntity.ok(
                businessMemberService.getMembers(businessId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessMemberResponseDto> getMember(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                businessMemberService.getMember(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessMemberResponseDto> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody BusinessMemberRequestDto request) {

        return ResponseEntity.ok(
                businessMemberService.updateMember(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long id) {

        businessMemberService.removeMember(id);

        return ResponseEntity.noContent().build();
    }
}