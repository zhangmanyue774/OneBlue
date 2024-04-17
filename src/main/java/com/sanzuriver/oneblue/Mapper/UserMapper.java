package com.sanzuriver.oneblue.Mapper;

import com.sanzuriver.oneblue.Entity.Permission;
import com.sanzuriver.oneblue.Entity.Role;
import com.sanzuriver.oneblue.Entity.User;
import com.sanzuriver.oneblue.Entity.UserRole;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 新建用户
     * @param user 用户
     * @return 影响一行数据
     */
    @Insert("INSERT INTO users (id,email, password) VALUES (#{id},#{email}, #{password})")
    Integer insertUser(User user);
    /**
     * 新建Admin用户
     */
    @Insert("INSERT OR IGNORE INTO users (email, password) VALUES (#{email}, #{password})")
    Integer insertAdminUser(User user);
    /**
     * 分配用户角色
     * @param userRole 用户ID和角色ID
     * @return 影响一行数据
     */
    @Insert("INSERT INTO user_roles (user_id, role_id) VALUES (#{userId}, #{roleId})")
    Integer insertUserRole(UserRole userRole);

    /**
     * 新建角色
     * @param name 角色名
     * @return 影响一行数据
     */
    @Insert("INSERT INTO roles (name) VALUES (#{name})")
    Integer insertRole(String name);
    /**
     * 新建权限
     * @param name 权限名
     * @return 影响一行数据
     */
    @Insert("INSERT INTO permissions (name) VALUES (#{name})")
    Integer insertPermission(String name);
    /**
     * 分配角色权限
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 影响一行数据
     */
    @Insert("INSERT INTO role_permissions (role_id, permission_id) VALUES (#{roleId}, #{permissionId})")
    Integer insertRolePermission(Integer roleId, Integer permissionId);
    /**
     * 查询用户权限
     * @param id 用户ID
     * @return 用户权限
     */
    @Select("SELECT p.id FROM users u JOIN user_roles ur ON u.id = ur.user_id JOIN roles r ON ur.role_id = r.id JOIN role_permissions rp ON r.id = rp.role_id JOIN permissions p ON rp.permission_id = p.id WHERE u.id = #{id}")
    List<Integer> selectUserPermission(Integer id);
    /**
     * 查询权限列表
        * @return 权限列表
     */
    @Select("SELECT * FROM permissions")
    List<Permission> selectPermissions();

    /**
     * 查询角色列表
     * @return 角色列表
     */
    @Select("SELECT * FROM roles")
    List<Role> selectRoles();
    /**
     * 查询用户id
     * @param userId 用户ID
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE id = #{id}")
    User selectUser(String userId);
    /**
     * 查询用户角色
     * @param id 用户ID
     * @return 角色信息
     */
    @Select("SELECT r.id, r.name FROM users u JOIN user_roles ur ON u.id = ur.user_id JOIN roles r ON ur.role_id = r.id WHERE u.id = #{id}")
    Role selectUserRole(Integer id);
    /**
     * 用户登录
     * @param email 用户名
     * @return 登录结果
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User login(String email);
}
