package com.sanzuriver.oneblue.Entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MusicTag {
    private String title;
    private String id;
    private String artist;
    private String album;
    private String year;
    private String lyrics;
    private String coverArt;
    private String fileName;
    private String playUrl;
    private String source;
}
