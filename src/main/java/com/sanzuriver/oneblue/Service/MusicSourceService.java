package com.sanzuriver.oneblue.Service;

import com.sanzuriver.oneblue.Entity.SourseVO.MusicTagResp;
import org.jaudiotagger.tag.Tag;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MusicSourceService {
    List<MusicTagResp> searchMusic(String keyword, int numPerPage, int pageNum);
    String getLyric(String id,String mid);
}
