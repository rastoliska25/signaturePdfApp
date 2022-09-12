package signature.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import signature.Logging;
import signature.model.FileEdit;
import signature.model.FileUploadResponse;

import java.io.IOException;
import java.util.Objects;

@Controller
public class TestController {

    @GetMapping("/")
    public String start() {
        return "index";
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }






    MultipartFile file;


    @PostMapping("/test")
    public ResponseEntity<FileUploadResponse> uploadFiles(@RequestParam("file") MultipartFile multipartFile) throws IOException {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        long size = multipartFile.getSize();

        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(fileName);
        response.setSize(size);

        file = multipartFile;

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/uploadFile")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        long size = multipartFile.getSize();

        FileEdit.editFile(multipartFile);

        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(fileName);
        response.setSize(size);
        Logging.logger.info("testovanie do suboru consumed through poll: {}", 10000);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
