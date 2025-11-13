import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    private JFrame frame;
    private JTextField display;
    private String currentNumber = "";
    private String previousNumber = "";
    private String operation = "";
    private boolean isNewNumber = true;

    public Main() {
        frame = new JFrame("Калькулятор");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 400);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);

        // Создаем дисплей
        display = new JTextField("0");
        display.setFont(new Font("Arial", Font.BOLD, 24));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        frame.add(display, BorderLayout.NORTH);

        // Создаем панель с кнопками используя GridBagLayout для гибкости
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Массив кнопок в виде двумерного массива
        String[][] buttonLabels = {
            {"C", "±", "%", "/"},
            {"7", "8", "9", "*"},
            {"4", "5", "6", "-"},
            {"1", "2", "3", "+"}
        };

        // Добавляем кнопки для первых 4 строк
        for (int row = 0; row < buttonLabels.length; row++) {
            for (int col = 0; col < buttonLabels[row].length; col++) {
                JButton btn = new JButton(buttonLabels[row][col]);
                btn.setFont(new Font("Arial", Font.BOLD, 18));
                btn.addActionListener(new ButtonClickListener());
                
                gbc.gridx = col;
                gbc.gridy = row;
                gbc.gridwidth = 1;
                
                buttonPanel.add(btn, gbc);
            }
        }
        
        // Последняя строка с особым расположением: 0 (2 колонки), . (1 колонка), = (1 колонка)
        gbc.gridy = 4;
        
        // Кнопка "0" - занимает 2 колонки
        JButton btn0 = new JButton("0");
        btn0.setFont(new Font("Arial", Font.BOLD, 18));
        btn0.addActionListener(new ButtonClickListener());
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        buttonPanel.add(btn0, gbc);
        
        // Кнопка "."
        JButton btnDot = new JButton(".");
        btnDot.setFont(new Font("Arial", Font.BOLD, 18));
        btnDot.addActionListener(new ButtonClickListener());
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        buttonPanel.add(btnDot, gbc);
        
        // Кнопка "=" - занимает 1 колонку
        JButton btnEquals = new JButton("=");
        btnEquals.setFont(new Font("Arial", Font.BOLD, 18));
        btnEquals.addActionListener(new ButtonClickListener());
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        buttonPanel.add(btnEquals, gbc);

        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.matches("[0-9]")) {
                // Цифры
                if (isNewNumber) {
                    currentNumber = command;
                    isNewNumber = false;
                } else {
                    currentNumber += command;
                }
                display.setText(currentNumber);
            } else if (command.equals(".")) {
                // Точка для десятичных чисел
                if (isNewNumber) {
                    currentNumber = "0.";
                    isNewNumber = false;
                } else if (!currentNumber.contains(".")) {
                    currentNumber += ".";
                }
                display.setText(currentNumber);
            } else if (command.equals("C")) {
                // Очистка
                currentNumber = "";
                previousNumber = "";
                operation = "";
                display.setText("0");
                isNewNumber = true;
            } else if (command.matches("[+\\-*/]")) {
                // Операции
                if (!previousNumber.isEmpty() && !currentNumber.isEmpty() && !operation.isEmpty()) {
                    calculate();
                }
                previousNumber = currentNumber.isEmpty() ? "0" : currentNumber;
                operation = command;
                currentNumber = "";
                isNewNumber = true;
            } else if (command.equals("=")) {
                // Равно
                if (!previousNumber.isEmpty() && !currentNumber.isEmpty() && !operation.isEmpty()) {
                    calculate();
                    operation = "";
                    previousNumber = "";
                }
            } else if (command.equals("±")) {
                // Смена знака
                if (!currentNumber.isEmpty() && !currentNumber.equals("0")) {
                    if (currentNumber.startsWith("-")) {
                        currentNumber = currentNumber.substring(1);
                    } else {
                        currentNumber = "-" + currentNumber;
                    }
                    display.setText(currentNumber);
                }
            } else if (command.equals("%")) {
                // Процент
                if (!currentNumber.isEmpty()) {
                    try {
                        double value = Double.parseDouble(currentNumber);
                        value = value / 100.0;
                        currentNumber = String.valueOf(value);
                        display.setText(currentNumber);
                        isNewNumber = true;
                    } catch (NumberFormatException ex) {
                        display.setText("Ошибка");
                    }
                }
            }
        }
    }

    private void calculate() {
        try {
            double prev = Double.parseDouble(previousNumber);
            double curr = Double.parseDouble(currentNumber);
            double result = 0;

            switch (operation) {
                case "+":
                    result = prev + curr;
                    break;
                case "-":
                    result = prev - curr;
                    break;
                case "*":
                    result = prev * curr;
                    break;
                case "/":
                    if (curr == 0) {
                        display.setText("Ошибка: деление на 0");
                        currentNumber = "";
                        previousNumber = "";
                        operation = "";
                        isNewNumber = true;
                        return;
                    }
                    result = prev / curr;
                    break;
            }

            // Форматируем результат
            if (result == (long) result) {
                currentNumber = String.valueOf((long) result);
            } else {
                currentNumber = String.valueOf(result);
            }
            display.setText(currentNumber);
            isNewNumber = true;
        } catch (NumberFormatException ex) {
            display.setText("Ошибка");
            currentNumber = "";
            previousNumber = "";
            operation = "";
            isNewNumber = true;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });
    }
}
