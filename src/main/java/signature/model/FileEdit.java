package signature.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class FileEdit {

    public static void editFile(MultipartFile multipartFile) throws IOException {

        File file = convert(multipartFile);

        PDDocument inptPdf = PDDocument.load(file);

        //adding image
        PDPage firstPage = inptPdf.getPage(0);
        PDImageXObject image2 = PDImageXObject.createFromFile("C:/projektySubory/signature.jpg", inptPdf);
        PDPageContentStream contentStream2 = new PDPageContentStream(inptPdf, firstPage, PDPageContentStream.AppendMode.APPEND, true, true);
        contentStream2.drawImage(image2, 105, 355, 100, 40);
        contentStream2.close();

        inptPdf.save("C:/projektySubory/newPDF.pdf");
        inptPdf.close();
        System.out.println("new pdf was created");

    }

    public static File convert(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));

        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }

        return convFile;
    }
}
