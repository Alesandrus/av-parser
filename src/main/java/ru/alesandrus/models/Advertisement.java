package ru.alesandrus.models;

import ru.alesandrus.models.enumerations.Category;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * @author Alexander Ivanov
 * @version 1.0
 * @since 13.01.2019
 */
@Entity
public class Advertisement implements Comparable<Advertisement> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private BigInteger price;

    private Timestamp lastUpdateTime;

    private Timestamp createTime;

    @Column(unique = true)
    private String url;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private AdOwner owner;

    private String city;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getPrice() {
        return price;
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public AdOwner getOwner() {
        return owner;
    }

    public void setOwner(AdOwner owner) {
        this.owner = owner;
    }

    @Override
    public int compareTo(Advertisement other) {
        return this.lastUpdateTime.compareTo(other.lastUpdateTime);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
