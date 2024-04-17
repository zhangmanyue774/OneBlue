package com.sanzuriver.oneblue.Service.Impl.Source;

import com.sanzuriver.oneblue.Entity.MusicTag;
import com.sanzuriver.oneblue.Entity.SourseVO.MusicTagResp;
import com.sanzuriver.oneblue.Service.MusicSourceService;
import org.jaudiotagger.tag.Tag;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("NetEaseMusicService")
public class NetEaseMusicServiceImpl implements MusicSourceService {
    @Override
    public List<MusicTagResp> searchMusic(String keyword, int numPerPage, int pageNum) {
        return null;
    }

    @Override
    public String getLyric(String id, String mid) {
        return null;
    }

}
