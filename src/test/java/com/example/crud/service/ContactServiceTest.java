package com.example.crud.service;

import com.example.crud.controller.CreateContactDto;
import com.example.crud.model.Contact;
import com.example.crud.repository.ContactRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactService contactService;

    @Captor
    private ArgumentCaptor<Contact> contactArgumentCaptor;

    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;

    @Nested
    class createContact {
        @Test
        @DisplayName("Should create a contact with sucess")
        void shouldCreateAContactWithSuccess(){

            // Arrange
            var contact = new Contact(
                    (long)5,
                    "usuario",
                    "12345678",
                    "email@email.com"
            );
            doReturn(contact).when(contactRepository).save(contactArgumentCaptor.capture());
            var input = new CreateContactDto("Melissa","12345","mel@gmail");

            // Act
            var output = contactService.createContact(input);

            //Assert
            assertNotNull(output);

            var contactCaptured = contactArgumentCaptor.getValue();

            assertEquals(input.nome(), contactCaptured.getNome());
            assertEquals(input.telefone(), contactCaptured.getTelefone());
            assertEquals(input.email(), contactCaptured.getEmail());

        }
        @Test
        @DisplayName("Should throw exception when error occurs")
        void shouldThrowExceptionWhenErrorOccurs() {

            // Arrange
            doThrow(new RuntimeException()).when(contactRepository).save(any());
            var input = new CreateContactDto("Melissa","12345","mel@gmail");

            // Act & Assert
            assertThrows(RuntimeException.class, () -> contactService.createContact(input));


        }
    }

    @Nested
    class getContactById {
        @Test
        @DisplayName("Should get contact by id with success when optional is present")
        void shouldGetContactByIdWithSuccessWhenOptionalIsPresent() {

            // Arrange
            var contact = new Contact(
                    (long)5,
                    "usuario",
                    "12345678",
                    "email@email.com"
            );
            doReturn(Optional.of(contact)).when(contactRepository).findById(longArgumentCaptor.capture());

            // Act
            var output = contactService.getContactById(contact.getId().toString());

            // Assert
            assertTrue(output.isPresent());
            assertEquals(contact.getId(), longArgumentCaptor.getValue());
        }

        @Test
        @DisplayName("Should get contact by id with success when optional is empty")
        void shouldGetContactByIdWithSuccessWhenOptionalIsEmpty() {

            // Arrange
            long contactId = (long) (Math.random() * 100);
            String contactIdString = Long.toString(contactId);

            doReturn(Optional.empty()).when(contactRepository).findById(longArgumentCaptor.capture());


            // Act
            var output = contactService.getContactById(contactIdString);

            // Assert
            assertTrue(output.isEmpty());
            assertEquals(contactId, longArgumentCaptor.getValue());
        }

    }
    @Nested
    class listContacts {
        @Test
        @DisplayName("Should return all contacts with success")
        void shouldReturnAllContactsWithSuccess(){

            // Arrange
            var contact = new Contact(
                    (long)5,
                    "usuario",
                    "12345678",
                    "email@email.com"
            );
            var contactList = List.of(contact);
            doReturn(contactList).when(contactRepository).findAll();

            // Act
            var output = contactService.listContacts();

            // Assert
            assertNotNull(output);
            assertEquals(contactList.size(), output.size());

        }
    }

    @Nested
    class deleteById {
        @Test
        @DisplayName("should delete contact with success when user exists")
        void shouldDeleteContactWithSuccessWhenUserExists() {

            // Arrange
            var contact = new Contact(
                    (long) (Math.random() * 100),
                    "usuario",
                    "12345678",
                    "email@email.com"
            );
            doReturn(true)
                    .when(contactRepository)
                    .existsById(longArgumentCaptor.capture());

            doNothing()
                    .when(contactRepository)
                    .deleteById(longArgumentCaptor.capture());
            var contactId = (long) (Math.random() * 100);
            var contactIdString = Long.toString(contactId);

            // Act
            contactService.deleteById(contactIdString);

            // Assert
            var idList = longArgumentCaptor.getAllValues();
            assertEquals(contactId, idList.get(0));
            assertEquals(contactId, idList.get(1));

            verify(contactRepository, times(1)).existsById(idList.get(0));
            verify(contactRepository, times(1)).existsById(idList.get(1));
        }

        @Test
        @DisplayName("should not delete contact with success when user not exists")
        void shouldNotDeleteContactWithSuccessWhenUserNotExists() {

            // Arrange
            doReturn(false)
                    .when(contactRepository)
                    .existsById(longArgumentCaptor.capture());

            var contactId = (long) (Math.random() * 100);
            var contactIdString = Long.toString(contactId);

            // Act
            contactService.deleteById(contactIdString);

            // Assert
            assertEquals(contactId, longArgumentCaptor.getValue());

            verify(contactRepository, times(1)).existsById(longArgumentCaptor.getValue());
            verify(contactRepository, times(0)).deleteById(any());
        }
    }

    @Nested
    class updateContactById {

        @Test
        @DisplayName("Should update contact by id with success when user exists and has username and password")
        void shouldUpdateContactByIdWhenContactExistsAndHasUsernameAndPassword() {

            // Arrange
            var updatedContactDto = new CreateContactDto(
                    "newusername",
                    "99999",
                    "newemail@gmail.com"
            );
            var contact = new Contact(
                    (long) (Math.random() * 100),
                    "usuario",
                    "12345678",
                    "email@email.com"
            );
            doReturn(Optional.of(contact))
                    .when(contactRepository)
                    .findById(longArgumentCaptor.capture());
            doReturn(contact)
                    .when(contactRepository)
                    .save(contactArgumentCaptor.capture());

            // Act
            contactService.updateContactById(contact.getId().toString(), updatedContactDto);

            // Assert
            assertEquals(contact.getId(), longArgumentCaptor.getValue());

            var contactCaptured = contactArgumentCaptor.getValue();

            assertEquals(updatedContactDto.nome(), contactCaptured.getNome());
            assertEquals(updatedContactDto.telefone(), contactCaptured.getTelefone());
            assertEquals(updatedContactDto.email(), contactCaptured.getEmail());

            verify(contactRepository, times(1))
                    .findById(longArgumentCaptor.getValue());
            verify(contactRepository, times(1))
                    .save(contact);
        }

        @Test
        @DisplayName("Should not update contact when user not exists")
        void shouldNotUpdateContactWhenContactNotExists() {

            // Arrange
            var updatedContactDto = new CreateContactDto(
                    "newusername",
                    "99999",
                    "newemail@gmail.com"
            );
            var contactId = (long) (Math.random() * 100);
            var contactIdString = Long.toString(contactId);

            doReturn(Optional.empty())
                    .when(contactRepository)
                    .findById(longArgumentCaptor.capture());


            // Act
            contactService.updateContactById(contactIdString, updatedContactDto);

            // Assert
            assertEquals(contactId, longArgumentCaptor.getValue());


            verify(contactRepository, times(1))
                    .findById(longArgumentCaptor.getValue());
            verify(contactRepository, times(0))
                    .save(any());
        }

    }
}
