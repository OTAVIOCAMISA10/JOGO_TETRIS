import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Gerenciador {

    // área do jogo principal
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    // Mino
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;
    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();
    private final Lock lock = new ReentrantLock();

    public static int dropInterval = 60;
    boolean gameOver;

    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

    int nivel = 1;
    int linhas;
    int pontos;

    public Gerenciador() {

        // quadro da área de jogo
        left_x = (Painel.WIDTH / 2) - (WIDTH / 2);
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + (WIDTH / 2) - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;

        NEXTMINO_X = right_x + 175;
        NEXTMINO_Y = top_y + 500;

        // inicial Mino
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
    }

    private Mino pickMino() {

        Mino mino = null;
        int i = new Random().nextInt(7);

        switch (i) {
            case 0: mino = new Mino_L1(); break;
            case 1: mino = new Mino_L2(); break;
            case 2: mino = new Mino_Square(); break;
            case 3: mino = new Mino_Bar(); break;
            case 4: mino = new Mino_T(); break;
            case 5: mino = new Mino_Z1(); break;
            case 6: mino = new Mino_Z2(); break;
        }
        return mino;
    }

    public void update() {

        if (!currentMino.active) {
            lock.lock(); // Acquiring the lock
            try {
                staticBlocks.add(currentMino.b[0]);
                staticBlocks.add(currentMino.b[1]);
                staticBlocks.add(currentMino.b[2]);
                staticBlocks.add(currentMino.b[3]);

                // GAME OVER
                if (currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y) {
                    gameOver = true;
                    Painel.music.stop();
                    Painel.se.play(2, false);
                }

                currentMino.deactivating = false;

                currentMino = nextMino;
                currentMino.setXY(MINO_START_X, MINO_START_Y);
                nextMino = pickMino();
                nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

                checkDelete();
            } finally {
                lock.unlock(); // Releasing the lock
            }
        } else {
            currentMino.update();
        }
    }

    private void checkDelete() {
        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount = 0;

        lock.lock(); // Acquiring the lock
        try {
            while (x < right_x && y < bottom_y) {
                for (int i = 0; i < staticBlocks.size(); i++) {
                    if (staticBlocks.get(i).x == x && staticBlocks.get(i).y == y) {
                        blockCount++;
                    }
                }

                x += Block.SIZE;

                if (x == right_x) {
                    if (blockCount == 12) {
                        effectCounterOn = true;
                        effectY.add(y);

                        for (int i = staticBlocks.size() - 1; i > -1; i--) {
                            if (staticBlocks.get(i).y == y) {
                                staticBlocks.remove(i);
                            }
                        }

                        lineCount++;
                        linhas++;

                        if (linhas % 10 == 0 && dropInterval > 1) {
                            nivel++;
                            if (dropInterval > 10) {
                                dropInterval -= 10;
                            } else {
                                dropInterval -= 1;
                            }
                        }

                        for (int i = 0; i < staticBlocks.size(); i++) {
                            if (staticBlocks.get(i).y < y) {
                                staticBlocks.get(i).y += Block.SIZE;
                            }
                        }
                    }

                    blockCount = 0;
                    x = left_x;
                    y += Block.SIZE;
                }
            }

            if (lineCount > 0) {
                Painel.se.play(1, false);
                int singleLinesScore = 10 + nivel;
                pontos += singleLinesScore + lineCount;
            }
        } finally {
            lock.unlock(); // Releasing the lock
        }
    }

    public void draw(Graphics2D g2) {

        // quadro tetris
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x - 4, top_y - 4, WIDTH + 8, HEIGHT + 8);

        // quadro próximo
        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.drawRect(x, y, 200, 200);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("PRÓXIMO", x + 30, y + 60);

        // quadro pontuação
        g2.drawRect(x, top_y, 250, 300);
        x += 40;
        y = top_y + 90;
        g2.drawString("NÍVEL: " + nivel, x, y);
        y += 70;
        g2.drawString("LINHAS: " + linhas, x, y);
        y += 70;
        g2.drawString("PONTOS: " + pontos, x, y);

        if (currentMino != null) {
            currentMino.draw(g2);
        }

        nextMino.draw(g2);

        lock.lock(); // Acquiring the lock
        try {
            for (int i = 0; i < staticBlocks.size(); i++) {
                staticBlocks.get(i).draw(g2);
            }
        } finally {
            lock.unlock(); // Releasing the lock
        }

        if (effectCounterOn) {
            effectCounter++;

            g2.setColor(Color.red);
            for (int i = 0; i < effectY.size(); i++) {
                g2.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);
            }

            if (effectCounter == 10) {
                effectCounterOn = false;
                effectCounter = 0;
                effectY.clear();
            }
        }

        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(50f));
        if (gameOver) {
            x = left_x + 25;
            y = top_y + 320;
            g2.drawString("GAME OVER", x, y);
        }
        if (Movimento.pausePressed) {
            x = left_x + 70;
            y = top_y + 320;
            g2.drawString("PAUSADO", x, y);
        }

        x = 65;
        y = top_y + 400;
        g2.setColor(Color.white);
        g2.setFont(new Font("Times New Roman", Font.ITALIC, 100));
        g2.drawString("TETRIS", x, y);
    }
}
