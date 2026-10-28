package com.cashbook.cashbookMember;

import com.cashbook.auth.users.User;
import com.cashbook.auth.users.UserRepository;
import com.cashbook.businessMember.BusinessAccessService;
import com.cashbook.cashbook.Cashbook;
import com.cashbook.cashbook.CashbookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CashbookMemberService {

    private final CashbookMemberRepository cashbookMemberRepository;
    private final CashbookRepository cashbookRepository;
    private final UserRepository userRepository;
    private final BusinessAccessService businessAccessService;

    /**
     * Share a cashbook with an existing registered user.
     * Only the business owner can manage sharing permissions.
     */
    public CashbookMemberResponse shareCashbook(
            Long cashbookId,
            ShareCashbookRequest request) {

        Cashbook cashbook = getCashbook(cashbookId);

        verifyCanManageSharing(cashbook);

        User user = userRepository
                .findByPhoneNumber(request.getPhoneNumber().trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No registered user found with that phone number"
                ));

        // Prevent the business owner from sharing with themselves.
        if (cashbook.getBusiness().getOwner() != null
                && cashbook.getBusiness().getOwner().getId()
                .equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The owner already has access to this cashbook"
            );
        }

        boolean alreadyShared =
                cashbookMemberRepository
                        .existsByCashbook_IdAndUser_Id(
                                cashbookId,
                                user.getId()
                        );

        if (alreadyShared) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This user already has access to the cashbook"
            );
        }

        CashbookMember member = new CashbookMember();
        member.setCashbook(cashbook);
        member.setUser(user);
        member.setMemberRole(request.getMemberRole());

        CashbookMember saved =
                cashbookMemberRepository.save(member);

        return toResponse(saved);
    }

    /**
     * List the users who have been explicitly shared this cashbook.
     */
    @Transactional(readOnly = true)
    public List<CashbookMemberResponse> getMembers(Long cashbookId) {

        Cashbook cashbook = getCashbook(cashbookId);

        verifyCanManageSharing(cashbook);

        return cashbookMemberRepository
                .findAllByCashbook_Id(cashbookId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Change a shared user's role between EDITOR and VIEWER.
     */
    public CashbookMemberResponse updateMemberRole(
            Long cashbookId,
            Long userId,
            CashbookMemberRole newRole) {

        Cashbook cashbook = getCashbook(cashbookId);

        verifyCanManageSharing(cashbook);

        if (newRole == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Member role is required"
            );
        }

        CashbookMember member = getMember(cashbookId, userId);

        member.setMemberRole(newRole);

        CashbookMember saved =
                cashbookMemberRepository.save(member);

        return toResponse(saved);
    }

    /**
     * Revoke a user's explicitly shared access.
     */
    public void removeMember(Long cashbookId, Long userId) {

        Cashbook cashbook = getCashbook(cashbookId);

        verifyCanManageSharing(cashbook);

        CashbookMember member = getMember(cashbookId, userId);

        cashbookMemberRepository.delete(member);
    }

    private Cashbook getCashbook(Long cashbookId) {
        return cashbookRepository.findById(cashbookId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cashbook not found"
                ));
    }

    private CashbookMember getMember(
            Long cashbookId,
            Long userId) {

        return cashbookMemberRepository
                .findByCashbook_IdAndUser_Id(
                        cashbookId,
                        userId
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Sharing record not found"
                ));
    }

    private void verifyCanManageSharing(Cashbook cashbook) {

        if (cashbook.getBusiness() == null
                || cashbook.getBusiness().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cashbook is not associated with a business"
            );
        }

        businessAccessService.verifyCanManageMembers(
                cashbook.getBusiness().getId()
        );
    }

    private CashbookMemberResponse toResponse(
            CashbookMember member) {

        User user = member.getUser();

        return new CashbookMemberResponse(
                member.getId(),
                user.getId(),
                user.getUsername(),
                user.getPhoneNumber(),
                member.getMemberRole(),
                member.getCreatedAt()
        );
    }
}
