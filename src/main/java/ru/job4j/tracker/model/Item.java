package ru.job4j.tracker.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Data
@Entity
@Table(name = "items")
public class Item {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    private Timestamp created;

    private String description;

    public Item(String name) {
        this.name = name;
    }

    public Item(int id, String name, Timestamp created) {
        this.id = id;
        this.name = name;
        this.created = created;
    }

    public Item(int id, String name, Timestamp created, String description) {
        this.id = id;
        this.name = name;
        this.created = created;
        this.description = description;
    }

    public Item() {

    }
}
