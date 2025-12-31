package com.ddliang.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 宠物请求DTO
 */
public class PetRequest {

    @NotBlank(message = "宠物类型不能为空")
    @Pattern(regexp = "猫|狗", message = "宠物类型必须是猫或狗")
    private String petType;

    @NotBlank(message = "宠物名字不能为空")
    @Size(min = 1, max = 50, message = "宠物名字长度必须在1-50个字符之间")
    private String name;

    @Size(max = 100, message = "品种长度不能超过100个字符")
    private String breed;

    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "公|母|未知", message = "性别必须是公、母或未知")
    private String gender;

    private LocalDate birthDate;

    @Size(max = 50, message = "毛色描述长度不能超过50个字符")
    private String color;

    private BigDecimal weight;

    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    private String avatarUrl;

    @Size(max = 255, message = "性格描述长度不能超过255个字符")
    private String personality;

    private String notes;

    // Getter和Setter方法
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
}