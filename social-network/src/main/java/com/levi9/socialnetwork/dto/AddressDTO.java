package com.levi9.socialnetwork.dto;

import com.levi9.socialnetwork.Model.Address;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddressDTO {
	private Long id;
	private String country;
	private String city;
	private String street;
	private int number;

	public AddressDTO() {
	}

	public AddressDTO(Long id, String country, String city, String street, int number) {
		this.id = id;
		this.country = country;
		this.city = city;
		this.street = street;
		this.number = number;
	}

	public AddressDTO(Address address) {
		this.id = address.getId();
		this.country = address.getCountry();
		this.city = address.getCity();
		this.street = address.getStreet();
		this.number = address.getNumber();
	}
}
