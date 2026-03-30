package com.tqp.cms.mapper;

import com.tqp.cms.dto.response.LabTestOrderDetailResponse;
import com.tqp.cms.dto.response.LabTestResultResponse;
import com.tqp.cms.entity.LabTestOrder;
import com.tqp.cms.entity.LabTestResult;
import org.springframework.stereotype.Component;

@Component
public class LabTestMapper {

    public LabTestOrderDetailResponse toDetailResponse(LabTestOrder order) {

        LabTestResult result = order.getLabTestResult();

        return LabTestOrderDetailResponse.builder()
                .id(order.getId())
                .testName(order.getTestName())
                .status(order.getStatus().name())
                .requestNote(order.getRequestNote())
                .result(result != null
                        ? LabTestResultResponse.builder()
                        .resultValue(result.getResultValue())
                        .normalRange(result.getNormalRange())
                        .attachmentUrl(result.getAttachmentUrl())
                        .build()
                        : null
                )
                .build();
    }
}
