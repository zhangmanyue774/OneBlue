package com.sanzuriver.oneblue.Mapper;

import com.sanzuriver.oneblue.Entity.Config;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface SystemConfigMapper {
    @Update("INSERT OR REPLACE INTO configs (key, value) VALUES (#{key}, #{value})")
    void insertConfig(@Param("key") String key, @Param("value") String value);

    default void insertMultipleConfigs(Map<String, Object> configs) {
        for (Map.Entry<String, Object> entry : configs.entrySet()) {
            insertConfig(entry.getKey(), entry.getValue().toString());
        }
    }

    @Select("SELECT key, value FROM configs")
    List<Config> selectAllConfig();
}
