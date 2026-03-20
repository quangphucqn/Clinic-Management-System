package com.tqp.cms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class MedicineCreationRequest {
    @NotBlank(message = "FIELD_REQUIRED")
    @Size(max = 50, message = "VALIDATION_ERROR")
    String code;

    @NotBlank(message = "FIELD_REQUIRED")
    @Size(max = 150, message = "VALIDATION_ERROR")
    String name;

    @NotBlank(message = "FIELD_REQUIRED")
    @Size(max = 255, message = "VALIDATION_ERROR")
    String unitName;

    String ingredient;

    @Size(max = 150, message = "VALIDATION_ERROR")
    String manufacturer;

    @NotNull(message = "FIELD_REQUIRED")
    @Positive(message = "VALIDATION_ERROR")
    BigDecimal price;

    @NotNull(message = "FIELD_REQUIRED")
    @PositiveOrZero(message = "VALIDATION_ERROR")
    Integer stockQuantity;

    String description;

    @Size(max = 500, message = "VALIDATION_ERROR")
    String imageUrl;
}
