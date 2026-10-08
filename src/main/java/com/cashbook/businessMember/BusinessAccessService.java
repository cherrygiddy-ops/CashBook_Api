package com.cashbook.businessMember;

import com.cashbook.auth.AuthService;
import com.cashbook.auth.users.User;
import com.cashbook.business.Business;
import com.cashbook.business.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BusinessAccessService {

    private final AuthService authService;
    private final BusinessRepository businessRepository;
    private final BusinessMemberRepository businessMemberRepository;

    /**
     * Get the currently authenticated user.
     */
    public User getCurrentUser() {
        return authService.getCurrentUser();
    }

    /**
     * Verify that the current user owns or belongs to the business.
     */
    public void verifyAccess(Long businessId) {
        Business business = getBusiness(businessId);
        User currentUser = getCurrentUser();

        if (isOwner(business, currentUser)) {
            return;
        }

        getMembership(businessId, currentUser.getId());
    }

    /**
     * Verify that the current user can view business data.
     * OWNER, ACCOUNTANT, and VIEWER can view.
     */
    public void verifyCanView(Long businessId) {
        getRole(businessId);
    }

    /**
     * Verify that the current user can create transactions.
     * OWNER and ACCOUNTANT can create.
     */
    public void verifyCanCreateTransaction(Long businessId) {
        Member_Role role = getRole(businessId);

        if (role == Member_Role.OWNER
                || role == Member_Role.ACCOUNTANT) {
            return;
        }

        throw forbidden("Your role does not permit creating transactions");
    }

    /**
     * Verify that the current user can update transactions.
     * OWNER and ACCOUNTANT can update.
     */
    public void verifyCanUpdateTransaction(Long businessId) {
        Member_Role role = getRole(businessId);

        if (role == Member_Role.OWNER
                || role == Member_Role.ACCOUNTANT) {
            return;
        }

        throw forbidden("Your role does not permit updating transactions");
    }

    /**
     * Verify that the current user can delete transactions.
     * Only OWNER can delete.
     */
    public void verifyCanDeleteTransaction(Long businessId) {
        if (getRole(businessId) == Member_Role.OWNER) {
            return;
        }

        throw forbidden("Only the business owner can delete transactions");
    }

    /**
     * Verify that the current user can manage business members.
     * Only OWNER can manage members.
     */
    public void verifyCanManageMembers(Long businessId) {
        if (getRole(businessId) == Member_Role.OWNER) {
            return;
        }

        throw forbidden("Only the business owner can manage members");
    }

    /**
     * Get the current user's role within a business.
     * The business owner automatically has OWNER permissions.
     */
    private Member_Role getRole(Long businessId) {
        Business business = getBusiness(businessId);
        User currentUser = getCurrentUser();

        if (isOwner(business, currentUser)) {
            return Member_Role.OWNER;
        }

        BusinessMember member =
                getMembership(businessId, currentUser.getId());

        if (member.getMemberRole() == null) {
            throw forbidden(
                    "No role has been assigned to this membership"
            );
        }

        return member.getMemberRole();
    }

    /**
     * Find a business or return HTTP 404.
     */
    private Business getBusiness(Long businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Business not found"
                        )
                );
    }

    /**
     * Check whether the user owns this business.
     */
    private boolean isOwner(Business business, User user) {
        return business.getOwner() != null
                && business.getOwner().getId().equals(user.getId());
    }

    /**
     * Find the user's membership or return HTTP 403.
     */
    private BusinessMember getMembership(
            Long businessId,
            Long userId
    ) {
        return businessMemberRepository
                .findByBusinessIdAndCashbookuserId(businessId, userId)
                .orElseThrow(() ->
                        forbidden(
                                "You do not have access to this business"
                        )
                );
    }

    /**
     * Create an HTTP 403 Forbidden exception.
     */
    private ResponseStatusException forbidden(String message) {
        return new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                message
        );
    }
}

