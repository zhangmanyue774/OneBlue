package com.sanzuriver.oneblue.Controller.Music;

import com.sanzuriver.oneblue.Service.MusicSourceService;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MusicSourceController {
    @Resource(name = "QQMusic")
    private MusicSourceService musicSourceService;
    @GetMapping("/search")
    public Object searchMusic(@Param("keyword") String keyword, @Param("numPerPage") int numPerPage, @Param("pageNum") int pageNum){
        return musicSourceService.searchMusic(keyword, numPerPage, pageNum);
    }
    @GetMapping("/lyric")
    public Object getLyric(@Param("id") String id, @Param("mid") String mid){
        return musicSourceService.getLyric(id, mid);
    }
}
