package signature.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.TextPosition;
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
import java.util.Objects;

@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
public class FileEdit {

    File file;

    PDDocument inptPdf;

    @Autowired
    SearchSubword searchSubword;


    public void convertFile(MultipartFile multipartFile) throws IOException {
        file = convert(multipartFile);
    }

    public void editFile2(MultipartFile imageFile) throws IOException {

        File image = convert(imageFile);

        inptPdf = PDDocument.load(file);


        //test---------------------------------------------------------------------------------------------------------

        searchSubword.printSubwordsImproved(inptPdf, "podp");
        //System.out.println(searchSubword.findSubwordsImproved(inptPdf, 1, "podpis"));

        //test---------------------------------------------------------------------------------------------------------


        //adding image
        PDPage firstPage = inptPdf.getPage(0);
        File imageJPG = pngToJpg(image);

        PDImageXObject image2 = PDImageXObject.createFromFile(imageJPG.getPath(), inptPdf);

        PDPageContentStream contentStream2 = new PDPageContentStream(inptPdf, firstPage, PDPageContentStream.AppendMode.APPEND, true, true);
        contentStream2.drawImage(image2, 105, 470, 100, 40);

        Logging.logger.info("Signature was added to PDF file");

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
