package ru.alesandrus.models;

import ru.alesandrus.models.enumerations.OwnerType;

import javax.persistence.*;
import java.util.List;

/**
 * @author Alexander Ivanov
 * @version 1.0
 * @since 15.01.2019
 */
@Entity
public class AdOwner {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "owner_id")
    private Long id;

    private String name;

    @Column(unique = true)
    private String url;

    private String contacts;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private OwnerType ownerType;

    @OneToMany(mappedBy = "owner")
    private List<Advertisement> ads;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public List<Advertisement> getAds() {
        return ads;
    }

    public void setAds(List<Advertisement> ads) {
        this.ads = ads;
    }

    public OwnerType getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(OwnerType ownerType) {
        this.ownerType = ownerType;
    }
}
