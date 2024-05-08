package com.sanzuriver.oneblue.Controller.Music;

import com.sanzuriver.oneblue.Service.MusicSourceService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MusicSourceController {
    @Resource
    private ApplicationContext applicationContext;
    @PostMapping("/search")
    public Object searchMusic(@RequestBody Map<String, Object> map){
        MusicSourceService musicSourceService = applicationContext.getBean((String) map.get("source"), MusicSourceService.class);
        return musicSourceService.searchMusic( (String) map.get("keyword"), (int) map.get("numPerPage"), (int) map.get("pageNum"));
    }
    @GetMapping("/lyric")
    public Object getLyric(@Param("id") String id,@Nonnull @Param("mid") String mid,@Param("source") String source){
        MusicSourceService musicSourceService = applicationContext.getBean(source, MusicSourceService.class);
        return musicSourceService.getLyric(id, mid);
    }
}
