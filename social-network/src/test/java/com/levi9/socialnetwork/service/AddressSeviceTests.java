package com.levi9.socialnetwork.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Address;
import com.levi9.socialnetwork.Model.Event;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.Repository.AddressRepository;
import com.levi9.socialnetwork.Repository.EventRepository;
import com.levi9.socialnetwork.Repository.UserRepository;
import com.levi9.socialnetwork.Service.impl.AddressServiceImpl;
import com.levi9.socialnetwork.Service.impl.EmailServiceImpl;
import com.levi9.socialnetwork.Service.impl.EventServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class AddressSeviceTests {

	private static final String NOT_FOUND_MESSAGE = "Address not found for this id :: ";
	private static final String ALREADY_EXISTS_MESSAGE = "Address already exists with this id :: ";

	@Mock
	private AddressRepository addressRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private EmailServiceImpl emailService;

	@InjectMocks
	private AddressServiceImpl addressService;

	static final Long addressId = 1L;

	@Test
	public void whenGetAddressItShouldReturnListOfAddresses() {

		given(addressRepository.findAll()).willReturn(List.of(new Address(), new Address()));

		assertThat(addressService.getAllAddresses()).hasSize(2);
		verify(addressRepository, times(1)).findAll();
	}

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

	}

	@Test
	void shouldFindAndReturnOneAddress() throws ResourceNotFoundException {

		Address expectedAddress = Address.builder().id(1L).country("Serbia").city("Kraljevo").street("Zelenjak")
				.number(1).build();
		when(addressRepository.findById(addressId)).thenReturn(Optional.of(expectedAddress));

		Address actual = addressService.getAddressById(1L);

		assertThat(actual).usingRecursiveComparison().isEqualTo(expectedAddress);
		verify(addressRepository, times(1)).findById(1L);
		verifyNoMoreInteractions(addressRepository);

	}

	@Test()
	void shouldNotFindAddressAndReturnException() throws ResourceNotFoundException {

		given(addressRepository.findById(addressId)).willAnswer(invocation -> {
			throw new ResourceNotFoundException("Address not found");
		});

		assertThrows(ResourceNotFoundException.class, () -> {

			addressService.getAddressById(addressId);

		});
	}

	@Test
	void testCreateAddress() {
		Address expectedAddress = Address.builder().id(1L).country("Serbia").city("Kraljevo").street("Zelenjak")
				.number(1).build();
		when(addressRepository.save(expectedAddress)).thenReturn(expectedAddress);

		Address actualUser = addressService.createAddress(expectedAddress);

		assertThat(expectedAddress).usingRecursiveComparison().isEqualTo(actualUser);
		verify(addressRepository, times(1)).save(expectedAddress);
		verifyNoMoreInteractions(userRepository);
	}

	@Test
	void testUpdateAddress() throws ResourceNotFoundException {
		Address expectedAddress = Address.builder().id(1L).country("Serbia").city("Kraljevo").street("Zelenjak")
				.number(1).build();

		when(addressRepository.findById(addressId)).thenReturn(Optional.of(expectedAddress));
		when(addressRepository.save(expectedAddress)).thenReturn(expectedAddress);

		Address actualUser = addressService.updateAddress(addressId, expectedAddress);

		assertThat(actualUser).usingRecursiveComparison().isEqualTo(expectedAddress);
		verify(addressRepository, times(1)).findById(addressId);
		verify(addressRepository, times(1)).save(expectedAddress);
		verifyNoMoreInteractions(userRepository);
	}

	@Test
	void shouldDeleteOneAddress() throws ResourceNotFoundException {

		Address expectedAddress = Address.builder().id(1L).country("Serbia").city("Kraljevo").street("Zelenjak")
				.number(1).build();
		when(addressRepository.save(expectedAddress)).thenReturn(expectedAddress);

		addressRepository.deleteById(addressId);

		verify(addressRepository, times(1)).deleteById(addressId);
		verifyNoMoreInteractions(addressRepository);
	}

	@Test
	void shouldDeleteOneEventReturnException() throws ResourceNotFoundException {

		Address expectedAddress = Address.builder().id(1L).country("Serbia").city("Kraljevo").street("Zelenjak")
				.number(1).build();
		when(addressRepository.save(expectedAddress)).thenReturn(expectedAddress);

		addressRepository.deleteById(2L);

		assertThrows(ResourceNotFoundException.class, () -> {

			addressService.deleteAddress(addressId);

		});

	}

}
