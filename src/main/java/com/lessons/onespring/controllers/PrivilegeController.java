package com.lessons.onespring.controllers;

import com.lessons.onespring.dto.PrivilegeDto;
import com.lessons.onespring.services.intf.PrivilegeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PrivilegeController {

    private PrivilegeService privilegeService;

    public PrivilegeController(PrivilegeService privilegeService) {
        this.privilegeService = privilegeService;
    }

    @GetMapping("/privileges")
    public List<PrivilegeDto> getPrivileges()
    {
        return privilegeService.findAll()
                .stream()
                .map(PrivilegeDto::entityToDto)
                .collect(Collectors.toList())
                ;
    }

}
