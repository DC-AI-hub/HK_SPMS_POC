package com.spms.backend.service.process.identifier;

import com.spms.backend.repository.entities.process.BusinessKeyEntities;
import com.spms.backend.repository.process.KeyGeneratorRepository;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.model.process.BusinessKeyModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessKeyGeneratorImplTest {

    @Mock
    private KeyGeneratorRepository keyGeneratorRepository;

    @InjectMocks
    private BusinessKeyGeneratorImpl businessKeyGenerator;

    private BusinessKeyEntities unoccupiedKey;
    private BusinessKeyEntities occupiedKey;

    @BeforeEach
    void setUp() {
        // Setup unoccupied key entity
        unoccupiedKey = new BusinessKeyEntities();
        unoccupiedKey.setId(1L);
        unoccupiedKey.setPrefix("TEST");
        unoccupiedKey.setSeq(100L);
        unoccupiedKey.setOccupiedBy(null);
        unoccupiedKey.setTarget(null);

        // Setup occupied key entity
        occupiedKey = new BusinessKeyEntities();
        occupiedKey.setId(2L);
        occupiedKey.setPrefix("OCCUPIED");
        occupiedKey.setSeq(200L);
        occupiedKey.setOccupiedBy("user123");
        occupiedKey.setTarget("process456");
    }

    @Test
    void occupiedBusinessKey_WhenKeyNotFound_ShouldThrowNotFoundException() {
        when(keyGeneratorRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> businessKeyGenerator.occupiedBusinessKey(999L, "user1", "target1"));

        assertEquals("Business key with id 999 not found", exception.getMessage());
        verify(keyGeneratorRepository, times(1)).findById(999L);
    }

    @Test
    void occupiedBusinessKey_WhenKeyAlreadyOccupied_ShouldThrowIllegalStateException() {
        when(keyGeneratorRepository.findById(2L)).thenReturn(Optional.of(occupiedKey));

        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> businessKeyGenerator.occupiedBusinessKey(2L, "user2", "target2"));

        assertEquals("Business key with id 2 is already occupied", exception.getMessage());
        verify(keyGeneratorRepository, times(1)).findById(2L);
    }

    @Test
    void occupiedBusinessKey_WhenValid_ShouldUpdateAndReturnModel() {
        when(keyGeneratorRepository.findById(1L)).thenReturn(Optional.of(unoccupiedKey));
        when(keyGeneratorRepository.save(any(BusinessKeyEntities.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BusinessKeyModel result = businessKeyGenerator.occupiedBusinessKey(1L, "user1", "target1");

        assertNotNull(result);
        assertEquals("TEST", result.getPrefix());
        assertEquals(100L, result.getSequence());
        assertNull(result.getSplit());
        
        // Verify occupation fields were updated
        assertNotNull(unoccupiedKey.getOccupiedBy());
        assertEquals("user1", unoccupiedKey.getOccupiedBy());
        assertEquals("target1", unoccupiedKey.getTarget());
        assertNotNull(unoccupiedKey.getOccupiedDate());
        
        verify(keyGeneratorRepository, times(1)).findById(1L);
        verify(keyGeneratorRepository, times(1)).save(unoccupiedKey);
    }

    @Test
    void generateBusinessKey_WhenNoExistingKey_ShouldStartSequenceAtOne() {
        when(keyGeneratorRepository.findMaxSeqByPrefix("NEW")).thenReturn(Optional.empty());
        when(keyGeneratorRepository.save(any(BusinessKeyEntities.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BusinessKeyModel result = businessKeyGenerator.generateBusinessKey("NEW", "-");

        assertNotNull(result);
        assertEquals("NEW", result.getPrefix());
        assertEquals(1L, result.getSequence());
        assertEquals("-", result.getSplit());
        
        verify(keyGeneratorRepository, times(1)).findMaxSeqByPrefix("NEW");
        verify(keyGeneratorRepository, times(1)).save(any(BusinessKeyEntities.class));
    }

    @Test
    void generateBusinessKey_WhenExistingKey_ShouldIncrementSequence() {
        when(keyGeneratorRepository.findMaxSeqByPrefix("EXISTING")).thenReturn(Optional.of(50L));
        when(keyGeneratorRepository.save(any(BusinessKeyEntities.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BusinessKeyModel result = businessKeyGenerator.generateBusinessKey("EXISTING", ":");

        assertNotNull(result);
        assertEquals("EXISTING", result.getPrefix());
        assertEquals(51L, result.getSequence());
        assertEquals(":", result.getSplit());
        
        verify(keyGeneratorRepository, times(1)).findMaxSeqByPrefix("EXISTING");
        verify(keyGeneratorRepository, times(1)).save(any(BusinessKeyEntities.class));
    }
}
