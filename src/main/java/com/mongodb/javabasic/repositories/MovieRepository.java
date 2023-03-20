package com.mongodb.javabasic.repositories;


import org.springframework.data.mongodb.repository.MongoRepository;

import com.mongodb.javabasic.model.Movie;

public interface MovieRepository extends MongoRepository<Movie, String> {

}