package com.cyparty.laihui.utilities;

import com.alibaba.druid.sql.visitor.functions.If;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.beans.Transient;
import java.util.*;

/**
 * Created by zhu on 2016/12/6.
 */

@Component
public class TaskUtils {
    @Autowired
    AppDB appDB;
    private List<Order> orderList = new ArrayList<>();
    private int i = 0;
    private long first_time = 0;

    /**
     * 每天凌晨3点执行
     */
//    @Scheduled(cron="0 0 3 * * ?")
    @Scheduled(cron = "0/1 * * * * ?")
    public void getInfo() {
        try {
            //检测所有待支付订单是否超出支付时间（15分钟）
            if (i == 0) {
                first_time = Utils.getCurrenTimeStamp();
            }
            String current_time = Utils.getCurrentTimeSubOrAdd(-15);
            String where = " where order_type=0 and is_enable=1 and order_status=2 and update_time <= '" + current_time + "'";
            orderList.clear();
            orderList = appDB.getOrderReview(where, 0);
            for (Order order : orderList) {
                //乘客支付超时，该订单取消
                String update_sql = " set order_status=0 , update_time='" + Utils.getCurrentTime() + "' where order_id=" + order.getOrder_id() + " and order_type=0";
                appDB.update("pc_orders", update_sql);
                update_sql = " set order_status=0 where _id=" + order.getOrder_id();
                appDB.update("pc_passenger_publish_info", update_sql);
                update_sql = " set order_status=5,update_time='" + Utils.getCurrentTime() + "' where order_type=2 and order_status=1 and order_id=" + order.getOrder_id();
                appDB.update("pc_orders", update_sql);

                System.out.println("订单号为：" + order.getOrder_id() + "的订单支付超时，已成功处理！");
            }
            i++;
            long current_timeStamp = Utils.getCurrenTimeStamp();
            long last_time = (current_timeStamp - first_time) / 1000;//得到s
            if (last_time < 3600) {
                //小于一小时
                if (last_time % 3600 == 0) {
                    System.out.println("未检测到支付超时订单！" + Utils.getCurrentTime() + "项目运行时间：" + last_time / 60 + "分钟");
                }
            }
            if (last_time >= 3600 && last_time <= 3600 * 24) {
                //小于1天
                if (last_time % 3600 == 0) {
                    int hour = new Long(last_time / 3600).intValue();
                    System.out.println("未检测到支付超时订单！" + Utils.getCurrentTime() + "项目运行时间：" + hour + "小时");
                }
            }
            if (last_time >= 3600 * 24) {
                //大于一天
                if (last_time % 3600 == 0) {
                    int day = new Long(last_time / (3600 * 24)).intValue();
                    int now_seconds = new Long(last_time % (3600 * 24)).intValue();
                    int hour = now_seconds / 3600;
                    if (last_time % 3600 == 0) {
                        System.out.println("未检测到支付超时订单！" + Utils.getCurrentTime() + "项目运行时间：" + day + "天" + hour + "小时");
                    }
                }
            }
            //更新所有完成行程订单的状态
            String now_time = Utils.getCurrentTimeSubOrAddHour(-24);
            String is_complete_where = " where action_type=0 and order_status=1 and is_complete!=1 and departure_time <='" + now_time + "'";

            List<PayLog> payLogList = appDB.getPayLog(is_complete_where);
            for (PayLog order : payLogList) {
                String update_sql = " set order_status=4 where order_type=0 and order_status=3  and order_id=" + order.getOrder_id();
                appDB.update("pc_orders", update_sql);
                update_sql = " set order_status=3 where order_type=2 and order_status=2  and order_id=" + order.getOrder_id();
                appDB.update("pc_orders", update_sql);
                update_sql = " set is_complete=1 where _id=" + order.get_id();
                appDB.update("pay_cash_log", update_sql);
                System.out.println("已检测到行程默认完成订单：" + order.get_id());
            }
            //更新所有行程结束仍未完成拼车的车单状态
            String uncomplete_where = " a left join pc_passenger_publish_info b on a.order_id=b._id where a.order_status<3 and a.order_status>=0 and order_type=0 and b.departure_time<='" + Utils.getCurrentTime() + "'";
            orderList = appDB.getOrderReview(uncomplete_where, 2);
            for (Order order : orderList) {
                //乘客车单失效，该订单取消
                String update_sql = " set order_status=5 , update_time='" + Utils.getCurrentTime() + "' where order_id=" + order.getOrder_id() + " and order_type=0";
                appDB.update("pc_orders", update_sql);

                update_sql = " set order_status=5 , update_time='" + Utils.getCurrentTime() + "' where order_id=" + order.getOrder_id() + " and order_type=2 and order_status>=0 ";
                appDB.update("pc_orders", update_sql);

                update_sql = " set is_enable=0 where _id = " + order.getOrder_id();
                appDB.update("pc_passenger_publish_info", update_sql);

                System.out.println("订单号为：" + order.getOrder_id() + "的订单超时，已成功处理！");
            }
            now_time = Utils.getCurrentTimeSubOrAddHour(0);
            //处理APP端超时车单
            String driver_where = " where is_enable=1 and departure_time <='" + now_time + "'";
            List<DepartureInfo> departureInfoList = appDB.getAppDriverDpartureInfo(driver_where);
            for (DepartureInfo departureInfo : departureInfoList) {
                String select_sql = " where user_id=" + departureInfo.getUser_id() + " and is_enable=1 and order_status in(0,1,2)";
                orderList = appDB.getOrderReview(select_sql, 0);
                if (orderList.size() == 0) {
                    String update_sql = " set is_enable=0 where _id = " + departureInfo.getR_id();
                    appDB.update("pc_driver_publish_info", update_sql);
                    System.out.println("车主车单超时，已成功处理！");

                }
            }
            //处理pc端超时车单
            String passneger_time_over_where = " set is_enable=0 where user_id=-5 and is_enable=1 and departure_time <='" + now_time + "'";
            boolean is_success = appDB.update("pc_passenger_publish_info", passneger_time_over_where);
            if (is_success) {
                System.out.println("PC端乘客车单超时，已成功处理！");
            }

            String driver_time_over_where = " set is_enable=0 where is_enable=1 and user_id<0 and departure_time <='" + now_time + "'";
            is_success = appDB.update("pc_driver_publish_info", driver_time_over_where);
            if (is_success) {
                System.out.println("PC端车主车单超时，已成功处理！");
            }
            //处理乘客超时必达单
            uncomplete_where = " a left join pc_passenger_publish_info b on a.order_id=b._id where (a.order_status=200 or a.order_status=100) and order_type=0 and b.departure_time<='" + Utils.getCurrentTime() + "'";
            orderList = appDB.getOrderReview(uncomplete_where, 2);
            for (Order order : orderList) {
                //乘客车单失效，该订单取消
                String update_sql = " set order_status=-1 , update_time='" + Utils.getCurrentTime() + "' where order_id=" + order.getOrder_id() + " and order_type=0";
                appDB.update("pc_orders", update_sql);

                List<PassengerOrder> passengerDepartureInfo = appDB.getPassengerDepartureInfo(" where a._id=" + order.getOrder_id() + " and is_enable = 1");
                if (passengerDepartureInfo.size() > 0) {
                    PassengerOrder passengerPublishInfo = passengerDepartureInfo.get(0);
                    update_sql = " set is_del= 0 where order_no='" + passengerPublishInfo.getPay_num() + "'";
                    appDB.update("arrive_driver_relation", update_sql);
                    RefundsLog refundsLog = new RefundsLog();
                    refundsLog.setOutTradeNo(passengerPublishInfo.getTrade_no());
                    refundsLog.setRefundsTime(new Date());
                    refundsLog.setRefundsPrice(passengerPublishInfo.getPay_money()*2+5);
                    refundsLog.setUserId(passengerPublishInfo.getUser_id());
                    appDB.createPassengerRefunds(refundsLog);
                }

                update_sql = " set order_status=5 , update_time='" + Utils.getCurrentTime() + "' where order_id=" + order.getOrder_id() + " and order_type=2 and order_status>=0 ";
                appDB.update("pc_orders", update_sql);

                update_sql = " set is_enable=0 where _id = " + order.getOrder_id();
                appDB.update("pc_passenger_publish_info", update_sql);


                System.out.println("订单号为：" + order.getOrder_id() + "的乘客必达订单超时，已成功处理！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("我进入了主函数");
    }

}
