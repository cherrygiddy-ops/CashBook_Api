package com.cashbook.businessMember;

import com.cashbook.auth.AuthService;
import com.cashbook.auth.users.User;
import com.cashbook.auth.users.UserRepository;
import com.cashbook.business.Business;
import com.cashbook.business.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BusinessMemberService {

    private final BusinessMemberRepository businessMemberRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final BusinessMemberMapper businessMemberMapper;
    private final AuthService authService;

    public BusinessMemberResponseDto addMember(
            BusinessMemberRequestDto request) {

        User currentUser = authService.getCurrentUser();

        Business business = businessRepository.findById(request.getBusinessId())
                .orElseThrow(() ->
                        new RuntimeException("Business not found"));

        // Only the owner can add members
        verifyOwner(business, currentUser);

        /*
         * Find the user using phone number.
         *
         * If the user exists, use the existing user.
         * If the user does not exist, create a new user.
         */
        User user = userRepository
                .findByPhoneNumber(request.getPhoneNumber())
                .orElseGet(() -> createUser(request.getPhoneNumber()));

        // Owner cannot be added as a member
        if (business.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "Business owner cannot be added as a member"
            );
        }

        // Prevent duplicate membership
        if (businessMemberRepository
                .existsByBusinessIdAndCashbookuserId(
                        business.getId(),
                        user.getId())) {

            throw new RuntimeException(
                    "User is already a member of this business"
            );
        }

        BusinessMember member = new BusinessMember();

        member.setBusiness(business);
        member.setCashbookuser(user);
        member.setMemberRole(request.getMemberRole());

        BusinessMember saved =
                businessMemberRepository.save(member);

        return businessMemberMapper.toResponse(saved);
    }

    /**
     * Creates a new user when the supplied phone number
     * does not exist in the system.
     */
    private User createUser(String phoneNumber) {

        User user = new User();

        user.setPhoneNumber(phoneNumber);

        // Set any other fields required by your User entity here.
        // For example:
        // user.setActive(true);
        // user.setVerified(false);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<BusinessMemberResponseDto> getMembers(
            Long businessId) {

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() ->
                        new RuntimeException("Business not found"));

        User currentUser = authService.getCurrentUser();

        // User must have access to the business
        verifyAccess(business, currentUser);

        return businessMemberRepository
                .findByBusinessId(businessId)
                .stream()
                .map(businessMemberMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BusinessMemberResponseDto getMember(Long id) {

        BusinessMember member =
                businessMemberRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Business member not found"));

        User currentUser = authService.getCurrentUser();

        verifyAccess(member.getBusiness(), currentUser);

        return businessMemberMapper.toResponse(member);
    }

    public BusinessMemberResponseDto updateMember(
            Long id,
            BusinessMemberRequestDto request) {

        BusinessMember member =
                businessMemberRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Business member not found"));

        User currentUser = authService.getCurrentUser();

        verifyOwner(member.getBusiness(), currentUser);

        // Business and user cannot be changed.
        // Only the member role can be changed.

        member.setMemberRole(request.getMemberRole());

        BusinessMember updated =
                businessMemberRepository.save(member);

        return businessMemberMapper.toResponse(updated);
    }

    public void removeMember(Long id) {

        BusinessMember member =
                businessMemberRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Business member not found"));

        User currentUser = authService.getCurrentUser();

        verifyOwner(member.getBusiness(), currentUser);

        businessMemberRepository.delete(member);
    }

    private void verifyOwner(
            Business business,
            User currentUser) {

        if (business.getOwner() == null ||
                !business.getOwner()
                        .getId()
                        .equals(currentUser.getId())) {

            throw new RuntimeException(
                    "Only the business owner can manage members"
            );
        }
    }

    private void verifyAccess(
            Business business,
            User currentUser) {

        if (business.getOwner() != null &&
                business.getOwner()
                        .getId()
                        .equals(currentUser.getId())) {
            return;
        }

        boolean member =
                businessMemberRepository
                        .existsByBusinessIdAndCashbookuserId(
                                business.getId(),
                                currentUser.getId()
                        );

        if (!member) {
            throw new RuntimeException(
                    "You do not have access to this business"
            );
        }
    }
}