package com.lessons.onespring.dto;

import com.lessons.onespring.entities.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class UserDto {
    private Long id;
    private String email;
    private boolean enabled;
    private String name;
    private String photo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dob;
    private List<PrivilegeDto> privileges;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    private @Nullable
    Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    private @Nullable
    Date updatedAt;
    private @Nullable
    String createdBy;
    private @Nullable
    String updatedBy;

    public static UserDto entityToDto(User user) {
        return new UserDto()
                .setId(user.getId())
                .setEmail(user.getEmail())
                .setEnabled(user.isEnabled())
                .setName(user.getName())
                .setPhoto("/downloadFile/" + user.getPhoto())
                .setDob(user.getDob())
                .setPrivileges(
                    user.getPrivileges().stream().map(PrivilegeDto::entityToDto)
                    .collect(Collectors.toList())
                )
                .setCreatedBy(user.getCreatedBy())
                .setUpdatedBy(user.getLastModifiedBy())
                .setCreatedAt(user.getCreatedDate())
                .setUpdatedAt(user.getLastModifiedDate())
                ;
    }
}
