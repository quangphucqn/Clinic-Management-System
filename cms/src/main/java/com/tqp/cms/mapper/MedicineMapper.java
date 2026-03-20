package com.tqp.cms.mapper;

import com.tqp.cms.dto.request.MedicineCreationRequest;
import com.tqp.cms.dto.response.MedicineResponse;
import com.tqp.cms.entity.Medicine;
import org.springframework.stereotype.Component;

@Component
public class MedicineMapper {
    public Medicine toMedicine(MedicineCreationRequest request) {
        return Medicine.builder()
                .code(request.getCode())
                .name(request.getName())
                .ingredient(request.getIngredient())
                .manufacturer(request.getManufacturer())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .build();
    }

    public MedicineResponse toMedicineResponse(Medicine medicine) {
        return MedicineResponse.builder()
                .id(medicine.getId())
                .code(medicine.getCode())
                .name(medicine.getName())
                .unitName(medicine.getUnit() != null ? medicine.getUnit().getName() : null)
                .ingredient(medicine.getIngredient())
                .manufacturer(medicine.getManufacturer())
                .price(medicine.getPrice())
                .stockQuantity(medicine.getStockQuantity())
                .description(medicine.getDescription())
                .imageUrl(medicine.getImageUrl())
                .imagePublicId(medicine.getImagePublicId())
                .active(medicine.isActive())
                .createdAt(medicine.getCreatedAt())
                .updatedAt(medicine.getUpdatedAt())
                .build();
    }
}
