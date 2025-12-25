package com.ddliang.backend.service;

import com.ddliang.backend.entity.Address;
import com.ddliang.backend.dto.AddressRequest;

import java.util.List;

public interface AddressService {

    /**
     * 获取用户的地址列表
     */
    List<Address> getUserAddresses(Long userId);

    /**
     * 根据ID获取地址
     */
    Address getAddressById(Long id, Long userId);

    /**
     * 添加地址
     */
    Address addAddress(Long userId, AddressRequest request);

    /**
     * 更新地址
     */
    Address updateAddress(Long id, Long userId, AddressRequest request);

    /**
     * 删除地址
     */
    boolean deleteAddress(Long id, Long userId);

    /**
     * 设置默认地址
     */
    boolean setDefaultAddress(Long id, Long userId);

    /**
     * 获取用户的默认地址
     */
    Address getDefaultAddress(Long userId);
}