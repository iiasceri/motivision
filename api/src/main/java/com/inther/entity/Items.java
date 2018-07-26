package com.inther.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
public class Items {

    @Id
    @GenericGenerator(name = "kaugen", strategy = "increment")
    @GeneratedValue(generator = "kaugen")
    private Long id;

    private String type;

    private String name;

    private String imagePath;

    private int price;

    @OneToMany(mappedBy = "items")
    private List<CharacterItem> characterItems;

    public Items() {}

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getPrice() {
        return price;
    }
}
