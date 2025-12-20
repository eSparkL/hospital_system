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
import com.shanzhu.hospital.common.R;
import com.shanzhu.hospital.common.Result;
import com.shanzhu.hospital.entity.po.Arrange;
import com.shanzhu.hospital.entity.po.Drug;
import com.shanzhu.hospital.entity.po.Orders;
import com.shanzhu.hospital.entity.vo.OrderArrangeVo;
import com.shanzhu.hospital.entity.vo.OrdersPageVo;
import com.shanzhu.hospital.mapper.ArrangeMapper;
import com.shanzhu.hospital.mapper.DrugMapper;
import com.shanzhu.hospital.mapper.OrderMapper;
import com.shanzhu.hospital.service.DrugService;
import com.shanzhu.hospital.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 挂号相关 服务层
 *
 */
@Service("OrderService")
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    private final OrderMapper orderMapper;

    private final ArrangeMapper arrangeMapper;

    private final DrugService drugService;
    private final DrugMapper drugMapper;

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
     * 查询挂号信息 - 分页
     *
     * @param pageNum  分页页数
     * @param pageSize 分页大小
     * @param query    查询条件
     * @return 挂号列表
     */
    @Override
    public OrdersPageVo findOrdersPages(Integer pageNum, Integer pageSize, String query) {
        //分页条件
        Page<Orders> page = new Page<>(pageNum, pageSize);

        //查询条件
        LambdaQueryWrapper<Orders> lambdaQuery = Wrappers.<Orders>lambdaQuery()
                .like(Orders::getPId, query);

        //分页查询
        IPage<Orders> iPage = this.page(page, lambdaQuery);

        //组装分页结果
        OrdersPageVo pageVo = new OrdersPageVo();
        pageVo.populatePage(iPage);

        return pageVo;
    }

    /**
     * 删除挂号单
     *
     * @param oId 挂号单id
     * @return 结果
     */
    @Override
    public Boolean deleteOrder(Integer oId) {
        return this.removeById(oId);
    }

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
    @Transactional(rollbackFor = Exception.class)
    public String updateOrder(Orders orders) {
        // orders.getODrug() 格式: "药物名:数量,药物名:数量"
        if (orders.getODrug() != null && !orders.getODrug().isEmpty()) {
            String[] drugItems = orders.getODrug().split(",");
            StringBuilder insufficient = new StringBuilder();

            for (String item : drugItems) {
                String[] arr = item.split(":");
                String drName = arr[0].trim();
                Integer quantity = Integer.parseInt(arr[1].trim());

                Drug drug = drugService.lambdaQuery().eq(Drug::getDrName, drName).one();
                if (drug == null) {
                    insufficient.append(drName).append("(不存在), ");
                    continue;
                }

                if (drug.getDrNumber() < quantity) {
                    insufficient.append(drName)
                            .append("(仅剩 ")
                            .append(drug.getDrNumber())
                            .append("), ");
                }
            }

            if (insufficient.length() > 0) {
                String msg = insufficient.substring(0, insufficient.length() - 2);
                return "提交失败：" + msg;
            }

            // 扣库存
            for (String item : drugItems) {
                String[] arr = item.split(":");
                String drName = arr[0].trim();
                Integer quantity = Integer.parseInt(arr[1].trim());
                Drug drug = drugService.lambdaQuery().eq(Drug::getDrName, drName).one();
                drug.setDrNumber(drug.getDrNumber() - quantity);
                drugService.updateById(drug);
            }
        }

        // 更新挂号单状态
        orders.setOState(1);
        orders.setOEnd(DateUtil.now());
        this.updateById(orders);

        return "更新挂号成功";
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
                //模糊匹配病患id
                .like(Orders::getPId, pid)
                //医生id
                .eq(Orders::getDId, dId)
                //状态1
                .eq(Orders::getOState, 1)
                //按状态降序
                .orderByDesc(Orders::getOState);

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
    public OrdersPageVo findOrderByDid(
            Integer pageNum, Integer pageSize, String pId, Integer dId
    ) {
        //分页条件
        Page<Orders> page = new Page<>(pageNum, pageSize);

        //查询条件
        LambdaQueryWrapper<Orders> lambdaQuery = Wrappers.<Orders>lambdaQuery()
                //模糊匹配病患id
                .like(Orders::getPId, pId)
                //医生id
                .eq(Orders::getDId, dId)
                //按状态降序
                .orderByDesc(Orders::getOState);

        //分页查询
        IPage<Orders> iPage = this.page(page, lambdaQuery);

        //组装结果
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
    public Boolean findTotalPrice(int oId) {
        Orders order = this.getById(oId);
        if (order.getOTotalPrice() != 0.00) {
            order.setOPriceState(0);

            this.updateById(order);
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
        //查询排班信息
        Arrange arrange = arrangeMapper.selectById(arId);

        OrderArrangeVo orderArrangeVo = new OrderArrangeVo();
        orderArrangeVo.setOrderDate(arrange.getArTime());

        return orderArrangeVo;
    }

    /**
     * 统计近20天科室人数
     *
     * @return 人数
     */
    @Override
    public List<String> countOrderSection() {
        LocalDate today = LocalDate.now();
        String startTime = today.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endTime = DateUtil.now(); // 当前时间

        return this.orderMapper.orderSection(startTime, endTime);
    }
    /**
     * 统计近今近10日挂号人数

     */
    @Override
    public List<Map<String, Object>> orderLast10Days() {
        return orderMapper.selectLast10Days(); // 直接返回数据
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderWithStockCheck(Orders orders) {
        if (orders.getODrug() != null && !orders.getODrug().isEmpty()) {
            String[] drugItems = orders.getODrug().split(",");
            // 用来收集库存不足的药物信息
            StringBuilder insufficient = new StringBuilder();

            for (String item : drugItems) {
                String[] arr = item.split(":");
                String drName = arr[0].trim();
                Integer quantity = Integer.parseInt(arr[1].trim());

                Drug drug = drugService.lambdaQuery().eq(Drug::getDrName, drName).one();
                if (drug == null) {
                    insufficient.append(drName).append("(不存在), ");
                    continue;
                }

                if (drug.getDrNumber() < quantity) {
                    insufficient.append(drName)
                            .append("(仅剩 ")
                            .append(drug.getDrNumber())
                            .append("), ");
                }
            }

            // 如果有不足药物，直接抛异常
            if (insufficient.length() > 0) {
                // 去掉最后的逗号和空格
                String msg = insufficient.substring(0, insufficient.length() - 2);
                throw new RuntimeException("提交失败：以下药物库存不足：" + msg);
            }

            // 扣库存
            for (String item : drugItems) {
                String[] arr = item.split(":");
                String drName = arr[0].trim();
                Integer quantity = Integer.parseInt(arr[1].trim());
                Drug drug = drugService.lambdaQuery().eq(Drug::getDrName, drName).one();
                drug.setDrNumber(drug.getDrNumber() - quantity);
                drugService.updateById(drug);
            }
        }

        // 更新挂号单状态
        orders.setOState(1);
        orders.setOEnd(DateUtil.now());
        this.updateById(orders);
    }



}
