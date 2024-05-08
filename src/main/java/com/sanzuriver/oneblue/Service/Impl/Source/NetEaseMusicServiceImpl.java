package com.sanzuriver.oneblue.Service.Impl.Source;

import com.fasterxml.jackson.databind.JsonNode;
import com.sanzuriver.oneblue.Common.Util.JsonUtil;
import com.sanzuriver.oneblue.Entity.MusicTag;
import com.sanzuriver.oneblue.Entity.SourseVO.MusicTagResp;
import com.sanzuriver.oneblue.Entity.VO.ResponseInfo;
import com.sanzuriver.oneblue.Service.MusicSourceService;
import jakarta.annotation.Resource;
import org.jaudiotagger.tag.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service("NetEase")
public class NetEaseMusicServiceImpl implements MusicSourceService {
    @Resource(name = "netEaseMusicRestTemplate")
    private RestTemplate restTemplate;
    @Override
    public List<MusicTagResp> searchMusic(String keyword, int numPerPage, int pageNum) {
        String url = "https://music.163.com/api/search/get/web?s=" +keyword+ "&type=1&offset=" + pageNum + "&total=true&limit=" + numPerPage;
        ResponseEntity<String > responseEntity= restTemplate.getForEntity(url, String.class);
        System.out.println(responseEntity.getBody());
        return getMusicListData(JsonUtil.parseJSONObject(responseEntity.getBody()));
    }
    public List<MusicTagResp> getMusicListData(JsonNode jsonNode){
        JsonNode songList = jsonNode.get("result").get("songs");
        List<MusicTagResp> musicTags = new ArrayList<>();
        for (JsonNode song : songList) {
            MusicTagResp musicTagResp = MusicTagResp.builder()
                    .title(song.get("name").asText())
                    .artist(LoopReading(song.get("artists"), "name"))
                    .album(song.get("album").get("name").asText())
                    .cover(song.get("album").get("artist").get("img1v1Url").asText())
                    .id(song.get("id").asText())
                    .mid("")
                    .year(getYear(song.get("album").get("publishTime").asLong()))
                    .source("NetEaseMusic")
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
    public String getYear(Long time){
        Instant instant = Instant.ofEpochMilli(time);
        LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Override
    public String getLyric(String id, String mid) {
        String baseUrl = "https://music.163.com/api/song/lyric?lv=-1&kv=-1&tv=-1"+"&id="+id;
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                baseUrl, String.class);
        return  JsonUtil.toJSONString(JsonUtil.parseJSONObject(responseEntity.getBody()).get("lrc").get("lyric").asText());
    }

}
