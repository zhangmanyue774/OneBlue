package com.sanzuriver.oneblue.Service.Impl.MusicInfo;

import com.sanzuriver.oneblue.Entity.MusicTag;
import com.sanzuriver.oneblue.Entity.SourseVO.MusicTagResp;
import com.sanzuriver.oneblue.Entity.VO.ResponseInfo;
import com.sanzuriver.oneblue.Mapper.MusicTagsMapper;
import com.sanzuriver.oneblue.Service.MusicInfoService;
import com.sanzuriver.oneblue.Service.MusicSourceService;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.HttpPropfind;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("webDavMusic")
@Slf4j
@RefreshScope
public class WebDavMusicInfoServiceImpl extends MusicInfoAbstract implements MusicInfoService {
    @Resource
    private MusicTagsMapper musicTagsMapper;
    @Resource
    private HttpClient client;
    @Resource
    private HttpClientContext context;
    @Value("${oneblue.web-dav.url}")
    private String url;
    @Override
    public List<MusicTag> getMusicList() {
        return musicTagsMapper.getWebDavMusicList();
    }
    @Override
    public boolean manualSetMusicTag(String fileName, MusicTag musicTag) {
        return false;
    }
    @Override
    @SneakyThrows
    //(仅支持单目录下的扫描)目录扫描同步法
    public void SynchronizeMusicListToDb() {
        long start = System.currentTimeMillis();
        DavPropertyNameSet set = new DavPropertyNameSet();
        HttpPropfind httpPropfind = new HttpPropfind(url, DavConstants.PROPFIND_ALL_PROP, set,DavConstants.DEPTH_1);
        HttpResponse response = this.client.execute(httpPropfind, this.context);
        MultiStatus multiStatus = httpPropfind.getResponseBodyAsMultiStatus(response);
        MultiStatusResponse[] responses = multiStatus.getResponses();
        long end = System.currentTimeMillis();
        log.info("WebDav扫描耗时:{}",end-start);
        Set<String> davMusicList = new HashSet<>();
        for (MultiStatusResponse res : responses) {
            String filePath = URLDecoder.decode(res.getHref(), StandardCharsets.UTF_8);
            String fileName = Paths.get(filePath).getFileName().toString();
            if (fileName.toLowerCase().endsWith(".mp3")||fileName.toLowerCase().endsWith(".flac")||fileName.endsWith("ogg")) {
                davMusicList.add(fileName);
            }
        }
        Set<String> dbMusicList = musicTagsMapper.getWebDavMusicListFileName();
        comparison(dbMusicList,davMusicList,false);
        log.info("音乐列表同步完成");
    }

    @Override
    public void initMusicListToDb() {

    }
}
