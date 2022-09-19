package signature.controller;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import signature.Logging;
import signature.model.FileEdit;
import signature.model.FileUploadResponse;

import java.io.*;
import java.util.*;

@Controller
public class TestController {

    public static HashMap<Integer, FileEdit> streamMap = new HashMap<Integer, FileEdit>();

    @GetMapping("/upload")
    public String upload(Model model) {
        Integer random = new Random().nextInt(100000000, 999999999);
        model.addAttribute("link", random);
        return "upload";
    }

    @GetMapping("/urls/{id}")
    public String urls(@PathVariable Integer id, Model model) {
        model.addAttribute("firstLink", "http://localhost:8080/first/" + id);

        model.addAttribute("secondLink", "http://localhost:8080/second/" + id);
        return "urls";
    }

    @GetMapping("/first/{id}")
    public String startFirst(@PathVariable Integer id, Model model) {
        model.addAttribute("link", id);
        return "index";
    }

    @GetMapping("/second/{id}")
    public String startSecond(@PathVariable Integer id, Model model) {
        model.addAttribute("link", id);
        return "indexSecond";
    }

    @GetMapping("/download/{id}")
    public String download(@PathVariable Integer id, Model model) {
        model.addAttribute("link", id);
        return "download";
    }

    @PostMapping("/receivePdf/{id}")
    public ResponseEntity<FileUploadResponse> uploadFiles(@PathVariable Integer id, @RequestParam("file") MultipartFile multipartFile) {

        FileEdit fileEdit = new FileEdit();

        streamMap.put(id, fileEdit);
        Logging.logger.info("File was added to hashmap with id: " + id);


        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        long size = multipartFile.getSize();

        try {
            streamMap.get(id).convertFile(multipartFile);
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


    @PostMapping("/receiveImageOne/{id}")
    public ResponseEntity<FileUploadResponse> uploadImageOne(@PathVariable Integer id, @RequestParam("file") MultipartFile multipartFile) {

        System.out.println(id);

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        long size = multipartFile.getSize();

        Logging.logger.info("Signature image was received: " + fileName + "  , size: " + size + " bytes");

        try {
            streamMap.get(id).editFile2(multipartFile, 1);
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

    @PostMapping("/receiveImageTwo/{id}")
    public ResponseEntity<FileUploadResponse> uploadImageTwo(@PathVariable Integer id, @RequestParam("file") MultipartFile multipartFile) {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        long size = multipartFile.getSize();

        Logging.logger.info("Signature image was received: " + fileName + "  , size: " + size + " bytes");

        try {
            streamMap.get(id).editFile2(multipartFile, 2);
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

    @GetMapping(value = "/downloadPdf/{id}")
    public ResponseEntity<byte[]> getPDF(@PathVariable Integer id) throws IOException {

        PDDocument document = new PDDocument();
        document = streamMap.get(id).save2();

        if (document == null) {
            Logging.logger.info("ERROR: Document to sign not found.");
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.save(byteArrayOutputStream);

        //document.close();

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
