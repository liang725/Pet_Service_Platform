// ServiceCareItemsServiceImpl.java
package com.ddliang.backend.service.impl;

import com.ddliang.backend.entity.ServiceCareItems;
import com.ddliang.backend.mapper.ServiceCareItemsMapper;
import com.ddliang.backend.service.ServiceCareItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceCareItemsServiceImpl implements ServiceCareItemsService {

    @Autowired
    private ServiceCareItemsMapper careItemsMapper;

    @Override
    public List<ServiceCareItems> getAllCareItems() {
        return careItemsMapper.findAll();
    }

    @Override
    public List<ServiceCareItems> getCareItemsByCategory(String category) {
        return careItemsMapper.findByCategory(category);
    }

    @Override
    public List<ServiceCareItems> getSinglePurchaseItems() {
        return careItemsMapper.findSinglePurchaseItems();
    }

    @Override
    public ServiceCareItems getCareItemById(Integer id) {
        return careItemsMapper.findById(id);
    }
}