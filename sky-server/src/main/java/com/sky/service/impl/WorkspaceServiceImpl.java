package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    /**
     * 查询今日运行数据
     * @return
     */
    @Override
    public BusinessDataVO getbusinessData() {
        LocalDate now = LocalDate.now();
        LocalDateTime beginTime = LocalDateTime.of(now, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(now, LocalTime.MAX);
        Map map=new HashMap<>();
        map.put("begin",beginTime);
        map.put("end",endTime);
        Integer newUsers=userMapper.countByMap(map); //新增用户数
        Integer totalOrderCount=orderMapper.OrderCountByMap(map);
        map.put("status", Orders.COMPLETED);
        Integer validOrderCount=orderMapper.OrderCountByMap(map);//有效订单数

        Double orderCompletionRate=0.0;
        if(totalOrderCount!=0){
            orderCompletionRate=validOrderCount.doubleValue()/totalOrderCount;//订单完成率
        }

        Double turnover=orderMapper.sumByMap(map);//营业额

        Double unitPrice=0.0;
        if(validOrderCount!=0){
            unitPrice=turnover/validOrderCount;
            unitPrice=roundToTwoDecimalPlaces(unitPrice);
        }

        return BusinessDataVO.builder()
                .newUsers(newUsers)
                .orderCompletionRate(orderCompletionRate)
                .turnover(turnover)
                .unitPrice(unitPrice)
                .validOrderCount(validOrderCount)
                .build();
    }

    /**
     * 查询订单管理数据
     * @return
     */
    @Override
    public OrderOverViewVO getOverviewOrders() {
        Integer allOrders=orderMapper.getStatistics(null);
        Integer cancelledOrders=orderMapper.getStatistics(Orders.CANCELLED);
        Integer completedOrders=orderMapper.getStatistics(Orders.COMPLETED);
        Integer deliveredOrders=orderMapper.getStatistics(Orders.DELIVERY_IN_PROGRESS);
        Integer waitingOrders=orderMapper.getStatistics(Orders.TO_BE_CONFIRMED);
        return OrderOverViewVO.builder()
                .allOrders(allOrders)
                .cancelledOrders(cancelledOrders)
                .completedOrders(completedOrders)
                .deliveredOrders(deliveredOrders)
                .waitingOrders(waitingOrders)
                .build();
    }

    /**
     * 查询菜品总览
     * @return
     */
    @Override
    public DishOverViewVO getOverviewDishes() {
        Integer sold = dishMapper.getStatistics(StatusConstant.ENABLE);
        Integer discontinued = dishMapper.getStatistics(StatusConstant.DISABLE);
        return DishOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }

    /**
     * 查询套餐总览
     * @return
     */
    @Override
    public SetmealOverViewVO getOverviewSetmeals() {
        Integer sold = setmealMapper.getStatistics(StatusConstant.ENABLE);
        Integer discontinued = setmealMapper.getStatistics(StatusConstant.DISABLE);
        return SetmealOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }

    /**
     * 保留double类型数据小数位为两位
     * @param value
     * @return
     */
    public static double roundToTwoDecimalPlaces(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
