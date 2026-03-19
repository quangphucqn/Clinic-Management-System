package com.tqp.cms.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicineResponse {
    UUID id;
    String code;
    String name;
    String unit;
    String ingredient;
    String manufacturer;
    BigDecimal price;
    Integer stockQuantity;
    String description;
    String imageUrl;
    String imagePublicId;
    boolean active;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
