package com.green.greengram;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUtils {
    // 파라미터 dateTime으로 넘어오는 값이 DB에 저장된 dateTime값
    public static void assertCurrentTimestamp(String dateTime) {
        // 자바에서 현재일시 데이터
        LocalDateTime expectedNow = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime actualNow = LocalDateTime.parse(dateTime, formatter);
        assertTrue(Duration.between(expectedNow, actualNow).getSeconds() <= 1);
    }
}
