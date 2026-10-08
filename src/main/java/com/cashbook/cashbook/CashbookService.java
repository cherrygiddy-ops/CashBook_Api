package com.cashbook.cashbook;


import com.cashbook.business.Business;
import com.cashbook.business.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CashbookService {

    private final CashbookRepository cashbookRepository;
    private final BusinessRepository businessRepository;
    private final CashbookMapper cashbookMapper;

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

        return cashbookRepository.findAll()
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

        if (!businessRepository.existsById(businessId)) {
            throw new RuntimeException(
                    "Business not found with id: " + businessId);
        }

        return cashbookRepository.findByBusinessId(businessId)
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