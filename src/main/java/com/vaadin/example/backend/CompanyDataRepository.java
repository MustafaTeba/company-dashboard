package com.vaadin.example.backend;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyDataRepository extends JpaRepository<CompanyData, Long> {

}
