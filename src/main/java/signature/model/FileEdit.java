package signature.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.web.multipart.MultipartFile;
import signature.Logging;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class FileEdit {

    static File file;

    static PDDocument inptPdf;

    public static void convertFile(MultipartFile multipartFile) throws IOException {
        file = convert(multipartFile);
    }

    public static void editFile2() throws IOException {

        inptPdf = PDDocument.load(file);

        //adding image
        PDPage firstPage = inptPdf.getPage(0);
        pngToJpg();

        PDImageXObject image2 = PDImageXObject.createFromFile("C:/projektySubory/signature.jpg", inptPdf);

        PDPageContentStream contentStream2 = new PDPageContentStream(inptPdf, firstPage, PDPageContentStream.AppendMode.APPEND, true, true);
        contentStream2.drawImage(image2, 105, 355, 100, 40);
        contentStream2.close();

        //inptPdf.save("C:/projektySubory/newPDF.pdf");

        /*
        String home = System.getProperty("user.home");
        inptPdf.save(home+"/Downloads/newPDF.pdf");

        inptPdf.close();
        System.out.println("new pdf was created");


        delete();

         */
    }




    public static File convert(MultipartFile multipartFile) throws IOException {
        File convFile = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(multipartFile.getBytes());
        } catch (Exception e) {
            Logging.logger.info(String.valueOf(e));
        }

        return convFile;
    }

    public static void pngToJpg() {
        try {
            File input = new File("X:/stahovanie/signature.png");
            File output = new File("C:/projektySubory/signature.jpg");

            BufferedImage image = ImageIO.read(input);
            BufferedImage result = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            result.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
            ImageIO.write(result, "jpg", output);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete() {
        String fileName = "X:/stahovanie/signature.png";
        try {
            Files.delete(Paths.get(fileName));
        } catch (IOException e) {
            Logging.logger.info(String.valueOf(e));
        }
    }

    public static void save() throws IOException {
        String home = System.getProperty("user.home");
        inptPdf.save(home+"/Downloads/newPDF.pdf");

        inptPdf.close();
        System.out.println("new pdf was created");

        delete();
    }
}
