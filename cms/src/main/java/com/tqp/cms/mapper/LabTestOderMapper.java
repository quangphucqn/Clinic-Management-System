package com.tqp.cms.mapper;


import com.tqp.cms.dto.request.LabTestOrderRequest;
import com.tqp.cms.dto.response.LabTestOrderResponse;
import com.tqp.cms.entity.*;
import org.springframework.stereotype.Component;




@Component
public class LabTestOderMapper {

    public LabTestOrder toEntity(LabTestOrderRequest req,
                                 MedicalRecord record,
                                 Patient patient,
                                 Doctor doctor) {

        return LabTestOrder.builder()
                .medicalRecord(record)
                .patient(patient)
                .doctor(doctor)
                .testName(req.getTestName())
                .requestNote(req.getRequestNote())
                .status(LabTestOrderStatus.REQUESTED)
                .build();
    }

    public LabTestOrderResponse toResponse(LabTestOrder order) {
        return LabTestOrderResponse.builder()
                .id(order.getId())
                .testName(order.getTestName())
                .status(order.getStatus().name())
                .build();
    }
}