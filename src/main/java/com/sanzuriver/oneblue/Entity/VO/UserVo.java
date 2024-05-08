package com.sanzuriver.oneblue.Entity.VO;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserVo {
    private String email;
    private String Avatar;
    private String access_token;
    private String refresh_token;
}
