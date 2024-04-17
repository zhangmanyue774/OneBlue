package com.sanzuriver.oneblue.Entity.SourseVO;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class QQMusicSingleSongRequestBody {
    private SingleComm comm;
    private SingleSongService get_song_detail;
    @Builder
    @Data
    public static class SingleSongService {
        private String module;
        private String method;
        private SingleSongParams param;
    }
    @Builder
    @Data
    public static class SingleSongParams {
        private String song_id;
        private String song_mid;
        private String song_type;
    }
    @Builder
    @Data
    public static class SingleComm {
        private String g_tk;
        private String uin;
        private String format;
        private int ct;
        private int cv;
        private String platform;
        private String uid;
        private String guid;
    }
}
