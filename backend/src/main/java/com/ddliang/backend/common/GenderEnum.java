package com.ddliang.backend.common;

public enum GenderEnum {
    UNKNOWN(0, "未知"),
    MALE(1, "公"),
    FEMALE(2, "母");

    private final Integer code;
    private final String text;

    GenderEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public Integer getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public static String getText(Integer code) {
        if (code == null) return UNKNOWN.getText();
        for (GenderEnum gender : GenderEnum.values()) {
            if (gender.getCode().equals(code)) {
                return gender.getText();
            }
        }
        return UNKNOWN.getText();
    }

    public static Integer getCode(String text) {
        for (GenderEnum gender : GenderEnum.values()) {
            if (gender.getText().equals(text)) {
                return gender.getCode();
            }
        }
        return UNKNOWN.getCode();
    }
}