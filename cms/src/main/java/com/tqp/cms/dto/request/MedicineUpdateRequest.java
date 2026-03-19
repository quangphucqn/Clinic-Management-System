package com.tqp.cms.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicineUpdateRequest {
    @Size(max = 150, message = "VALIDATION_ERROR")
    String name;

    @Size(max = 255, message = "VALIDATION_ERROR")
    String unit;

    String ingredient;

    @Size(max = 150, message = "VALIDATION_ERROR")
    String manufacturer;

    @Positive(message = "VALIDATION_ERROR")
    BigDecimal price;

    @PositiveOrZero(message = "VALIDATION_ERROR")
    Integer stockQuantity;

    String description;

    @Size(max = 500, message = "VALIDATION_ERROR")
    String imageUrl;
}
