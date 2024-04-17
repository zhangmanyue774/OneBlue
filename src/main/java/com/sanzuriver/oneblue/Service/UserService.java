package com.sanzuriver.oneblue.Service;


import com.sanzuriver.oneblue.Entity.User;
import com.sanzuriver.oneblue.Entity.VO.UserDetails;

public interface UserService {
    UserDetails loadUserByUsername(String userId);
    Integer login(User user);
    Object register(User user, Integer role);
    void insertAdminUser(User user);
}
