import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

public class DuplicatedFiles {
    static String COMMA_DELIMITER = "\"";
    static String SLASH_DELIMITER = "/";

    public void deleteDuplicate(String data) {
        String[] cell;
        cell = data.split(COMMA_DELIMITER);

        String filePath = cell[3];
        Path path = Paths.get(filePath);
        deleteFile(path, true);
    }

    public void readData(String file) {
        Path csvPath = Paths.get(file);

        try (Stream<String> stream = Files.lines(csvPath, StandardCharsets.UTF_16LE)) {
            stream
                    .skip(1)
                    .forEach(this::deleteDuplicate);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean deleteFile (Path file, boolean duplicate) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dateText = formatter.format(date);
        try {
            if (duplicate) {
                String[] values = file.toString().split(SLASH_DELIMITER);

                if (values.length > 4) {
                    if (values[3].equalsIgnoreCase("photo")) {
                        if (Files.deleteIfExists(file)) {
                            System.out.println(dateText + " Delete file: " + file);
                        }
                        return true;
                    }
                }
            } else {
                if (Files.deleteIfExists(file)) {
                    System.out.println(dateText + " Delete file: " + file);
                }
                return true;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getMostRecentDirectory(String directoriesPath) {
        File file = new File(directoriesPath);

        if (!file.exists()) {
            return "";
        }

        String[] directories = file.list((current, name) -> new File(current, name).isDirectory());

        DateFormat dFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ssa");
        for (int i = 0; i < directories.length; i++) {
            File f = new File(directoriesPath + SLASH_DELIMITER + directories[i]);
            Date d = new Date(f.lastModified());
            directories[i]+= "|" + dFormat.format(d);
        }

        List<Date> dates = new ArrayList<>();
        String dFmt = "MM/dd/yyyy hh:mm:ssa";
        SimpleDateFormat formatter = new SimpleDateFormat(dFmt, Locale.ENGLISH);
        for (String directory : directories) {
            try {
                String dateStrg = directory.substring(directory.indexOf("|") + 1, directory.length());
                Date d = formatter.parse(dateStrg);
                dates.add(d);
            } catch (ParseException ex) {
                System.out.println(ex.getMessage());
            }
        }

        Date mostRecentDate = Collections.max(dates);

        DateFormat targetFormat = new SimpleDateFormat("EEEE - MMMM dd, yyyy - hh:mm:ssa");
        String mrd = targetFormat.format(mostRecentDate);

        String mostRecentFolder = "";
        String theRecentFolderDate = "";
        for (String directory : directories) {
            try {
                String datePart = directory.substring(directory.indexOf("|") + 1, directory.length());
                Date dirDate = formatter.parse(datePart);
                theRecentFolderDate = targetFormat.format(dirDate);
                if (theRecentFolderDate.equals(mrd)) {
                    mostRecentFolder = directory.substring(0, directory.indexOf("|"));
                    break;
                }
            } catch (ParseException ex) {
                System.out.println(ex.getMessage());
            }
        }

        return mostRecentFolder;
    }

    public void unzip(Path source, Path target) throws ZipException {
        new ZipFile(source.toFile()).extractAll(target.toString());
    }
}
