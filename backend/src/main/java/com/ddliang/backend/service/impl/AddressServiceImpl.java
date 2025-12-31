package com.ddliang.backend.service.impl;

import com.ddliang.backend.entity.Address;
import com.ddliang.backend.dto.AddressRequest;
import com.ddliang.backend.mapper.AddressMapper;
import com.ddliang.backend.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public List<Address> getUserAddresses(Long userId) {
        return addressMapper.findByUserId(userId);
    }

    @Override
    public Address getAddressById(Long id, Long userId) {
        // 检查地址是否属于该用户
        if (addressMapper.checkOwnership(id, userId) == 0) {
            throw new RuntimeException("地址不存在或无权限访问");
        }
        return addressMapper.findById(id);
    }

    @Override
    @Transactional
    public Address addAddress(Long userId, AddressRequest request) {
        // 如果设为默认地址，先取消其他默认地址
        if (request.getIsDefault()) {
            addressMapper.clearDefaultByUserId(userId);
        }

        // 创建新地址 - 映射前端字段到后端字段
        Address address = new Address();
        address.setUserId(userId);
        address.setReceiverName(request.getRecipientName());  // recipientName -> receiverName
        address.setReceiverPhone(request.getPhone());         // phone -> receiverPhone
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setAddress(request.getDetailAddress());       // detailAddress -> address
        address.setFullAddress(request.getProvince() + request.getCity() + 
                              request.getDistrict() + request.getDetailAddress());
        address.setIsDefault(request.getIsDefault());
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());

        addressMapper.insert(address);
        return address;
    }

    @Override
    @Transactional
    public Address updateAddress(Long id, Long userId, AddressRequest request) {
        // 检查地址是否属于该用户
        if (addressMapper.checkOwnership(id, userId) == 0) {
            throw new RuntimeException("地址不存在或无权限访问");
        }

        // 如果设为默认地址，先取消其他默认地址
        if (request.getIsDefault()) {
            addressMapper.clearDefaultByUserId(userId);
        }

        // 获取现有地址
        Address address = addressMapper.findById(id);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }

        // 更新地址信息 - 映射前端字段到后端字段
        address.setReceiverName(request.getRecipientName());  // recipientName -> receiverName
        address.setReceiverPhone(request.getPhone());         // phone -> receiverPhone
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setAddress(request.getDetailAddress());       // detailAddress -> address
        address.setFullAddress(request.getProvince() + request.getCity() + 
                              request.getDistrict() + request.getDetailAddress());
        address.setIsDefault(request.getIsDefault());
        address.setUpdatedAt(LocalDateTime.now());

        addressMapper.update(address);
        return address;
    }

    @Override
    @Transactional
    public boolean deleteAddress(Long id, Long userId) {
        // 检查地址是否属于该用户
        if (addressMapper.checkOwnership(id, userId) == 0) {
            throw new RuntimeException("地址不存在或无权限访问");
        }

        return addressMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean setDefaultAddress(Long id, Long userId) {
        // 检查地址是否属于该用户
        if (addressMapper.checkOwnership(id, userId) == 0) {
            throw new RuntimeException("地址不存在或无权限访问");
        }

        // 先取消所有默认地址
        addressMapper.clearDefaultByUserId(userId);
        
        // 设置新的默认地址
        return addressMapper.setDefault(id) > 0;
    }

    @Override
    public Address getDefaultAddress(Long userId) {
        return addressMapper.findDefaultByUserId(userId);
    }
}