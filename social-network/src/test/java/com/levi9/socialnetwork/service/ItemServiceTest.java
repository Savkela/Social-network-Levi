package com.levi9.socialnetwork.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.Item;
import com.levi9.socialnetwork.Model.User;
import com.levi9.socialnetwork.Repository.ItemRepository;
import com.levi9.socialnetwork.Repository.UserRepository;
import com.levi9.socialnetwork.Service.impl.EmailServiceImpl;
import com.levi9.socialnetwork.Service.impl.ItemServiceImpl;
import com.levi9.socialnetwork.dto.ItemDTO;

@RunWith(MockitoJUnitRunner.class)
class ItemServiceTest {

	private static final String NOT_FOUND_MESSAGE = "Item not found for this id :: ";
	private static final String ALREADY_EXISTS_MESSAGE = "Item already exists with this id :: ";

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private EmailServiceImpl emailService;

	@InjectMocks
	private ItemServiceImpl itemService;

	static final Long itemId = 1L;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void shouldFindAndReturnOneItem() throws ResourceNotFoundException {
		Item expectedItem = Item.builder()
				.id(1L)
				.link("https://www.slikomania.rs/fotky6509/fotos/CWFTR026.jpg")
				.build();
		
		given(itemRepository.findItemById(itemId))
				.willReturn(Optional.of(expectedItem));

		ItemDTO actual = itemService.getItemById(itemId);
		assertThat(actual).usingRecursiveComparison().isEqualTo(expectedItem);
		verify(itemRepository, times(1)).findItemById(1L);
		verifyNoMoreInteractions(itemRepository);
	}

	@Test
	void shouldNotFindItemAndReturnException() throws ResourceNotFoundException {

		given(itemRepository.findById(itemId)).willAnswer(invocation -> {
			throw new ResourceNotFoundException("Item not found");
		});

		assertThrows(ResourceNotFoundException.class, () -> {

			itemService.getItemById(itemId);

		});
	}

	@Test
	void shouldDeleteOneItem() throws ResourceNotFoundException {

		Item expectedItem = Item.builder().id(1L).link("https://www.slikomania.rs/fotky6509/fotos/CWFTR026.jpg")
				.build();
		when(itemRepository.save(expectedItem)).thenReturn(expectedItem);

		itemRepository.deleteById(itemId);

		verify(itemRepository, times(1)).deleteById(itemId);
		verifyNoMoreInteractions(itemRepository);
	}

	@Test
	void shouldDeleteOneItemReturnException() throws ResourceNotFoundException {

		Item expectedItem = Item.builder().id(1L).link("https://www.slikomania.rs/fotky6509/fotos/CWFTR026.jpg")
				.build();
		when(itemRepository.save(expectedItem)).thenReturn(expectedItem);

		itemRepository.deleteById(2L);

		assertThrows(ResourceNotFoundException.class, () -> {

			itemService.deleteItem(itemId);

		});
	}

	@Test
	void testCreateItem() {
		Item newItem = Item.builder().id(itemId)
				.link("http://www.10naj.com/wp-content/uploads/2016/08/zvezdananoc_.jpg").build();

		ItemDTO newItemDTO = ItemDTO.builder().id(itemId)
				.link("http://www.10naj.com/wp-content/uploads/2016/08/zvezdananoc_.jpg").build();

		given(itemRepository.save(newItem))
				.willReturn(newItem);

		Long returnedItemId = itemService.createItem(newItemDTO);

		assertThat(returnedItemId).isEqualTo(newItem.getId());
	}

	@Test
	void testUpdateItem() throws ResourceNotFoundException {
		ItemDTO expectedItemDTO = ItemDTO.builder().id(itemId)
				.link("https://www.slikomania.rs/fotky6509/fotos/CWFTR026.jpg").build();
		Item expectedItem = Item.builder()
				.id(itemId)
				.link("https://www.slikomania.rs/fotky6509/fotos/CWFTR026.png")
				.build();

		given(itemRepository.findItemById(itemId))
				.willReturn(Optional.of(expectedItem));
		given(itemRepository.save(expectedItem))
				.willReturn(expectedItem);

		ItemDTO returnedItemDTO = itemService.updateItem(itemId, expectedItemDTO);
		assertThat(returnedItemDTO).isEqualTo(expectedItemDTO);
	}

	@Test
	void givenItemIdDeleteId() throws ResourceNotFoundException {
		given(itemRepository.existsById(itemId))
				.willReturn(true);
		doNothing().when(itemRepository).deleteById(itemId);

		itemService.deleteItem(itemId);
		verify(itemRepository, times(1)).existsById(itemId);
		verify(itemRepository, times(1)).deleteById(itemId);
		verifyNoMoreInteractions(itemRepository);
	}

	@Test
	void givenItemIdThrowResourceNotFoundException() throws ResourceNotFoundException {
		given(itemRepository.existsById(itemId))
				.willReturn(false);

		assertThrows(ResourceNotFoundException.class,
				() -> itemService.deleteItem(itemId));
		verify(itemRepository, times(1)).existsById(itemId);
		verifyNoMoreInteractions(itemRepository);
	}

}
