package com.mongodb.javabasic.controller;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.javabasic.service.WriteService;

@RestController
public class WriteConcernController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private WriteService writeService;

    //MongoDatabase db = mongoClient.getDatabase("demo").withWriteConcern(WriteConcern.MAJORITY).withReadConcern(ReadConcern.LOCAL).withReadPreference(ReadPreference.primary());
    //MongoCollection<Document> collection = db.getCollection("test").withWriteConcern(WriteConcern.MAJORITY).withReadConcern(ReadConcern.LOCAL).withReadPreference(ReadPreference.primary());
    @GetMapping("/write")
    public void writeCocern() throws InterruptedException {
        MongoCollection<Document> collection = mongoClient.getDatabase("demo").getCollection("test");
        collection.insertOne(new Document());
        this.writeService.write(collection.withWriteConcern(new WriteConcern(0)));
        this.writeService.write(collection.withWriteConcern(WriteConcern.MAJORITY));
        this.writeService.write(collection.withWriteConcern(WriteConcern.W3));
        this.writeService.write(collection.withWriteConcern(new WriteConcern(4, 5000)));
        this.writeService.write(collection.withWriteConcern(new WriteConcern(5, 5000)));
    }

}
