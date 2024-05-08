package com.example.crud.controller;

import com.example.crud.model.Contact;
import com.example.crud.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @GetMapping
    public List<Contact> list(){
        return contactRepository.findAll();
    }

    @PostMapping
    public void saveContact(@RequestBody Contact contact){
        contactRepository.save(contact);

    }

    @PutMapping
    public void updateContact(@RequestBody Contact contact){
        if(contact.getId() > 0){
            contactRepository.save(contact);
        } else {
            throw new IllegalArgumentException("O contato não existe");
        }

    }

    @DeleteMapping
    public void deleteContact(@RequestBody Contact contact){
        if(contact.getId() > 0){
            contactRepository.deleteById(contact.getId());
        } else {
            throw new IllegalArgumentException("O contato não existe");
        }

    }
}
