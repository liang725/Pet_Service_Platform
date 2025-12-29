// ServiceItemServiceImpl.java
package com.ddliang.backend.service.impl;

import com.ddliang.backend.dto.ServiceItemResponse;
import com.ddliang.backend.entity.ServiceItem;
import com.ddliang.backend.mapper.ServiceItemMapper;
import com.ddliang.backend.service.ServiceItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceItemServiceImpl implements ServiceItemService {

    @Autowired
    private ServiceItemMapper serviceItemMapper;

    @Override
    public List<ServiceItemResponse> getAllServiceItems() {
        return convertToResponse(serviceItemMapper.findAllActive());
    }

    @Override
    public List<ServiceItemResponse> getRecommendedServices() {
        return convertToResponse(serviceItemMapper.findRecommended());
    }

    @Override
    public ServiceItemResponse getServiceById(Integer id) {
        ServiceItem item = serviceItemMapper.findById(id);
        return item != null ? convertToResponse(item) : null;
    }

    @Override
    public List<ServiceItemResponse> getServicesByCategory(String category) {
        return convertToResponse(serviceItemMapper.findByCategory(category));
    }

    private ServiceItemResponse convertToResponse(ServiceItem item) {
        ServiceItemResponse response = new ServiceItemResponse();
        response.setId(item.getId());
        response.setCategory(item.getCategory());
        response.setItemName(item.getItemName());
        response.setBriefDesc(item.getBriefDesc());
        response.setBasePrice(item.getBasePrice());
        response.setIsDiscount(item.getIsDiscount());
        response.setDiscountPercentage(item.getDiscountPercentage());
        response.setFinalPrice(item.getFinalPrice());
        response.setIsRecommended(item.getIsRecommended());
        response.setDuration(item.getDuration());
        response.setThumbnailUrl(item.getThumbnailUrl());
        return response;
    }

    private List<ServiceItemResponse> convertToResponse(List<ServiceItem> items) {
        return items.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
}