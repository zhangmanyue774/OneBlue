package com.sanzuriver.oneblue.Mapper;

import com.sanzuriver.oneblue.Entity.MusicTag;
import com.sanzuriver.oneblue.Entity.Shares;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;


@Mapper
public interface MusicTagsMapper {
    @Select("select FileName from music_tag")
    Set<String> getMusicListFileName();
    @Delete("delete from music_tag where FileName = #{fileName}")
    void deleteMusicTag(String fileName);
    @Select("select value from configs where key = 'isInit'")
    String getIsInit();
    @Select("insert into configs (key,value) values ('isInit',#{value})")
    void insertIsInit(String value);
    //插入音乐标签
    @Insert("insert into music_tag(artist, album, year, title, lyrics, cover_art, filename, play_url,SOURCE) values (#{artist},#{album},#{year},#{title},#{lyrics},#{coverArt},#{fileName},#{playUrl},#{source})")
    void insertMusicTag(MusicTag musicTag);
    @Select("select * from music_tag")
    List<MusicTag> getMusicList();
//    webDav
    @Select("select FileName from webdav_music_tag")
    Set<String> getWebDavMusicListFileName();
    @Delete("delete from webdav_music_tag where FileName = #{fileName}")
    void deleteWebDavMusicTag(String fileName);
    @Select("insert into webdav_music_tag(artist, album, year, title, lyrics, cover_art, filename, play_url,SOURCE) values (#{artist},#{album},#{year},#{title},#{lyrics},#{coverArt},#{fileName},#{playUrl},#{source})")
    void insertWebDavMusicTag(MusicTag musicTag);
    @Select("select * from webdav_music_tag")
    List<MusicTag> getWebDavMusicList();
    @Select("select * from shares where share_id = #{shareId}")
    Shares getShareMusic(String shareId);
    @Insert("insert into shares(share_song,share_id,share_expire_time) values (#{shareSong},#{shareId},#{shareExpireTime})")
    void insertShareMusic(Shares shares);
    @Delete("delete from shares where share_id = #{shareId}")
    void deleteShareMusic(String shareId);
    @Select("select * from shares")
    List<Shares> getShareMusicList();
}
