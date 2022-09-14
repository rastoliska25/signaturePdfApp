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

    @GetMapping("/download")
    public String download() {
        return "download";
    }

    MultipartFile file;

    @PostMapping("/receivePdf")
    public ResponseEntity<FileUploadResponse> uploadFiles(@RequestParam("file") MultipartFile multipartFile) throws IOException {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        long size = multipartFile.getSize();

        FileEdit.convertFile(multipartFile);

        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(fileName);
        response.setSize(size);
        Logging.logger.info(fileName + "  " + size);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<String> uploadFiles2() throws IOException {

        FileEdit.save();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/receiveImage")
    public ResponseEntity<FileUploadResponse> uploadImages(@RequestParam("file") MultipartFile multipartFile) throws IOException {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        long size = multipartFile.getSize();

        FileEdit.editFile2(multipartFile);

        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(fileName);
        response.setSize(size);
        Logging.logger.info(fileName + "  " + size);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
