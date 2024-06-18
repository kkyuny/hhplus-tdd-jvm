package io.hhplus.tdd;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PointServiceTest {
    private PointService pointService;
    private List<UserPoint> userPoints;
    private List<PointHistory> pointHistories;

    @BeforeEach
    void setUp() {
        userPoints = new ArrayList<>();
        pointHistories = new ArrayList<>();
        pointService = new PointService(userPoints, pointHistories);
    }

    /* 테스트 케이스˚
     1. 성공 케이스
      1) user point 조회 -> user 있음
      2) point 충전 -> user 있음
      3) point 사용 -> user 있고, 사용 point < 기존 point

     2. 실패 케이스
      1) user point 조회 -> user 없음
      2) point 충전 -> user 없음
      3) point 사용 -> (1) user 없거나 (2) user 있지만 사용 point > 기존 point
     */

    @DisplayName("1-1 case")
    @Test
    void testGetUserPoint() {
        // user point 조회 -> user 있음
        // Given
        long userId = 1L;
        long point = 100L;
        UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());
        userPoints.add(userPoint);

        // When
        UserPoint result = pointService.getUserPoint(userId);

        // Then
        assertEquals(userId, result.id());
        assertEquals(point, result.point());
    }

    @DisplayName("2-1 case")
    @Test
    void testGetUserPointNotExistUser() {
        // user point 조회 -> user 없음
        // Given
        long userId = 2L;
        long point = 100L;

        // When
        UserPoint result = pointService.getUserPoint(userId);

        // Then
        assertEquals(userId, result.id());
        assertEquals(point, result.point());
    }

    @DisplayName("1-2 case")
    @Test
    void testChargePoints() {
        // point 충전 -> user 있음
        // Given
        long userId = 3L;
        long point = 100L;
        long amount = 100L;

        UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());
        userPoints.add(userPoint);

        // When
        UserPoint chargeResult = pointService.chargePoints(userId, amount);
        List<PointHistory> historyResult = pointService.getPointHistories(userId);

        // Then
        assertEquals(point + amount, chargeResult.point());
        assertFalse(historyResult.isEmpty());
    }

    @DisplayName("2-2 case")
    @Test
    void testChargePointsNotExistUser() {
        // point 충전 -> user 없음
        // Given
        long userId = 4L;
        long point = 100L;
        long amount = 100L;

        // When
        UserPoint chargeResult = pointService.chargePoints(userId, amount);

        // Then
        assertEquals(point + amount, chargeResult.point());
    }

    @DisplayName("1-3 case")
    @Test
    void testUsePoints() {
        // point 사용 -> user 있고, 사용 point < 기존 point
        // Given
        long userId = 5L;
        long point = 200L;
        long amount = 100L;

        UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());
        userPoints.add(userPoint);

        // When
        UserPoint chargeResult = pointService.usePoints(userId, amount);
        List<PointHistory> historyResult = pointService.getPointHistories(userId);

        // Then
        assertEquals(point - amount, chargeResult.point());
        assertFalse(historyResult.isEmpty());
    }

    @DisplayName("2-3-1 case")
    @Test
    void testUsePointsNotExistUser() {
        // point 사용 -> (1) user 없거나
        // Given
        long userId = 6L;
        long point = 200L;
        long amount = 100L;
//d
        // When
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            pointService.usePoints(userId, amount);
        });

        // Then
        assertEquals("User not found", exception.getMessage());
    }

    @DisplayName("2-3-2 case")
    @Test
    void testUsePointsOverAmount() {
        // point 사용 -> (2) user 있지만 사용 point > 기존 point
        // Given
        long userId = 7L;
        long point = 200L;
        long amount = 300L;

        UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());
        userPoints.add(userPoint);

        // When
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            pointService.usePoints(userId, amount);
        });

        // Then
        assertEquals("Not enough points available", exception.getMessage());
    }
}
