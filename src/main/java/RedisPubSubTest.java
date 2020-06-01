import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public class RedisPubSubTest {
    public static void main(String[] args) {
        JedisPool pool = new JedisPool();

        //模拟消费者*2
        new MySubThread(pool).start();
        new MySubThread(pool).start();

        try(Jedis jedis = pool.getResource()){
            for (int i = 0; i < 100; i++) {
                jedis.publish("MyChannel","message:"+i);
            }
        }

    }

    static class MyPubSub extends JedisPubSub{
        @Override
        public void onMessage(String channel, String message) {
            System.out.println(channel +" : " + message);
        }
    }

    static class MySubThread  extends Thread{
        JedisPool pool ;

        public MySubThread(JedisPool pool){
            this.pool = pool;
        }

        @Override
        public void run() {
            try(Jedis jedis = pool.getResource()){
                jedis.subscribe(new MyPubSub(),"MyChannel");
            }
        }
    }
}
