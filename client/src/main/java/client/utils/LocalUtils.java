package client.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.Scanner;
import java.util.TreeSet;

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
    }
}
