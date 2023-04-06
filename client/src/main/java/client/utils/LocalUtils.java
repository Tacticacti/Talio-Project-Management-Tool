package client.utils;

import java.io.*;
import java.util.*;

import static client.utils.CustomizationUtils.customizationData;

public class LocalUtils {
    private Set<Long> boards;
    private File file;

    private String path;
    public boolean inited;

    public LocalUtils() { inited = false; }

    public String getPath() {
        return path;
    }

    public void setPath(String path) throws IOException {
        this.path = path;
        boards = new TreeSet<>();
        File folder = new File("data");
        folder.mkdir();
        path = path.substring(7);
        file = new File(folder, path + "@saved_boards.txt");
        if(!file.exists()) {
            file.createNewFile();
        }

        inited = true;
    }

    public void fetch() throws IOException {
        if(!inited) {
            throw new IOException("loader wasnt initialized");
        }
        boards.clear();
        Scanner scanner = new Scanner(file);
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            try {
                long id = Long.parseLong(line);
                boards.add(id);
            }
            catch(Exception e) {
                System.out.println("cannot cast: " + line + "\nAborting!");
            }
        }
    }

    public Set<Long> getBoards() {
        return boards;
    }

    public void add(Long id) throws IOException {
        if(isAdded(id))
            return;
        boards.add(id);
        write();
    }

    public void remove(Long id) throws IOException {
        if(!isAdded(id))
            return;
        boards.remove(id);
        write();
    }

    public boolean isAdded(Long id) {
        return boards.contains(id);
    }

    public void write() throws IOException {
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        for(Long id : boards) {
            bw.write(id.toString());
            bw.newLine();
        }
        bw.close();
    }

    public void reset() throws IOException {
        boards.clear();
        write();

        customizationData.clear();
        writeCustomization();

    }


    public static void writeCustomization() {

        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdir();
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("data/customization.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PrintWriter printWriter = new PrintWriter(fileWriter);

        for (Map.Entry<Long, String> entry : customizationData.entrySet()) {
            String key = String.valueOf(entry.getKey());
            String value = entry.getValue();
            printWriter.println(key + ":" + value);
        }

        printWriter.close();
        try {
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static void readCustomization() {
        customizationData.clear();

        try {
            File file = new File("data/customization.txt");

            if (!file.exists()) {
                return; // exit the method if the file doesn't exist
            }

            Scanner scanner = new Scanner(file);


            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                int index = line.indexOf(':');

                if (index != -1) {
                    String key = line.substring(0, index).trim();
                    String value = line.substring(index + 1).trim();
                    customizationData.put(Long.valueOf(key), value);
                }
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


}
