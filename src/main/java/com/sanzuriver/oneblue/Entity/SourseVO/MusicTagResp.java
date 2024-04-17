package com.sanzuriver.oneblue.Entity.SourseVO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MusicTagResp {
    private String title;
    private String artist;
    private String album;
    private String year;
    private String cover;
    private String id;
    private String mid;
    private String source;
}
