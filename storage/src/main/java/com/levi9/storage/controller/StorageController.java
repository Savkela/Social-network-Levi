package com.levi9.storage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.levi9.storage.model.Storage;
import com.levi9.storage.service.StorageService;

@RestController
@RequestMapping(value = "api/storage")
public class StorageController {

	@Autowired
	private StorageService storageService;

	@GetMapping("/{id}")
	public String getLinkById(@PathVariable(value = "id") Long id)
			 {

		String storage = storageService.getLinkById(id);
		return storage;
	}

}
