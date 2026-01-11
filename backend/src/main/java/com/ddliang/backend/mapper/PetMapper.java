package com.ddliang.backend.mapper;

import com.ddliang.backend.entity.Pet;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 宠物Mapper接口
 */
@Mapper
public interface PetMapper {

    /**
     * 查询用户的所有宠物
     */
    @Select("SELECT * FROM pets WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Pet> findByUserId(Integer userId);

    /**
     * 根据ID查询宠物
     */
    @Select("SELECT * FROM pets WHERE id = #{id}")
    Pet findById(Integer id);

    /**
     * 插入新宠物
     */
    @Insert("INSERT INTO pets(user_id, pet_type, name, breed, gender, birth_date, color, " +
            "weight, avatar_url, personality, notes) " +
            "VALUES(#{userId}, #{petType}, #{name}, #{breed}, #{gender}, #{birthDate}, #{color}, " +
            "#{weight}, #{avatarUrl}, #{personality}, #{notes})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Pet pet);

    /**
     * 更新宠物信息
     */
    @Update("UPDATE pets SET " +
            "pet_type = #{petType}, " +
            "name = #{name}, " +
            "breed = #{breed}, " +
            "gender = #{gender}, " +
            "birth_date = #{birthDate}, " +
            "color = #{color}, " +
            "weight = #{weight}, " +
            "avatar_url = #{avatarUrl}, " +
            "personality = #{personality}, " +
            "notes = #{notes}, " +
            "updated_at = NOW() " +
            "WHERE id = #{id} AND user_id = #{userId}")
    int update(Pet pet);

    /**
     * 删除宠物
     */
    @Delete("DELETE FROM pets WHERE id = #{id} AND user_id = #{userId}")
    int delete(Integer id, Integer userId);

    /**
     * 统计用户宠物数量
     */
    @Select("SELECT COUNT(*) FROM pets WHERE user_id = #{userId}")
    int countByUserId(Integer userId);

    /**
     * 检查宠物是否属于用户
     */
    @Select("SELECT COUNT(*) FROM pets WHERE id = #{id} AND user_id = #{userId}")
    int existsByIdAndUserId(Integer id, Integer userId);

    /**
     * 管理端：分页查询所有宠物
     */
    @Select("<script>" +
            "SELECT p.*, u.username as owner_name " +
            "FROM pets p " +
            "LEFT JOIN users u ON p.user_id = u.id " +
            "<where>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "   AND (p.name LIKE CONCAT('%', #{keyword}, '%') " +
            "   OR p.breed LIKE CONCAT('%', #{keyword}, '%') " +
            "   OR u.username LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "</where>" +
            "ORDER BY p.created_at DESC " +
            "LIMIT #{limit} OFFSET #{offset}" +
            "</script>")
    List<Map<String, Object>> findAllWithOwner(@Param("offset") int offset,
                                               @Param("limit") int limit,
                                               @Param("keyword") String keyword);

    /**
     * 管理端：统计宠物总数
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM pets p " +
            "LEFT JOIN users u ON p.user_id = u.id " +
            "<where>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "   AND (p.name LIKE CONCAT('%', #{keyword}, '%') " +
            "   OR p.breed LIKE CONCAT('%', #{keyword}, '%') " +
            "   OR u.username LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "</where>" +
            "</script>")
    int countAll(@Param("keyword") String keyword);

    /**
     * 管理端：根据ID查询宠物（包含主人信息）
     */
    @Select("SELECT p.*, u.username as owner_name, u.nickname as owner_nickname " +
            "FROM pets p " +
            "LEFT JOIN users u ON p.user_id = u.id " +
            "WHERE p.id = #{id}")
    Map<String, Object> findByIdWithOwner(Integer id);

    /**
     * 管理端：更新宠物（不需要用户ID验证）
     */
    @Update("UPDATE pets SET " +
            "user_id = #{userId}, " +
            "pet_type = #{petType}, " +
            "name = #{name}, " +
            "breed = #{breed}, " +
            "gender = #{gender}, " +
            "birth_date = #{birthDate}, " +
            "color = #{color}, " +
            "weight = #{weight}, " +
            "avatar_url = #{avatarUrl}, " +
            "personality = #{personality}, " +
            "notes = #{notes}, " +
            "updated_at = NOW() " +
            "WHERE id = #{id}")
    int updateByAdmin(Pet pet);

    /**
     * 管理端：删除宠物（不需要用户ID验证）
     */
    @Delete("DELETE FROM pets WHERE id = #{id}")
    int deleteByAdmin(Integer id);

    /**
     * 管理端：获取宠物统计信息
     */
    @Select("SELECT " +
            "COUNT(*) as total_pets, " +
            "COUNT(DISTINCT user_id) as total_owners, " +
            "SUM(CASE WHEN pet_type = '猫' THEN 1 ELSE 0 END) as cat_count, " +
            "SUM(CASE WHEN pet_type = '狗' THEN 1 ELSE 0 END) as dog_count, " +
            "SUM(CASE WHEN gender = '公' THEN 1 ELSE 0 END) as male_count, " +
            "SUM(CASE WHEN gender = '母' THEN 1 ELSE 0 END) as female_count " +
            "FROM pets")
    Map<String, Object> getStats();

    /**
     * 根据用户ID和宠物名检查是否存在
     */
    @Select("SELECT COUNT(*) FROM pets WHERE user_id = #{userId} AND name = #{name}")
    int existsByNameAndUserId(@Param("name") String name, @Param("userId") Integer userId);

    /**
     * 获取最近创建的宠物
     */
    @Select("SELECT * FROM pets ORDER BY created_at DESC LIMIT #{limit}")
    List<Pet> findRecentPets(@Param("limit") int limit);

    /**
     * 按类型统计
     */
    @Select("SELECT pet_type, COUNT(*) as count FROM pets GROUP BY pet_type")
    List<Map<String, Object>> countByPetType();

    /**
     * 按性别统计
     */
    @Select("SELECT gender, COUNT(*) as count FROM pets GROUP BY gender")
    List<Map<String, Object>> countByGender();
}