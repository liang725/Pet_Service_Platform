package com.ddliang.backend.mapper;

import com.ddliang.backend.entity.Pet;
import org.apache.ibatis.annotations.*;

import java.util.List;

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
}