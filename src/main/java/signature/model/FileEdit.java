package signature.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.TextPosition;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import signature.Logging;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
public class FileEdit {

    File file;

    PDDocument inptPdf;

    @Autowired
    SearchSubword searchSubword;


    public void convertFile(MultipartFile multipartFile) throws IOException {
        file = convert(multipartFile);
        inptPdf = PDDocument.load(file);
    }


    public void editFile2(MultipartFile imageFile, Integer id) throws IOException {

        File image = convert(imageFile);

        List<Float> listResult = null;
        //getting coordinates + page number
        if (id == 1) {
            listResult = searchSubword.printSubwordsImprovedList(inptPdf, "podpis1");
        } else {
            listResult = searchSubword.printSubwordsImprovedList(inptPdf, "podpis2");
        }

        float x = listResult.get(0);
        float y = listResult.get(1);
        Float pageNumber = listResult.get(2);

        //adding image to position
        PDPage firstPage = inptPdf.getPage((int) (pageNumber - 1));
        File imageJPG = pngToJpg(image);

        PDImageXObject image2 = PDImageXObject.createFromFile(imageJPG.getPath(), inptPdf);

        PDPageContentStream contentStream2 = new PDPageContentStream(inptPdf, firstPage, PDPageContentStream.AppendMode.APPEND, true, true);
        contentStream2.drawImage(image2, x, 825 - y, 100, 40);

        if (id == 1) {
            Logging.logger.info("First signature was added to PDF file to coordinates x: " + x + " y: " + (825 - y));
        } else {
            Logging.logger.info("Second signature was added to PDF file to coordinates x: " + x + " y: " + (825 - y));
        }
        contentStream2.close();
    }

    public File convert(MultipartFile multipartFile) throws IOException {
        File convFile = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(multipartFile.getBytes());
        } catch (Exception e) {
            Logging.logger.info(String.valueOf(e));
        }

        return convFile;
    }

    public File pngToJpg(File imageFile) {
        File output = null;
        try {
            output = File.createTempFile("signature", ".jpg");

            BufferedImage image = ImageIO.read(imageFile);
            BufferedImage result = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            result.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
            ImageIO.write(result, "jpg", output);

            Logging.logger.info("Signature image was converted from PNG to JPG");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    public PDDocument save2() throws IOException {
        return inptPdf;
    }
}
