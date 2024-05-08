package com.sanzuriver.oneblue.Service.Impl.MusicInfo;

import com.sanzuriver.oneblue.Entity.MusicTag;
import com.sanzuriver.oneblue.Entity.Shares;
import com.sanzuriver.oneblue.Entity.VO.ResponseInfo;
import com.sanzuriver.oneblue.Mapper.MusicTagsMapper;
import com.sanzuriver.oneblue.Service.MusicInfoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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


    //流式传输音乐，支持任意点资源获取，而不是整个文件传输
    @Override
    @SneakyThrows
    public ResponseEntity<StreamingResponseBody> getMusicPlay(String fileName, HttpServletRequest request) {
        File file = new File(musicFolderPath + "/" + fileName);
        long fileLength = file.length();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "audio/mpeg");
        headers.add("Accept-Ranges", "bytes");

        if (request.getHeader("Range") == null) {
            headers.add("Content-Length", String.valueOf(fileLength));
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.status(HttpStatus.OK)
                    .headers(headers)
                    .body(outputStream -> resource.getInputStream().transferTo(outputStream));
        } else {
            long start = 0, end = 0;
            String[] ranges = request.getHeader("Range").replace("bytes=", "").split("-");
            start = Long.parseLong(ranges[0]);
            if (ranges.length > 1) {
                end = Long.parseLong(ranges[1]);
            } else {
                end = fileLength - 1;
            }
            final long[] finalStart = {start};
            long finalEnd = end;
            headers.add("Content-Length", String.valueOf(end - start + 1));
            headers.add("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .body(outputStream -> {
                        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
                            randomAccessFile.seek(finalStart[0]);
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = randomAccessFile.read(buffer)) != -1 && finalStart[0] + bytesRead <= finalEnd) {
                                outputStream.write(buffer, 0, bytesRead);
                                finalStart[0] += bytesRead;
                            }
                        }
                    });
        }
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
    @Deprecated
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
        AudioFile audioFile = AudioFileIO.read(new File(musicFolderPath+"/" + fileName));
        return audioFile.getTag();
    }
    @Override
    public ResponseEntity<StreamingResponseBody> getMusicBySid(String sid,HttpServletRequest request) {
        Shares shares = musicTagsMapper.getShareMusic(sid);
        if (shares == null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("application/json"))
                    .body(out -> out.write("分享链接不存在".getBytes()));
        }
        if (isExpire(Long.parseLong(shares.getShareExpireTime()))) {
            musicTagsMapper.deleteShareMusic(sid);
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("application/json"))
                    .body(out -> out.write("分享链接已过期".getBytes()));
        }
        return getMusicPlay(shares.getShareSong(),request);
    }
    @Value("${oneblue.domain}")
    private String domain;
    @Override
    public String setShareLink(String fileName) {
        String shareId = UUID.randomUUID().toString();
        musicTagsMapper.insertShareMusic(Shares.builder()
                .shareSong(fileName)
                .shareId(shareId)
                .shareExpireTime(String.valueOf(Instant.now().getEpochSecond() + 1800))
                .build());
        return domain+"/music/share/"+shareId;
    }
    public boolean isExpire(long expireTime){
        Instant current = Instant.now();
        return current.getEpochSecond() > expireTime;
    }
    @Scheduled(fixedRate = 1800000)
    public void deleteExpireShareMusic(){
        List<Shares> shares = musicTagsMapper.getShareMusicList();
        shares.forEach(share -> {
            if (isExpire(Long.parseLong(share.getShareExpireTime()))) {
                musicTagsMapper.deleteShareMusic(share.getShareId());
            }
        });
    }

}
