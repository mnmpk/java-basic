package com.mongodb.javabasic.service;

import java.util.Date;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.mongodb.MongoWriteConcernException;
import com.mongodb.client.MongoCollection;

@Service
public class WriteService {
    Logger logger = LoggerFactory.getLogger(getClass());
    
    @Retryable(retryFor = MongoWriteConcernException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void write(MongoCollection<Document> collection) throws InterruptedException {
        StopWatch sw = new StopWatch();
        logger.info("Start:"+collection.getWriteConcern().toString());
        sw.start();
        logger.info(collection.insertOne(new Document().append("t", new Date())).toString());
        sw.stop();
        logger.info("End, Time: " + sw.getTotalTimeMillis() + "ms");
        Thread.sleep(500);
    }

    @Recover
    private void abort(){
        logger.info("aborted");
    }
}
