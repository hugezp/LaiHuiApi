package com.cyparty.laihui.domain;

/**
 * Created by zhu on 2016/8/29.
 * 对应  pc_passenger_publish_info
 */
public class PassengerOrder {
    private int _id;
    private int is_enable;//是否可用
    private int order_type;//订单类型
    private int driver_order_id;
    private int passenger_order_id;
    private int user_id;//用户id
    private int p_id;//id
    private int seats;//座位数
    private String boarding_point;//起点
    private String breakout_point;//终点

    private String description;//描述
    private String create_time;//创建时间
    private String departure_time;//出发时间
    private String start_time;//开始时间

    private String mobile;//手机号
    private int source;//ios或者Android
    private int pay_status;//支付状态
    private double pay_money;//支付金额
    private String  pay_privilege;//支付特权
    private String  pay_num;//支付单号
    private String  user_name;//用户名
    private String  user_avatar;//用户头像
    private String  remark;//备注
    private int departure_address_code=0;
    private int departure_city_code=0;
    private int destination_address_code=0;
    private int destination_city_code=0;
    private int order_status=0;

    public int getIs_enable() {
        return is_enable;
    }

    public void setIs_enable(int is_enable) {
        this.is_enable = is_enable;
    }

    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public int getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int order_type) {
        this.order_type = order_type;
    }

    public int getPassenger_order_id() {
        return passenger_order_id;
    }

    public void setPassenger_order_id(int passenger_order_id) {
        this.passenger_order_id = passenger_order_id;
    }

    public int getorder_status() {
        return order_status;
    }

    public void setorder_status(int order_status) {
        this.order_status = order_status;
    }

    public int getDeparture_address_code() {
        return departure_address_code;
    }

    public void setDeparture_address_code(int departure_address_code) {
        this.departure_address_code = departure_address_code;
    }

    public int getDeparture_city_code() {
        return departure_city_code;
    }

    public void setDeparture_city_code(int departure_city_code) {
        this.departure_city_code = departure_city_code;
    }

    public int getDestination_address_code() {
        return destination_address_code;
    }

    public void setDestination_address_code(int destination_address_code) {
        this.destination_address_code = destination_address_code;
    }

    public int getDestination_city_code() {
        return destination_city_code;
    }

    public void setDestination_city_code(int destination_city_code) {
        this.destination_city_code = destination_city_code;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public int getOrder_status() {
        return order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }

    public String getPay_num() {
        return pay_num;
    }

    public void setPay_num(String pay_num) {
        this.pay_num = pay_num;
    }

    public int getPay_status() {
        return pay_status;
    }

    public void setPay_status(int pay_status) {
        this.pay_status = pay_status;
    }

    public double getPay_money() {
        return pay_money;
    }

    public void setPay_money(double pay_money) {
        this.pay_money = pay_money;
    }

    public String getPay_privilege() {
        return pay_privilege;
    }

    public void setPay_privilege(String pay_privilege) {
        this.pay_privilege = pay_privilege;
    }

    public String getDeparture_time() {
        return departure_time;
    }

    public void setDeparture_time(String departure_time) {
        this.departure_time = departure_time;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }




    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getDriver_order_id() {
        return driver_order_id;
    }

    public void setDriver_order_id(int driver_order_id) {
        this.driver_order_id = driver_order_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getBoarding_point() {
        return boarding_point;
    }

    public void setBoarding_point(String boarding_point) {
        this.boarding_point = boarding_point;
    }

    public String getBreakout_point() {
        return breakout_point;
    }

    public void setBreakout_point(String breakout_point) {
        this.breakout_point = breakout_point;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemark(){ return remark;}

    public void setRemark(String remark){this.remark =remark;}
}
