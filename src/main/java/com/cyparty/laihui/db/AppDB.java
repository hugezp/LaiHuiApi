package com.cyparty.laihui.db;


import com.cyparty.laihui.domain.*;
import com.cyparty.laihui.mapper.*;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.alibaba.druid.support.console.Option.SQL;

/**
 * Created by zhu on 2015/12/29.
 */
public class AppDB {
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplateObject;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    public void procedureUpdateUser(String transaction_name, String mobile, int status, String name, String idsn, int id, String token, String ip,int source,String ip_location,int p_id) {
        String SQL = "call " + transaction_name;
        SQL = SQL + "('" + mobile + "'," + status + ",'" + name + "','" + idsn + "'" + "," + id + ",'" + token + "','" + ip + "',"+source+",'"+ip_location+"',"+p_id+")";
        jdbcTemplateObject.execute(SQL);
    }

    public void procedureValidateCarOwner(int now_status, CarOwnerInfo carOwnerInfo) {
        String SQL = "call create_car_validate";
        String where = "(" + now_status + "," + carOwnerInfo.getUser_id() + ",'" + carOwnerInfo.getCar_owner_name() + "','" + carOwnerInfo.getIdsn() + "','" + carOwnerInfo.getPic_licence() + "','" + carOwnerInfo.getCar_id() + "','" + carOwnerInfo.getCar_owner() + "','" + carOwnerInfo.getCar_brand() + "','" + carOwnerInfo.getCar_type() + "','" + carOwnerInfo.getCar_color() + "','" + carOwnerInfo.getCar_reg_date() + "','" + carOwnerInfo.getPic_licence2() + "','" + carOwnerInfo.getReason() + "')";
        SQL = SQL + where;
        jdbcTemplateObject.execute(SQL);
    }

    /**
     * 得到同一手机号2个小时之内发送验证码次数
     */

    public int getSendCodeTimes(String mobile) {
        String sql = "SELECT count(*)total FROM pc_sms_code where mobile=? and create_time>'" + Utils.getCurrentTimeSubOrAdd(-120) + "'";
        //int count=jdbcTemplateObject.queryForInt(sql);
        Map<String, Object> now = jdbcTemplateObject.queryForMap(sql, new Object[]{mobile});
        int total = Integer.parseInt(String.valueOf((long) now.get("total")));
        return total;
    }

    //
    public void createSMS(String mobile, String code) {
        String SQL = "insert into pc_sms_code(mobile,code,create_time) VALUES (?,?,?)";
        jdbcTemplateObject.update(SQL, new Object[]{mobile, code, Utils.getCurrentTime()});
    }

    public void createUserToken(String token, int id) {
        String SQL = "insert into pc_user_token(token,user_id,create_time,update_time) VALUES (?,?,?,?)";
        jdbcTemplateObject.update(SQL, new Object[]{token, id, Utils.getCurrentTime(), Utils.getCurrentTime()});
    }

    public String getUserTokenByID(int id) {
        String SQL = "SELECT  * from pc_user_token  where _id=?";
        Map<String, Object> now = jdbcTemplateObject.queryForMap(SQL, new Object[]{id});
        String token = (String) now.get("token");
        return token;
    }

    public List<Code> getSMS(String where) {
        String SQL = "SELECT * FROM pc_sms_code " + where + " ORDER BY create_time DESC ";
        List<Code> codeList = jdbcTemplateObject.query(SQL, new CodeMapper());
        return codeList;
    }

    //创建用户
    public List<User> getUserList(String where) {
        String SQL = "SELECT * FROM pc_user "+where ;
        List<User> userList = jdbcTemplateObject.query(SQL, new UserMapper());
        return userList;
    }

    //得到token对应的id
    public int getIDByToken(String token) {
        String SQL = "SELECT user_id from pc_user_token where token=?";
        List<UserToken> userTokenList =jdbcTemplateObject.query(SQL,new Object[]{token}, new UserIDMapper());
        int id=0;
        if(userTokenList.size()>0){
            id=userTokenList.get(0).getUser_id();
        }
        return id;
    }

    public List<CarOwnerInfo> getCarOwnerInfo1(String where) {
        String SQL = "SELECT * FROM pc_car_owner_info a , pc_user b where a.user_id=b._id " + where;
        List<CarOwnerInfo> carOwnerInfoList = jdbcTemplateObject.query(SQL, new CarOwnerMapper());
        return carOwnerInfoList;
    }
    public List<CarOwnerInfo> getCarOwnerInfo(String where) {
        String SQL = "SELECT * FROM pc_car_owner_info a left join pc_user b on a.user_id=b._id " + where;
        List<CarOwnerInfo> carOwnerInfoList = jdbcTemplateObject.query(SQL, new CarOwnerMapper());
        return carOwnerInfoList;
    }


    public List<CarTypeData> getCarTypeData(String where) {
        String SQL = "SELECT * FROM pc_car_type " + where;
        List<CarTypeData> carTypeDataList = jdbcTemplateObject.query(SQL, new CarTypeMapper());
        return carTypeDataList;
    }

    public int createPCHDeparture(final DepartureInfo departureInfo,final int source) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int autoIncId = 0;

        jdbcTemplateObject.update(con -> {
            String sql = "insert into pc_driver_publish_info(mobile,departure_time,init_seats,create_time,is_enable,user_id,boarding_point,breakout_point,departure_city_code,destination_city_code,departure_address_code,destination_address_code,source,current_seats,remark) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, departureInfo.getMobile());
            ps.setString(2, departureInfo.getStart_time());
            ps.setInt(3, departureInfo.getInit_seats());
            ps.setString(4, Utils.getCurrentTime());
            ps.setInt(5, 1);
            ps.setInt(6, departureInfo.getUser_id());
            ps.setString(7,departureInfo.getBoarding_point());
            ps.setString(8,departureInfo.getBreakout_point());
            ps.setInt(9,departureInfo.getDeparture_city_code());
            ps.setInt(10,departureInfo.getDestination_city_code());
            ps.setInt(11,departureInfo.getDeparture_address_code());
            ps.setInt(12,departureInfo.getDestination_address_code());
            ps.setInt(13,source);
            ps.setInt(14, departureInfo.getCurrent_seats());
            ps.setString(15, departureInfo.getRemark());
            return ps;
        }, keyHolder);

        autoIncId = keyHolder.getKey().intValue();
        return autoIncId;
    }

    public int getTotalCount(String table, String where) {
        String sql = "SELECT count(*)total FROM  " + table + " " + where;
        //int count=jdbcTemplateObject.queryForInt(sql);
        Map<String, Object> now = jdbcTemplateObject.queryForMap(sql);
        int total = Integer.parseInt(String.valueOf((long) now.get("total")));
        return total;
    }

    public boolean update(String table_name, String where) {
        boolean is_success = true;
        String SQL = "update  " + table_name + where;
        int count = jdbcTemplateObject.update(SQL);
        if (count < 1) {
            is_success = false;
        }
        return is_success;
    }

    public boolean delete(String table_name, String where) {
        boolean is_success = true;
        String SQL = "DELETE  FROM " + table_name + where;
        int count = jdbcTemplateObject.update(SQL);
        if (count < 1) {
            is_success = false;
        }
        return is_success;
    }


    public List<Tag> getTags(String where) {
        String SQL = "SELECT * FROM pc_departure_tags " + where;
        List<Tag> tags = jdbcTemplateObject.query(SQL, new TagMapper());
        return tags;
    }


    public List<PassengerOrder> getPassengerDepartureInfo(String where) {
        String SQL = "SELECT * FROM pc_passenger_publish_info a left join pc_user b on a.user_id=b._id " + where;
        List<PassengerOrder> passengerPublishInfoList = jdbcTemplateObject.query(SQL, new PassengerPublishInfoMapper());
        return passengerPublishInfoList;
    }

    public int getCount(String table, String where) {
        String sql = "SELECT count(*)total FROM  " + table + where;
        //int count=jdbcTemplateObject.queryForInt(sql);
        Map<String, Object> now = jdbcTemplateObject.queryForMap(sql);
        int total = Integer.parseInt(String.valueOf((long) now.get("total")));
        return total;
    }


    //创建司机预定乘客

    public boolean createDriverBookingPassenger(DriverBookingPassenger dbp) {
        boolean is_success = true;
        String SQL = "insert into pc_driver_booking_passenger(driver_id,driver_order_id,passenger_order_id,status,create_time,booking_seats) VALUES (?,?,?,?,?,?)";
        int count = jdbcTemplateObject.update(SQL, new Object[]{dbp.getDriver_id(), dbp.getDriver_order_id(), dbp.getPassenger_order_id(), 0, Utils.getCurrentTime(), dbp.getBooking_seats()});
        if (count < 1) {
            is_success = false;
        }
        return is_success;
    }

    public List<DriverBookingPassenger> getDriverBookingPassenger(String where) {
        String SQL = "SELECT * FROM pc_driver_booking_passenger " + where;
        List<DriverBookingPassenger> passengerOrders = jdbcTemplateObject.query(SQL, new DrvierBookingPassengerMapper());
        return passengerOrders;
    }

    /**
     * APP调整接口
     */
    public List<DepartureInfo> getAppDriverDpartureInfo(String where) {
        String SQL = "select * from pc_driver_publish_info a  left join pc_user b on a.mobile=b.user_mobile "+ where ;
        List<DepartureInfo> appDriverDepartureInfoMapperList = jdbcTemplateObject.query(SQL, new APPDriverDepartureInfoMapper());
        return appDriverDepartureInfoMapperList;
    }
    /**
     * 获取附近车主列表
     */
    public List<DriverAndCar> getOwenrList(String where) {
        String SQL = "select a.*,b.car_color,b.car_type ,c.* from pc_driver_publish_info a,pc_car_owner_info b ,pc_user c where  a.user_id=b.user_id and a.mobile =c.user_mobile  "+ where ;
        List<DriverAndCar> NearByOwenrList = jdbcTemplateObject.query(SQL, new APPDriverAndCarMapper());
        return NearByOwenrList;
    }





    //创建乘客发车单
    public boolean createPassengerDeparture(PassengerOrder passengerOrder) {
        boolean is_success = true;
        String SQL = "insert into pc_passenger_publish_info(user_id,departure_time,booking_seats,boarding_point,breakout_point,description,create_time,is_enable,departure_city_code,destination_city_code,departure_address_code,destination_address_code,order_status,price,source,trade_no,remark) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        int count = jdbcTemplateObject.update(SQL, new Object[]{passengerOrder.getUser_id(), passengerOrder.getStart_time(), passengerOrder.getSeats(), passengerOrder.getBoarding_point(), passengerOrder.getBreakout_point(), passengerOrder.getDescription(),  Utils.getCurrentTime(),1,passengerOrder.getDeparture_city_code(),passengerOrder.getDestination_city_code(),passengerOrder.getDeparture_address_code(),passengerOrder.getDestination_address_code(),0,passengerOrder.getPay_money(),passengerOrder.getSource(),passengerOrder.getPay_num(),passengerOrder.getRemark()});
        if (count < 1) {
            is_success = false;
        }
        return is_success;
    }

    public int getMaxID(String parameter, String table) {
        String sql = "SELECT Max(" + parameter + ")id FROM  " + table;
        Integer id = jdbcTemplateObject.queryForObject(sql, Integer.class);
        return id.intValue();
    }



    //创建乘客订单
    public boolean createPassengerOrder(PassengerOrder passengerOrder,int source) {
        boolean is_success = true;
        String SQL = "insert into pc_passenger_orders(user_id,order_id,booking_seats,boarding_point,breakout_point,description,create_time,order_status,is_enable,source,departure_city_code,destination_city_code,departure_address_code,destination_address_code) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        int count = jdbcTemplateObject.update(SQL, new Object[]{passengerOrder.getUser_id(), passengerOrder.getDriver_order_id(),passengerOrder.getSeats(), passengerOrder.getBoarding_point(), passengerOrder.getBreakout_point(),passengerOrder.getDescription(),Utils.getCurrentTime(),0,1,source,passengerOrder.getDeparture_city_code(),passengerOrder.getDestination_city_code(),passengerOrder.getDeparture_address_code(),passengerOrder.getDestination_address_code()});
        if (count < 1) {
            is_success = false;
        }
        return is_success;
    }

    public List<UserHistory> getUserHistory(String where) {
        String SQL = "SELECT * FROM cyparty_pc.pc_user_role_action   "+where;
        List<UserHistory> historyList = jdbcTemplateObject.query(SQL, new UserHistoryMapper());
        return historyList;
    }



    public List<PassengerOrder> getPassengerList(String where) {
        String SQL = "SELECT * FROM pc_passenger_publish_info a " + "  left join pc_user b on a.user_id=b._id "+ where ;
        List<PassengerOrder> passengerOrders = jdbcTemplateObject.query(SQL, new PassengerPublishInfoMapper());
        return passengerOrders;
    }
    public List<PassengerOrder> getPassengerList1(String where) {
        String SQL = "SELECT * FROM pc_passenger_publish_info a , pc_user b where a.user_id=b._id "+ where ;
        List<PassengerOrder> passengerOrders = jdbcTemplateObject.query(SQL, new PassengerPublishInfoMapper());
        return passengerOrders;
    }
    public List<Carousel> getCarousel(String where) {
        String SQL = "SELECT * FROM pc_carousel " + where ;
        List<Carousel> carouselList = jdbcTemplateObject.query(SQL, new CarouselMapper());
        return carouselList;
    }
    //创建广告
    public boolean createAd(Adviertisement adviertisement) {
        boolean is_success = true;

        String SQL = "insert into pc_dest_ad_detail(dest_name,ad_pic_url,ad_link,ad_weight,ad_updated_time,ad_create_time,ad_status) VALUES (?,?,?,?,?,?,?)";
        int count = jdbcTemplateObject.update(SQL, new Object[]{adviertisement.getDest_name(), adviertisement.getAd_pic_url(), adviertisement.getAd_link(),adviertisement.getAd_weight(),Utils.getCurrentTime(), Utils.getCurrentTime(),1});
        if (count < 1) {
            is_success = false;
        }

        return is_success;
    }

    //创建广告统计
    public boolean createSuggestion(int id,String  advice,String email,int source,String url) {
        boolean is_success = true;

        String SQL = "insert into pc_user_suggestion(user_id,advice,create_time,contact,source,user_screenshots) VALUES (?,?,?,?,?,?)";
        int count = jdbcTemplateObject.update(SQL, new Object[]{id, advice,Utils.getCurrentTime(),email,source,url});
        if (count < 1) {
            is_success = false;
        }

        return is_success;
    }
    public List<ApkUpdate> getApkUpdated(String where) {
        String SQL = "SELECT * FROM pc_apk_updated " + where ;
        List<ApkUpdate> apkUpdates = jdbcTemplateObject.query(SQL, new ApkUpdatedMapper());
        return apkUpdates;
    }

    public List<DriverGrabInfo> getDriverGrabOrder(String where) {
        String SQL = "SELECT * FROM (SELECT a._id,passenger_order_id,driver_id,a.create_time,a.status,a.source,user_mobile,user_avatar,user_name FROM copy_cyparty_pc.pc_driver_booking_passenger a left join pc_user b on a.driver_id=b._id "+where+")c left join pc_car_owner_info d on c.driver_id=d.user_id ";
        List<DriverGrabInfo> grabInfos = jdbcTemplateObject.query(SQL, new DriverGrabOrderMapper());
        return grabInfos;
    }
    /**
     * 后台支付管理
     */


    //创建订单记录
    public boolean createOrderReview(Order order) {
        boolean is_success = true;
        String SQL = "insert into pc_orders(user_id,order_id,create_time,order_status,order_type,source,is_enable,update_time) VALUES (?,?,?,?,?,?,?,?)";
        int count = jdbcTemplateObject.update(SQL, new Object[]{order.getUser_id(),order.getOrder_id(),order.getCreate_time(),order.getOrder_status(),order.getOrder_type(),order.getSource(),1,order.getCreate_time()});
        if (count < 1) {
            is_success = false;
        }

        return is_success;
    }

    public List<Order> getOrderReview(String where,int type) {
        String SQL = "SELECT * FROM pc_orders " + where;
        List<Order> orders =new ArrayList<>();
        switch (type){
            case 0: //不关联表
                orders=jdbcTemplateObject.query(SQL, new OrderMapper());
                break;
            case 1: //关联user表
                orders=jdbcTemplateObject.query(SQL, new OrderUserMapper());
                break;
            case 2: //关联乘客出行表
                orders=jdbcTemplateObject.query(SQL, new PassengerDepartureMapper());
                break;
            default:
                break;
        }

        return orders;
    }
    public boolean createPayNotify(AlipayNotify alipayNotify, int source) {
        boolean is_success = true;
        String SQL = "insert into pc_alipay_log(buyer_email,buyer_id,seller_id,seller_email,price,trade_status,out_trade_no,trade_no,notify_type,notify_time,receipt_amount,buyer_pay_amount,point_amount,notify_id,pay_source) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        int count = jdbcTemplateObject.update(SQL, new Object[]{alipayNotify.getBuyer_email(), alipayNotify.getBuyer_id(), alipayNotify.getSeller_id(), alipayNotify.getSeller_email(), alipayNotify.getPrice(), alipayNotify.getTrade_status(),  alipayNotify.getOut_trade_no(),   alipayNotify.getTrade_no(), alipayNotify.getNotify_type(),  alipayNotify.getNotify_time(), alipayNotify.getReceipt_amount(), alipayNotify.getBuyer_pay_amount(), alipayNotify.getPoint_amount(), alipayNotify.getNotify_id(),source});
        if (count < 1) {
            is_success = false;
        }
        return is_success;
    }

    public List<AlipayNotify> getAlipayNotify(String where) {
        String SQL = "SELECT * FROM pc_alipay_log" + where;
        List<AlipayNotify> alipayNotifyList = jdbcTemplateObject.query(SQL, new RowMapper<AlipayNotify>() {
            @Override
            public AlipayNotify mapRow(ResultSet resultSet, int i) throws SQLException {
                AlipayNotify alipayNotify=new AlipayNotify();
                alipayNotify.setOut_trade_no(resultSet.getString("out_trade_no"));
                alipayNotify.setTrade_status(resultSet.getString("trade_status"));
                return alipayNotify;
            }
        });
        return alipayNotifyList;
    }


    public boolean createPayLog(PayLog pay) {
        boolean is_success = true;
        String SQL = "insert into pay_cash_log(order_id,user_id,p_id,driver_id,cash,create_time,action_type,pay_type,order_status,departure_time,is_complete) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        int count = jdbcTemplateObject.update(SQL, new Object[]{pay.getOrder_id(), pay.getUser_id(), pay.getP_id(), pay.getDriver_id(),pay.getCash(), Utils.getCurrentTime(),pay.getAction_type(),pay.getPay_type(),pay.getOrder_status(),pay.getDeparture_time(),0});
        if (count < 1) {
            is_success = false;
        }
        return is_success;
    }
    //得到支付信息
    public List<PayLog> getPayLog(String where) {
        String SQL = "SELECT * FROM pay_cash_log " + where ;
        List<PayLog> payLogList = jdbcTemplateObject.query(SQL, new PayLogMapper());
        return payLogList;
    }
    public List<Campaign> getCampaign(String where) {
        String SQL = "SELECT * FROM pc_campaign " + where;
        List<Campaign> campaignList = jdbcTemplateObject.query(SQL, new CampaignMapper());
        return campaignList;
    }

    public boolean createPayBack(PayBack pay) {
        boolean is_success = true;
        String SQL = "insert into pc_application_pay_back(order_id,user_id,pay_type,pay_account,pay_reason,create_time,pay_status) VALUES (?,?,?,?,?,?,?)";
        int count = jdbcTemplateObject.update(SQL, new Object[]{pay.getOrder_id(), pay.getUser_id(), pay.getPay_type(), pay.getPay_account(),pay.getPay_reason(), Utils.getCurrentTime(),0});
        if (count < 1) {
            is_success = false;
        }
        return is_success;
    }
    //添加常用路线
    public boolean createCommonRoute(int user_id, String departure_city, String departure_address, String departure_lon, String departure_lat, String destinat_city, String destinat_address, String destinat_lon, String destinat_lat, int is_enable) {
        boolean is_success = true;
        String SQL = "insert into pc_common_route(user_id,departure_city,departure_address,departure_lon,departure_lat,destinat_city,destinat_address,destinat_lon,destinat_lat,create_time,update_time,is_enable) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        int count = jdbcTemplateObject.update(SQL, new Object[]{user_id,departure_city,departure_address, departure_lon, departure_lat, destinat_city,destinat_address,destinat_lon,destinat_lat, Utils.getCurrentTime(), Utils.getCurrentTime(),is_enable});
        if (count<1){
            is_success = false;
       }
       return is_success;
    }

    public  List<CommonRoute> getCommonRoute(String where){
        String SQL = "select * from pc_common_route "+where;
        List<CommonRoute> commonRouteList = jdbcTemplateObject.query(SQL, new CommonRouteMapper());
        return commonRouteList;
    }

    public List<InviteIimit> getinviteIimit(String where) {
        String SQL = "select * from pc_invite_limit "+where;
        List<InviteIimit> inviteIimitList = jdbcTemplateObject.query(SQL, new InviteIimitMapper());
        return inviteIimitList;
    }

    public boolean createInviteIimit(int user_id,int driver_id,String confirm_time) {
        boolean is_success = true;
        String SQL = "insert into pc_invite_limit(passenger_id,driver_id,invite_time) VALUES (?,?,?)";
        int count = jdbcTemplateObject.update(SQL, new Object[]{user_id,driver_id,confirm_time});
        if (count<1){
            is_success = false;
        }
        return is_success;
    }
    //将推送消息存入数据库
    public boolean createPush(int order_id,int push_id,int receive_id,int push_type,String alert,int type,String sound ,String data,int status,String user_name,String link_url){
        boolean is_success = true;
        String SQL = "insert into pc_push_notification (order_id,push_id,receive_id,push_type,alert,type,sound,data,time,status,is_enable,user_name,link_url) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        int count=jdbcTemplateObject.update(SQL, new Object[]{order_id,push_id,receive_id,push_type,alert,type,sound,data,Utils.getCurrentTime(),status,1,user_name,link_url});
        if (count<1){
            is_success = false;
        }
        return is_success;
    }

    public List<PushNotification> getPushList(String where){
        String SQL = "select * from pc_push_notification "+where;
        List<PushNotification> pushList = jdbcTemplateObject.query(SQL,new PushNotificationMapper());
        return pushList;
    }

}

