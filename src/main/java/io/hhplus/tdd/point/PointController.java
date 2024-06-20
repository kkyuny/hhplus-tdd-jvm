package io.hhplus.tdd.point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    @Autowired
    PointService pointService;

    // user point 조회
    @GetMapping("{id}")
    public Optional<UserPoint> point(
            @PathVariable long id
    ) {
        return pointService.getUserPoint(id);
    }

    // point history 조회
    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @PathVariable long id
    ) {
        return pointService.getPointHistories(id);
    }

    // point 충전
    @PatchMapping("{id}/charge")
    public UserPoint charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        return pointService.chargePoints(id, amount);
    }

    // point 사용
    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        return pointService.usePoints(id, amount);
    }
}
