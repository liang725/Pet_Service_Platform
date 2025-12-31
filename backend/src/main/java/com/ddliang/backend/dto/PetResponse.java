package com.ddliang.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 宠物响应DTO
 */
public class PetResponse {
    private Integer id;
    private Integer userId;
    private String petType;
    private String name;
    private String breed;
    private String gender;
    private LocalDate birthDate;
    private String color;
    private BigDecimal weight;
    private String avatarUrl;
    private String personality;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 计算年龄
    public String getAge() {
        if (birthDate == null) {
            return "未知";
        }

        LocalDate now = LocalDate.now();
        long years = java.time.temporal.ChronoUnit.YEARS.between(birthDate, now);
        long months = java.time.temporal.ChronoUnit.MONTHS.between(birthDate, now) % 12;

        if (years == 0) {
            return months + "个月";
        } else if (months == 0) {
            return years + "岁";
        } else {
            return years + "岁" + months + "个月";
        }
    }

    // 获取宠物图标
    public String getPetIcon() {
        if ("狗".equals(petType)) {
            return "🐕";
        } else if ("猫".equals(petType)) {
            return "🐈";
        } else {
            return "🐾";
        }
    }

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}