package com.sanzuriver.oneblue.Service;

import java.util.List;
import java.util.Map;

public interface SystemConfigService {
    void getSystemConfig();
    Object updateSystemConfig(Map<String, Object> configs);
    Object getConfig();
}
