
package com.cashbook.business;

import com.cashbook.auth.AuthService;
import com.cashbook.auth.users.User;
import com.cashbook.businessMember.BusinessMember;
import com.cashbook.businessMember.BusinessMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final BusinessMapper businessMapper;
    private final AuthService authService;
    private final BusinessMemberRepository businessMemberRepository;

    @Transactional
    public BusinessResponseDTO createBusiness(BusinessRequestDto request) {
        User currentUser = authService.getCurrentUser();

        Business business = businessMapper.toEntity(request);
        business.setOwner(currentUser);

        return businessMapper.toResponse(
                businessRepository.save(business)
        );
    }

    @Transactional(readOnly = true)
    public List<BusinessResponseDTO> getMyBusinesses() {
        User currentUser = authService.getCurrentUser();
        Long userId = currentUser.getId();

        Map<Long, Business> accessibleBusinesses = new LinkedHashMap<>();

        businessRepository.findByOwnerId(userId)
                .forEach(business ->
                        accessibleBusinesses.put(business.getId(), business)
                );

        List<BusinessMember> memberships =
                businessMemberRepository.findByCashbookuserId(userId);

        memberships.stream()
                .map(BusinessMember::getBusiness)
                .filter(business -> business != null)
                .forEach(business ->
                        accessibleBusinesses.put(business.getId(), business)
                );

        return accessibleBusinesses.values()
                .stream()
                .map(businessMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BusinessResponseDTO getBusiness(Long id) {
        User currentUser = authService.getCurrentUser();

        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Business not found"
                ));

        verifyOwner(business, currentUser);

        return businessMapper.toResponse(business);
    }

    @Transactional
    public BusinessResponseDTO updateBusiness(
            Long id,
            BusinessRequestDto request
    ) {
        User currentUser = authService.getCurrentUser();

        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Business not found"
                ));

        verifyOwner(business, currentUser);

        business.setBusinessName(request.getBusinessName());
        business.setEmail(request.getEmail());
        business.setPhoneNumber(request.getPhoneNumber());
        business.setAddress(request.getAddress());

        return businessMapper.toResponse(
                businessRepository.save(business)
        );
    }

    @Transactional
    public void deleteBusiness(Long id) {
        User currentUser = authService.getCurrentUser();

        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Business not found"
                ));

        verifyOwner(business, currentUser);
        businessRepository.delete(business);
    }

    private void verifyOwner(Business business, User currentUser) {
        if (business.getOwner() == null
                || currentUser == null
                || currentUser.getId() == null
                || !business.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to manage this business"
            );
        }
    }
}
