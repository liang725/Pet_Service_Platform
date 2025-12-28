package com.ddliang.backend.service;

import com.ddliang.backend.dto.PetRequest;
import com.ddliang.backend.dto.PetResponse;
import com.ddliang.backend.entity.Pet;

import java.util.List;

/**
 * 宠物服务接口
 */
public interface PetService {

    /**
     * 获取用户的所有宠物
     */
    List<PetResponse> getUserPets(Integer userId);

    /**
     * 根据ID获取宠物
     */
    PetResponse getPetById(Integer id, Integer userId);

    /**
     * 添加宠物
     */
    PetResponse addPet(PetRequest request, Integer userId);

    /**
     * 更新宠物
     */
    PetResponse updatePet(Integer id, PetRequest request, Integer userId);

    /**
     * 删除宠物
     */
    void deletePet(Integer id, Integer userId);

    /**
     * 统计用户宠物数量
     */
    int countUserPets(Integer userId);
}