// com.ddliang.backend.controller.AdminPetController.java
package com.ddliang.backend.controller;

import com.ddliang.backend.common.PageResult;
import com.ddliang.backend.common.Result;
import com.ddliang.backend.dto.PetRequest;
import com.ddliang.backend.dto.PetResponse;
import com.ddliang.backend.service.AdminPetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理端宠物控制器
 */
@RestController
@RequestMapping("/api/admin/pets")
// @CrossOrigin(origins = "http://localhost:5173") // 注释掉跨域配置，使用nginx代理
public class AdminPetController {

    @Autowired
    private AdminPetService adminPetService;

    /**
     * 分页查询所有宠物（支持搜索）
     */
    @GetMapping
    public Result<PageResult<PetResponse>> getPets(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        try {
            PageResult<PetResponse> result = adminPetService.getAllPets(page, pageSize, keyword);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取宠物列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取宠物（管理端不需要用户ID验证）
     */
    @GetMapping("/{id}")
    public Result<PetResponse> getPetById(@PathVariable Integer id) {
        try {
            PetResponse pet = adminPetService.getPetById(id);
            return Result.success(pet);
        } catch (Exception e) {
            return Result.error("获取宠物失败: " + e.getMessage());
        }
    }

    /**
     * 添加宠物（管理端可以指定用户ID）
     */
    @PostMapping
    public Result<PetResponse> addPet(@Valid @RequestBody PetRequest petRequest) {
        try {
            PetResponse pet = adminPetService.addPet(petRequest);
            return Result.success("添加宠物成功", pet);
        } catch (Exception e) {
            return Result.error("添加宠物失败: " + e.getMessage());
        }
    }

    /**
     * 更新宠物信息（管理端）
     */
    @PutMapping("/{id}")
    public Result<PetResponse> updatePet(@PathVariable Integer id,
                                         @Valid @RequestBody PetRequest petRequest) {
        try {
            PetResponse pet = adminPetService.updatePet(id, petRequest);
            return Result.success("更新宠物成功", pet);
        } catch (Exception e) {
            return Result.error("更新宠物失败: " + e.getMessage());
        }
    }

    /**
     * 删除宠物（管理端）
     */
    @DeleteMapping("/{id}")
    public Result<?> deletePet(@PathVariable Integer id) {
        try {
            adminPetService.deletePet(id);
            return Result.success("删除宠物成功");
        } catch (Exception e) {
            return Result.error("删除宠物失败: " + e.getMessage());
        }
    }

    /**
     * 获取宠物统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getPetStats() {
        try {
            Map<String, Object> stats = adminPetService.getPetStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计信息失败: " + e.getMessage());
        }
    }
}