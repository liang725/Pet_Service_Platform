package com.ddliang.backend.service.impl;

import com.ddliang.backend.common.PageResult;
import com.ddliang.backend.dto.PetRequest;
import com.ddliang.backend.dto.PetResponse;
import com.ddliang.backend.entity.Pet;
import com.ddliang.backend.mapper.PetMapper;
import com.ddliang.backend.service.AdminPetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminPetServiceImpl implements AdminPetService {

    @Autowired
    private PetMapper petMapper;

    @Override
    public PageResult<PetResponse> getAllPets(int page, int pageSize, String keyword) {
        // 参数验证
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;
        if (pageSize > 100) pageSize = 100;

        // 计算偏移量
        int offset = (page - 1) * pageSize;

        // 查询数据
        List<Map<String, Object>> petMaps = petMapper.findAllWithOwner(offset, pageSize, keyword);

        // 转换为PetResponse
        List<PetResponse> pets = petMaps.stream()
                .map(this::convertMapToResponse)
                .collect(Collectors.toList());

        // 查询总数
        int total = petMapper.countAll(keyword);

        // 返回分页结果
        return PageResult.of(pets, total, page, pageSize);
    }

    @Override
    public PetResponse getPetById(Integer id) {
        Map<String, Object> petMap = petMapper.findByIdWithOwner(id);
        if (petMap == null || petMap.isEmpty()) {
            throw new RuntimeException("宠物不存在");
        }
        return convertMapToResponse(petMap);
    }

    @Override
    @Transactional
    public PetResponse addPet(PetRequest request) {
        // 验证用户ID是否存在（这里可以添加用户验证逻辑）
        // 在实际项目中，可能需要调用UserService验证用户是否存在

        // 创建Pet实体
        Pet pet = new Pet();
        pet.setUserId(request.getUserId());
        pet.setPetType(request.getPetType());
        pet.setName(request.getName());
        pet.setBreed(request.getBreed());
        pet.setGender(request.getGender());
        pet.setBirthDate(request.getBirthDate());
        pet.setColor(request.getColor());
        pet.setWeight(request.getWeight());
        pet.setAvatarUrl(request.getAvatarUrl());
        pet.setPersonality(request.getPersonality());
        pet.setNotes(request.getNotes());

        // 插入数据库
        int result = petMapper.insert(pet);
        if (result <= 0) {
            throw new RuntimeException("添加宠物失败");
        }

        // 返回新创建的宠物信息
        return getPetById(pet.getId());
    }

    @Override
    @Transactional
    public PetResponse updatePet(Integer id, PetRequest request) {
        // 检查宠物是否存在
        Pet existingPet = petMapper.findById(id);
        if (existingPet == null) {
            throw new RuntimeException("宠物不存在");
        }

        // 更新Pet实体
        Pet pet = new Pet();
        pet.setId(id);
        pet.setUserId(request.getUserId());
        pet.setPetType(request.getPetType());
        pet.setName(request.getName());
        pet.setBreed(request.getBreed());
        pet.setGender(request.getGender());
        pet.setBirthDate(request.getBirthDate());
        pet.setColor(request.getColor());
        pet.setWeight(request.getWeight());
        pet.setAvatarUrl(request.getAvatarUrl());
        pet.setPersonality(request.getPersonality());
        pet.setNotes(request.getNotes());

        // 更新数据库
        int result = petMapper.updateByAdmin(pet);
        if (result <= 0) {
            throw new RuntimeException("更新宠物失败");
        }

        // 返回更新后的宠物信息
        return getPetById(id);
    }

    @Override
    @Transactional
    public void deletePet(Integer id) {
        // 检查宠物是否存在
        Pet existingPet = petMapper.findById(id);
        if (existingPet == null) {
            throw new RuntimeException("宠物不存在");
        }

        // 删除宠物
        int result = petMapper.deleteByAdmin(id);
        if (result <= 0) {
            throw new RuntimeException("删除宠物失败");
        }
    }

    @Override
    public Map<String, Object> getPetStats() {
        Map<String, Object> statsMap = petMapper.getStats();
        if (statsMap == null) {
            statsMap = new HashMap<>();
            statsMap.put("total_pets", 0);
            statsMap.put("total_owners", 0);
            statsMap.put("cat_count", 0);
            statsMap.put("dog_count", 0);
            statsMap.put("male_count", 0);
            statsMap.put("female_count", 0);
        }

        // 添加更多统计信息
        Map<String, Object> result = new HashMap<>();
        result.put("totalPets", statsMap.get("total_pets"));
        result.put("totalOwners", statsMap.get("total_owners"));
        result.put("catCount", statsMap.get("cat_count"));
        result.put("dogCount", statsMap.get("dog_count"));
        result.put("maleCount", statsMap.get("male_count"));
        result.put("femaleCount", statsMap.get("female_count"));
        result.put("unknownGenderCount",
                ((Number)statsMap.getOrDefault("total_pets", 0)).intValue() -
                        ((Number)statsMap.getOrDefault("male_count", 0)).intValue() -
                        ((Number)statsMap.getOrDefault("female_count", 0)).intValue());

        return result;
    }

    /**
     * 将数据库查询的Map转换为PetResponse
     */
    private PetResponse convertMapToResponse(Map<String, Object> map) {
        PetResponse response = new PetResponse();

        // 基本字段
        response.setId(getInteger(map.get("id")));
        response.setUserId(getInteger(map.get("user_id")));
        response.setPetType(getString(map.get("pet_type")));
        response.setName(getString(map.get("name")));
        response.setBreed(getString(map.get("breed")));
        response.setGender(getString(map.get("gender")));

        // 处理日期字段
        Object birthDate = map.get("birth_date");
        if (birthDate instanceof java.sql.Date) {
            response.setBirthDate(((java.sql.Date) birthDate).toLocalDate());
        } else if (birthDate instanceof LocalDate) {
            response.setBirthDate((LocalDate) birthDate);
        }

        // 其他字段
        response.setColor(getString(map.get("color")));
        response.setWeight(getBigDecimal(map.get("weight")));
        response.setAvatarUrl(getString(map.get("avatar_url")));
        response.setPersonality(getString(map.get("personality")));
        response.setNotes(getString(map.get("notes")));

        // 时间字段
        response.setCreatedAt(getLocalDateTime(map.get("created_at")));
        response.setUpdatedAt(getLocalDateTime(map.get("updated_at")));

        return response;
    }

    // 辅助方法
    private Integer getInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof Number) return ((Number) obj).intValue();
        return null;
    }

    private String getString(Object obj) {
        return obj == null ? null : obj.toString();
    }

    private BigDecimal getBigDecimal(Object obj) {
        if (obj == null) return null;
        if (obj instanceof BigDecimal) return (BigDecimal) obj;
        if (obj instanceof Number) return BigDecimal.valueOf(((Number) obj).doubleValue());
        try {
            return new BigDecimal(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime getLocalDateTime(Object obj) {
        if (obj == null) return null;
        if (obj instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) obj).toLocalDateTime();
        }
        if (obj instanceof LocalDateTime) {
            return (LocalDateTime) obj;
        }
        return null;
    }
}