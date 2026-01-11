// ServiceCareItemsController.java
package com.ddliang.backend.controller;

import com.ddliang.backend.common.Result;
import com.ddliang.backend.entity.ServiceCareItems;
import com.ddliang.backend.service.ServiceCareItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service/care")
// @CrossOrigin(origins = "http://localhost:5173") // 注释掉跨域配置，使用nginx代理
public class ServiceCareItemsController {

    @Autowired
    private ServiceCareItemsService careItemsService;

    /**
     * 获取所有护理项目
     */
    @GetMapping("/items")
    public Result<?> getAllCareItems() {
        try {
            List<ServiceCareItems> items = careItemsService.getAllCareItems();
            return Result.success("获取护理项目成功", items);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 按分类获取护理项目
     */
    @GetMapping("/items/category/{category}")
    public Result<?> getCareItemsByCategory(@PathVariable String category) {
        try {
            List<ServiceCareItems> items = careItemsService.getCareItemsByCategory(category);
            return Result.success("获取护理项目成功", items);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 获取可单独购买的护理项目
     */
    @GetMapping("/items/single-purchase")
    public Result<?> getSinglePurchaseItems() {
        try {
            List<ServiceCareItems> items = careItemsService.getSinglePurchaseItems();
            return Result.success("获取可单独购买护理项目成功", items);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 获取护理项目详情
     */
    @GetMapping("/items/{id}")
    public Result<?> getCareItemById(@PathVariable Integer id) {
        try {
            ServiceCareItems item = careItemsService.getCareItemById(id);
            if (item == null) {
                return Result.error(404, "护理项目不存在");
            }
            return Result.success("获取护理项目详情成功", item);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 获取按分类分组的护理项目
     */
    @GetMapping("/items/grouped")
    public Result<?> getGroupedCareItems() {
        try {
            List<ServiceCareItems> allItems = careItemsService.getAllCareItems();
            // 前端会自行分组，这里直接返回所有项目
            return Result.success("获取分组护理项目成功", allItems);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }
}