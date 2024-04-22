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
public class LocalMusicInfoServiceImpl extends MusicInfoAbstract implements MusicInfoService {
    @Value("${oneblue.music-folder-path}")
    private String musicFolderPath;
    @Resource
    private MusicTagsMapper musicTagsMapper;
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
        comparison(dBMusicList, diskMusicList, true);
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
    public Tag getTag(String fileName) {
        AudioFile audioFile = AudioFileIO.read(new File(musicFolderPath + fileName));
        return audioFile.getTag();
    }

}
