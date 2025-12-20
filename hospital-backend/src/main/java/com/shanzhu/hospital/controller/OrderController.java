package com.shanzhu.hospital.controller;

import cn.hutool.core.date.DateUtil;
import com.shanzhu.hospital.common.R;
import com.shanzhu.hospital.entity.po.Orders;
import com.shanzhu.hospital.entity.vo.OrderArrangeVo;
import com.shanzhu.hospital.entity.vo.OrdersPageVo;
import com.shanzhu.hospital.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 挂号 控制层
 *
 */
@RestController
@RequestMapping("order")
@RequiredArgsConstructor
@Tag(name = "挂号接口", description = "挂号相关操作接口")
public class OrderController {

    private final OrderService orderService;

    /**
     * 更新挂号单
     *
     * @param orders 挂号单信息
     * @return 结果
     */
    @PostMapping("updateOrder")
    @Operation(summary = "更新挂号单", description = "更新挂号单信息")
    public R<String> updateOrder(@RequestBody Orders orders) {
        String resultMsg = orderService.updateOrder(orders);
        if (resultMsg.startsWith("更新挂号成功")) {
            return R.ok(resultMsg);
        } else {
            return R.error(resultMsg);
        }
    }



    /**
     * 缴费
     *
     * @param oId 挂号单id
     * @return 结果
     */
    @RequestMapping("updatePrice")
    @Operation(summary = "挂号单缴费", description = "为指定挂号单进行缴费操作")
    public R<Boolean> updatePrice(@Parameter(description = "挂号单ID") Integer oId) {
        if (BooleanUtils.isTrue(orderService.updatePrice(oId))) {
            return R.ok("缴费成功");
        }

        return R.ok("缴费失败");
    }

    /**
     * 查询已完结挂号单
     *
     * @param pageNum  分页页数
     * @param pageSize 分页大小
     * @param pid      病患id
     * @param dId      医生id
     * @return 挂号单
     */
    @RequestMapping("findOrderFinish")
    @Operation(summary = "查询已完结挂号单", description = "根据病患ID和医生ID查询已完结的挂号单")
    public R<OrdersPageVo> findOrderFinish(
            @Parameter(description = "页码") @RequestParam(value = "pageNumber") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "size") Integer pageSize,
            @Parameter(description = "病患ID") @RequestParam(value = "query") String pid,
            @Parameter(description = "医生ID") @RequestParam(value = "dId") Integer dId
    ) {
        return R.ok(orderService.findOrderFinish(pageNum, pageSize, pid, dId));
    }

    /**
     * 根据dId查询挂号
     *
     * @param pageNum  分页页数
     * @param pageSize 分页大小
     * @param pId      病患id
     * @param dId      医生id
     * @return 挂号单
     */
    @RequestMapping("findOrderByDid")
    @Operation(summary = "根据医生ID查询挂号", description = "根据医生ID和病患ID查询挂号信息")
    public R<OrdersPageVo> findOrderByDid(
            @Parameter(description = "页码") @RequestParam(value = "pageNumber") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "size") Integer pageSize,
            @Parameter(description = "病患ID") @RequestParam(value = "query") String pId,
            @Parameter(description = "医生ID") @RequestParam(value = "dId") Integer dId
    ) {
        return R.ok(orderService.findOrderByDid(pageNum, pageSize, pId, dId));
    }

    /**
     * 统计今天挂号人数
     *
     * @return 人数
     */
    @RequestMapping("orderPeople")
    @Operation(summary = "统计今日挂号人数", description = "统计今日所有科室的挂号总人数")
    public R<Integer> countOrderPeople() {
        return R.ok(orderService.countOrderPeople(DateUtil.today()));
    }

    /**
     * 统计今天某个医生挂号人数
     *
     * @param dId 医生id
     * @return 人数
     */
    @RequestMapping("orderPeopleByDid")
    @Operation(summary = "统计医生今日挂号人数", description = "统计指定医生今日的挂号人数")
    public R<Integer> countOrderPeopleByDid(@Parameter(description = "医生ID") Integer dId) {
        return R.ok(orderService.countOrderPeopleByDid(DateUtil.today(), dId));
    }

    /**
     * 统计挂号男女人数
     *
     * @return 人数
     */
    @RequestMapping("orderGender")
    @Operation(summary = "统计挂号患者性别分布", description = "统计所有挂号患者中的男女人数")
    public R<List<String>> countOrderGender() {
        return R.ok(orderService.countOrderGender());
    }

//    /**
//     * 增加诊断及医生意见
//     *
//     * @param order 挂号单信息
//     * @return 结果
//     */
//    @PostMapping("updateOrderByAdd")
//    @Operation(summary = "增加诊断和医生意见", description = "为挂号单添加诊断结果和医生意见")
//    public R<Boolean> updateOrderByAdd(@Parameter(description = "挂号单信息对象") @RequestBody Orders order) {
//        if (orderService.updateOrderByAdd(order)) {
//            return R.ok("增加诊断及医生意见成功");
//        }
//
//        return R.error("增加诊断及医生意见失败");
//    }

    /**
     * 判断诊断后购买药物是否已经缴费
     *
     * @param oId 挂号单id
     * @return 结果
     */
    @RequestMapping("findTotalPrice")
    @Operation(summary = "检查药物是否已缴费", description = "判断诊断后的药物是否已经完成缴费")
    public R<Boolean> findTotalPrice(@Parameter(description = "挂号单ID") Integer oId) {
        if (BooleanUtils.isTrue(orderService.findTotalPrice(oId))) {
            return R.ok("未缴费");
        }

        return R.error("无需缴费");
    }

    /**
     * 获取挂号时间段
     *
     * @param arId 排班id
     * @return 时间段
     */
    @RequestMapping("findOrderTime")
    @Operation(summary = "获取挂号时间段", description = "根据排班ID获取可用的挂号时间段")
    public R<OrderArrangeVo> findOrderTime(@Parameter(description = "排班ID") String arId) {
        return R.ok(orderService.findOrderTime(arId));
    }

    /**
     * 统计近今天各科室人数
     *
     * @return 人数
     */
    @RequestMapping("orderSection")
    @Operation(summary = "统计近20天各科室人数", description = "统计最近20天内各科室的挂号人数统计")
    public R<List<String>> countOrderSection() {
        return R.ok(orderService.countOrderSection());
    }

    /**
     * 统计近十天挂号人数
     *
     * @return 人数
     */
    @GetMapping("/orderLast10Days")
    public R<List<Map<String, Object>>> orderLast10Days() {
        return R.ok(orderService.orderLast10Days());
    }
}
