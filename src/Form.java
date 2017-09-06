import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class Form extends JFrame {

    // Глобальные элементы
    JTable table;
    JTextField selectTF;
    JTextField funkTF;
    JComboBox lengthsCB;

    // Глобальны данные
    ArrayList<File> files = new ArrayList<File>();
    final String[] FUNKS = {"Регистр", "Удлинение", "Укорачивание"};
    FileTableModel model = new FileTableModel(files);
    //   String selectedFunk = null;

    public Form() {
        super("FNQE");
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Панель выбора дериктории
        JPanel selectPanel = new JPanel(new BorderLayout());
        JLabel selectLabel = new JLabel("Выбор папки сфайлами для редактирования");
        selectTF = new JTextField();
        selectTF.setEnabled(false);
        JButton selectBt = new JButton("Обзор");

        selectPanel.add(selectLabel, BorderLayout.NORTH);
        selectPanel.add(selectTF, BorderLayout.CENTER);
        selectPanel.add(selectBt, BorderLayout.EAST);
        add(selectPanel, BorderLayout.NORTH);

        // Панель выбора файлов
        table = new JTable(model);
        selectBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int response = fileChooser.showDialog(Form.this, null);
                String openPath = null;
                if (response == JFileChooser.APPROVE_OPTION) {
                    openPath = fileChooser.getSelectedFile().toString();
                    selectTF.setText(openPath);
                    setCatalog(openPath);
                }
            }
        });
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Панель функций
        JPanel funkPanel = new JPanel(new BorderLayout());
        final JComboBox selectFunkCB = new JComboBox(FUNKS);
        funkPanel.add(selectFunkCB, BorderLayout.WEST);

        // Панель регистра
        final JPanel regPanel = new JPanel(new FlowLayout());
        JButton toLowBt = new JButton("toLow");
        JButton toUpBt = new JButton("toUp");
        toLowBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                renameFiles(mods.LOW);
            }
        });
        toUpBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                renameFiles(mods.UP);
            }
        });
        regPanel.add(toLowBt);
        regPanel.add(toUpBt);
        // Панель удлинения
        final JPanel lengthPanel = new JPanel(new FlowLayout());
        funkTF = new JTextField(20);
        JButton prefixBt = new JButton("Префикс");
        JButton postfixBt = new JButton("Постфис");
        prefixBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                renameFiles(mods.PREFIX);
            }
        });
        postfixBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                renameFiles(mods.POSTFIX);
            }
        });
        lengthPanel.add(funkTF);
        lengthPanel.add(prefixBt);
        lengthPanel.add(postfixBt);
        // Панель укорачивания
        JPanel delPanel = new JPanel(new FlowLayout());
        Integer[] nums = new Integer[19];
        for (int i = 1; i <= 19; i++) nums[i - 1] = i;
        lengthsCB = new JComboBox(nums);
        JButton prefDelBt = new JButton("Удалить начальные символы");
        JButton postDelBt = new JButton("Удалить конечные символы");
        prefDelBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                renameFiles(mods.DELPREF);
            }
        });
        postDelBt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                renameFiles(mods.DELPOST);
            }
        });
        delPanel.add(lengthsCB);
        delPanel.add(prefDelBt);
        delPanel.add(postDelBt);


        final JPanel undefPanel = new JPanel(new CardLayout());
        undefPanel.add(regPanel, FUNKS[0]);
        undefPanel.add(lengthPanel, FUNKS[1]);
        undefPanel.add(delPanel, FUNKS[2]);
        funkPanel.add(undefPanel, BorderLayout.CENTER);
        final CardLayout layout = (CardLayout) undefPanel.getLayout();
        layout.show(undefPanel, FUNKS[0]);
        selectFunkCB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                layout.show(undefPanel, (String) selectFunkCB.getSelectedItem());
            }
        });
        add(funkPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void renameFiles(mods mod) {
        int[] is = table.getSelectedRows();
        String[] brokenName;
        String rName;
        String name;
        int extensionPosition;
        for (int i = 0; i < is.length; i++) {
            name = table.getValueAt(i, 0).toString();
            extensionPosition = name.lastIndexOf(".");
            String[] splitType = {name.substring(0, extensionPosition), name.substring(extensionPosition)};
            try {
                switch (mod) {
                    case LOW:
                        name = name.toLowerCase();
                        break;
                    case UP:
                        name = splitType[0].toUpperCase() + splitType[1];
                        break;
                    case PREFIX:
                        if (funkTF.getText().equals(""))
                            throw new EmptyFieldException();
                        else
                            name = funkTF.getText() + name;
                        break;
                    case POSTFIX:
                        if (funkTF.getText().equals(""))
                            throw new EmptyFieldException();
                        else {
                            name = splitType[0] + funkTF.getText() + splitType[1];
                        }
                        break;
                    case DELPREF:
                        name = name.substring((Integer) lengthsCB.getSelectedItem());
                        break;
                    case DELPOST:
                        name = name.substring(0, extensionPosition - (Integer) lengthsCB.getSelectedItem()) + splitType[1];
                        break;
                }
            } catch (EmptyFieldException excp) {
                JOptionPane.showMessageDialog(this, excp.getMessage(), "Пустое текстовое поле", JOptionPane.ERROR_MESSAGE);
            } catch (StringIndexOutOfBoundsException excp) {
                JOptionPane.showMessageDialog(this, excp.getMessage(), "Ошибка длинны удаления", JOptionPane.ERROR_MESSAGE);
            } catch (Exception excp){
                JOptionPane.showMessageDialog(this, excp.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
            rName = selectTF.getText()+ "//" + name;
            model.getFileAt(i).renameTo(new File(rName));
        }
        setCatalog(selectTF.getText());
        model.fireTableDataChanged();
    }

    private void setCatalog(String path) {
        try {
            File catalog = new File(path);
            File[] openCatalog = catalog.listFiles();
            files.clear();
            for (int i =0; i<openCatalog.length; i++)
                files.add(openCatalog[i]);

            model.fireTableDataChanged();
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(this, "В выбранной дериктории не найдено файлов.", "Пустая дериктория", JOptionPane.ERROR_MESSAGE);
        }
    }
}
