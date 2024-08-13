package com.example.contactapi.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.contactapi.domain.Doctor;

@Repository
public interface DoctorRepo extends JpaRepository<Doctor, String> {

    @Override
    Optional<Doctor> findById(String id);

}
