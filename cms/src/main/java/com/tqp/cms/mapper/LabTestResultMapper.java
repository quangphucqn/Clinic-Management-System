package com.tqp.cms.mapper;

import com.tqp.cms.dto.request.LabTestResultRequest;
import com.tqp.cms.entity.LabTestOrder;
import com.tqp.cms.entity.LabTestResult;
import org.springframework.stereotype.Component;

@Component
public class LabTestResultMapper {

    public LabTestResult toEntity(LabTestResultRequest req,
                                  LabTestOrder order) {

        return LabTestResult.builder()
                .labTestOrder(order)
                .resultValue(req.getResultValue())
                .normalRange(req.getNormalRange())
                .attachmentUrl(req.getAttachmentUrl())
                .build();
    }
}
