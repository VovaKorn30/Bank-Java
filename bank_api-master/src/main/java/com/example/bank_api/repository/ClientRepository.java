package com.example.bank_api.repository;

import com.example.bank_api.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findByLastnameAndFirstnameAndMiddlename(String lastname, String firstname, String middlename);


    @Query("UPDATE Client SET lastname = :l, firstname = :f, middlename = :m, age = :age WHERE id = :id")
    @Modifying
    void updateNameAndAge(Long id, String l, String f, String m, Integer age);
}
