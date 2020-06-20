package com.lessons.onespring.services.impl;


import com.lessons.onespring.entities.Privilege;
import com.lessons.onespring.repositories.PrivilegeRepository;
import com.lessons.onespring.services.intf.PrivilegeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrivilegeServiceImpl implements PrivilegeService {

    private PrivilegeRepository privilegeRepository;

    public PrivilegeServiceImpl(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    @Override
    public List<Privilege> findAll() {
        return privilegeRepository.findAll();
    }
}
