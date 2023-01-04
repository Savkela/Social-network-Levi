package com.levi9.socialnetwork.Model;

import com.levi9.socialnetwork.dto.AddressDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "address", schema = "public")
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "street")
    private String street;

    @Column(name = "number")
    private int number;

    public Address(Long id, String country, String city, String street, int number) {
        super();
        this.id = id;
        this.country = country;
        this.city = city;
        this.street = street;
        this.number = number;
    }

    public Address(AddressDTO addressDTO) {
        this.id = addressDTO.getId();
        this.country = addressDTO.getCountry();
        this.city = addressDTO.getCity();
        this.street = addressDTO.getStreet();
        this.number = addressDTO.getNumber();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Address address = (Address) o;
        return id != null && Objects.equals(id, address.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
