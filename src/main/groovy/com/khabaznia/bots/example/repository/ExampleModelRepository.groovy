package com.khabaznia.bots.example.repository

import com.khabaznia.bots.example.model.ExampleModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExampleModelRepository extends JpaRepository<ExampleModel, Long> {
}