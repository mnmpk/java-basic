package com.mongodb.javabasic.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mongodb.javabasic.model.Person;

public interface PersonRepository extends MongoRepository<Person, String> {

}