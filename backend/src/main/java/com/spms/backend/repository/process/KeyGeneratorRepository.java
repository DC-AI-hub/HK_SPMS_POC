package com.spms.backend.repository.process;

import com.spms.backend.repository.entities.process.BusinessKeyEntities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface KeyGeneratorRepository  extends
        JpaRepository<BusinessKeyEntities,Long> {

    /**
     * Get the max sequence by prefix
     * @param prefix The prefix to search for
     * @return The maximum sequence number for the given prefix
     */
    @Query("SELECT MAX(b.seq) FROM BusinessKeyEntities b WHERE b.prefix = :prefix")
    Optional<Long> findMaxSeqByPrefix(@Param("prefix") String prefix);

    /**
     * Update the occupiedBy, occupiedDate and target by id
     * @param id The ID of the BusinessKeyEntities to update
     * @param occupiedBy The new occupiedBy value
     * @param occupiedDate The new occupiedDate value
     * @param target The new target value
     */
    @Modifying
    @Query("UPDATE BusinessKeyEntities b SET b.occupiedBy = :occupiedBy, b.occupiedDate = :occupiedDate, b.target = :target WHERE b.id = :id")
    void updateOccupiedFields(@Param("id") Long id, 
                             @Param("occupiedBy") String occupiedBy, 
                             @Param("occupiedDate") Long occupiedDate, 
                             @Param("target") String target);

}
