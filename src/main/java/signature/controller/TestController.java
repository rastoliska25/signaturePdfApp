package signature.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import signature.Logging;
import signature.model.FileDownloadUtil;
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


    @GetMapping("/test2")
    public ResponseEntity<String> uploadFiles() throws IOException {

        FileEdit.editFile2();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<String> uploadFiles2() throws IOException {

        FileEdit.save();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    //cez postman
    @PostMapping("/uploadFile")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        long size = multipartFile.getSize();

        FileEdit.convertFile(multipartFile);

        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(fileName);
        response.setSize(size);
        Logging.logger.info(fileName + "  " + size);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/downloadFile/{fileCode}")
    public ResponseEntity<?> downloadFile(@PathVariable("fileCode") String fileCode) {
        FileDownloadUtil downloadUtil = new FileDownloadUtil();

        Resource resource = null;
        try {
            resource = downloadUtil.getFileAsResource(fileCode);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (resource == null) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }

    @PostMapping("/receiveImage")
    public ResponseEntity<FileUploadResponse> uploadImages(@RequestParam("image") MultipartFile multipartFile) throws IOException {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        long size = multipartFile.getSize();

        //FileEdit.convertFile(multipartFile);

        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(fileName);
        response.setSize(size);
        Logging.logger.info(fileName + "  " + size);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}
