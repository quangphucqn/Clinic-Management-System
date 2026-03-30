package com.tqp.cms.service;

import com.tqp.cms.dto.request.MedicineCreationRequest;
import com.tqp.cms.dto.request.MedicineUpdateRequest;
import com.tqp.cms.dto.response.MedicineImageResponse;
import com.tqp.cms.dto.response.MedicineResponse;
import org.springframework.data.domain.Page;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MedicineService {
    MedicineResponse createMedicine(MedicineCreationRequest request);

    MedicineResponse createMedicine(MedicineCreationRequest request, MultipartFile file);

    MedicineResponse getMedicineById(UUID medicineId);

    Page<MedicineResponse> getMedicines(int page, int size, String name);

    MedicineResponse updateMedicine(UUID medicineId, MedicineUpdateRequest request);

    MedicineResponse updateMedicine(UUID medicineId, MedicineUpdateRequest request, MultipartFile file);

    void softDeleteMedicine(UUID medicineId);

    MedicineImageResponse uploadMedicineImage(UUID medicineId, MultipartFile file);
}
