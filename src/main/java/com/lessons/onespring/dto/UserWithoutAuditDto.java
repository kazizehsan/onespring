package com.lessons.onespring.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lessons.onespring.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class UserWithoutAuditDto {
    private Long id;
    private String email;
    private boolean enabled;
    private String name;
    private String photo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dob;

    public static UserWithoutAuditDto entityToDto(@Nullable User user) {
        if (user== null){
            return null;
        }
        return new UserWithoutAuditDto()
                .setId(user.getId())
                .setEmail(user.getEmail())
                .setEnabled(user.isEnabled())
                .setName(user.getName())
                .setPhoto("/downloadFile/" + user.getPhoto())
                .setDob(user.getDob());
    }
}
