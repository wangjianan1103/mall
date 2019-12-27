package com.core.mall.service.core.impl;

import com.core.mall.service.core.DelayQueueService;
import org.springframework.stereotype.Service;

@Service
public class DelayQueueServiceImpl implements DelayQueueService {

    private final String QUEUE_KEY = "delay_queue";

//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;
//
//    @Override
//    public void push(Queue queue) {
//        int time = Utility.getCurrentTimeStamp() + queue.getTime();
//        String taskId = queue.toString();
//        redisTemplate.opsForZSet().add(this.QUEUE_KEY, taskId, time);
//    }
//
//    @Override
//    public Set<String> pop(Integer count, Integer previous) {
//        count = count == null ? 0 : 5;
//        previous = previous == null ? 0 : 5;
//
//        int until_ts = Utility.getCurrentTimeStamp() - previous;
//        Set<String> result = redisTemplate.opsForZSet().rangeByScore(this.QUEUE_KEY, 0, until_ts, 0, count);
//
//        if (result != null) {
//            for (String key : result) {
//                redisTemplate.opsForZSet().remove(this.QUEUE_KEY, key);
//            }
//        }
//        return result;
//    }
//
//    @Data
//    public static class Queue {
//        private String taskId;
//        private Integer time;
//        private Integer type;
//
//        public Queue(String taskId, Integer time, Integer type) {
//            this.taskId = taskId;
//            this.time = time;
//            this.type = type;
//        }
//    }
}
