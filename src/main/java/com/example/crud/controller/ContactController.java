package com.example.crud.controller;

import com.example.crud.model.Contact;
import com.example.crud.repository.ContactRepository;
import com.example.crud.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @GetMapping
    public ResponseEntity<List<Contact>> listContacts(){
        var contacts = contactService.listContacts();
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/{contactId}")
    public ResponseEntity<Contact> getUserById(@PathVariable("contactId") String contactId){
        var contact = contactService.getContactById(contactId);
        if (contact.isPresent()) {
            return ResponseEntity.ok(contact.get());
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping
    public ResponseEntity<Contact> createContact (@RequestBody CreateContactDto createContactDto){
        var contactId = contactService.createContact(createContactDto);
        return ResponseEntity.created(URI.create("/contacts/" + contactId.toString())).build();
    }

    @PutMapping("/{contactId}")
    public ResponseEntity<Void> updateContactById(@PathVariable("contactId") String contactId,
                                                  @RequestBody CreateContactDto createContactDto){
        contactService.updateContactById(contactId, createContactDto);
        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<Void> deleteById(@PathVariable("contactId") String contactId) {
        contactService.deleteById(contactId);
        return ResponseEntity.noContent().build();
    }
}
