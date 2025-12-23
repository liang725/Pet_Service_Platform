package com.ddliang.backend.service;

import com.ddliang.backend.dto.ServiceItemResponse;
import java.util.List;

public interface ServiceItemService {

    List<ServiceItemResponse> getAllServiceItems();

    List<ServiceItemResponse> getRecommendedServices();

    ServiceItemResponse getServiceById(Integer id);

    List<ServiceItemResponse> getServicesByCategory(String category);
}