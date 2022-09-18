package signature.controller;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import signature.Logging;
import signature.model.FileEdit;
import signature.model.FileUploadResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Controller
public class TestController {

    @Autowired
    FileEdit fileEdit;

    public static HashMap<String, FileEdit> streamMap = new HashMap<String, FileEdit>();

    public static List<FileEdit> fileEditList = new ArrayList<>();

    public static List<Integer> codeListId = new ArrayList<>();

    public static List<String> codeList = new ArrayList<>();

    public static Integer editID = 0;

    @GetMapping("/upload")
    public String upload(Model model) {
        //model.addAttribute("link", streamMap.get("0"));
        model.addAttribute("link", 123);
        return "upload";
    }

    @GetMapping("/urls/{id}")
    public String urls(@PathVariable String id, Model model) {
        model.addAttribute("firstLink", "http://localhost:8080/first/" + id);

        model.addAttribute("secondLink", "http://localhost:8080/second/" + id);
        return "urls";
    }

    @GetMapping("/first/{id}")
    public String startFirst(@PathVariable String id, Model model) {
        model.addAttribute("firstLink", id);
        return "index";
    }

    @GetMapping("/second/{id}")
    public String startSecond(@PathVariable String id) {
        return "indexSecond";
    }

    @GetMapping("/download")
    public String download() {
        return "download";
    }

    @PostMapping("/receivePdf/{id}")
    public ResponseEntity<FileUploadResponse> uploadFiles(@PathVariable String id, @RequestParam("file") MultipartFile multipartFile) {

        streamMap.put(id,fileEdit);
        fileEditList.add(fileEdit);
        Logging.logger.info("File was added to hashmap with id: " + id);


        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        long size = multipartFile.getSize();

        try {
            //fileEditList.get(editID).convertFile(multipartFile);
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
    public ResponseEntity<FileUploadResponse> uploadImageOne(@PathVariable String id, @RequestParam("file") MultipartFile multipartFile) {

        System.out.println(id);

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        long size = multipartFile.getSize();

        Logging.logger.info("Signature image was received: " + fileName + "  , size: " + size + " bytes");

        try {
            //fileEditList.get(editID).editFile2(multipartFile, 1);
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

    @PostMapping("/receiveImageTwo")
    public ResponseEntity<FileUploadResponse> uploadImageTwo(@RequestParam("file") MultipartFile multipartFile) {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        long size = multipartFile.getSize();

        Logging.logger.info("Signature image was received: " + fileName + "  , size: " + size + " bytes");

        try {
            fileEditList.get(editID).editFile2(multipartFile, 2);
            //streamMap.get(Integer.valueOf(id)).editFile2(multipartFile, 1);
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

        PDDocument document = new PDDocument();

        document = fileEditList.get(editID).save2();

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
