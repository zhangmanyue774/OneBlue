package com.sanzuriver.oneblue.Entity.VO;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserVo {
    private String email;
    private String Token;
    private String Avatar;
}
