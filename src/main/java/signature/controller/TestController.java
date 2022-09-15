package signature.controller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import signature.Logging;
import signature.exception.ApiRequestException;
import signature.model.FileEdit;
import signature.model.FileUploadResponse;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public String download() throws ApiRequestException {
        return "download";
    }

    @Autowired
    FileEdit fileEdit;


    @PostMapping("/receivePdf")
    public ResponseEntity<FileUploadResponse> uploadFiles(@RequestParam("file") MultipartFile multipartFile) {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        long size = multipartFile.getSize();

        try {
            fileEdit.convertFile(multipartFile);
        } catch (IOException ex) {
            Logging.logger.info("ERROR: Input PDF not found:\n");
            ex.printStackTrace();
            System.exit(1);
        }

        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(fileName);
        response.setSize(size);
        Logging.logger.info("PDF was received: " + fileName + "  size: " + size + " bytes");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/receiveImage")
    public ResponseEntity<FileUploadResponse> uploadImages(@RequestParam("file") MultipartFile multipartFile) {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        long size = multipartFile.getSize();

        Logging.logger.info("Signature image was received: " + fileName + "  , size: " + size + " bytes");

        try {
            fileEdit.editFile2(multipartFile);
        } catch (IOException ex) {
            Logging.logger.info("ERROR: Image file not found:\n");
            ex.printStackTrace();
            System.exit(1);
        }

        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(fileName);
        response.setSize(size);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/downloadPdf")
    public ResponseEntity<byte[]> getPDF() throws IOException {

        PDDocument document = null;

        document = fileEdit.save2();

        if (document == null) {
            Logging.logger.info("ERROR: Document to sign not found.");
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.save(byteArrayOutputStream);
        document.close();
        InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        byte[] bytes = IOUtils.toByteArray(inputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        String filename = "output.pdf";

        Logging.logger.info("PDF was downloaded: " + filename);

        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
}
