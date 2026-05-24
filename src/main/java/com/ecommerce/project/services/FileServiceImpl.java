package com.ecommerce.project.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{

    @Override
    public String uploadImage(String path, MultipartFile imageFile) throws IOException {
        String originalFileName = imageFile.getOriginalFilename();

        String randomId = UUID.randomUUID().toString();

        String imageFileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf(".")));

        String imageFilePath = path + File.separator + imageFileName;

        File file = new File(path);

        if(!file.exists()) {
            file.mkdir();
        }

        Files.copy(imageFile.getInputStream(), Paths.get(imageFilePath));

        return imageFileName;
    }
}
