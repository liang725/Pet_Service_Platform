package com.ddliang.backend.service.impl;

import com.ddliang.backend.dto.PetRequest;
import com.ddliang.backend.dto.PetResponse;
import com.ddliang.backend.entity.Pet;
import com.ddliang.backend.mapper.PetMapper;
import com.ddliang.backend.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 宠物服务实现类
 */
@Service
public class PetServiceImpl implements PetService {

    @Autowired
    private PetMapper petMapper;

    @Override
    public List<PetResponse> getUserPets(Integer userId) {
        List<Pet> pets = petMapper.findByUserId(userId);
        return pets.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public PetResponse getPetById(Integer id, Integer userId) {
        Pet pet = petMapper.findById(id);
        if (pet == null || !pet.getUserId().equals(userId)) {
            throw new RuntimeException("宠物不存在或无权访问");
        }
        return convertToResponse(pet);
    }

    @Override
    @Transactional
    public PetResponse addPet(PetRequest request, Integer userId) {
        Pet pet = new Pet();
        pet.setUserId(userId);
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

        int result = petMapper.insert(pet);
        if (result > 0) {
            return convertToResponse(pet);
        } else {
            throw new RuntimeException("添加宠物失败");
        }
    }

    @Override
    @Transactional
    public PetResponse updatePet(Integer id, PetRequest request, Integer userId) {
        // 检查宠物是否存在且属于该用户
        if (petMapper.existsByIdAndUserId(id, userId) == 0) {
            throw new RuntimeException("宠物不存在或无权修改");
        }

        Pet pet = new Pet();
        pet.setId(id);
        pet.setUserId(userId);
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

        int result = petMapper.update(pet);
        if (result > 0) {
            return getPetById(id, userId);
        } else {
            throw new RuntimeException("更新宠物失败");
        }
    }

    @Override
    @Transactional
    public void deletePet(Integer id, Integer userId) {
        // 检查宠物是否存在且属于该用户
        if (petMapper.existsByIdAndUserId(id, userId) == 0) {
            throw new RuntimeException("宠物不存在或无权删除");
        }

        int result = petMapper.delete(id, userId);
        if (result == 0) {
            throw new RuntimeException("删除宠物失败");
        }
    }

    @Override
    public int countUserPets(Integer userId) {
        return petMapper.countByUserId(userId);
    }

    /**
     * 将Pet实体转换为PetResponse
     */
    private PetResponse convertToResponse(Pet pet) {
        PetResponse response = new PetResponse();
        response.setId(pet.getId());
        response.setUserId(pet.getUserId());
        response.setPetType(pet.getPetType());
        response.setName(pet.getName());
        response.setBreed(pet.getBreed());
        response.setGender(pet.getGender());
        response.setBirthDate(pet.getBirthDate());
        response.setColor(pet.getColor());
        response.setWeight(pet.getWeight());
        response.setAvatarUrl(pet.getAvatarUrl());
        response.setPersonality(pet.getPersonality());
        response.setNotes(pet.getNotes());
        response.setCreatedAt(pet.getCreatedAt());
        response.setUpdatedAt(pet.getUpdatedAt());
        return response;
    }
}