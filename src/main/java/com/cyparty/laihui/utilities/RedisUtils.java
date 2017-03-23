package com.cyparty.laihui.utilities;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.*;

/**
 * Created by zhu on 2017/2/6.
 */
public class RedisUtils {
    private static Jedis jedis;

    public static void main(String[] args) {
        getConnection();
        try {
            GameRankSimaple();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected static void getConnection(){
        //连接本地的 Redis 服务 10.81.108.197
        jedis = new Jedis("10.81.108.197", 6379);
        jedis.auth("zhubangkui");
        //查看服务是否运行
        System.out.println("Server is running: "+jedis.ping());
    }

    public static void GameRankSimaple(){
    try{
        long begin_time= System.currentTimeMillis()/1000;
        int TOTAL_SIZE=10;
        //Key(键)
        String key = "游戏名：奔跑吧，阿里！";
        //清除可能的已有数据
        jedis.del(key);
        //模拟生成若干个游戏玩家
        List<String> playerList = new ArrayList<String>();
        for (int i = 0; i < TOTAL_SIZE; ++i)
        {
            //随机生成每个玩家的ID UUID.randomUUID().toString()

            playerList.add(i+"");
        }
        //记录每个玩家的得分
        for (int i = 0; i < playerList.size(); i++)
        {
            //随机生成数字，模拟玩家的游戏得分
            int score = (int)(Math.random()*5000);
            String member = playerList.get(i);
            //System.out.println("玩家ID：" + member + "， 玩家得分: " + score);
            //将玩家的ID和得分，都加到对应key的SortedSet中去
            jedis.zadd(key, score, member);
        }

      /*
        //从对应key的SortedSet中获取已经排好序的玩家列表
        Set<Tuple> scoreList = jedis.zrevrangeWithScores(key, 0, -1);
        for (Tuple item : scoreList) {
            System.out.println("玩家ID："+item.getElement()+"， 玩家得分:"+Double.valueOf(item.getScore()).intValue());
        }*/
        while (true){
            //输出打印全部玩家排行榜
            System.out.println();
            System.out.println("       "+key);
            System.out.println("       全部玩家排行榜                    ");
            //更新得分
            Set<Tuple> scoreList = jedis.zrevrangeWithScores(key, 0, -1);
            for (Tuple item : scoreList) {
                int score = (int)(Math.random()*5000);
                String member = item.getElement();
                jedis.zadd(key, score, member);
            }
            scoreList = jedis.zrevrangeWithScores(key, 0, -1);
            for (Tuple item : scoreList) {
                System.out.println("玩家ID："+item.getElement()+"， 玩家得分:"+Double.valueOf(item.getScore()).intValue());
            }
            //输出打印Top5玩家排行榜
            System.out.println();
            System.out.println("       "+key);
            System.out.println("       Top 玩家");
            scoreList = jedis.zrevrangeWithScores(key, 0, 4);
            for (Tuple item : scoreList) {
                System.out.println("玩家ID："+item.getElement()+"， 玩家得分:"+Double.valueOf(item.getScore()).intValue());
            }
            System.out.println("游戏进行中，玩家分数待更新。。。");
            Thread.sleep(1000*60);
            System.out.println("玩家分数已更新！！！");

        }
        /*//输出打印特定玩家列表
        System.out.println();
        System.out.println("         "+key);
        System.out.println("          积分在1000至2000的玩家");
        //从对应key的SortedSet中获取已经积分在1000至2000的玩家列表
        scoreList = jedis.zrangeByScoreWithScores(key, 1000, 2000);
        for (Tuple item : scoreList) {
            System.out.println("玩家ID："+item.getElement()+"， 玩家得分:"+Double.valueOf(item.getScore()).intValue());
        }*/
    } catch (Exception e) {
        e.printStackTrace();
    }finally{
        jedis.quit();
        jedis.close();
    }

    }
    /**
           * redis存储字符串
     */

      public static void actionString() {
               //-----添加数据----------
         jedis.set("name","zhubangkui");//向key-->name中放入了value-->xinxin
         System.out.println(jedis.get("name"));//执行结果：xinxin

         jedis.append("name", " is me"); //拼接
         System.out.println(jedis.get("name"));
         jedis.del("name");  //删除某个键
         System.out.println(jedis.get("name"));
         //设置多个键值对
         jedis.mset("name","zhubangkui","age","23","mobile","1383****275");
         jedis.incr("age"); //进行加1操作
         System.out.println("name:"+jedis.get("name") + "  age=" + jedis.get("age") + "  mobile=" + jedis.get("mobile"));

      }

        /**
              * redis操作Map
         */

      public static  void actionMap() {
         //-----添加数据----------
         Map<String, String> map = new HashMap<String, String>();
         map.put("name", "zhubangkui");
         map.put("age", "24");
         map.put("mobile", "1383****275");
         jedis.hmset("user",map);
         //取出user中的name，执行结果:[minxr]-->注意结果是一个泛型的List
         //第一个参数是存入redis中map对象的key，后面跟的是放入map中的对象的key，后面的key可以跟多个，是可变参数
         List<String> rsmap = jedis.hmget("user", "name", "age", "mobile");
         System.out.println(rsmap);

         //删除map中的某个键值
         jedis.hdel("user","age");
         System.out.println(jedis.hmget("user", "age")); //因为删除了，所以返回的是null
         System.out.println(jedis.hlen("user")); //返回key为user的键中存放的值的个数2
         System.out.println(jedis.exists("user"));//是否存在key为user的记录 返回true
         System.out.println(jedis.hkeys("user"));//返回map对象中的所有key
         System.out.println(jedis.hvals("user"));//返回map对象中的所有value

         Iterator<String> iter=jedis.hkeys("user").iterator();
         while (iter.hasNext()){
             String key = iter.next();
             System.out.println(key+":"+jedis.hmget("user",key));
         }
      }

    /**
           * jedis操作List
     */

      public static void actionList(){
        //开始前，先移除所有的内容
         jedis.del("java framework");
         System.out.println(jedis.lrange("java framework",0,-1));
         //先向key java framework中存放三条数据,倒序插入
         jedis.lpush("java framework","spring");
         jedis.lpush("java framework","struts");
         jedis.lpush("java framework","hibernate");
         //再取出所有数据jedis.lrange是按范围取出，
         // 第一个是key，第二个是起始位置，第三个是结束位置，jedis.llen获取长度 -1表示取得所有
         System.out.println(jedis.lrange("java framework",0,-1));

         jedis.del("java framework");
         //正序插入
         jedis.rpush("java framework","spring");
         jedis.rpush("java framework","struts");
         jedis.rpush("java framework","hibernate");
         System.out.println(jedis.lrange("java framework",0,-1));

         List<String> data=jedis.lrange("java framework",0,-1);
         for(int i=0;i<data.size();i++){
             System.out.println(data.get(i));
             System.out.println(jedis.lindex("java framework",i));
         }
      }

    /**
          * jedis操作Set
     */

     public static void actionSet(){
       //添加
         jedis.del("user");
         jedis.sadd("user","liuling");
         jedis.sadd("user","xinxin");
         jedis.sadd("user","ling");
         jedis.sadd("user","zhangxinxin");
         jedis.sadd("user","who");
         //移除noname
         jedis.srem("user","who");
         Set<String> data=jedis.smembers("user");
         System.out.println(jedis.smembers("user"));//获取所有加入的value
         System.out.println(jedis.sismember("user", "who"));//判断 who 是否是user集合的元素
         System.out.println(jedis.srandmember("user"));
         System.out.println(jedis.scard("user"));//返回集合的元素个数

     }


     public static void sort() throws InterruptedException {
         //jedis 排序
         //注意，此处的rpush和lpush是List的操作。是一个双向链表（但从表现来看的）
         jedis.del("a");//先清除数据，再加入数据进行测试
         jedis.rpush("a", "1");
         jedis.lpush("a","6");
         jedis.lpush("a","3");
         jedis.lpush("a","9");
         System.out.println(jedis.lrange("a",0,-1));// [9, 3, 6, 1]
         System.out.println(jedis.sort("a")); //[1, 3, 6, 9]  //输出排序后结果
         System.out.println(jedis.lrange("a",0,-1));
     }

     public static void actionChinese() {
         jedis.set("newname", "中文测试");
         System.out.println(jedis.get("newname"));
     }


}
