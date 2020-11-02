import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.*;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class MainFrame extends JFrame
{
    // Начальные размеры окна приложения
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    // Объект диалогового окна для выбора файлов
    private JFileChooser fileChooser = null;
    // Пункты меню
    private final JCheckBoxMenuItem showAxisMenuItem;
    private final JCheckBoxMenuItem showMarkersMenuItem;
    // Компонент-отображатель графика
    private final GraphicsDisplay display = new GraphicsDisplay();
    // Флаг, указывающий на загруженность данных графика
    private boolean fileLoaded = false;

    public MainFrame()
    {
        // Вызов конструктора предка Frame
        super("Построение графиков функций на основе заранее подготовленных файлов");
        // Установка размеров окна
        setSize(WIDTH, HEIGHT);
        // Отцентрировать окно приложения на экране
        setLocationRelativeTo(null);
        // Развѐртывание окна на весь экран
        setExtendedState(MAXIMIZED_BOTH);
        // Создать и установить полосу меню
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        // Добавить пункт меню "Файл"
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);
        // Создать действие по открытию файла
        Action openGraphicsAction = new AbstractAction("Открыть файл с графиком")
        {
            public void actionPerformed(ActionEvent event)
            {
                if (fileChooser == null)
                {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION)
                    openGraphics(fileChooser.getSelectedFile());
            }
        };
        // Добавить соответствующий элемент меню
        fileMenu.add(openGraphicsAction);
        // Создать пункт меню "График"
        JMenu graphicsMenu = new JMenu("График");
        menuBar.add(graphicsMenu);
        // Создать действие для реакции на активацию элемента "Показывать оси координат"
        Action showAxisAction = new AbstractAction("Показывать оси координат")
        {
            public void actionPerformed(ActionEvent event)
            {
                // свойство showAxis класса GraphicsDisplay истина, если элемент меню
                // showAxisMenuItem отмечен флажком, и ложь - в противном случае
                display.setShowAxis(showAxisMenuItem.isSelected());
            }
        };
        showAxisMenuItem = new JCheckBoxMenuItem(showAxisAction);
        // Добавить соответствующий элемент в меню
        graphicsMenu.add(showAxisMenuItem);

        // Элемент по умолчанию включен (отмечен флажком)
        showAxisMenuItem.setSelected(true);
         // Повторить действия для элемента "Показывать маркеры точек"
        Action showMarkersAction = new AbstractAction("Показывать маркеры точек")
        {
            public void actionPerformed(ActionEvent event)
            {
                // по аналогии с showAxisMenuItem
                display.setShowMarkers(showMarkersMenuItem.isSelected());
            }
        };
        showMarkersMenuItem =new JCheckBoxMenuItem(showMarkersAction);
        graphicsMenu.add(showMarkersMenuItem);
        // Элемент по умолчанию включен (отмечен флажком)
        showMarkersMenuItem.setSelected(true);
        // Зарегистрировать обработчик событий, связанных с меню "График"
        graphicsMenu.addMenuListener(new GraphicsMenuListener());
        // Установить GraphicsDisplay в цент граничной компоновки
        getContentPane().add(display, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    // Считывание данных графика из существующего файла
    protected void openGraphics(File selectedFile)
    {
        try
        {
            // Шаг 1 - Открыть поток чтения данных, связанный с входным файловым потоком
            DataInputStream in = new DataInputStream(
                    new BufferedInputStream(
                            new FileInputStream(selectedFile)));
            /* Шаг 2 - Зная объѐм данных в потоке ввода можно вычислить,
             * сколько памяти нужно зарезервировать в массиве:
             * * Всего байт в потоке - in.available() байт;
             * * Размер одного числа Double - Double.SIZE бит, или Double.SIZE/8 байт;
             * * Так как числа записываются парами, то число пар меньше в 2 раза */
            Double[][] graphicsData = new Double[in.available() / (Double.SIZE / 8) / 2][];

            // Шаг 3 - Цикл чтения данных (пока в потоке есть данные)
            int i = 0;
            while (in.available() > 0)
            {
                // Первой из потока читается координата точки X
                double x = in.readDouble();
                // Затем - значение графика Y в точке X
                double y = in.readDouble();
                // Прочитанная пара координат добавляется в массив
                graphicsData[i++] = new Double[]{x, y};
            }
            graphicsData = new Double[][]{new Double[]{-2.0,4.0},new Double[]{-1.7894736842105263,3.2022160664819945},new Double[]{-1.5789473684210527,2.4930747922437675},new Double[]{-1.368421052631579,1.8725761772853187},new Double[]{-1.1578947368421053,1.3407202216066483},new Double[]{-0.9473684210526316,0.8975069252077563},new Double[]{-0.736842105263158,0.5429362880886428},new Double[]{-0.5263157894736843,0.27700831024930755},new Double[]{-0.3157894736842106,0.09972299168975075},new Double[]{-0.10526315789473695,0.011080332409972322},new Double[]{0.10526315789473673,0.011080332409972275},new Double[]{0.3157894736842106,0.09972299168975075},new Double[]{0.5263157894736841,0.27700831024930733},new Double[]{0.7368421052631575,0.5429362880886421},new Double[]{0.9473684210526314,0.8975069252077559},new Double[]{1.1578947368421053,1.3407202216066483},new Double[]{1.3684210526315788,1.872576177285318},new Double[]{1.5789473684210522,2.493074792243766},new Double[]{1.789473684210526,3.2022160664819936},new Double[]{2.0,4.0}};
            // Шаг 4 - Проверка, имеется ли в списке в результате чтения хотя бы одна пара координат
            if (graphicsData.length > 0)
            {
                // Да - установить флаг загруженности данных
                fileLoaded = true;
                // Вызывать метод отображения графика
                display.showGraphics(graphicsData);
            }
            // Шаг 5 - Закрыть входной поток
            in.close();
        } catch (FileNotFoundException ex)
        {
            // В случае исключительной ситуации типа "Файл не найден" показать сообщение об ошибке
            JOptionPane.showMessageDialog(MainFrame.this, "Указанный файл не найден", "Ошибка загрузки данных",
                    JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex)
        {
            // В случае ошибки ввода из файлового потока показать сообщение об ошибке
            JOptionPane.showMessageDialog(MainFrame.this, "Ошибка чтения координат точек из файла", "Ошибка загрузки " +
                    "данных", JOptionPane.WARNING_MESSAGE);
        }
    }
// Класс-слушатель событий, связанных с отображением меню
private class GraphicsMenuListener implements MenuListener
{
    // Обработчик, вызываемый перед показом меню
    public void menuSelected(MenuEvent e)
    {
        // Доступность или недоступность элементов меню "График" определяется загруженностью данных
        showAxisMenuItem.setEnabled(fileLoaded);
        showMarkersMenuItem.setEnabled(fileLoaded);
    }

    // Обработчик, вызываемый после того, как меню исчезло с экрана
    public void menuDeselected(MenuEvent e)
    {
    }

    // Обработчик, вызываемый в случае отмены выбора пункта меню (очень редкая ситуация)
    public void menuCanceled(MenuEvent e)
    {
    }
}
}