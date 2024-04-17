package com.sanzuriver.oneblue.Entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRole {
    private Integer userId;
    private Integer roleId;
}
