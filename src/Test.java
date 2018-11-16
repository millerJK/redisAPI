import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.*;

import static java.util.Objects.hash;

/**
 * redis 基本使用api
 * Redis 数据是存储在内存中，一旦服务器关闭存储的数据就消失了
 */
public class Test {

    public static void main(String[] args) {

        JedisPool jedisPool = new JedisPool("127.0.0.1", 6379);
        Jedis jedis = jedisPool.getResource();

//        baseString(jedis);
//        hash(jedis);
//        list(jedis);
//        set(jedis);
        sortSetTest(jedis);

        jedis.close();
        jedisPool.close();
    }


    /**
     * String 类型 增删改查
     *
     * @param jedis
     */
    public static void baseString(Jedis jedis) {

        //增 设置一对键值对
        jedis.set("java", "hello world");
        //增 设置多对键值对
        String mset = jedis.mset("key1", "value1", "key2", "1"); //mset = ok

        //删
        jedis.del("java");
        //删 删除多个key
        jedis.del("key1", "key2", "key3");

        //改
        jedis.set("java", "xxxx");


        //查
        String java = jedis.get("java");
        //查 获取多条数据
        List<String> mget = jedis.mget("key1", "key2", "key3"); //返回value 集合


        //filter
        Boolean exists = jedis.exists("java");

        //自增自减少
        jedis.set("key", "1");
        Long value = jedis.incr("key"); //value = 2
        Long key = jedis.incrBy("key", 5); //指定增加多少
        Long key1 = jedis.decr("key");//value = 2
        Long key2 = jedis.decrBy("key", 5);//指定减少多少

    }


    /**
     * redis  map类型 实际上就是HashMap  保证唯一性
     * redis 保证key的唯一性，同时不允许key value两者任何为null
     *
     * @param jedis
     */
    public static void hash(Jedis jedis) {

        Map<String, String> datas = new HashMap<>();
        datas.put("username", "dahuang");
        datas.put("age", "11");
        datas.put("sex", "women");
        datas.put("hair", "yellow");


        //增
        jedis.hset("user", "interests", "game"); //增添单个属性
        jedis.hmset("user", datas);//设置集合

        //删
        jedis.hdel("user", "username", "sex");

        //改
        jedis.hset("user", "username", "dahuang1");

        //查
        String women = jedis.hget("user", "women");//获取单个属性
        List<String> hmget = jedis.hmget("user", "husband", "sex");
        Map<String, String> user = jedis.hgetAll("user");//获取user所有属性值
        Set<String> keys = jedis.hkeys("user");//获取user的说有key值
        List<String> values = jedis.hvals("user");//获取user的所有value值

        //自增自减
        jedis.hincrBy("user", "age1", 2);


        boolean isExists = jedis.hexists("user", "sex1");//检查某一个元素是否存在

    }

    /**
     * redis list类型 linkedList 左右节点都可以插入和删除数据  增删改查
     *
     * @param jedis
     */
    public static void list(Jedis jedis) {

        //增
        jedis.lpush("list3", "1", "2", "3", "4", "5", "6", "7");  //链表左侧插入数据
        jedis.rpush("list3", "234"); //链表右侧插入数据
        jedis.linsert("list3", BinaryClient.LIST_POSITION.BEFORE, "2", "8"); //列表中2前面插入8

        //删
        jedis.lpop("list3"); //左侧弹出一个数据
        jedis.rpop("list3"); //右侧弹出一个数据
        jedis.lrem("list3", 4, "7");  //值为7的数据值删除4个

        //改
        jedis.lset("list3", 0, "7");

        //查
        String list31 = jedis.lindex("list3", 0); //获取下标0的数据
        List<String> list3 = jedis.lrange("list3", 0, -1); //获取list中所有数据

        //获取长度
        Long llen = jedis.llen("list3");

        //一个元素从原来的列表的右边弹出，并插入到另外一个列表中
        String rpoplpush = jedis.rpoplpush("list3", "list4");


    }


    /**
     * redis 无序set 集合
     *
     * @param jedis
     */
    public static void set(Jedis jedis) {

        //增
        jedis.sadd("set", "a", "b", "c", "d", "e");

        //删
        jedis.srem("set", "a", "b", "c");

        //改


        //查
        Set<String> sets = jedis.smembers("set");

        //查询是不是存在某个元素
        boolean isContain = jedis.sismember("set", "1");


        //获取set集合数量
        Long aLong = jedis.scard("set");//集合长度


        //交集 并集 差集
        Set<String> sdiff = jedis.sdiff("set", "set1");//差集
        Set<String> sinter = jedis.sinter("set", "set1");//交集
        Set<String> sunion = jedis.sunion("set", "set1");//并集

    }

    /**
     * redis SortedSet有序集合set 有序并且保证数据唯一性
     */
    public static void sortSetTest(Jedis jedis) {

        Map<String, Double> maps = new HashMap<>();
        maps.put("mark", 50d);
        maps.put("mark1", 60d);
        maps.put("mark2", 40d);
        maps.put("mark3", 70d);

        //增
        jedis.zadd("sortedset", 80, "xiaoming");
        jedis.zadd("sortedset", maps);


        //删
        jedis.zremrangeByRank("sortedset", 0, -1); //删除有序列表所有数据
        jedis.zremrangeByScore("sortedset", 50, 80);
        jedis.zrem("sortedset", "mark");
        jedis.zrem("sortedset", "mark", "mark1");


        //改


        //查
        Set<String> sortedset = jedis.zrange("sortedset", 0, -1); //正序排列
        Set<String> revsortedset = jedis.zrevrange("sortedset", 0, -1); //倒叙排列
        Set<String> sortedset1 = jedis.zrangeByScore("sortedset", 50, 80);//按照分数进行排列


    }


}
