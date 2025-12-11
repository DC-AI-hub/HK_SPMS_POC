package com.spms.backend.service.process.identifier;

import com.spms.backend.repository.entities.process.BusinessKeyEntities;
import com.spms.backend.repository.process.KeyGeneratorRepository;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.model.process.BusinessKeyModel;
import com.spms.backend.service.process.BusinessKeyGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class BusinessKeyGeneratorImpl implements BusinessKeyGenerator {


    private final KeyGeneratorRepository keyGeneratorRepository;

    public BusinessKeyGeneratorImpl(KeyGeneratorRepository keyGeneratorRepository) {
        this.keyGeneratorRepository = keyGeneratorRepository;
    }

    @Transactional
    @Override
    public BusinessKeyModel generateBusinessKey(String keyPrefix, String split) {
        Optional<Long> maxSeq =
                this.keyGeneratorRepository.findMaxSeqByPrefix(keyPrefix);
        BusinessKeyEntities keyEntities = new BusinessKeyEntities();
        if (maxSeq.isPresent()) {
            keyEntities.setSeq(maxSeq.get() + 1L);
        } else {
            keyEntities.setSeq(1);
        }
        keyEntities.setPrefix(keyPrefix);
        this.keyGeneratorRepository.save(keyEntities);

        // Create and return the BusinessKeyModel
        BusinessKeyModel model = new BusinessKeyModel();
        model.setPrefix(keyPrefix);
        model.setSequence(keyEntities.getSeq());
        model.setSplit(split);
        return model;
    }

    @Transactional
    @Override
    public BusinessKeyModel occupiedBusinessKey(Long keyId, String occupiedId, String occupiedTarget) {
        // Find the entity by keyId
        BusinessKeyEntities keyEntity = keyGeneratorRepository.findById(keyId)
                .orElseThrow(() -> new NotFoundException("Business key with id " + keyId + " not found"));

        // Check if already occupied
        if (keyEntity.getOccupiedBy() != null || keyEntity.getTarget() != null) {
            throw new IllegalStateException("Business key with id " + keyId + " is already occupied");
        }
        // Update occupation fields
        keyEntity.setOccupiedBy(occupiedId);
        keyEntity.setTarget(occupiedTarget);
        keyEntity.setOccupiedDate(new Date().getTime());
        keyGeneratorRepository.save(keyEntity);

        // Create and return the BusinessKeyModel
        BusinessKeyModel model = new BusinessKeyModel();
        model.setPrefix(keyEntity.getPrefix());
        model.setSequence(keyEntity.getSeq());
        model.setSplit(null); // Split not stored in entity
        return model;
    }
}
