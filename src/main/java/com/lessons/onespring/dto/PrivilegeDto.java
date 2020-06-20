package com.lessons.onespring.dto;

import com.lessons.onespring.entities.Privilege;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class PrivilegeDto {
    private Long id;
    private String name;

    public static PrivilegeDto entityToDto(Privilege privilege) {
        return new PrivilegeDto()
                .setId(privilege.getId())
                .setName(privilege.getName())
                ;
    }
}
