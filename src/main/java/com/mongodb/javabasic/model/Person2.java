package com.mongodb.javabasic.model;

//import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class Person2 {
    //@Id
    private int id;
    private String firstname;
    private String lastname;
}