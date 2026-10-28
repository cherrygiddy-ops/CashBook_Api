package com.cashbook.cashbook;


import com.cashbook.business.Business;
import com.cashbook.business.BusinessRepository;
import com.cashbook.businessMember.BusinessMemberRepository;
import com.cashbook.cashbookMember.CashbookMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cashbook.businessMember.BusinessAccessService;
import com.cashbook.businessMember.BusinessMember;
import com.cashbook.cashbookMember.CashbookMember;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CashbookService {

    private final CashbookRepository cashbookRepository;
    private final BusinessRepository businessRepository;
    private final CashbookMapper cashbookMapper;
    private final BusinessAccessService businessAccessService;
    private final BusinessMemberRepository businessMemberRepository;
    private final CashbookMemberRepository cashbookMemberRepository;

    public CashbookResponseDto create(CashbookRequestDto request) {

        Business business = businessRepository.findById(request.getBusinessId())
                .orElseThrow(() ->
                        new RuntimeException("Business not found with id: "
                                + request.getBusinessId()));

        if (cashbookRepository.existsByNameAndBusinessId(
                request.getName(),
                request.getBusinessId())) {

            throw new RuntimeException(
                    "Cashbook already exists in this business");
        }

        Cashbook cashbook = new Cashbook();

        cashbook.setName(request.getName());
        cashbook.setDescription(request.getDescription());
        cashbook.setBusiness(business);

        Cashbook saved = cashbookRepository.save(cashbook);

        return cashbookMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CashbookResponseDto> getAll() {
        Long userId = businessAccessService.getCurrentUser().getId();

        // Businesses owned by the current user.
        Set<Long> businessIds = businessRepository.findByOwnerId(userId)
                .stream()
                .map(Business::getId)
                .collect(Collectors.toSet());

        // Businesses where the current user is a member.
        List<BusinessMember> memberships =
                businessMemberRepository.findByCashbookuserId(userId);

        memberships.stream()
                .map(member -> member.getBusiness().getId())
                .forEach(businessIds::add);

        // Cashbooks available through business access.
        Map<Long, Cashbook> accessible = new LinkedHashMap<>();

        if (!businessIds.isEmpty()) {
            cashbookRepository.findByBusinessIdIn(businessIds)
                    .forEach(book -> accessible.put(book.getId(), book));
        }

        // Cashbooks shared directly with the current user.
        List<CashbookMember> shared =
                cashbookMemberRepository.findByUser_Id(userId);

        shared.stream()
                .map(CashbookMember::getCashbook)
                .forEach(book -> accessible.put(book.getId(), book));

        return accessible.values()
                .stream()
                .map(cashbookMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CashbookResponseDto getById(Long id) {

        Cashbook cashbook = cashbookRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cashbook not found with id: " + id));

        return cashbookMapper.toResponse(cashbook);
    }

    @Transactional(readOnly = true)
    public List<CashbookResponseDto> getByBusiness(Long businessId) {

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Business not found"
                ));

        var currentUser = businessAccessService.getCurrentUser();
        Long userId = currentUser.getId();

        boolean isOwner = business.getOwner() != null
                && business.getOwner().getId().equals(userId);

        boolean isBusinessMember = businessMemberRepository
                .findByBusinessIdAndCashbookuserId(businessId, userId)
                .isPresent();

        if (isOwner || isBusinessMember) {
            return cashbookRepository.findByBusinessId(businessId)
                    .stream()
                    .map(cashbookMapper::toResponse)
                    .toList();
        }

        // A direct cashbook share does not grant access to every
        // cashbook in the business.
        return cashbookMemberRepository.findByUser_Id(userId)
                .stream()
                .map(CashbookMember::getCashbook)
                .filter(cashbook ->
                        cashbook.getBusiness() != null
                                && businessId.equals(
                                cashbook.getBusiness().getId()
                        )
                )
                .collect(Collectors.toMap(
                        Cashbook::getId,
                        cashbook -> cashbook,
                        (first, duplicate) -> first,
                        LinkedHashMap::new
                ))
                .values()
                .stream()
                .map(cashbookMapper::toResponse)
                .toList();
    }

    public CashbookResponseDto update(
            Long id,
            CashbookRequestDto request) {

        Cashbook cashbook = cashbookRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cashbook not found with id: " + id));

        Business business = businessRepository.findById(request.getBusinessId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Business not found with id: "
                                        + request.getBusinessId()));

        cashbook.setName(request.getName());
        cashbook.setDescription(request.getDescription());
        cashbook.setBusiness(business);

        Cashbook updated = cashbookRepository.save(cashbook);

        return cashbookMapper.toResponse(updated);
    }

    public void delete(Long id) {

        Cashbook cashbook = cashbookRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cashbook not found with id: " + id));

        cashbookRepository.delete(cashbook);
    }
}