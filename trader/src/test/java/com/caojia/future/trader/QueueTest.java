package com.caojia.future.trader;

import java.util.LinkedList;
import java.util.Queue;

import com.alibaba.fastjson.JSON;

public class QueueTest {

    public static void main(String[] args) {
        Queue<String> queue = new LinkedList<String>();
        
        queue.offer("1");
        queue.offer("2");
        queue.offer("3");
        
        if(queue.size() == 3){
            queue.offer("4");
            queue.poll();
        }
        
        System.out.println(JSON.toJSONString(queue));
    }
}
