import javax.swing.table.AbstractTableModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class FileTableModel extends AbstractTableModel {

    private HashMap<String, File> helpMap;
    private ArrayList<File> files;
    private String path;
    private String fileTime = null;

    public FileTableModel() {
        super();
    }

    public FileTableModel(ArrayList<File> files) {
        super();
        this.files = files;
        helpMap = new HashMap<>();
    }

    public void initHelpMap(){
        for (File file : files)
            helpMap.put(file.getName(), file);
    }

    public int getRowCount() {
        return files.size();
    }

    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Имя файла";
            case 1:
                return "Время создания";
            default:
                return "+";
        }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return files.get(rowIndex).getName();
            case 1:
                return getFileTime(rowIndex);
            default:
                return null;
        }
    }

    private Object getFileTime(int rowIndex) {
        try {
            Path file = Paths.get(files.get(rowIndex).getPath());
            BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
            fileTime = attrs.creationTime().toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return fileTime;
        }
    }

    public File getFileAtName(String fileName) {
        return helpMap.get(fileName);
    }
}
