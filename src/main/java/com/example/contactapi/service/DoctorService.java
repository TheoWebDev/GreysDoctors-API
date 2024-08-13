package com.example.contactapi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.example.contactapi.constant.Constant.PHOTO_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.contactapi.domain.Doctor;
import com.example.contactapi.repo.DoctorRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j // log.info instead of System.out.println("");
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepo doctorRepo;

    public Page<Doctor> getAllDoctors(int page, int size) {
        return doctorRepo.findAll(PageRequest.of(page, size, Sort.by("name")));
    }

    public Doctor getDoctorByID(String id) {
        return doctorRepo.findById(id).orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    public Doctor createDoctor(Doctor doctor) {
        return doctorRepo.save(doctor);
    }

    public void deleteDoctor(Doctor doctor) {
        //
    }

    public String uploadPhoto(String id, MultipartFile file) {
        log.info("Saving picture for user ID: {}", id);
        Doctor doctor = getDoctorByID(id);
        String photoUrl = photoFunction.apply(id, file);
        doctor.setPhotoUrl(photoUrl);
        doctorRepo.save(doctor);
        return photoUrl;
    }

    private final Function<String, String> fileExtension = filename -> Optional.of(filename).filter(name -> name.contains("."))
        .map(name -> "." + name.substring(filename.lastIndexOf(".") + 1)).orElse(".png");

    private final BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {
        String filename = id + fileExtension.apply(image.getOriginalFilename());
        try {
            Path fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();
            if(!Files.exists(fileStorageLocation)) { Files.createDirectories(fileStorageLocation); }
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(filename), REPLACE_EXISTING);
            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/doctors/image/" + filename).toUriString();
        } catch (IOException exception) {
            throw new RuntimeException("Unable to save image");
        }
    };
}
