package com.shanzhu.hospital.service.serviceImpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shanzhu.hospital.entity.po.Arrange;
import com.shanzhu.hospital.entity.po.Orders;
import com.shanzhu.hospital.entity.vo.OrderArrangeVo;
import com.shanzhu.hospital.entity.vo.OrdersPageVo;
import com.shanzhu.hospital.mapper.ArrangeMapper;
import com.shanzhu.hospital.mapper.OrderMapper;
import com.shanzhu.hospital.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 挂号相关 服务层
 *
 */
@Service("OrderService")
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    private final OrderMapper orderMapper;

    private final ArrangeMapper arrangeMapper;

    /**
     * 添加挂号单
     *
     * @param order 挂号单信息
     * @param arId  排班id
     * @return 结果
     */
    @Override
    public Boolean addOrder(Orders order, String arId) {
        String oStart = order.getOStart().substring(0, 22);
        //查询患者当前时间段是否有未诊断的挂号单
        List<Orders> existOrders = lambdaQuery()
                //患者id
                .eq(Orders::getPId, order.getPId())
                //挂号时间段
                .eq(Orders::getOStart, oStart)
                //未诊断
                .eq(Orders::getOState, 0)
                .list();

        //存在未诊断的挂号单
        if(CollUtil.isNotEmpty(existOrders)){
            return Boolean.FALSE;
        }

        //挂号单信息
        order.setOId(RandomUtil.randomInt(1000, 9999));
        order.setOState(0);
        order.setOPriceState(0);
        order.setOStart(oStart);

        //保存挂号单
        return this.save(order);

    }

    /**
     * 查询病患挂号
     *
     * @param pId 病患id
     * @return 挂号信息
     */
    @Override
    public List<Orders> findOrderByPid(Integer pId) {
        return orderMapper.findOrderByPid(pId);
    }

    /**
     * 查询挂号
     *
     * @param oId 挂号单id
     * @return 挂号信息
     */
    @Override
    public Orders findOrderByOid(Integer oId) {
        return orderMapper.findOrderByOid(oId);
    }

    /**
     * 查看当天挂号
     *
     * @param dId    医生id
     * @param oStart 日期时间
     * @return 挂号数据
     */
    @Override
    public List<Orders> findOrderByNull(Integer dId, String oStart) {
        return orderMapper.findOrderByNull(dId, oStart);
    }

    /**
     * 更新挂号单
     *
     * @param orders 挂号单信息
     * @return 结果
     */
    @Override
    public Boolean updateOrder(Orders orders) {
        //设置挂号单状态
        orders.setOState(1);

        //设置挂号单结束时间
        orders.setOEnd(DateUtil.now());

        //更新
        return this.updateById(orders);
    }

    /**
     * 缴费
     *
     * @param oId 挂号单id
     * @return 结果
     */
    @Override
    public Boolean updatePrice(Integer oId) {
        return lambdaUpdate()
                //更新缴费状态1
                .set(Orders::getOPriceState, 1)
                //更新费用0
                .set(Orders::getOTotalPrice, 0.00)
                .update();
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
    @Override
    public OrdersPageVo findOrderFinish(
            Integer pageNum, Integer pageSize, String pid, Integer dId
    ) {
        //分页条件
        Page<Orders> page = new Page<>(pageNum, pageSize);

        //查询条件
        LambdaQueryWrapper<Orders> lambdaQuery = Wrappers.<Orders>lambdaQuery()
                //挂号单结束时间不为空
                .isNotNull(Orders::getOEnd)
                //病患id
                .eq(Orders::getPId, pid)
                //医生id
                .eq(dId != null, Orders::getDId, dId)
                //按照挂号单结束时间倒序
                .orderByDesc(Orders::getOEnd);

        //分页查询
        IPage<Orders> iPage = this.page(page, lambdaQuery);

        //组装结果
        OrdersPageVo pageVo = new OrdersPageVo();
        pageVo.populatePage(iPage);

        return pageVo;
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
@Override
public OrdersPageVo findOrderByDid(
        Integer pageNum, Integer pageSize, String pId, Integer dId
) {
    if (dId == null) {
        throw new IllegalArgumentException("医生ID不能为空");
    }

    Page<Orders> page = new Page<>(pageNum, pageSize);

    LambdaQueryWrapper<Orders> lambdaQuery = Wrappers.<Orders>lambdaQuery()
            .eq(Orders::getDId, dId)
            .orderByAsc(Orders::getOStart);

    // 如果 query 不为空，则加上 pId 条件
    if (pId != null && !pId.trim().isEmpty()) {
        try {
            Integer pIdInt = Integer.parseInt(pId);
            lambdaQuery.eq(Orders::getPId, pIdInt);
        } catch (NumberFormatException e) {
            log.warn("病患ID格式错误: {}");
            return new OrdersPageVo(); // 返回空结果
        }
    }

    IPage<Orders> iPage = this.page(page, lambdaQuery);

    OrdersPageVo pageVo = new OrdersPageVo();
    pageVo.populatePage(iPage);

    return pageVo;
}


    /**
     * 统计挂号人数
     *
     * @return 人数
     * @Param oStart 时间
     */
    @Override
    public Integer countOrderPeople(String oStart) {
        return orderMapper.orderPeople(oStart);
    }

    /**
     * 统计今天某个医生挂号人数
     *
     * @param oStart 时间
     * @param dId    医生id
     * @return 人数
     */
    @Override
    public Integer countOrderPeopleByDid(String oStart, Integer dId) {
        return orderMapper.orderPeopleByDid(oStart, dId);
    }

    /**
     * 统计挂号男女人数
     *
     * @return 人数
     */
    public List<String> countOrderGender() {
        return orderMapper.orderGender();
    }

    /**
     * 增加诊断及医生意见
     *
     * @param order 挂号单信息
     * @return 结果
     */
    public Boolean updateOrderByAdd(Orders order) {
        if (orderMapper.updateOrderByAdd(order) == 0) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    /**
     * 判断诊断后购买药物是否已经缴费
     *
     * @param oId 挂号单id
     * @return 结果
     */
    @Override
    public Boolean findTotalPrice(int oId) {
        Orders orders = lambdaQuery()
                //挂号单id
                .eq(Orders::getOId, oId)
                //费用大于0
                .gt(Orders::getOTotalPrice, 0)
                .one();

        //存在未缴费订单
        if (orders != null) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    /**
     * 获取挂号时间段
     *
     * @param arId 排班id
     * @return 时间段
     */
    @Override
    public OrderArrangeVo findOrderTime(String arId) {
        //通过排班id查询排班信息
        Arrange arrange = this.arrangeMapper.selectById(arId);

        //组装数据
        OrderArrangeVo vo = new OrderArrangeVo();
        vo.setOrderDate(arrange.getArTime());

        return vo;
    }

    /**
     * 统计近20天科室人数
     *
     * @return 人数
     */
    public List<String> countOrderSection() {
        //计算20天前的时间
        String endTime = DateUtil.format(DateUtil.date(), DatePattern.NORM_DATE_PATTERN);
        String startTime = DateUtil.format(DateUtil.offsetDay(DateUtil.date(), -20), DatePattern.NORM_DATE_PATTERN);

        return orderMapper.orderSection(startTime, endTime);
    }

    /**
     * 查询指定日期的所有预约
     * 
     * @param date 日期 (格式: yyyy-MM-dd)
     * @return 预约列表
     */
    @Override
    public List<Orders> getOrdersByDate(String date) {
        // 使用 LambdaQueryWrapper 查询指定日期的预约
        return lambdaQuery()
                // 使用 LIKE 匹配日期部分 (假设 oStart 字段存储的是日期时间字符串)
                .like(Orders::getOStart, date)
                // 确保只查询未完成的预约
                .eq(Orders::getOState, 0)
                .list();
    }
    
    /**
     * 分页查询挂号信息
     * 
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param query 查询条件
     * @return 挂号信息分页结果
     */
    @Override
    public OrdersPageVo findOrdersPages(Integer pageNum, Integer pageSize, String query) {
        //分页条件
        Page<Orders> page = new Page<>(pageNum, pageSize);

        //查询条件
        LambdaQueryWrapper<Orders> lambdaQuery = Wrappers.<Orders>lambdaQuery()
                .like(Orders::getOId, query);

        //分页查询
        IPage<Orders> iPage = this.page(page, lambdaQuery);

        //组装结果
        OrdersPageVo pageVo = new OrdersPageVo();
        pageVo.populatePage(iPage);

        return pageVo;
    }
    
    /**
     * 删除挂号单
     * 
     * @param oId 挂号单ID
     * @return 删除结果
     */
    @Override
    public Boolean deleteOrder(Integer oId) {
        return this.removeById(oId);
    }
}