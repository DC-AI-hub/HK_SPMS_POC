package com.spms.backend.controller.process;

import com.spms.backend.controller.dto.process.CreateProcessDefinitionRequest;
import com.spms.backend.controller.dto.process.ProcessDefinitionDTO;
import com.spms.backend.repository.entities.idm.User;
import com.spms.backend.service.idm.UserModelFulfilledSupporter;
import com.spms.backend.service.idm.UserService;
import com.spms.backend.service.idm.impl.UserModelFulfilledSupporterImpl;
import com.spms.backend.service.model.idm.UserModel;
import com.spms.backend.service.model.process.ProcessDefinitionModel;
import com.spms.backend.service.process.ProcessDefinitionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProcessDefinitionTests {

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository; // Fixes the missing bean

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProcessDefinitionService processService;

    @Autowired
    private ObjectMapper objectMapper;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        UserModel userModel = new UserModel();
        userModel.setId(1L);
        userModel.setUsername("test-user");
        userModel.setType(User.UserType.STAFF);
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(userService.getFulfilledSupporter()).thenReturn(new UserModelFulfilledSupporterImpl(userService));
        when(userService.getUserById(anyLong())).thenReturn(userModel);


    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateDefinition_Success() throws Exception {
        // Setup authentication handled by @WithMockUser

        // Prepare valid request payload
        CreateProcessDefinitionRequest request = new CreateProcessDefinitionRequest();
        request.setProcessName("Order Fulfillment");
        request.setProcessKey("order_fulfillment");
        request.setProcessDescription("Test process description");
        request.setBusinessOwner(1L); // Use Long values
        request.setOwner(1L); // Use Long values


        // Execute POST request
        mockMvc.perform(post("/api/v1/process/definitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber()) // Expect Long value
                .andExpect(jsonPath("$.version").doesNotExist()) // Version not in response
                .andExpect(result -> {
                    ProcessDefinitionDTO dto =  objectMapper.readValue(result.getResponse().getContentAsString(), ProcessDefinitionDTO.class);
                    ProcessDefinitionModel model = processService.getProcessDefinition(dto.getId());
                    Assertions.assertNotNull(model);
                });

        // Verify service interaction
        //verify(processService, times(1)).createProcessDefinition(any(ProcessDefinitionModel.class));
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSearchDefinitions_EmptyResult() throws Exception {

        // Execute GET request
        mockMvc.perform(get("/api/v1/process/definitions/search")
                .param("search", "nonExistingTerm")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSearchDefinitions_Pagination() throws Exception {
        // Create mock data
        List<ProcessDefinitionModel> definitions = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            ProcessDefinitionModel model = new ProcessDefinitionModel();
            model.setId((long) i);
            model.setName("Process " + i);
            model.setKey("process_" + i);
            definitions.add(model);
        }

        definitions.forEach(x -> {
            processService.createProcessDefinition(x);
        });


        // Create page 1 (0-indexed) with 5 items, sorted by name ascending
        PageRequest pageRequest = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "name"));
        Page<ProcessDefinitionModel> page = new PageImpl<>(
                definitions.stream()
                        .sorted(Comparator.comparing(ProcessDefinitionModel::getName))
                        .skip(5)
                        .limit(5)
                        .collect(Collectors.toList()),
                pageRequest,
                15
        );

        // Mock service response
        //when(processService.getProcessDefinitions(null, pageRequest)).thenReturn(page);

        // Execute GET request
        mockMvc.perform(get("/api/v1/process/definitions/search")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.number").value(1));
    }
    
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetDefinition_Deleted() throws Exception {
        // Mock service to throw NotFoundException for deleted definition
        String deletedDefinitionId = "999";

        // Execute GET request
        mockMvc.perform(get("/api/v1/process/definitions/" + deletedDefinitionId))
                .andExpect(status().isNotFound());
    }
}
