package com.tqp.cms.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tqp.cms.dto.request.MedicineCreationRequest;
import com.tqp.cms.dto.request.MedicineUpdateRequest;
import com.tqp.cms.dto.response.MedicineImageResponse;
import com.tqp.cms.dto.response.MedicineResponse;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.mapper.MedicineMapper;
import com.tqp.cms.repository.MedicineRepository;
import com.tqp.cms.repository.UnitRepository;
import com.tqp.cms.service.MedicineService;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MedicineServiceImpl implements MedicineService {
    MedicineRepository medicineRepository;
    UnitRepository unitRepository;
    MedicineMapper medicineMapper;
    Cloudinary cloudinary;

    @Override
    @Transactional
    public MedicineResponse createMedicine(MedicineCreationRequest request) {
        return createMedicine(request, null);
    }

    @Override
    @Transactional
    public MedicineResponse createMedicine(MedicineCreationRequest request, MultipartFile file) {
        if (medicineRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.MEDICINE_EXISTED);
        }
        var medicine = medicineMapper.toMedicine(request);
        medicine.setUnit(resolveOrCreateUnit(request.getUnitName()));
        var savedMedicine = medicineRepository.save(medicine);
        if (file != null && !file.isEmpty()) {
            uploadMedicineImage(savedMedicine.getId(), file);
            savedMedicine = medicineRepository.findById(savedMedicine.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.MEDICINE_NOT_FOUND));
        }
        return medicineMapper.toMedicineResponse(savedMedicine);
    }

    @Override
    public MedicineResponse getMedicineById(UUID medicineId) {
        var medicine = medicineRepository.findById(medicineId)
                .filter(m -> m.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.MEDICINE_NOT_FOUND));
        return medicineMapper.toMedicineResponse(medicine);
    }

    @Override
    public Page<MedicineResponse> getMedicines(int page, int size, String name, String category) {
        Pageable pageable = PageRequest.of(page, size,Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<com.tqp.cms.entity.Medicine> medicines;
        boolean hasName = name != null && !name.isBlank();
        boolean hasCategory = category != null && !category.isBlank();
        if (hasName && hasCategory) {
            medicines = medicineRepository.findByNameAndCategory(name, category, pageable);
        } else if (hasName) {
            medicines = medicineRepository.findByActiveTrueAndNameContainingIgnoreCase(name, pageable);
        } else if (hasCategory) {
            medicines = medicineRepository.findByActiveTrueAndUnit_NameContainingIgnoreCase(category, pageable);
        } else {
            medicines = medicineRepository.findByActiveTrue(pageable);
        }
        return medicines.map(medicineMapper::toMedicineResponse);
    }

    @Override
    @Transactional
    public MedicineResponse updateMedicine(UUID medicineId, MedicineUpdateRequest request) {
        return updateMedicine(medicineId, request, null);
    }

    @Override
    @Transactional
    public MedicineResponse updateMedicine(UUID medicineId, MedicineUpdateRequest request, MultipartFile file) {
        var medicine = medicineRepository.findById(medicineId)
                .filter(m -> m.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.MEDICINE_NOT_FOUND));

        if (request.getName() != null) {
            medicine.setName(request.getName());
        }
        if (request.getUnitName() != null) {
            medicine.setUnit(resolveOrCreateUnit(request.getUnitName()));
        }
        if (request.getIngredient() != null) {
            medicine.setIngredient(request.getIngredient());
        }
        if (request.getManufacturer() != null) {
            medicine.setManufacturer(request.getManufacturer());
        }
        if (request.getPrice() != null) {
            medicine.setPrice(request.getPrice());
        }
        if (request.getStockQuantity() != null) {
            medicine.setStockQuantity(request.getStockQuantity());
        }
        if (request.getDescription() != null) {
            medicine.setDescription(request.getDescription());
        }
        if (request.getImageUrl() != null) {
            medicine.setImageUrl(request.getImageUrl());
        }

        var savedMedicine = medicineRepository.save(medicine);
        if (file != null && !file.isEmpty()) {
            uploadMedicineImage(savedMedicine.getId(), file);
            savedMedicine = medicineRepository.findById(savedMedicine.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.MEDICINE_NOT_FOUND));
        }

        return medicineMapper.toMedicineResponse(savedMedicine);
    }

    @Override
    @Transactional
    public void softDeleteMedicine(UUID medicineId) {
        var medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new AppException(ErrorCode.MEDICINE_NOT_FOUND));
        medicineRepository.delete(medicine);
    }

    private com.tqp.cms.entity.Unit resolveOrCreateUnit(String unitName) {
        if (unitName == null || unitName.isBlank()) {
            throw new AppException(ErrorCode.FIELD_REQUIRED);
        }
        return unitRepository.findByName(unitName.trim())
                .orElseGet(() -> unitRepository.save(com.tqp.cms.entity.Unit.builder().name(unitName.trim()).build()));
    }

    @Override
    @Transactional
    public MedicineImageResponse uploadMedicineImage(UUID medicineId, MultipartFile file) {
        if (file == null || file.isEmpty() || file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        var medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new AppException(ErrorCode.MEDICINE_NOT_FOUND));

        try {
            String publicId = "cms/medicines/medicine-%s-image".formatted(medicineId);

            if (medicine.getImagePublicId() != null && !medicine.getImagePublicId().isBlank()) {
                cloudinary.uploader().destroy(medicine.getImagePublicId(), ObjectUtils.asMap("resource_type", "image"));
            }

            var uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "resource_type", "image",
                            "overwrite", true
                    )
            );

            String imageUrl = (String) uploadResult.get("secure_url");
            String imagePublicId = (String) uploadResult.get("public_id");

            if (imageUrl == null || imagePublicId == null) {
                throw new AppException(ErrorCode.MEDICINE_IMAGE_UPLOAD_FAILED);
            }

            medicine.setImageUrl(imageUrl);
            medicine.setImagePublicId(imagePublicId);
            medicineRepository.save(medicine);

            return MedicineImageResponse.builder()
                    .medicineId(medicine.getId())
                    .imageUrl(imageUrl)
                    .imagePublicId(imagePublicId)
                    .build();
        } catch (Exception exception) {
            throw new AppException(ErrorCode.MEDICINE_IMAGE_UPLOAD_FAILED);
        }
    }
}
