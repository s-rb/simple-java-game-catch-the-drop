package ru.rb.list.s;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class GameWindow extends JFrame {

    private static GameWindow gameWindow;
    private static long lastFrameTime; // Переменная времени между кадрами
    private static Image background;
    private static Image gameOver;
    private static Image drop;
    // Переменные для движения капли
    private static float dropLeft = 200; // Коорд.Х левого верхнего угла
    private static float dropTop = -100; // Коорд.Y левого верхнего угла
    private static float dropVelocity = 100; // Скорость капли
    private static int score = 0; // Набранные очки

    public static void main(String[] args) throws IOException {
        background = ImageIO.read(GameWindow.class.getResourceAsStream("resources/background.jpg"));
        gameOver = ImageIO.read(GameWindow.class.getResourceAsStream("resources/gameOver.png"));
        drop = ImageIO.read(GameWindow.class.getResourceAsStream("resources/drop.png"));
        gameWindow = new GameWindow();
        gameWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // при закрытии окна закрывается программа
        gameWindow.setLocation(200, 100); // в этой точке появляется окно. От левого верхнего угла
        gameWindow.setSize(906, 478);
        gameWindow.setResizable(false);
        lastFrameTime = System.nanoTime();
        GameField gameField = new GameField();
        gameField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Координаты нажатия мыши
                int x = e.getX();
                int y = e.getY();
                float dropRight = dropLeft + drop.getWidth(null);
                float dropBottom = dropTop + drop.getHeight(null);
                boolean isDrop = x >= dropLeft && x <= dropRight && y >= dropTop && y<= dropBottom;
                if (isDrop) {
                    dropTop = -100; // Если каплю нажали, выводим ее за границу окна
                    dropLeft = (int) (Math.random() * (gameField.getWidth() - drop.getWidth(null))); // Переводим каплю по x в случайное место в границах окна
                    // увеличиваем скорость
                    dropVelocity += 7;
                    score++;
                    gameWindow.setTitle("Score: " + score); // Выводим очки в загаловке окна
                }

            }
        });
        gameWindow.add(gameField);
        gameWindow.setVisible(true);
    }

    private static void onRepaint(Graphics g) {
        long currentTime = System.nanoTime();
        float deltaTime = (currentTime - lastFrameTime) * 0.000000001f; // в секунды
        lastFrameTime = currentTime;
        dropTop = dropTop + dropVelocity * deltaTime;
        g.drawImage(background, 0, 0, null);
        g.drawImage(drop, (int) dropLeft, (int) dropTop, null);
        if (dropTop > gameWindow.getHeight()) g.drawImage(gameOver, 280, 120, null);
    }

    private static class GameField extends JPanel {
        // Когда отрисовывается граф.компонент, то у него внутри вызывается метод paintComponent, которому передается
        // Объект класса Graphics, с помощью которого он рисуется
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Сначала вызываем родительский метод
            onRepaint(g);
            repaint(); // без этого момента, paintComponent вызывается только когда требуется отрисовать панель
        }
    }
}
