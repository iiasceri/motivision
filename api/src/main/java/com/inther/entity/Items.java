package com.inther.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class Items {

    @Id
    @GenericGenerator(name = "kaugen", strategy = "increment")
    @GeneratedValue(generator = "kaugen")
    private Long ID;

    private String type;

    private String imageUrl;

    private int price;

    @OneToMany(mappedBy = "items")
    private CharacterItems characterItems;

    public Items() {}

    public Long getID() {
        return ID;
    }

    public String getType() {
        return type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getPrice() {
        return price;
    }
}
