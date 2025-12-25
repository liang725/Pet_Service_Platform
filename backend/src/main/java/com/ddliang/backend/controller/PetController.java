package com.ddliang.backend.controller;

import com.ddliang.backend.common.Result;
import com.ddliang.backend.dto.PetRequest;
import com.ddliang.backend.dto.PetResponse;
import com.ddliang.backend.service.PetService;
import com.ddliang.backend.util.JwtUtil;
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

    @Autowired
    private JwtUtil jwtUtil; // 注入JwtUtil

    /**
     * 获取当前用户的所有宠物
     */
    @GetMapping
    public Result<List<PetResponse>> getUserPets(HttpServletRequest request) {
        try {
            Integer userId = getUserIdFromToken(request);
            if (userId == null) {
                return Result.error(401, "用户未登录");
            }

            List<PetResponse> pets = petService.getUserPets(userId);
            return Result.success(pets);
        } catch (Exception e) {
            return Result.error("获取宠物列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取宠物
     */
    @GetMapping("/{id}")
    public Result<PetResponse> getPetById(@PathVariable Integer id, HttpServletRequest request) {
        try {
            Integer userId = getUserIdFromToken(request);
            if (userId == null) {
                return Result.error(401, "用户未登录");
            }

            PetResponse pet = petService.getPetById(id, userId);
            return Result.success(pet);
        } catch (Exception e) {
            return Result.error("获取宠物失败: " + e.getMessage());
        }
    }

    /**
     * 添加新宠物
     */
    @PostMapping
    public Result<PetResponse> addPet(@Valid @RequestBody PetRequest petRequest,
                                      BindingResult bindingResult,
                                      HttpServletRequest request) {
        try {
            if (bindingResult.hasErrors()) {
                return Result.error(400, bindingResult.getFieldError().getDefaultMessage());
            }

            Integer userId = getUserIdFromToken(request);
            if (userId == null) {
                return Result.error(401, "用户未登录");
            }

            PetResponse pet = petService.addPet(petRequest, userId);
            return Result.success("添加宠物成功", pet);
        } catch (Exception e) {
            return Result.error("添加宠物失败: " + e.getMessage());
        }
    }

    /**
     * 更新宠物信息
     */
    @PutMapping("/{id}")
    public Result<PetResponse> updatePet(@PathVariable Integer id,
                                         @Valid @RequestBody PetRequest petRequest,
                                         BindingResult bindingResult,
                                         HttpServletRequest request) {
        try {
            if (bindingResult.hasErrors()) {
                return Result.error(400, bindingResult.getFieldError().getDefaultMessage());
            }

            Integer userId = getUserIdFromToken(request);
            if (userId == null) {
                return Result.error(401, "用户未登录");
            }

            PetResponse pet = petService.updatePet(id, petRequest, userId);
            return Result.success("更新宠物成功", pet);
        } catch (Exception e) {
            return Result.error("更新宠物失败: " + e.getMessage());
        }
    }

    /**
     * 删除宠物
     */
    @DeleteMapping("/{id}")
    public Result<?> deletePet(@PathVariable Integer id, HttpServletRequest request) {
        try {
            Integer userId = getUserIdFromToken(request);
            if (userId == null) {
                return Result.error(401, "用户未登录");
            }

            petService.deletePet(id, userId);
            return Result.success("删除宠物成功");
        } catch (Exception e) {
            return Result.error("删除宠物失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户宠物数量
     */
    @GetMapping("/count")
    public Result<Map<String, Integer>> countUserPets(HttpServletRequest request) {
        try {
            Integer userId = getUserIdFromToken(request);
            if (userId == null) {
                return Result.error(401, "用户未登录");
            }

            int count = petService.countUserPets(userId);
            return Result.success(Map.of("count", count));
        } catch (Exception e) {
            return Result.error("获取宠物数量失败: " + e.getMessage());
        }
    }

    /**
     * 从JWT Token中获取用户ID
     */
    private Integer getUserIdFromToken(HttpServletRequest request) {
        try {
            // 从Header中获取Authorization
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return null;
            }

            // 提取token
            String token = authHeader.substring(7);

            // 从token中获取用户ID
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("获取用户ID失败: " + e.getMessage());
            return null;
        }
    }
}