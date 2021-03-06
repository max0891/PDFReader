import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

public class PDFReader {
    public static String InputPDFFilename = "Acura Integra Service Manual 1997.pdf";
    public static String OutPDFFilePath = InputPDFFilename;
    public static String OutTessFileName = "texts/Acura Integra Service Manual 1997_";
    public static String OutImageName = "images/image_";
    public static final int THREADSNUM = 8;

    static {
        File textfolder = new File(OutTessFileName.substring(0, OutTessFileName.indexOf('/')));
        if (!textfolder.exists())
            textfolder.mkdir();
        File imagefolder = new File(OutImageName.substring(0, OutImageName.indexOf('/')));
        if (!imagefolder.exists())
            imagefolder.mkdir();
    }
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        try {
            PDFSplitter splitter = new PDFSplitter(InputPDFFilename,OutPDFFilePath.replace(".pdf","/"));
            PDFBookmarks bookmarks = new PDFBookmarks(InputPDFFilename,OutPDFFilePath.replace(".pdf", "/Bookmarks.txt") , splitter.getPages());

            PDFFile pdffile;
            int CountOfPages = 0;
            RandomAccessFile raf = new RandomAccessFile(InputPDFFilename, "r");

            FileChannel channel = raf.getChannel();
            ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

            pdffile = new PDFFile(buf);
            CountOfPages = pdffile.getNumPages();
            CountOfPages = 100;

            ArrayList<PDFPage> list = new ArrayList<PDFPage>(CountOfPages);
            IntStream.range(0,CountOfPages).forEach((i) -> list.add(i,pdffile.getPage(i + 1))); //pages start from 1
//            list.stream().parallel().forEach((page) -> new PDFStreamConverter(page));
// 
            ForkJoinPool forkJoinPool = new ForkJoinPool(THREADSNUM);
            forkJoinPool.submit(() ->
                    list.stream().parallel().forEach((page) -> new PDFStreamConverter(page))
            ).get();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        long finish = System.currentTimeMillis();
        long time = finish - start;
        System.out.println(time);

    }

}
