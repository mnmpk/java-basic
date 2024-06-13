package com.mongodb.javabasic.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.bson.Document;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.MongoClientSettings;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.javabasic.model.Movie;
import com.mongodb.javabasic.model.Person;
import com.mongodb.javabasic.model.Person2;
import com.mongodb.javabasic.repositories.MovieRepository;
import com.mongodb.javabasic.repositories.Person2Repository;
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
    private MongoTemplate mongoTemplate;

    @Autowired
    private CodecRegistry pojoCodecRegistry;

    @Autowired
    PersonRepository repository;
    @Autowired
    Person2Repository person2Repository;
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    ConversionService conversionService;
    @Autowired
    MongoConverter mongoConverter;

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
        // person.setId("test");
        person.setFirstname("M");
        person.setLastname("MA");

        return collection.insertOne(person);
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
        person.setLocation(new GeoJsonPoint(123.2, 22.3));
        return repository.insert(person);
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
        AggregateIterable<Person> result = collection
                .aggregate(Arrays.asList(new Document("$match", new Document("firstname", "M"))));
        return result.first();
    }

    @GetMapping("/test-codec-convert")
    public Person testCodecConvert() {
        return pojoCodecRegistry.get(Person.class).decode(new Document("firstname","M").toBsonDocument().asBsonReader(), DecoderContext.builder().build());
    }
    @GetMapping("/test-spring-convert")
    public Person testSpringConvert() {
        //mongoTemplate.getConverter();
        return mongoConverter.read(Person.class, new Document("firstname","M"));
    }
    //mongoConverter.getConversionService();
    //return conversionService.convert(new Document("firstname","M"), Person.class);

    @GetMapping("/insert-movie")
    public InsertOneResult insertMovie() {
        MongoDatabase database = mongoClient.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Movie> collection = database.getCollection("movie", Movie.class);
        Movie movie = new Movie();
        // movie.setId(new ObjectId());
        movie.setPlot("Plot");
        movie.setTitle("Title");
        movie.setStartAt(LocalDateTime.now());
        movie.setEndAt(new Date());

        return collection.insertOne(movie);
    }

    @GetMapping("/spring-insert-movie")
    public Movie springInsertMovie() {
        Movie movie = new Movie();
        movie.setPlot("Plot");
        movie.setTitle("Title");
        movie.setStartAt(LocalDateTime.now());
        movie.setEndAt(new Date());
        return movieRepository.insert(movie);
    }

    @GetMapping("/spring-find-all-movie")
    public List<Movie> springFindAllMovie() {
        return movieRepository.findAll();
    }

    @GetMapping("/find-all-movie")
    public List<Movie> findAllMovie() {
        MongoDatabase database = mongoClient.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Movie> collection = database.getCollection("movie", Movie.class);
        List<Movie> movies = new ArrayList<>();
        collection.find().forEach((p) -> {
            movies.add(p);
        });
        return movies;
    }


    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    @GetMapping("/bulk-upsert-person")
    public BulkWriteResult bulkUpdatePerson() {

        MongoDatabase database = mongoClient.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Person2> collection = database.getCollection("person", Person2.class);
        List<WriteModel<Person2>> operations = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Person2 person = new Person2();
            person.setId(i+1);
            person.setFirstname(String.valueOf(alphabet.charAt(new Random().nextInt(alphabet.length()))));
            person.setLastname("MA");
            //operations.add(
            //        new ReplaceOneModel<Person>(Filters.eq("pId", person.getId()), person, new ReplaceOptions().upsert(true)));
            operations.add(new UpdateOneModel<>(Filters.eq("_id",person.getId()),
                    Updates.combine(Updates.set("firstname", person.getFirstname()),Updates.set("lastname", person.getLastname())),
                    new UpdateOptions().upsert(true)));
        }
        return collection.bulkWrite(operations);
    }

    @GetMapping("/spring-bulk-upsert-person")
    public BulkWriteResult springBulkUpdatePerson() {
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkMode.UNORDERED, Person2.class);
        for (int i = 0; i < 100; i++) {
            Person2 person = new Person2();
            person.setId(i+1);
            person.setFirstname(String.valueOf(alphabet.charAt(new Random().nextInt(alphabet.length()))));
            person.setLastname("MA");
            Query query = new Query().addCriteria(new Criteria("_id").is(person.getId()));
            //bulkOps.replaceOne(query, person, FindAndReplaceOptions.options().upsert());
            Update update = new Update().set("firstname", person.getFirstname()).set("lastname", person.getLastname());
            bulkOps.upsert(query, update);
        }
        return bulkOps.execute();
    }
    @GetMapping("/save-all")
    public List<Person2> saveAll() {
        List<Person2> persons = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Person2 person = new Person2();
            person.setId(i+1);
            person.setFirstname(String.valueOf(alphabet.charAt(new Random().nextInt(alphabet.length()))));
            person.setLastname("MA");
            persons.add(person);
        }
        return person2Repository.saveAll(persons);
    }
}
