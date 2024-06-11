package com.mongodb.javabasic.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mongodb.javabasic.model.Person2;

public interface Person2Repository extends MongoRepository<Person2, String> {

}