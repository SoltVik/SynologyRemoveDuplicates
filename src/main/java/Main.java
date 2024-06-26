import net.lingala.zip4j.exception.ZipException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    static String PATH_DIR = "/volume1/homes/system/logs/synoreport/DublicatFinder";
    static String PATH_CSV = "/csv/";
    static String ARCHIVE_NAME = "duplicate_file.csv.zip";

    public static void main(String[] args) throws ZipException {
        int deletedFiles = 0;
        DuplicatedFiles df = new DuplicatedFiles();

        String lastDir = PATH_DIR + "/" +df.getMostRecentDirectory(PATH_DIR);
        String lastFile = lastDir + PATH_CSV + ARCHIVE_NAME;

        Path zipFilePath = Paths.get(lastFile);
        Path unzipFilePath = Paths.get(lastDir+PATH_CSV);

        df.unzip(zipFilePath, unzipFilePath);
        String filePath = lastDir + PATH_CSV + "duplicate_file.csv";

        df.readData(filePath);

    }
}