package com.khabaznia.bots.example.service

import com.khabaznia.bots.example.model.ExampleModel
import com.khabaznia.bots.example.model.ExampleModelEntry
import com.khabaznia.bots.example.repository.ExampleModelEntryRepository
import com.khabaznia.bots.example.repository.ExampleModelRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.khabaznia.bots.core.util.SessionUtil.currentUser

@Slf4j
@Service
class ExampleModelService {

    @Autowired
    private ExampleModelRepository repository
    @Autowired
    private ExampleModelEntryRepository entryRepository

    List<ExampleModel> getAll() {
        repository.findAll()
    }

    List<ExampleModelEntry> getAllEntries() {
        entryRepository.findAll()
    }

    List<ExampleModelEntry> getAllEntriesForCurrentUser() {
        entryRepository.getAllByUserCode(currentUser.code)
    }

    List<ExampleModelEntry> getAllWithNotEmptyAbbreviation() {
        entryRepository.getAllByAbbreviationNotNull()
    }
}
