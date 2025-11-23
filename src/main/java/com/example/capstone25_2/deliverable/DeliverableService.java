package com.example.capstone25_2.deliverable;

import com.example.capstone25_2.deliverable.Deliverable;
import com.example.capstone25_2.deliverable.DeliverableRepository;
import com.example.capstone25_2.deliverable.dto.DeliverableCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliverableService {

    private final DeliverableRepository deliverableRepository;

    @Transactional
    public Long createDeliverable(DeliverableCreateRequestDto requestDto) {
        Deliverable deliverable = requestDto.toEntity();
        deliverableRepository.save(deliverable);
        return deliverable.getId();
    }

    @Transactional(readOnly = true)
    public List<Deliverable> getAllDeliverables() {
        return deliverableRepository.findAll();
    }
}