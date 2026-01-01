package com.ddliang.backend.mapper;

import com.ddliang.backend.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM users WHERE phone = #{phone}")
    User findByPhone(String phone);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);

    /**
     * 插入新用户（包含角色字段）
     */
    @Insert("INSERT INTO users(username, password, nickname, phone, role, email, status) " +
            "VALUES(#{username}, #{password}, #{nickname}, #{phone}, #{role}, #{email}, 1)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 更新最后登录时间
     */
    @Update("UPDATE users SET last_login_at = NOW() WHERE id = #{userId}")
    int updateLastLoginTime(Integer userId);

    /**
     * 根据ID查询用户
     */
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Integer id);

    // ==================== 管理端用户管理接口 ====================

    /**
     * 查询用户列表（分页）
     */
    @Select("SELECT id, username, role, status, " +
            "created_at, updated_at, last_login_at " +
            "FROM users " +
            "ORDER BY created_at DESC " +
            "LIMIT #{offset}, #{limit}")
    List<User> findAllUsersForAdmin(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 搜索用户（分页）
     */
    @Select("SELECT id, username, role, status, " +
            "created_at, updated_at, last_login_at " +
            "FROM users " +
            "WHERE username LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY created_at DESC " +
            "LIMIT #{offset}, #{limit}")
    List<User> searchUsersForAdmin(@Param("keyword") String keyword,
                                    @Param("offset") int offset,
                                    @Param("limit") int limit);

    /**
     * 统计用户总数
     */
    @Select("SELECT COUNT(*) FROM users")
    int countAllUsers();

    /**
     * 统计搜索结果数量
     */
    @Select("SELECT COUNT(*) FROM users " +
            "WHERE username LIKE CONCAT('%', #{keyword}, '%')")
    int countSearchUsers(@Param("keyword") String keyword);

    /**
     * 更新用户信息（只更新角色和状态）
     */
    @Update("UPDATE users SET " +
            "role = #{role}, " +
            "status = #{status} " +
            "WHERE id = #{id}")
    int update(User user);

    /**
     * 更新用户密码
     */
    @Update("UPDATE users SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Integer id, @Param("password") String password);

    /**
     * 更新用户状态
     */
    @Update("UPDATE users SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    /**
     * 删除用户
     */
    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);
}