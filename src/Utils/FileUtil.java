package Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    private File file;
    private List<String> content;
    public String teamName, host;
    public int playerNumber;
    public long port;

    public FileUtil(File targetFie) {
        file = targetFie;
        content = new ArrayList<>();
        try {
            content = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // for (String string : content) {
        // System.out.println(string);
        // }
    }

    public void getProjectInfo() {
        playerNumber = Integer.parseInt(content.get(9).substring(12));
        teamName = content.get(11).substring(6, content.get(11).length() - 1);
        host = content.get(12).substring(6, content.get(12).length() - 1);
        port = Long.parseLong(content.get(13).substring(5));
    }

    public void setProjectInfo(String teamName, String host, int playerNumber, long port) {
        this.teamName = teamName;
        this.host = host;
        this.playerNumber = playerNumber;
        this.port = port;
        content.set(9, "NUM_PLAYERS=" + playerNumber);
        content.set(11, "team=\"" + teamName + "\"");
        content.set(12, "host=\"" + host + "\"");
        content.set(13, "port=" + String.valueOf(port));
        try {
            FileWriter cleaner = new FileWriter(file, false);
            cleaner.write("");
            cleaner.flush();
            cleaner.close();
            FileWriter fw = new FileWriter(file, true);
            for (String string : content) {
                fw.write(string + "\n");
                fw.flush();
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getStrategy() {
        
    }
}


