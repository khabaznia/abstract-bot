package com.khabaznia.bots.example.repository

import com.khabaznia.bots.example.model.ExampleModelEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExampleModelEntryRepository extends JpaRepository<ExampleModelEntry, Long> {

    List<ExampleModelEntry> getAllByAbbreviationNotNull()

    List<ExampleModelEntry> getAllByUserCode(String userCode)
}