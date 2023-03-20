package com.mongodb.javabasic.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.javabasic.model.Movie;
import com.mongodb.javabasic.model.Person;
import com.mongodb.javabasic.repositories.MovieRepository;
import com.mongodb.javabasic.repositories.PersonRepository;

@RestController
public class ApplicationController {
    @Value("${spring.data.mongodb.uri}")
    private String uri;
    @Value("${spring.data.mongodb.host}")
    private String host;
    @Value("${spring.data.mongodb.port}")
    private String port;
    @Value("${spring.data.mongodb.database}")
    private String databaseName;
    @Value("${spring.data.mongodb.username}")
    private String username;
    @Value("${spring.data.mongodb.password}")
    private String password;

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private CodecRegistry pojoCodecRegistry;

    @Autowired
    PersonRepository repository;
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    ConversionService conversionService;

    @GetMapping("/self-create-client")
    public Document selfCreateClient() {
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection("movies");
            Document doc = collection.find(Filters.eq("title", "Back to the Future")).first();
            return doc;
        }
    }

    @GetMapping("/self-create-client-pojo")
    public Movie selfCreateClientPOJO() {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(pojoCodecProvider));
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
            MongoCollection<Movie> collection = database.getCollection("movies", Movie.class);
            Movie movie = collection.find(Filters.eq("title", "Back to the Future")).first();
            return movie;
        }
    }

    @GetMapping("/insert-document")
    public InsertOneResult insertDocument() {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection("person");
        Document doc1 = new Document("color", "red").append("qty", 5);

        return collection.insertOne(doc1);
    }

    @GetMapping("/insert-person")
    public InsertOneResult insertPerson() {
        MongoDatabase database = mongoClient.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Person> collection = database.getCollection("person", Person.class);
        Person person = new Person();
        //person.setId("test");
        person.setFirstname("M");
        person.setLastname("MA");

        return collection.insertOne(person);
    }

    @GetMapping("/insert-movie")
    public InsertOneResult insertMovie() {
        MongoDatabase database = mongoClient.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Movie> collection = database.getCollection("movies", Movie.class);
        Movie movie = new Movie();
        //movie.setId(new ObjectId());
        movie.setPlot("Plot");
        movie.setTitle("Title");

        return collection.insertOne(movie);
    }

    @GetMapping("/spring-create-client")
    public Document springCreateClient() {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection("movies");
        Document doc = collection.find(Filters.eq("title", "Back to the Future")).first();
        return doc;
    }

    @GetMapping("/spring-create-client-pojo")
    public Movie springCreateClientPOJO() {
        MongoDatabase database = mongoClient.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Movie> collection = database.getCollection("movies", Movie.class);
        Movie movie = collection.find(Filters.eq("title", "Back to the Future")).first();
        return movie;
    }

    @GetMapping("/spring-insert")
    public Person springInsert() {
        Person person = new Person();
        person.setFirstname("M");
        person.setLastname("MA");
        return repository.insert(person);
    }
    @GetMapping("/spring-insert-movie")
    public Movie springInsertMovie() {
        Movie movie = new Movie();
        movie.setPlot("Plot");
        movie.setTitle("Title");
        movie.setStartAt(LocalDateTime.now());
        return movieRepository.insert(movie);
    }

    @GetMapping("/spring-find-all")
    public List<Person> springFindAll() {
        return repository.findAll();
    }

    @GetMapping("/find-all")
    public List<Person> findAll() {
        MongoDatabase database = mongoClient.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Person> collection = database.getCollection("person", Person.class);
        List<Person> persons = new ArrayList<>();
        collection.find().forEach((p) -> {
            persons.add(p);
        });
        return persons;
    }

    @GetMapping("/delete-all")
    public void deleteAll() {
        repository.deleteAll();
    }


    @GetMapping("/test-agg")
    public Person testAgg() {
        MongoDatabase database = mongoClient.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Person> collection = database.getCollection("person", Person.class);
        AggregateIterable<Person> result = collection.aggregate(Arrays.asList(new Document("$match", new Document("firstname", "M"))));
        return result.first();
    }
    
}
