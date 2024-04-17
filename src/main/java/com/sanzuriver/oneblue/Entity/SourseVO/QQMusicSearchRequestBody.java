package com.sanzuriver.oneblue.Entity.SourseVO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QQMusicSearchRequestBody {
    private Comm comm;
    private SearchService music_search_SearchCgiService_DoSearchForQQMusicDesktop;

    @Data
    @Builder
    public static class SearchService {
        private String module;
        private String method;
        private SearchParams param;
    }
    @Builder
    @Data
    public static class Comm {
        private String wid;
        private String tmeAppID;
        private String authst;
        private String uid;
        private String gray;
        private String OpenUDID;
        private String ct;
        private String patch;
        private String psrf_qqopenid;
        private String sid;
        private String psrf_access_token_expiresAt;
        private String cv;
        private String gzip;
        private String qq;
        private String nettype;
        private String psrf_qqunionid;
        private String psrf_qqaccess_token;
        private String tmeLoginType;
    }
    @Data
    @Builder
    public static class SearchParams {
        private int num_per_page;
        private int page_num;
        private String remoteplace;
        private int search_type;
        private String query;
        private int grp;
        private String searchid;
        private int nqc_flag;

    }

}
