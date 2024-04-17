package com.sanzuriver.oneblue.Service.Impl.MusicInfo;

import com.sanzuriver.oneblue.Entity.MusicTag;
import com.sanzuriver.oneblue.Mapper.MusicTagsMapper;
import com.sanzuriver.oneblue.Service.MusicInfoService;
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
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Service("LocalMusic")
@RefreshScope
@Slf4j
public class LocalMusicInfoServiceImpl implements MusicInfoService {
    @Value("${oneblue.music-folder-path}")
    private String musicFolderPath;
    @Resource
    private MusicTagsMapper musicTagsMapper;
    @Resource(name = "qqMusicRestTemplate")
    private RestTemplate restTemplate;
    @Override
    public List<MusicTag> getMusicList() {
        return musicTagsMapper.getMusicList();
    }

    @Override
    public byte[] getMusicCover(String fileName) {
        return getTag(fileName).getFirstArtwork().getBinaryData();
    }

    @Override
    @SneakyThrows
    public byte[] getMusicPlay(String fileName) {
        return new InputStreamResource(new FileInputStream(musicFolderPath + fileName)).getInputStream().readAllBytes();
    }

    @Override
    public boolean manualSetMusicTag(String fileName, MusicTag musicTag) {
        return false;
    }

    @SneakyThrows
    @Override
    public void SynchronizeMusicListToDb() {
        Set<String> dBMusicList = musicTagsMapper.getMusicListFileName();
        Set<String> diskMusicList = Files.walk(Paths.get(musicFolderPath))
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toSet());
        //获取交集(无需同步的文件)
        Set<String> common = new HashSet<>(dBMusicList);
        common.retainAll(diskMusicList);
        //获取差集(需要同步的文件)
        //数据库独有文件(删除)
        Set<String> dbExclusive = new HashSet<>(dBMusicList);
        dbExclusive.removeAll(diskMusicList);
        dbExclusive.forEach(musicTagsMapper::deleteMusicTag);
        //磁盘独有文件(刮削（文件操作）并加入数据库)
        Set<String> diskExclusive = new HashSet<>(diskMusicList);
        diskExclusive.removeAll(dBMusicList);
        //Todo:刮削并加入数据库 传入差集
        log.info("音乐列表同步完成");
    }

    @Override
    @SneakyThrows
    public void initMusicListToDb() {
        if(musicTagsMapper.getIsInit().equals("true")) {
            log.info("音乐列表已初始化");
            return;
        }
        List<String> diskMusicList = Files.walk(Paths.get(musicFolderPath))
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .map(Path::toString)
                .toList();
        //Todo:传入全集diskMusicList并刮削写入数据库
        log.info("音乐列表初始化完成");

    }
    @SneakyThrows
    //手动设置完整音乐标签(先打TAG 未完成再写入数据库）
    void setMusicTag(String fileName,MusicTag musicTag) {
        AudioFile audioFile = AudioFileIO.read(new File(musicFolderPath + fileName));
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
            tag.setField(getCoverByte(musicTag.getCoverArt()));
        }
        AudioFileIO.write(audioFile);

        musicTagsMapper.insertMusicTag(musicTag);
    }
    public Artwork getCoverByte(String coverUrl) {
        try {
            byte[] imageBytes = restTemplate.getForObject(coverUrl, byte[].class);
            Artwork artwork = ArtworkFactory.getNew();
            artwork.setBinaryData(imageBytes);
            artwork.setMimeType("image/jpeg");
            return artwork;
        } catch (Exception e) {
            log.error("封面资源不存在:{}", e.getMessage());
            return null;
        }
    }
    //均需要获取网络资源Tag 如何处理MusicSourceService和Info的关系

    @SneakyThrows
    public Tag getTag(String fileName) {
        AudioFile audioFile = AudioFileIO.read(new File(musicFolderPath + fileName));
        return audioFile.getTag();
    }

}
