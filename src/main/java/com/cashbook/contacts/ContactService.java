package com.cashbook.contacts;

import com.cashbook.business.Business;
import com.cashbook.business.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final BusinessRepository businessRepository;
    private final ContactMapper contactMapper;

    public ContactResponseDto create(ContactRequestDto request) {

        Business business = businessRepository.findById(request.getBusinessId())
                .orElseThrow(() ->
                        new RuntimeException("Business not found"));

        if (contactRepository.existsByNameAndBusinessId(
                request.getName(),
                request.getBusinessId())) {

            throw new RuntimeException(
                    "Contact with this name already exists in this business");
        }

        Contact contact = new Contact();

        contact.setName(request.getName());
        contact.setPhoneNumber(request.getPhoneNumber());
        contact.setEmail(request.getEmail());
        contact.setBusiness(business);

        Contact saved = contactRepository.save(contact);

        return contactMapper.toResponse(saved);
    }

    public List<ContactResponseDto> getAll() {

        return contactRepository.findAll()
                .stream()
                .map(contactMapper::toResponse)
                .toList();
    }

    public ContactResponseDto getById(Long id) {

        Contact contact = contactRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Contact not found"));

        return contactMapper.toResponse(contact);
    }

    public List<ContactResponseDto> getByBusiness(Long businessId) {

        if (!businessRepository.existsById(businessId)) {
            throw new RuntimeException("Business not found");
        }

        return contactRepository.findByBusinessId(businessId)
                .stream()
                .map(contactMapper::toResponse)
                .toList();
    }

    public ContactResponseDto update(
            Long id,
            ContactRequestDto request) {

        Contact contact = contactRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Contact not found"));

        Business business = businessRepository.findById(request.getBusinessId())
                .orElseThrow(() ->
                        new RuntimeException("Business not found"));

        contact.setName(request.getName());
        contact.setPhoneNumber(request.getPhoneNumber());
        contact.setEmail(request.getEmail());
        contact.setBusiness(business);

        Contact updated = contactRepository.save(contact);

        return contactMapper.toResponse(updated);
    }

    public void delete(Long id) {

        if (!contactRepository.existsById(id)) {
            throw new RuntimeException("Contact not found");
        }

        contactRepository.deleteById(id);
    }
}