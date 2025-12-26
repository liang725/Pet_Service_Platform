package com.ddliang.backend.mapper;

import com.ddliang.backend.entity.Address;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressMapper {

    /**
     * 根据用户ID获取地址列表
     */
    @Select("SELECT id, user_id, receiver_name as receiverName, receiver_phone as receiverPhone, " +
            "province, city, district, address, full_address as fullAddress, " +
            "is_default as isDefault, created_at as createdAt, updated_at as updatedAt " +
            "FROM user_addresses WHERE user_id = #{userId} ORDER BY is_default DESC, created_at DESC")
    List<Address> findByUserId(@Param("userId") Long userId);

    /**
     * 根据ID获取地址
     */
    @Select("SELECT id, user_id, receiver_name as receiverName, receiver_phone as receiverPhone, " +
            "province, city, district, address, full_address as fullAddress, " +
            "is_default as isDefault, created_at as createdAt, updated_at as updatedAt " +
            "FROM user_addresses WHERE id = #{id}")
    Address findById(@Param("id") Long id);

    /**
     * 添加地址
     */
    @Insert("INSERT INTO user_addresses (user_id, receiver_name, receiver_phone, province, city, district, " +
            "address, full_address, is_default, created_at, updated_at) " +
            "VALUES (#{userId}, #{receiverName}, #{receiverPhone}, #{province}, #{city}, #{district}, " +
            "#{address}, #{fullAddress}, #{isDefault}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Address address);

    /**
     * 更新地址
     */
    @Update("UPDATE user_addresses SET receiver_name = #{receiverName}, receiver_phone = #{receiverPhone}, " +
            "province = #{province}, city = #{city}, district = #{district}, " +
            "address = #{address}, full_address = #{fullAddress}, " +
            "is_default = #{isDefault}, updated_at = NOW() WHERE id = #{id}")
    int update(Address address);

    /**
     * 删除地址
     */
    @Delete("DELETE FROM user_addresses WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 取消用户的所有默认地址
     */
    @Update("UPDATE user_addresses SET is_default = 0 WHERE user_id = #{userId}")
    int clearDefaultByUserId(@Param("userId") Long userId);

    /**
     * 设置默认地址
     */
    @Update("UPDATE user_addresses SET is_default = 1 WHERE id = #{id}")
    int setDefault(@Param("id") Long id);

    /**
     * 获取用户的默认地址
     */
    @Select("SELECT id, user_id, receiver_name as receiverName, receiver_phone as receiverPhone, " +
            "province, city, district, address, full_address as fullAddress, " +
            "is_default as isDefault, created_at as createdAt, updated_at as updatedAt " +
            "FROM user_addresses WHERE user_id = #{userId} AND is_default = 1 LIMIT 1")
    Address findDefaultByUserId(@Param("userId") Long userId);

    /**
     * 检查地址是否属于指定用户
     */
    @Select("SELECT COUNT(*) FROM user_addresses WHERE id = #{id} AND user_id = #{userId}")
    int checkOwnership(@Param("id") Long id, @Param("userId") Long userId);
}