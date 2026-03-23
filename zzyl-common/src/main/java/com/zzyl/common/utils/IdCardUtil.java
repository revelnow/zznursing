package com.zzyl.common.utils;

import java.time.LocalDate;
import java.time.Period;

public class IdCardUtil {

    /**
     * 根据身份证号计算周岁
     * @param idCard 身份证号（15位或18位）
     * @return 年龄（int）
     */
    public static int getAgeByIdCard(String idCard) {
        if (idCard == null || idCard.trim().isEmpty()) {
            throw new IllegalArgumentException("身份证号不能为空");
        }

        String card = idCard.trim();
        LocalDate birthDate;

        if (card.length() == 18) {
            // 18位身份证：第7-14位是出生日期 yyyyMMdd
            birthDate = parseBirth(card.substring(6, 14));
        } else if (card.length() == 15) {
            // 15位身份证：第7-12位是出生日期 yyMMdd，默认补19
            birthDate = parseBirth("19" + card.substring(6, 12));
        } else {
            throw new IllegalArgumentException("身份证号长度不正确");
        }

        LocalDate today = LocalDate.now();
        if (birthDate.isAfter(today)) {
            throw new IllegalArgumentException("身份证出生日期非法");
        }

        return Period.between(birthDate, today).getYears();
    }

    private static LocalDate parseBirth(String yyyymmdd) {
        try {
            int year = Integer.parseInt(yyyymmdd.substring(0, 4));
            int month = Integer.parseInt(yyyymmdd.substring(4, 6));
            int day = Integer.parseInt(yyyymmdd.substring(6, 8));
            return LocalDate.of(year, month, day);
        } catch (Exception e) {
            throw new IllegalArgumentException("身份证出生日期解析失败: " + yyyymmdd, e);
        }
    }
}
