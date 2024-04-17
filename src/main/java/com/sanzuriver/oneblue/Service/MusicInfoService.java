package com.sanzuriver.oneblue.Service;

import com.sanzuriver.oneblue.Entity.MusicTag;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MusicInfoService {
    //获取音乐列表:包含(音乐链接,图片链接（依赖于接口实现）
    List<MusicTag> getMusicList();
    //封面接口
    byte[] getMusicCover(String fileName);
    //音乐接口
    byte[] getMusicPlay(String fileName);
    //标签纠错刮削
    boolean manualSetMusicTag(String fileName,MusicTag musicTag);
//    --------------------------内部服务------------------------
    //数据库磁盘数据差异对比
    //方案1:文件删除接口同步删除(file模块实现)
    //方案2:HashSet差集获取(乱序数据库磁盘同步)
    void SynchronizeMusicListToDb();
    //方案3:系统首次初始化写如数据库
    void initMusicListToDb();


}
