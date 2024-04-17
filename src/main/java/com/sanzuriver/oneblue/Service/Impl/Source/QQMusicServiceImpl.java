package com.sanzuriver.oneblue.Service.Impl.Source;

import com.fasterxml.jackson.databind.JsonNode;
import com.sanzuriver.oneblue.Common.Util.JsonUtil;
import com.sanzuriver.oneblue.Entity.MusicTag;
import com.sanzuriver.oneblue.Entity.SourseVO.MusicTagResp;
import com.sanzuriver.oneblue.Entity.SourseVO.QQMusicSearchRequestBody;
import com.sanzuriver.oneblue.Entity.SourseVO.QQMusicSingleSongRequestBody;
import com.sanzuriver.oneblue.Entity.VO.ResponseInfo;
import com.sanzuriver.oneblue.Service.MusicSourceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("QQMusic")
@Slf4j
public class QQMusicServiceImpl implements MusicSourceService {
    @Resource(name = "qqMusicRestTemplate")
    private RestTemplate restTemplate;

    @Override
    public List<MusicTagResp> searchMusic(String keyword, int numPerPage, int pageNum) {
        QQMusicSearchRequestBody qqMusicSearchRequestBody =
                QQMusicSearchRequestBody.builder()
                        .comm(QQMusicSearchRequestBody.Comm.builder().wid("")
                                .tmeAppID("qqmusic")
                                .authst("")
                                .uid("")
                                .gray("0")
                                .OpenUDID("2d484d3157d4ed482e406e6c5fdcf8c3d3275deb")
                                .ct("6")
                                .patch("2")
                                .psrf_qqopenid("")
                                .sid("")
                                .psrf_access_token_expiresAt("")
                                .cv("80600")
                                .gzip("0")
                                .qq("")
                                .nettype("2")
                                .psrf_qqunionid("")
                                .psrf_qqaccess_token("")
                                .tmeLoginType("2")
                                .build())
                        .music_search_SearchCgiService_DoSearchForQQMusicDesktop(QQMusicSearchRequestBody.SearchService.builder()
                                .module("music.search.SearchCgiService")
                                .method("DoSearchForQQMusicDesktop")
                                .param(QQMusicSearchRequestBody.SearchParams.builder()
                                        .num_per_page(numPerPage)
                                        .page_num(pageNum)
                                        .remoteplace("txt.mac.search")
                                        .query(keyword)
                                        .search_type(0)
                                        .grp(1)
                                        .searchid("some-uuid")
                                        .nqc_flag(0)
                                        .build())
                                .build())
                        .build();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                "https://u.y.qq.com/cgi-bin/musicu.fcg",
                qqMusicSearchRequestBody,
                String.class
        );

        // 获取响应体
        String responseBody = responseEntity.getBody();
        System.out.println(responseBody);
        // 解析响应体
        JsonNode jsonNode = JsonUtil.parseJSONObject(responseBody);
        return getMusicListData(jsonNode);
    }

    @Override
    public String getLyric(String id, String mid) {
        QQMusicSingleSongRequestBody qqMusicSingleSongRequestBody = QQMusicSingleSongRequestBody.builder()
                .comm(QQMusicSingleSongRequestBody.SingleComm.builder()
                        .g_tk("0")
                        .uin("")
                        .format("json")
                        .ct(6)
                        .cv(80600)
                        .platform("wk_v17")
                        .uid("")
                        .guid("fghij")
                        .build())
                .get_song_detail(QQMusicSingleSongRequestBody.SingleSongService.builder()
                        .module("music.pf_song_detail_svr")
                        .method("get_song_detail")
                        .param(QQMusicSingleSongRequestBody.SingleSongParams.builder()
                                .song_id(id)
                                .song_mid(mid)
                                .song_type("0")
                                .build())
                        .build())
                .build();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                "https://u.y.qq.com/cgi-bin/musicu.fcg",
                qqMusicSingleSongRequestBody,
                String.class
        );
        String responseBody = responseEntity.getBody();
        JsonNode jsonNode = JsonUtil.parseJSONObject(responseBody);
        return getMusicLyricData(jsonNode);
    }

    public List<MusicTagResp> getMusicListData(JsonNode jsonNode){
        JsonNode songList = jsonNode.get("music_search_SearchCgiService_DoSearchForQQMusicDesktop").get("data").get("body").get("song").get("list");
        List<MusicTagResp> musicTags = new ArrayList<>();
        for (JsonNode song : songList) {
            MusicTagResp musicTagResp = MusicTagResp.builder()
                    .title(song.get("title").asText())
                    .artist(LoopReading(song.get("singer"), "name"))
                    .album(song.get("album").get("title").asText())
                    .cover("https://y.qq.com/music/photo_new/T002R300x300M000"+song.get("album").get("mid").asText()+".jpg")
                    .id(song.get("id").asText())
                    .mid(song.get("mid").asText())
                    .year(song.get("time_public").asText())
                    .source("QQMusic")
                    .build();
            musicTags.add(musicTagResp);
        }
        return musicTags;
    }
    public String LoopReading(JsonNode node,String key){
        List<String> list = new ArrayList<>();
        for (JsonNode jsonNode : node) {
            list.add(jsonNode.get(key).asText());
        }
        return String.join(",", list);

    }
    public String getMusicLyricData(JsonNode jsonNode){
        JsonNode jsonNode1 = jsonNode.get("get_song_detail");
        if(jsonNode1.get("code").asText().equals("0")){
            JsonNode infoArray = jsonNode1.get("data").get("info");
            JsonNode lastElement = infoArray.get(infoArray.size() - 1);
            Map.Entry<String, Object> songData = new AbstractMap.SimpleEntry<>("lyric", lastElement.get("content").get(0).get("value").asText());
            return JsonUtil.toJSONString(new ResponseInfo<>(songData));
        }
        return ResponseInfo.fail("获取歌词失败").toString();
    }
}
