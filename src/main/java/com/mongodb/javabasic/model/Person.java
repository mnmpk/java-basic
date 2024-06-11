package com.mongodb.javabasic.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.geo.GeoJson;

//import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class Person {
    //@Id
    private ObjectId id;
    private int pId;
    private String firstname;
    private String lastname;
    private GeoJson<?> location;
}