package com.lessons.onespring.services.intf;

import com.lessons.onespring.entities.Privilege;

import java.util.List;

public interface PrivilegeService {
    List<Privilege> findAll();
}
