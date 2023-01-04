package com.levi9.storage.serviceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levi9.storage.model.Storage;
import com.levi9.storage.repository.StorageRepository;
import com.levi9.storage.service.StorageService;

@Service
public class StorageServiceImpl implements StorageService {

	@Autowired

	private StorageRepository storageRepository;

	public String getLinkById(Long id) {
		Optional<Storage> storage = storageRepository.findById(id);

		Storage storage1 = storage.get();

		return storage1.getLink();
	}

}
