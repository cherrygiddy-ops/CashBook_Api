package com.cashbook.category;
import com.cashbook.business.Business;
import com.cashbook.business.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BusinessRepository businessRepository;
    private final CategoryMapper categoryMapper;

    public CategoryResponseDto create(CategoryRequestDto request) {

        Business business = businessRepository.findById(request.getBusinessId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Business not found with id: "
                                        + request.getBusinessId()));

        if (categoryRepository.existsByNameAndBusinessId(
                request.getName(),
                request.getBusinessId())) {

            throw new RuntimeException(
                    "Category already exists in this business");
        }

        Category category = new Category();

        category.setName(request.getName());
        category.setBusiness(business);

        Category saved = categoryRepository.save(category);

        return categoryMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAll() {

        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponseDto getById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Category not found with id: " + id));

        return categoryMapper.toResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getByBusiness(Long businessId) {

        if (!businessRepository.existsById(businessId)) {
            throw new RuntimeException(
                    "Business not found with id: " + businessId);
        }

        return categoryRepository.findByBusinessId(businessId)
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    public CategoryResponseDto update(
            Long id,
            CategoryRequestDto request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Category not found with id: " + id));

        Business business = businessRepository.findById(
                        request.getBusinessId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Business not found with id: "
                                        + request.getBusinessId()));

        category.setName(request.getName());
        category.setBusiness(business);

        Category updated = categoryRepository.save(category);

        return categoryMapper.toResponse(updated);
    }

    public void delete(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Category not found with id: " + id));

        categoryRepository.delete(category);
    }
}
