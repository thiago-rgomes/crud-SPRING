package com.example.crud.service;

import com.example.crud.controller.CreateContactDto;
import com.example.crud.model.Contact;
import com.example.crud.repository.ContactRepository;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    private ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public Long createContact(CreateContactDto createContactDto){
        var entity = new Contact(null, createContactDto.nome(), createContactDto.telefone(), createContactDto.email());

        var contactSaved = contactRepository.save(entity);
        return contactSaved.getId();

    }

    public Optional<Contact> getContactById(String contactId){

        return contactRepository.findById(Long.parseLong(contactId));
    }

    public List<Contact> listContacts(){
        return contactRepository.findAll();
    }

    public void updateContactById(String contactId,
                                  CreateContactDto createContactDto){
        var id = Long.parseLong(contactId);

        var contactEntity = contactRepository.findById(id);

        if (contactEntity.isPresent()){
            var contact = contactEntity.get();

            if (createContactDto.nome() != null){
                contact.setNome(createContactDto.nome());
            }
            if (createContactDto.telefone() != null){
                contact.setTelefone(createContactDto.telefone());
            }
            if (createContactDto.email() != null){
                contact.setEmail(createContactDto.email());
            }

            contactRepository.save(contact);
        }

    }

    public void deleteById(String contactId){
        var id = Long.parseLong(contactId);
        var contactExists = contactRepository.existsById(id);

        if (contactExists) {
            contactRepository.deleteById(id);
        }
    }
}
