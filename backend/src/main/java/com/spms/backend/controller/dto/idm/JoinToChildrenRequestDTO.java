package com.spms.backend.controller.dto.idm;

import lombok.Data;

import java.util.List;

@Data
public class JoinToChildrenRequestDTO {

    private List<Long> ids;

}
