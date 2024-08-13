package com.example.contactapi.resource;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.data.domain.Page;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.example.contactapi.constant.Constant.PHOTO_DIRECTORY;
import com.example.contactapi.domain.Doctor;
import com.example.contactapi.service.DoctorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorResource {
    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor) {
        return ResponseEntity.created(URI.create("/doctors/userID")).body(doctorService.createDoctor(doctor));
    }

    @GetMapping
    public ResponseEntity<Page<Doctor>> getDoctors(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size)
    {
        return ResponseEntity.ok().body(doctorService.getAllDoctors(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctor(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok().body(doctorService.getDoctorByID(id));
    }

    @PutMapping("/photo")
    public ResponseEntity<String> uploadPhoto(
        @RequestParam("id") String id,
        @RequestParam("file")MultipartFile file)
    {
        return ResponseEntity.ok().body(doctorService.uploadPhoto(id, file));
    }

    @GetMapping(path = "/image/{filename}", produces = { IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE })
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(PHOTO_DIRECTORY + filename));
    }
}