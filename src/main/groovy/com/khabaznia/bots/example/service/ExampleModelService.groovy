package com.khabaznia.bots.example.service

import com.khabaznia.bots.example.model.ExampleModel
import com.khabaznia.bots.example.repository.ExampleModelRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Slf4j
@Service
class ExampleModelService {

    @Autowired
    private ExampleModelRepository repository

    List<ExampleModel> getAll() {
        repository.findAll()
    }
}
