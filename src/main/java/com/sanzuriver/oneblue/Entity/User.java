package com.sanzuriver.oneblue.Entity;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private Integer id;
    @Email
    private String email;
    private String password;
}
