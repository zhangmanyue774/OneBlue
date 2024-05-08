package com.sanzuriver.oneblue.Service.Impl.MusicInfo;

import com.sanzuriver.oneblue.Common.Util.JsonUtil;
import com.sanzuriver.oneblue.Entity.MusicTag;
import com.sanzuriver.oneblue.Entity.SourseVO.MusicTagResp;
import com.sanzuriver.oneblue.Mapper.MusicTagsMapper;
import com.sanzuriver.oneblue.Service.MusicSourceService;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public abstract class MusicInfoAbstract {
    @Resource
    private MusicTagsMapper musicTagsMapper;
    @Resource(name = "QQMusic")
    private MusicSourceService musicSourceService;
    @Resource(name = "qqMusicRestTemplate")
    private RestTemplate restTemplate;
    @Value("${oneblue.web-dav.url}")
    private String webDavMusicFolderPath;
    @Value("${oneblue.music-folder-path}")
    private String localMusicFolderPath;
    @Value("${oneblue.domain}")
    private String domain;
    void comparison(Set<String> dbSet,Set<String>realSet,boolean isLocal){
        //共有文件
        Set<String> common = new HashSet<>(dbSet);
        common.retainAll(realSet);
        //数据库独有文件(删除)
        Set<String> dbExclusive = new HashSet<>(dbSet);
        dbExclusive.removeAll(common);
        if(isLocal) dbExclusive.forEach(musicTagsMapper::deleteMusicTag);
        else dbExclusive.forEach(musicTagsMapper::deleteWebDavMusicTag);
        //Dav独有文件(刮削并加入数据库/直接获取网络资源加入数据库（无文件操作）)
        Set<String> realExclusive = new HashSet<>(realSet);
        realExclusive.removeAll(common);//dav与共有文件取差集（新增）
        System.out.println(realExclusive);
        long startTime = System.currentTimeMillis();
        // 创建一个线程池
        ExecutorService executorService = Executors.newFixedThreadPool(5);

// 提交任务到线程池
        for (String fileName : realExclusive) {
            executorService.submit(() -> updateScrape(fileName, isLocal));
        }

// 关闭线程池
        executorService.shutdown();

        try {
            // 等待所有任务完成
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                // 超时后，停止所有正在执行的任务
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            // 如果等待过程中发生中断，也要停止所有正在执行的任务
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
//        realExclusive.forEach(fileName -> updateScrape(fileName,isLocal));
        long endTime = System.currentTimeMillis();
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
        log.info("{}扫描完成,新增歌曲：{}",isLocal?"本地":"WebDav",realExclusive.size());
    }
    void updateScrape(String fileName, boolean isLocal) {
        log.info("线程{}开始刮削{}音乐{}",Thread.currentThread().getName(),isLocal?"本地":"WebDav", fileName);
        String songName = fileName.replaceAll("\\.\\w+$", "");
        MusicTagResp musicTagResp = null;
        try {
            musicTagResp = musicSourceService.searchMusic(songName, 1, 1).getFirst();
        }
        catch (Exception e){
            log.error("刮削{}音乐{}失败",isLocal?"本地":"WebDav", fileName);
            return;
        }
        String Lyric = JsonUtil.parseJSONObject(musicSourceService.getLyric(musicTagResp.getId(),musicTagResp.getMid())).get("data").get("lyric").asText();
        if (isLocal){
            //本地更新逻辑
            //Tag更新逻辑
            //数据库逻辑
            upDateLocalMusicTag(fileName,MusicTag.builder()
                    .year(musicTagResp.getYear())
                    .album(musicTagResp.getAlbum().isEmpty()?"未分类专辑":musicTagResp.getAlbum())
                    .artist(musicTagResp.getArtist())
                    .title(musicTagResp.getTitle())
                    .fileName(fileName)
                    .lyrics(Lyric.isEmpty()?"暂无歌词":Lyric)
                    .coverArt(musicTagResp.getCover())
                    .playUrl(domain+"/music/play/"+songName)
                    .source("local")
                    .build());
        }
        else {
            //webDavTag更新逻辑
            //数据库逻辑
            musicTagsMapper.insertWebDavMusicTag(MusicTag.builder()
                            .year(musicTagResp.getYear())
                            .album(musicTagResp.getAlbum().isEmpty()?"未分类专辑":musicTagResp.getAlbum())
                            .artist(musicTagResp.getArtist())
                            .title(musicTagResp.getTitle())
                            .fileName(fileName)
                            .lyrics(Lyric.isEmpty()?"暂无歌词":Lyric)
                            .coverArt(musicTagResp.getCover())
                    //TODO:规则拼接
                            .playUrl("https://alist.sanzuriver.cn/d/Music/"+ fileName)
                            .source("webDav")
                    .build());
        }
//        log.info("新增{}音乐{}刮削完成",isLocal?"本地":"WebDav", fileName);
    }
    @SneakyThrows
    void upDateLocalMusicTag(String fileName,MusicTag musicTag){
        AudioFile audioFile = AudioFileIO.read(new File(localMusicFolderPath+"/" + fileName));
        Tag tag = audioFile.getTag();
        if(tag.getFirst(FieldKey.ARTIST).isEmpty()) {
            tag.setField(FieldKey.ARTIST,musicTag.getArtist());
        }
        if(tag.getFirst(FieldKey.ALBUM).isEmpty()) {
            tag.setField(FieldKey.ALBUM,musicTag.getAlbum());
        }
        if(tag.getFirst(FieldKey.YEAR).isEmpty()) {
            tag.setField(FieldKey.YEAR,musicTag.getYear());
        }
        if(tag.getFirst(FieldKey.TITLE).isEmpty()) {
            tag.setField(FieldKey.TITLE,musicTag.getTitle());
        }
        if(tag.getFirst(FieldKey.LYRICS).isEmpty()) {
            tag.setField(FieldKey.LYRICS,musicTag.getLyrics());
        }
        if(tag.getFirstArtwork() != null && tag.getFirstArtwork().getBinaryData().length == 0) {
            byte[] imageBytes = restTemplate.getForObject(musicTag.getCoverArt(), byte[].class);
            Artwork artwork = ArtworkFactory.getNew();
            artwork.setBinaryData(imageBytes);
            artwork.setMimeType("image/jpeg");
            tag.setField(artwork);
        }
        musicTag.setCoverArt(domain+"/music/cover/" + fileName);
        AudioFileIO.write(audioFile);
        System.out.println(musicTag);
        musicTagsMapper.insertMusicTag(musicTag);
    }
}
