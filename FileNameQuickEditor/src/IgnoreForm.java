import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashSet;
import java.util.List;

public class IgnoreForm extends JDialog {

    JTextField addTF;
    IgnoreForm tf;
    HashSet dataSet;
    JList list;

    IgnoreForm() {
        super();
        tf = this;
        setTitle("Список игнорируемых расширений");
        setBounds(200, 200, 500, 400);
        setModal(true);
        setLayout(new BorderLayout());
        dataSet = new HashSet<>(MainForm.getIgnorStrings());

        // Меню
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        JMenuItem saveItem = new JMenuItem("Сохранить список");
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                String extension = "sis";
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Сохранение списка игнорируемых исключений", extension);
                fileChooser.setFileFilter(filter);
                String savePath;
                int response = fileChooser.showSaveDialog(IgnoreForm.this);
                if (response == JFileChooser.APPROVE_OPTION) {
                    savePath = fileChooser.getSelectedFile().toString();
                    // Проверка на создание нового файла или перезапись старого
                    if (!savePath.contains(".".concat(extension)))
                        savePath += ".".concat(extension);
                    try (FileWriter writer = new FileWriter(savePath, false)) {
                        writer.append((dataSet.toString()));
                    } catch (IOException exception) {
                        JOptionPane.showMessageDialog(IgnoreForm.this, exception.getMessage());
                    }
                }
            }
        });
        JMenuItem loadItem = new JMenuItem("Загрузить список");
        loadItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                String extension = "sis";
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Сохранение списка игнорируемых исключений", extension);
                fileChooser.setFileFilter(filter);
                String loadPath;
                int response = fileChooser.showSaveDialog(IgnoreForm.this);
                if (response == JFileChooser.APPROVE_OPTION) {
                    loadPath = fileChooser.getSelectedFile().getPath();
                    try (BufferedReader br = new BufferedReader(new FileReader(loadPath))){
                        String s = br.readLine();
                        dataSet.clear();
                        for (String is: s.split(", ")){
                            dataSet.add(clearingExtension(is));
                        }
                        list.setListData(dataSet.toArray());
                    }catch (Exception exception){
                        JOptionPane.showMessageDialog(IgnoreForm.this, exception.getMessage());
                    }
                }
            }
        });

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Центр
        list = new JList<>(MainForm.getIgnorStrings().toArray());
        dataSet = new HashSet<>(MainForm.getIgnorStrings());
        add(new JScrollPane(list), BorderLayout.CENTER);

        // Низ
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        final JButton delBt = new JButton("Удалить");
        delBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> selectedStrings = list.getSelectedValuesList();
                for (int i = 0; i < selectedStrings.size(); i++) {
                    dataSet.remove(selectedStrings.get(i));
                    list.setListData(dataSet.toArray());
                }
            }
        });
        c.weighty = 1;
        buttonPanel.add(delBt, c);
        JPanel addPanel = new JPanel(new FlowLayout());
        addTF = new JTextField(10);
        addTF.setToolTipText("Введите расширение файла без точки");
        JButton addBt = new JButton("Добавить");
        addBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dataSet.add(clearingExtension(addTF.getText().toLowerCase()));
                list.setListData(dataSet.toArray());
                addTF.setText("");
            }
        });
        addPanel.setBorder(BorderFactory.createTitledBorder("Добавление исключений"));
        addPanel.add(addTF);
        addPanel.add(addBt);
        c.weighty = 2;
        buttonPanel.add(addPanel, c);
        JButton confirmBt = new JButton("Принять исключения");
        confirmBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainForm.setIgnorStrings(dataSet);
                tf.dispose();
            }
        });
        c.weighty = 1;
        buttonPanel.add(confirmBt, c);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);

        // Панель добавления исключения
    }

    // Функция отчистки расширения от посторонних символов
    private String clearingExtension(String string) {
        char[] symbols = {'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm'};
        StringBuilder builder = new StringBuilder(string), r = new StringBuilder();
        for (int i = 0; i < builder.length(); i++)
            for (int j = 0; j < symbols.length; j++)
                if (builder.charAt(i) == symbols[j])
                    r.append(builder.charAt(i));
        return r.toString();
    }
}
