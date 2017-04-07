import com.sun.pdfview.PDFPage;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PDFStreamConverter {

    public PDFStreamConverter(PDFPage page)
    {
        int CurrentPageNumber = page.getPageNumber();
        int width = 2400;
        int height = 2800;

        Rectangle rect = new Rectangle(0, 0, (int) page.getBBox().getWidth(), (int) page.getBBox().getHeight());

        Image PDFImage = page.getImage(width, height,
                rect,
                null,
                true,
                true
        );

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D bufImageGraphics = bufferedImage.createGraphics();
        bufImageGraphics.drawImage(PDFImage, 0, 0, null);

        SaveIamge(bufferedImage, CurrentPageNumber);
        writeTessText(bufferedImage, CurrentPageNumber);

    }
    private void SaveIamge(BufferedImage image, int pagenum)
    {
        try {
            File ImageFile = new File(PDFReader.OutImageName  + String.valueOf(pagenum) + ".png");
            ImageIO.write(image, "png", ImageFile);
        } catch (IOException e) {
            e.printStackTrace();}
    }

    private void writeTessText(BufferedImage img, int CurrentPageNumber)
    {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(PDFReader.OutTessFileName + CurrentPageNumber + ".txt"));
            Tesseract tess = new Tesseract();
            tess.setLanguage("eng");
            String text = tess.doOCR(img);
            bw.append(text);
            bw.close();
        } catch (TesseractException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
