package com.levi9.socialnetwork.Controller;

import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Address;
import com.levi9.socialnetwork.Service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

	@Autowired
	private AddressService addressService;

	@GetMapping
	public java.util.List<Address> getAllAddresses() {

		return this.addressService.getAllAddresses();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Address> getAddress(@PathVariable(value = "id") Long addressId)
			throws ResourceNotFoundException {
		Address address = addressService.getAddressById(addressId);
		return new ResponseEntity<>(address, HttpStatus.OK);
	}

	@PostMapping
	public Address createAddress(@RequestBody Address address) {

		return addressService.createAddress(address);
	}

	@PutMapping("/{id}")
	public Address updateAddress(@PathVariable(value = "id") Long addressId, @RequestBody Address addressDetails)
			throws ResourceNotFoundException {

		return addressService.updateAddress(addressId, addressDetails);
	}

	@DeleteMapping("/{id}")
	public Map<String, Boolean> deleteAddress(@PathVariable(value = "id") Long addressId)
			throws ResourceNotFoundException {

		return addressService.deleteAddress(addressId);
	}

}
