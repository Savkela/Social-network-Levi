package com.levi9.socialnetwork.Service;

import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Address;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface AddressService {

    public java.util.List<Address> getAllAddresses();

    public Address getAddressById(Long addressId) throws ResourceNotFoundException;

    public Address createAddress(Address address);

    public Address updateAddress(Long addressId, @RequestBody Address addressDetails)
            throws ResourceNotFoundException;

    public Map<String, Boolean> deleteAddress(Long addressId) throws ResourceNotFoundException;
}
