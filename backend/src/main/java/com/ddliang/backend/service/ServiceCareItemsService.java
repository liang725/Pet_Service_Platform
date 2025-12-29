// ServiceCareItemsService.java
package com.ddliang.backend.service;

import com.ddliang.backend.entity.ServiceCareItems;
import java.util.List;

public interface ServiceCareItemsService {
    List<ServiceCareItems> getAllCareItems();
    List<ServiceCareItems> getCareItemsByCategory(String category);
    List<ServiceCareItems> getSinglePurchaseItems();
    ServiceCareItems getCareItemById(Integer id);
}