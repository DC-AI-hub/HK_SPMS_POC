package com.spms.backend.service.process.impl;

import com.spms.backend.repository.entities.process.FormVersionEntity;
import com.spms.backend.repository.process.FormVersionRepository;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.model.process.FormDefinitionModel;
import com.spms.backend.service.model.process.FormVersionModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FormServiceImplTests {

    @Mock
    private FormVersionRepository formVersionRepository;

    @InjectMocks
    private FormServiceImpl formService;

    private FormDefinitionModel createFormModel(String version) {
        FormDefinitionModel model = new FormDefinitionModel();
        model.setVersion(version);
        model.setName("Test Form");
        model.setDefinition("{}");
        return model;
        // publishVersion tests
    }

    // publishVersion tests
    @Test
    void publishVersion_SuccessfulPublication_UpdatesModel() {
        // Setup
        FormVersionModel model = new FormVersionModel();
        model.setDeprecated(true);
        model.setName("Test Form");
        model.setVersion(1000 * 1000L);//1.0.0
        model.setKey("leave-request");
        
        FormVersionEntity entity = FormVersionModel.toEntity(model);
        entity.setId(1L);
        entity.setDeprecated(false);
        when(formVersionRepository.save(any(FormVersionEntity.class)))
            .thenReturn(entity);

        // Execute
        FormVersionModel result = formService.publishVersion(model);

        // Verify
        assertFalse(result.isDeprecated());
        verify(formVersionRepository).save(any(FormVersionEntity.class));
    }

    @Test
    void publishVersion_NullModel_ThrowsException() {
        assertThrows(NullPointerException.class, () -> formService.publishVersion(null));
    }
    
    private FormVersionEntity createVersionEntity(Long version) {
        FormVersionEntity entity = new FormVersionEntity();
        entity.setId(1L);
        entity.setKey("leave-request");
        entity.setVersion(version);
        entity.setName("Test Form");
        entity.setFormDefinition("{}");
        return entity;
    }

    @Test
    void createFormVersion_ValidRequest_SavesEntity() {
        // Setup
        FormDefinitionModel model = createFormModel("1.0.0");
        when(formVersionRepository.findByKeyAndVersion(any(), any()))
            .thenReturn(Optional.empty());
        when(formVersionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Execute
        FormVersionModel result = formService.createFormVersion("leave-request", model);

        // Verify
        assertNotNull(result);
        assertEquals(100000000L, result.getVersion());
        verify(formVersionRepository).save(any());
    }

    @Test
    void createFormVersion_DuplicateVersion_ThrowsException() {
        // Setup
        FormDefinitionModel model = createFormModel("1.0.0");
        when(formVersionRepository.findByKeyAndVersion(any(), any()))
            .thenReturn(Optional.of(new FormVersionEntity()));

        // Execute & Verify
        assertThrows(IllegalArgumentException.class, () -> 
            formService.createFormVersion("leave-request", model)
        );
    }

    @Test
    void createFormVersion_InvalidVersion_ThrowsException() {
        // Test with an invalid version string (non-numeric parts)
        FormDefinitionModel model = createFormModel("invalid-version");
        assertThrows(IllegalArgumentException.class, () -> 
            formService.createFormVersion("leave-request", model)
        );
    }

    @Test
    void createFormVersion_NullKey_ThrowsException() {
        FormDefinitionModel model = createFormModel("1.0.0");
        assertThrows(NullPointerException.class, () -> 
            formService.createFormVersion(null, model)
        );
    }

    @Test
    void createFormVersion_NullModel_ThrowsException() {
        assertThrows(NullPointerException.class, () -> 
            formService.createFormVersion("leave-request", null)
        );
    }
    @Test
    void getVersion_ExistingVersion_ReturnsModel() {
        // Setup
        Long versionLong = 100000000L; // 1.0.0
        FormVersionEntity entity = createVersionEntity(versionLong);
        when(formVersionRepository.findByKeyAndVersion(any(), eq(versionLong)))
            .thenReturn(Optional.of(entity));

        // Execute
        FormVersionModel result = formService.getVersion("leave-request", "1.0.0");

        // Verify
        assertNotNull(result);
        assertEquals(100000000L, result.getVersion());
    }

    @Test
    void getVersion_NonExistentVersion_ThrowsNotFoundException() {
        // Setup
        when(formVersionRepository.findByKeyAndVersion(any(), any()))
            .thenReturn(Optional.empty());

        // Execute & Verify
        assertThrows(NotFoundException.class, () -> 
            formService.getVersion("leave-request", "1.0.0")
        );
    }

    @Test
    void getVersion_InvalidVersion_ThrowsException() {
        // Execute & Verify
        assertThrows(IllegalArgumentException.class, () -> 
            formService.getVersion("leave-request", "invalid-version")
        );
    }

    // deprecateVersion tests
    @Test
    void deprecateVersion_SuccessfulDeprecation_UpdatesEntity() {
        // Setup
        String key = "leave-request";
        String version = "1.0.0";
        Long versionLong = 100000000L; // 1.0.0
        FormVersionEntity entity = createVersionEntity(versionLong);
        entity.setDeprecated(false);
        when(formVersionRepository.findByKeyAndVersion(eq(key), eq(versionLong)))
            .thenReturn(Optional.of(entity));
        when(formVersionRepository.save(any())).thenReturn(entity);

        // Execute
        formService.deprecateVersion(key, version);

        // Verify
        assertTrue(entity.isDeprecated());
        verify(formVersionRepository).save(entity);
    }

    @Test
    void deprecateVersion_AlreadyDeprecated_NoChange() {
        // Setup
        String key = "leave-request";
        String version = "1.0.0";
        Long versionLong = 100000000L; // 1.0.0
        FormVersionEntity entity = createVersionEntity(versionLong);
        entity.setDeprecated(true);
        when(formVersionRepository.findByKeyAndVersion(eq(key), eq(versionLong)))
            .thenReturn(Optional.of(entity));

        // Execute
        formService.deprecateVersion(key, version);

        // Verify
        verify(formVersionRepository, never()).save(any());
    }

    @Test
    void deprecateVersion_NullKey_ThrowsException() {
        // Execute & Verify
        assertThrows(NullPointerException.class, () -> 
            formService.deprecateVersion(null, "1.0.0")
        );
    }

    @Test
    void deprecateVersion_NullVersion_ThrowsException() {
        // Execute & Verify
        assertThrows(NullPointerException.class, () -> 
            formService.deprecateVersion("leave-request", null)
        );
    }

    @Test
    void deprecateVersion_NonExistentVersion_ThrowsException() {
        // Setup
        String key = "leave-request";
        String version = "1.0.0";
        Long versionLong = 100000000L; // 1.0.0
        when(formVersionRepository.findByKeyAndVersion(eq(key), eq(versionLong)))
            .thenReturn(Optional.empty());

        // Execute & Verify
        assertThrows(NotFoundException.class, () -> 
            formService.deprecateVersion(key, version)
        );
    }
    // getLatestVersion tests
    @Test
    void getLatestVersion_MultipleVersions_ReturnsHighestVersion() {
        // Setup
        String key = "leave-request";
        FormVersionEntity version1 = createVersionEntity(100000000L); // 1.0.0
        version1.setVersion(100000000L);
        FormVersionEntity version2 = createVersionEntity(200000000L); // 2.0.0
        version2.setVersion(200000000L);

        when(formVersionRepository.findFirstByKeyOrderByVersionDesc(key))
            .thenReturn(Optional.of(version2));

        // Execute
        FormVersionModel result = formService.getLatestVersion(key);

        // Verify
        assertEquals(200000000L, result.getVersion());
    }

    @Test
    void getLatestVersion_NoVersions_ThrowsNotFoundException() {
        // Setup
        String key = "leave-request";
        when(formVersionRepository.findFirstByKeyOrderByVersionDesc(key))
            .thenReturn(Optional.empty());

        // Execute & Verify
        assertThrows(NotFoundException.class, () -> formService.getLatestVersion(key));
    }

    @Test
    void getLatestVersion_DeprecatedVersions_ReturnsHighestVersion() {
        // Setup
        String key = "leave-request";
        FormVersionEntity version1 = createVersionEntity(100000000L); // 1.0.0
        version1.setDeprecated(true);
        FormVersionEntity version2 = createVersionEntity(200000000L); // 2.0.0
        version2.setDeprecated(true);

        when(formVersionRepository.findFirstByKeyOrderByVersionDesc(key))
            .thenReturn(Optional.of(version2));

        // Execute
        FormVersionModel result = formService.getLatestVersion(key);

        // Verify
        assertEquals(200000000L, result.getVersion());
    }
    // listVersions tests
    @Test
    void listVersions_DescendingOrder_ReturnsSortedList() {
        // Setup
        String key = "leave-request";
        FormVersionEntity version1 = createVersionEntity(10000L); // 1.0.0
        version1.setPublishedDate(1000L);
        FormVersionEntity version2 = createVersionEntity(20000L); // 2.0.0
        version2.setPublishedDate(2000L);
        
        when(formVersionRepository.findByKeyOrderByPublishedDateDesc(eq(key), any()))
            .thenReturn(org.springframework.data.domain.Page.empty());

        // Execute
        List<FormVersionModel> result = formService.listVersions(key);

        // Verify
        // Since we're testing the order, we need to check that the repository is called with descending order
        verify(formVersionRepository).findByKeyOrderByPublishedDateDesc(eq(key), any());
    }

    @Test
    void listVersions_EmptyResult_ReturnsEmptyList() {
        // Setup
        String key = "leave-request";
        when(formVersionRepository.findByKeyOrderByPublishedDateDesc(eq(key), any()))
            .thenReturn(org.springframework.data.domain.Page.empty());

        // Execute
        List<FormVersionModel> result = formService.listVersions(key);

        // Verify
        assertTrue(result.isEmpty());
    }

    // batchUpdateDeprecatedStatus tests
    @Test
    void batchUpdateDeprecatedStatus_MultipleKeys_UpdatesAllVersions() {
        // Setup
        List<String> keys = List.of("form1", "form2");
        boolean deprecated = true;
        
        FormVersionEntity entity1 = createVersionEntity(10000L);
        FormVersionEntity entity2 = createVersionEntity(20000L);
        
        when(formVersionRepository.findByKeyOrderByPublishedDateDesc(eq("form1"), any()))
            .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(entity1)));
        when(formVersionRepository.findByKeyOrderByPublishedDateDesc(eq("form2"), any()))
            .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(entity2)));
        when(formVersionRepository.save(any())).thenReturn(entity1, entity2);

        // Execute
        int count = formService.batchUpdateDeprecatedStatus(keys, deprecated);

        // Verify
        assertEquals(2, count);
        assertTrue(entity1.isDeprecated());
        assertTrue(entity2.isDeprecated());
        verify(formVersionRepository, times(2)).save(any());
    }

    @Test
    void batchUpdateDeprecatedStatus_EmptyKeysList_ReturnsZero() {
        // Setup
        List<String> keys = Collections.emptyList();
        boolean deprecated = true;

        // Execute
        int count = formService.batchUpdateDeprecatedStatus(keys, deprecated);

        // Verify
        assertEquals(0, count);
        verify(formVersionRepository, never()).findByKeyOrderByPublishedDateDesc(any(), any());
        verify(formVersionRepository, never()).save(any());
    }

    @Test
    void batchUpdateDeprecatedStatus_UpdateToDeprecatedTrue_SetsDeprecated() {
        // Setup
        List<String> keys = List.of("form1");
        boolean deprecated = true;
        
        FormVersionEntity entity = createVersionEntity(10000L);
        entity.setDeprecated(false);
        
        when(formVersionRepository.findByKeyOrderByPublishedDateDesc(eq("form1"), any()))
            .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(entity)));
        when(formVersionRepository.save(any())).thenReturn(entity);

        // Execute
        int count = formService.batchUpdateDeprecatedStatus(keys, deprecated);

        // Verify
        assertEquals(1, count);
        assertTrue(entity.isDeprecated());
    }

    @Test
    void batchUpdateDeprecatedStatus_UpdateToDeprecatedFalse_SetsNotDeprecated() {
        // Setup
        List<String> keys = List.of("form1");
        boolean deprecated = false;
        
        FormVersionEntity entity = createVersionEntity(10000L);
        entity.setDeprecated(true);
        
        when(formVersionRepository.findByKeyOrderByPublishedDateDesc(eq("form1"), any()))
            .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(entity)));
        when(formVersionRepository.save(any())).thenReturn(entity);

        // Execute
        int count = formService.batchUpdateDeprecatedStatus(keys, deprecated);

        // Verify
        assertEquals(1, count);
        assertFalse(entity.isDeprecated());
    }

    // rollbackVersion tests
    @Test
    void rollbackVersion_SuccessfulRollback_ActivatesVersion() {
        // Setup
        String key = "leave-request";
        Long version = 100000000L; // 1.0.0
        FormVersionEntity entity = createVersionEntity(version);
        entity.setDeprecated(true);
        when(formVersionRepository.findByKeyAndVersion(key, version))
            .thenReturn(Optional.of(entity));
        when(formVersionRepository.save(any())).thenReturn(entity);

        // Execute
        FormVersionModel result = formService.rollbackVersion(key, version);

        // Verify
        assertFalse(entity.isDeprecated());
        assertEquals(100000000L, result.getVersion());
    }

    @Test
    void rollbackVersion_NonExistentVersion_ThrowsException() {
        // Setup
        String key = "leave-request";
        Long version = 10000L;
        when(formVersionRepository.findByKeyAndVersion(key, version))
            .thenReturn(Optional.empty());

        // Execute & Verify
        assertThrows(NotFoundException.class, () -> 
            formService.rollbackVersion(key, version)
        );
    }

    @Test
    void rollbackVersion_AlreadyActiveVersion_NoChange() {
        // Setup
        String key = "leave-request";
        Long version = 10000L;
        FormVersionEntity entity = createVersionEntity(version);
        entity.setDeprecated(false);
        when(formVersionRepository.findByKeyAndVersion(key, version))
            .thenReturn(Optional.of(entity));
        //when(formVersionRepository.save(entity)).thenReturn(entity);

        // Execute
        FormVersionModel result = formService.rollbackVersion(key, version);

        // Verify
        assertFalse(entity.isDeprecated()); // still false
        verify(formVersionRepository, never()).save(any());
    }

    // searchVersions tests
    @Test
    void searchVersions_CaseInsensitiveSearch_ReturnsMatchingVersions() {
        // Setup
        String keyword = "test";
        Pageable pageable = Pageable.unpaged();
        FormVersionEntity entity = createVersionEntity(10000L);
        entity.setName("Test Form");
        Page<FormVersionEntity> page = new PageImpl<>(List.of(entity));
        when(formVersionRepository.findByNameContainingIgnoreCase(eq(keyword), eq(pageable)))
            .thenReturn(page);

        // Execute
        Page<FormVersionModel> result = formService.searchVersions(keyword, pageable);

        // Verify
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Form", result.getContent().get(0).getName());
    }

    @Test
    void searchVersions_PartialMatch_ReturnsMatchingVersions() {
        // Setup
        String keyword = "est";
        Pageable pageable = Pageable.unpaged();
        FormVersionEntity entity = createVersionEntity(10000L);
        entity.setName("Test Form");
        Page<FormVersionEntity> page = new PageImpl<>(List.of(entity));
        when(formVersionRepository.findByNameContainingIgnoreCase(eq(keyword), eq(pageable)))
            .thenReturn(page);

        // Execute
        Page<FormVersionModel> result = formService.searchVersions(keyword, pageable);

        // Verify
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Form", result.getContent().get(0).getName());
    }

    @Test
    void searchVersions_NoMatches_ReturnsEmptyPage() {
        // Setup
        String keyword = "nonexistent";
        Pageable pageable = Pageable.unpaged();
        Page<FormVersionEntity> page = Page.empty();
        when(formVersionRepository.findByNameContainingIgnoreCase(eq(keyword), eq(pageable)))
            .thenReturn(page);

        // Execute
        Page<FormVersionModel> result = formService.searchVersions(keyword, pageable);

        // Verify
        assertTrue(result.isEmpty());
    }

    @Test
    void searchVersions_Paging_RespectsPageable() {
        // Setup
        String keyword = "test";
        Pageable pageable = PageRequest.of(0, 10);
        FormVersionEntity entity = createVersionEntity(10000L);
        entity.setName("Test Form");
        Page<FormVersionEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(formVersionRepository.findByNameContainingIgnoreCase(eq(keyword), eq(pageable)))
            .thenReturn(page);

        // Execute
        Page<FormVersionModel> result = formService.searchVersions(keyword, pageable);

        // Verify
        assertEquals(1, result.getTotalElements());
        assertEquals(10, result.getSize());
    }
}
