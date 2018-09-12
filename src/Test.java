import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

    public static void main(String[] args) {

        JedisPool jedisPool = new JedisPool("127.0.0.1", 6379);
        Jedis jedis = jedisPool.getResource();

        stringTest(jedis);
        hashTest(jedis);
        listTest(jedis);

        jedis.close();
        jedisPool.close();
    }


    public static void stringTest(Jedis jedis) {

        //增
        jedis.set("java", "hello world");
        jedis.mset("key1", "value1", "key2", "1");
        //删
        jedis.del("java");
        //改
        jedis.set("java", "xxxx");
        jedis.incr("key2");
        jedis.decr("key2");
        jedis.incrBy("key2", 4);
        jedis.decrBy("key2", 3);
        //查
        String java = jedis.get("java");
        Boolean exists = jedis.exists("java");

        System.out.println("exists = " + exists);
        System.out.println("java = " + java);
    }


    public static void hashTest(Jedis jedis) {

        Map<String, String> datas = new HashMap<>();
        datas.put("username", "dahuang");
        datas.put("age", "11");
        datas.put("age1", "12");
        datas.put("sex", "women");
        datas.put("sex1", "women");
        datas.put("husband", "haha");

        //增
        jedis.hset("user", "interests", "game"); //增添单个属性
        jedis.hmset("user", datas);//设置集合
        //删
        jedis.hdel("user", "username", "sex");
        //改
        jedis.hincrBy("user", "age1", 2);
        jedis.hset("user", "username", "dahuang1");
        //查
        boolean isExists = jedis.hexists("user", "sex1");//检查某一个元素是否存在
        String women = jedis.hget("user", "women");//获取单个属性
        List<String> hmget = jedis.hmget("user", "husband", "sex");
        Map<String, String> user = jedis.hgetAll("user");//获取user所有属性值


        System.out.println("isExists = " + isExists);
        System.out.println("hmget = " + hmget.toString());
        System.out.println("women = " + women);
        System.out.println("user = " + user);


    }

    public static void listTest(Jedis jedis) {

        //增
        if (jedis.llen("list3") == 0)
            jedis.lpush("list3", "1", "2", "3", "4", "5", "6", "7");
        //删
        jedis.lpop("list3"); //左侧弹出一个数据
        jedis.rpop("list3"); //右侧弹出一个数据
        jedis.lrem("list3", 4, "7");
        //改
        jedis.lset("list3", 0, "7");
        jedis.linsert("list3", BinaryClient.LIST_POSITION.BEFORE, "2", "8");

        //查
        String list31 = jedis.lindex("list3", 1); //获取下标1的数据
        List<String> list3 = jedis.lrange("list3", 0, -1);

        //获取集合长度
        Long llen = jedis.llen("list3");
        System.out.println("llen = " + llen);

        //
        String rpoplpush = jedis.rpoplpush("list3", "list4");

        System.out.println("list31 = " + list31);
        System.out.println("list3 = " + list3.toString());
    }




}
