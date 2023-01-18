package com.mongodb.javabasic.model;

import java.util.List;

import org.bson.types.ObjectId;

import lombok.Data;

@Data
public class Movie {
    ObjectId id;
    String plot;
    List<String> genres;
    String title;
}