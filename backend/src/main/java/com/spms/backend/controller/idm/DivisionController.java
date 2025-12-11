package com.spms.backend.controller.idm;

import com.spms.backend.controller.dto.idm.DivisionDTO;
import com.spms.backend.service.idm.DivisionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/divisions")
public class DivisionController {

    private final DivisionService divisionService;

    public DivisionController(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    /**
     * Retrieves a paginated list of divisions with optional name filtering.
     * Supports search queries and standard pagination parameters.
     *
     * @param search Optional search string to filter divisions by name (case-insensitive)
     * @param pageable Pagination configuration (page, size, sort)
     * @return ResponseEntity containing a page of DivisionDTO objects
     */
    @GetMapping
    public ResponseEntity<Page<DivisionDTO>> getAllDivisions(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(divisionService.searchDivisions(search, pageable)
                .map(DivisionDTO::fromDivisionModel));
    }

    /**
     * Creates a new division from the provided division data.
     * Validates input and returns the created division representation.
     *
     * @param divisionDTO Division data transfer object containing creation parameters
     * @return ResponseEntity containing the created DivisionDTO
     */
    @PostMapping
    public ResponseEntity<DivisionDTO> createDivision(@RequestBody DivisionDTO divisionDTO) {
        return ResponseEntity.ok(DivisionDTO.fromDivisionModel(
                divisionService.createDivision(divisionDTO.toDivisionModel())));
    }

    /**
     * Updates an existing division identified by the path variable ID.
     * Applies partial updates to division fields and returns the updated representation.
     *
     * @param id ID of the division to update
     * @param divisionDTO Updated division data
     * @return ResponseEntity containing the updated DivisionDTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<DivisionDTO> updateDivision(
            @PathVariable Long id,
            @RequestBody DivisionDTO divisionDTO) {
        return ResponseEntity.ok(DivisionDTO.fromDivisionModel(
                divisionService.updateDivision(id, divisionDTO.toDivisionModel())));
    }

    /**
     * Deletes a single division identified by the path variable ID.
     * Returns 204 No Content on successful deletion.
     *
     * @param id ID of the division to delete
     * @return ResponseEntity with no content status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDivision(@PathVariable Long id) {
        divisionService.deleteDivision(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes multiple divisions in a single operation based on provided IDs.
     * Returns 204 No Content on successful bulk deletion.
     *
     * @param ids List of division IDs to delete
     * @return ResponseEntity with no content status
     */
    @DeleteMapping("/bulk")
    public ResponseEntity<Void> bulkDeleteDivisions(@RequestBody List<Long> ids) {
        divisionService.bulkDeleteDivisions(ids);
        return ResponseEntity.noContent().build();
    }
}
