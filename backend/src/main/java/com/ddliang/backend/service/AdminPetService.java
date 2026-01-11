// com.ddliang.backend.service.AdminPetService.java
package com.ddliang.backend.service;

import com.ddliang.backend.common.PageResult;
import com.ddliang.backend.dto.PetRequest;
import com.ddliang.backend.dto.PetResponse;

import java.util.Map;

/**
 * 管理端宠物服务接口
 */
public interface AdminPetService {

    /**
     * 分页查询所有宠物
     */
    PageResult<PetResponse> getAllPets(int page, int pageSize, String keyword);

    /**
     * 根据ID获取宠物（包含主人信息）
     */
    PetResponse getPetById(Integer id);

    /**
     * 添加宠物（可以指定用户ID）
     */
    PetResponse addPet(PetRequest request);

    /**
     * 更新宠物
     */
    PetResponse updatePet(Integer id, PetRequest request);

    /**
     * 删除宠物
     */
    void deletePet(Integer id);

    /**
     * 获取宠物统计信息
     */
    Map<String, Object> getPetStats();
}