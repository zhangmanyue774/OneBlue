package com.sanzuriver.oneblue.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private Integer id;
    private String email;
    private String password;
}
