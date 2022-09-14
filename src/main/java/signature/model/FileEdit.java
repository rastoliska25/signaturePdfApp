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
import java.util.Arrays;
import java.util.Objects;

public class FileEdit {

    static File file;

    static PDDocument inptPdf;

    public static void convertFile(MultipartFile multipartFile) throws IOException {
        System.out.println(multipartFile.getSize());
        //System.out.println(Arrays.toString(multipartFile.getBytes()));
        file = convert(multipartFile);
    }

    public static void editFile2(MultipartFile imageFile) throws IOException {

        File image = convert(imageFile);

        inptPdf = PDDocument.load(file);

        //adding image
        PDPage firstPage = inptPdf.getPage(0);
        File imageJPG = pngToJpg(image);

        PDImageXObject image2 = PDImageXObject.createFromFile(imageJPG.getPath(), inptPdf);

        PDPageContentStream contentStream2 = new PDPageContentStream(inptPdf, firstPage, PDPageContentStream.AppendMode.APPEND, true, true);
        contentStream2.drawImage(image2, 105, 355, 100, 40);
        contentStream2.close();
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

    public static File pngToJpg(File imageFile) {
        File output = null;
        try {
            File input = imageFile;
            output = new File("C:/projektySubory/signature.jpg");

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
        return output;
    }

    public static void save() throws IOException {
        String home = System.getProperty("user.home");
        inptPdf.save(home + "/Downloads/newPDF.pdf");

        inptPdf.close();
        System.out.println("new pdf was created");

    }
}
