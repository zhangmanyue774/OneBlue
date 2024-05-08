package com.sanzuriver.oneblue.Service;

import java.util.List;
import java.util.Map;

public interface SystemConfigService {
    void getSystemConfig();
    Object updateSystemConfig(Map<String, Object> configs);
    Object getConfig();
    //本地多文件夹创建软链接 - > 单文件夹
    void createSymbolicLinks();
}
