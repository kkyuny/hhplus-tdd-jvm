package io.hhplus.tdd.point;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PointService {
    private final List<UserPoint> userPoints;
    private final List<PointHistory> pointHistories;

    public PointService(List<UserPoint> userPoints, List<PointHistory> pointHistories) {
        this.userPoints = userPoints;
        this.pointHistories = pointHistories;
    }

    public UserPoint getUserPoint(long userId) {
        return userPoints.stream()
                .filter(up -> up.id() == userId)
                .findFirst()
                .orElse(UserPoint.empty(userId));
    }

    public List<PointHistory> getPointHistories(long userId) {
        return pointHistories.stream()
                .filter(ph -> ph.userId() == userId)
                .collect(Collectors.toList());
    }

    public UserPoint chargePoints(long userId, long amount) {
        Optional<UserPoint> optionalUserPoint = userPoints.stream()
                .filter(up -> up.id() == userId)
                .findFirst();

        UserPoint updatedUserPoint;
        if (optionalUserPoint.isPresent()) {
            UserPoint userPoint = optionalUserPoint.get();
            long newPoint = userPoint.point() + amount;
            updatedUserPoint = new UserPoint(userId, newPoint, System.currentTimeMillis());
            userPoints.remove(userPoint);
        } else {
            updatedUserPoint = new UserPoint(userId, amount, System.currentTimeMillis());
        }

        userPoints.add(updatedUserPoint);

        PointHistory history = new PointHistory(
                generateHistoryId(),
                userId,
                amount,
                TransactionType.CHARGE,
                System.currentTimeMillis()
        );
        pointHistories.add(history);

        return updatedUserPoint;
    }

    public UserPoint usePoints(long userId, long amount) {
        Optional<UserPoint> optionalUserPoint = userPoints.stream()
                .filter(up -> up.id() == userId)
                .findFirst();

        if (optionalUserPoint.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        UserPoint userPoint = optionalUserPoint.get();
        if (userPoint.point() < amount) {
            throw new IllegalArgumentException("Not enough points available");
        }

        long newPoint = userPoint.point() - amount;
        UserPoint updatedUserPoint = new UserPoint(userId, newPoint, System.currentTimeMillis());
        userPoints.remove(userPoint);
        userPoints.add(updatedUserPoint);

        PointHistory history = new PointHistory(
                generateHistoryId(),
                userId,
                amount,
                TransactionType.USE,
                System.currentTimeMillis()
        );
        pointHistories.add(history);

        return updatedUserPoint;
    }

    private long generateHistoryId() {
        return pointHistories.size() + 1;
    }
}
