package com.cashbook.business;

import com.cashbook.auth.AuthService;
import com.cashbook.auth.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final BusinessMapper businessMapper;
    private final AuthService authService;

    @Transactional
    public BusinessResponseDTO createBusiness(BusinessRequestDto request) {

        User currentUser = authService.getCurrentUser();

        Business business = businessMapper.toEntity(request);

        business.setOwner(currentUser);

        Business savedBusiness = businessRepository.save(business);

        return businessMapper.toResponse(savedBusiness);
    }

    @Transactional(readOnly = true)
    public List<BusinessResponseDTO> getMyBusinesses() {

        User currentUser = authService.getCurrentUser();

        return businessRepository.findByOwnerId(currentUser.getId())
                .stream()
                .map(businessMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BusinessResponseDTO getBusiness(Long id) {

        User currentUser = authService.getCurrentUser();

        Business business = businessRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Business not found"));

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
                .orElseThrow(() ->
                        new RuntimeException("Business not found"));

        verifyOwner(business, currentUser);

        business.setBusinessName(request.getBusinessName());
        business.setEmail(request.getEmail());
        business.setPhoneNumber(request.getPhoneNumber());
        business.setAddress(request.getAddress());

        Business updatedBusiness =
                businessRepository.save(business);

        return businessMapper.toResponse(updatedBusiness);
    }

    @Transactional
    public void deleteBusiness(Long id) {

        User currentUser = authService.getCurrentUser();

        Business business = businessRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Business not found"));

        verifyOwner(business, currentUser);

        businessRepository.delete(business);
    }

    private void verifyOwner(
            Business business,
            User currentUser
    ) {

        if (!business.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException(
                    "You are not authorized to access this business"
            );
        }
    }
}