package com.ddliang.backend.controller;

import com.ddliang.backend.common.Result;
import com.ddliang.backend.dto.PetRequest;
import com.ddliang.backend.dto.PetResponse;
import com.ddliang.backend.service.PetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 宠物控制器
 */
@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "http://localhost:5173")
public class PetController {

    @Autowired
    private PetService petService;

    /**
     * 获取当前用户的所有宠物
     */
    @GetMapping
    public Result<List<PetResponse>> getUserPets(HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        List<PetResponse> pets = petService.getUserPets(userId);
        return Result.success(pets);
    }

    /**
     * 根据ID获取宠物
     */
    @GetMapping("/{id}")
    public Result<PetResponse> getPetById(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        PetResponse pet = petService.getPetById(id, userId);
        return Result.success(pet);
    }

    /**
     * 添加新宠物
     */
    @PostMapping
    public Result<PetResponse> addPet(@Valid @RequestBody PetRequest petRequest,
                                      BindingResult bindingResult,
                                      HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.error(400, bindingResult.getFieldError().getDefaultMessage());
        }

        Integer userId = getUserIdFromRequest(request);
        PetResponse pet = petService.addPet(petRequest, userId);
        return Result.success("添加宠物成功", pet);
    }

    /**
     * 更新宠物信息
     */
    @PutMapping("/{id}")
    public Result<PetResponse> updatePet(@PathVariable Integer id,
                                         @Valid @RequestBody PetRequest petRequest,
                                         BindingResult bindingResult,
                                         HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.error(400, bindingResult.getFieldError().getDefaultMessage());
        }

        Integer userId = getUserIdFromRequest(request);
        PetResponse pet = petService.updatePet(id, petRequest, userId);
        return Result.success("更新宠物成功", pet);
    }

    /**
     * 删除宠物
     */
    @DeleteMapping("/{id}")
    public Result<?> deletePet(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        petService.deletePet(id, userId);
        return Result.success("删除宠物成功");
    }

    /**
     * 获取用户宠物数量
     */
    @GetMapping("/count")
    public Result<Map<String, Integer>> countUserPets(HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        int count = petService.countUserPets(userId);
        return Result.success(Map.of("count", count));
    }

    /**
     * 从请求中获取用户ID（简化版，实际应该从JWT token中解析）
     */
    private Integer getUserIdFromRequest(HttpServletRequest request) {
        // 从Header中获取用户ID（简化处理）
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                return Integer.parseInt(userIdHeader);
            } catch (NumberFormatException e) {
                // 如果解析失败，返回默认用户ID（测试用）
                return 1;
            }
        }

        // 模拟用户ID，实际应该从JWT token解析
        // 这里为了简化，返回默认值
        return 1;
    }
}