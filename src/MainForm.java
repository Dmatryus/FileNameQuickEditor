import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

class MainForm extends JFrame {

    private final String[] FUNKS = {"Регистр", "Удлинение", "Укорачивание", "Изменение подстрок", "Инкрименирование записей", "Удаление файлов"};
    private HashMap<String, JTextField> funkTF = new HashMap<>();
    // Глобальные элементы
    // Общие
    private JTable table;
    private JTextField selectTF;
    // Укорачивание
    private JComboBox<Integer> lengthsCB;
    // Инкрименирование
    private JCheckBox beginWriteCheckB;
    private JTextField initValTF;

    // Глобальны данные
    private ArrayList<File> files = new ArrayList<>();
    private JComboBox<String> selectVarAddInc;
    private FileTableModel model = new FileTableModel(files);
    private static HashSet<String> ignorStrings = new HashSet<>();
    private int sizeNumInc = 0;

    static HashSet<String> getIgnorStrings() {
        return ignorStrings;
    }
    //   String selectedFunk = null;

    static void setIgnorStrings(HashSet<String> ignorStrings) {
        MainForm.ignorStrings = ignorStrings;
    }

    MainForm() {
        super("FNQE");
        setSize(1100, 600);
        setMinimumSize(new Dimension(1100, 200));

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
                int response = fileChooser.showDialog(MainForm.this, null);
                String openPath;
                if (response == JFileChooser.APPROVE_OPTION) {
                    openPath = fileChooser.getSelectedFile().toString();
                    selectTF.setText(openPath);
                    setCatalog(openPath);
                }
            }
        });
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.setRowSorter(new TableRowSorter<>(model));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Панель функций
        JPanel funkPanel = new JPanel(new BorderLayout());
        final JComboBox<String> selectFunkComboB = new JComboBox<>(FUNKS);
        funkPanel.add(selectFunkComboB, BorderLayout.WEST);

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
        funkTF.put("extension", new JTextField(20));
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
        lengthPanel.add(funkTF.get("extension"));
        lengthPanel.add(prefixBt);
        lengthPanel.add(postfixBt);

        // Панель укорачивания
        JPanel delPanel = new JPanel(new FlowLayout());
        Integer[] nums = new Integer[19];
        for (int i = 1; i <= 19; i++) nums[i - 1] = i;
        lengthsCB = new JComboBox<>(nums);
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

        // Панель изменения подстрок
        JPanel substringPanel = new JPanel(new FlowLayout());
        funkTF.put("sub", new JTextField(20));
        funkTF.get("sub").setToolTipText("Подстрока, которую следует заменить в исходном названии");
        JLabel subLabel = new JLabel("заменить на");
        funkTF.put("newsub", new JTextField(20));
        funkTF.get("newsub").setToolTipText("Подстрока на которую замениться введённая");
        JButton delSubBt = new JButton("Изменить");
        delSubBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renameFiles(mods.SUB);
            }
        });
        substringPanel.add(funkTF.get("sub"));
        substringPanel.add(subLabel);
        substringPanel.add(funkTF.get("newsub"));
        substringPanel.add(delSubBt);

        // Панель инкриминирования
        JPanel incPanel = new JPanel(new FlowLayout());
        initValTF = new JTextField(5);
        initValTF.setToolTipText("Ввод начального значения");
        beginWriteCheckB = new JCheckBox("Писать число в начале конструкции");
        funkTF.put("inc", new JTextField(20));
        funkTF.get("inc").setToolTipText("Ввод текстовой части конструкции");
        String[] varAddInc = {"Префиксное добавление", "Постфиксное добавление", "Полная замена"};
        selectVarAddInc = new JComboBox<>(varAddInc);
        JButton exeBt = new JButton("Переименовать");
        exeBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int init = 0;
                if (!initValTF.getText().equals(""))
                    init = Integer.parseInt(initValTF.getText());
                sizeNumInc = getSizeInt(table.getSelectedRowCount() + init);
                renameFiles(mods.INC);
            }
        });
        incPanel.add(initValTF);
        incPanel.add(beginWriteCheckB);
        incPanel.add(funkTF.get("inc"));
        incPanel.add(selectVarAddInc);
        incPanel.add(exeBt);

        // Панель удаления файлов
        JPanel delFilePanel = new JPanel(new FlowLayout());
        JButton delFileBt = new JButton("Удалить файлы");
        delFileBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] is = table.getSelectedRows();
                String name, fullName;
                for (int i = 0; i < is.length; i++) {
                    name = table.getValueAt(i, 0).toString();
                    fullName = selectTF.getText() + "//" + name;
                    model.getFileAt(i).delete();
                    setCatalog(fullName);
                    model.fireTableDataChanged();
                }
            }
        });
        delFilePanel.add(delFileBt);

        final JPanel undefPanel = new JPanel(new CardLayout());
        undefPanel.add(regPanel, FUNKS[0]);
        undefPanel.add(lengthPanel, FUNKS[1]);
        undefPanel.add(delPanel, FUNKS[2]);
        undefPanel.add(substringPanel, FUNKS[3]);
        undefPanel.add(incPanel, FUNKS[4]);
        undefPanel.add(delFilePanel, FUNKS[5]);
        funkPanel.add(undefPanel, BorderLayout.CENTER);
        final CardLayout layout = (CardLayout) undefPanel.getLayout();
        layout.show(undefPanel, FUNKS[0]);
        selectFunkComboB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                layout.show(undefPanel, (String) selectFunkComboB.getSelectedItem());
            }
        });
        add(funkPanel, BorderLayout.SOUTH);

        // Меню
        JMenuBar menuBar = new JMenuBar();
        JMenu settingMenu = new JMenu("Настройки");
        JMenuItem ignoreMI = new JMenuItem("Список игнорируемых расширений файлов");
        ignoreMI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new IgnoreForm();
            }
        });

        settingMenu.add(ignoreMI);
        menuBar.add(settingMenu);
        setJMenuBar(menuBar);

        ignorStrings.add("db");     // Расширение временных файлов, которые создаются при изменении системой

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private int getSizeInt(int num) {
        int count = 0;
        while (num > 0) {
            count++;
            num /= 10;
        }
        return count;
    }

    private void renameFiles(mods mod) {
        int[] is = table.getSelectedRows();
        String rName;
        String name;

        int extensionPosition, inc = 0;
        for (int i = 0; i < is.length; i++) {
            name = table.getValueAt(i, 0).toString();
            extensionPosition = name.lastIndexOf(".");
            String[] splitType = {name.substring(0, extensionPosition), name.substring(extensionPosition)};

            if (!ignorStrings.contains(splitType[1].substring(1))) {
                try {
                    switch (mod) {
                        case LOW:
                            name = name.toLowerCase();
                            break;
                        case UP:
                            name = splitType[0].toUpperCase() + splitType[1];
                            break;
                        case PREFIX:
                            if (funkTF.get("extension").getText().equals(""))
                                throw new EmptyFieldException();
                            else
                                name = funkTF.get("extension").getText() + name;
                            break;
                        case POSTFIX:
                            if (funkTF.get("extension").getText().equals(""))
                                throw new EmptyFieldException();
                            else {
                                name = splitType[0] + funkTF.get("extension").getText() + splitType[1];
                            }
                            break;
                        case DELPREF:
                            name = name.substring((Integer) lengthsCB.getSelectedItem());
                            break;
                        case DELPOST:
                            name = name.substring(0, extensionPosition - (Integer) lengthsCB.getSelectedItem()) + splitType[1];
                            break;
                        case SUB:
                            name = splitType[0].replaceAll(funkTF.get("sub").getText(), funkTF.get("newsub").getText()) + splitType[1];
                            break;
                        case INC:
                            name = incName(inc, splitType[0]) + splitType[1];
                            inc++;
                            break;
                    }
                } catch (EmptyFieldException excp) {
                    JOptionPane.showMessageDialog(this, excp.getMessage(), "Пустое текстовое поле", JOptionPane.ERROR_MESSAGE);
                } catch (StringIndexOutOfBoundsException excp) {
                    JOptionPane.showMessageDialog(this, excp.getMessage(), "Ошибка длинны удаления", JOptionPane.ERROR_MESSAGE);
                } catch (Exception excp) {
                    JOptionPane.showMessageDialog(this, excp.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
                rName = selectTF.getText() + "//" + name;
                model.getFileAt(i).renameTo(new File(rName));
            }

        }
        setCatalog(selectTF.getText());
        model.fireTableDataChanged();
    }

    private void setCatalog(String path) {
        try {
            File catalog = new File(path);
            File[] openCatalog = catalog.listFiles();
            files.clear();
            Collections.addAll(files, openCatalog);

            model.fireTableDataChanged();
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(this, "В выбранной дериктории не найдено файлов.", "Пустая дериктория", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String incName(int i, String beginS) {
        // Начальное значение
        int inc = i;
        if (!(initValTF.getText().isEmpty() || initValTF.getText().equals("")))
            inc = Integer.getInteger(initValTF.getText()) + i;
        // Формирование конструкции
        String r = funkTF.get("inc").getText();
        String addString = "";
        for (int j = 0; j < sizeNumInc - getSizeInt(inc); j++)
            addString += '0';
        if (inc > 0)
            addString += Integer.toString(inc);
        if (beginWriteCheckB.isSelected())
            r = addString + r;
        else
            r += addString;
        switch (selectVarAddInc.getSelectedIndex()) {
            case 0:
                return r + beginS;
            case 1:
                return beginS + r;
            case 2:
                return r;
            default:
                return beginS;
        }
    }
}
