package com.cyparty.laihui.domain;

/**
 * Created by dupei on 2017/3/17 0017.
 * 推送消息
 */
public class PushNotification {
   private int _id;//订单id
   private int order_id;//订单id
   private int push_id;//推送者id
   private int receive_id;//接受者id
   private int push_type;//标识身份
   private String alert;//消息内容
   private int type;//消息类型
   private String sound;
   private String data;//具体数据
   private String time;//推送的时间
   private int status;//状态 0 未读 1 已读
   private int is_enable;//状态 0 未读 1 已读
   private String user_name;

   public int get_id() {
      return _id;
   }

   public void set_id(int _id) {
      this._id = _id;
   }

   public int getIs_enable() {
      return is_enable;
   }

   public void setIs_enable(int is_enable) {
      this.is_enable = is_enable;
   }

   public int getOrder_id(){return order_id;}

   public void setOrder_id(int order_id){this.order_id=order_id;}

   public int getPush_id(){return push_id;}

   public void setPush_id(int push_id){this.push_id=push_id;}

   public int getReceive_id(){return receive_id;}

   public void setReceive_id(int receive_id){this.receive_id=receive_id;}

   public int getPush_type(){return push_type;}

   public void setPush_type(int push_type){this.push_type=push_type;}

   public String getAlert(){return alert;}

   public void setAlert(String alert){this.alert=alert;}

   public int getType(){return type;}

   public void setType(int type){this.type=type;}

   public String getSound(){return sound;}

   public void setSound(String sound){this.sound=sound;}

   public String getData(){return data;}

   public void setData(String data) {this.data = data;}

   public String getTime(){return time;}

   public void setTime(String time){this.time=time; }

   public int getStatus(){return status;}

   public void setStatus(int status){this.status=status;}

   public String getUser_name(){return user_name;}

   public void setUser_name(String user_name){this.user_name=user_name;}
}
