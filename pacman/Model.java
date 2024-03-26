package pacman;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Model extends JPanel implements ActionListener {

    private Dimension d;
    private final Font smallFont = new Font("Arial", Font.BOLD, 14);
    private boolean inGame = false;
    private boolean dying = false;

    private final int blockSize = 24;
    private final int numBlocks = 15;
    private final int screenSize = numBlocks * blockSize;
    private final int maxGhosts = 12;
    private final int pacmanSpeed = 6;

    private int numGhosts = 6;
    private int lives, score;
    private int[] dx, dy;
    private int[] ghostX, ghostY, ghostDX, ghostDY, ghostSpeed;

    private Image heart, ghost;
    private Image up, down, left, right;

    private int pacmanX, pacmanY, pacmanDX, pacmanDY;
    private int redDX, redDY;

    private final short levelData[] = {
            19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
            17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
            0,  0,  0,  0,  0,  0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
            19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
            17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20,
            17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20,
            21, 0,  0,  0,  0,  0,  0,   0, 17, 16, 16, 16, 16, 16, 20,
            17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
    };

    private final int validSpeeds[] = {1, 2, 3, 4, 6, 8};
    private final int maxSpeed = 6;

    private int currentSpeed = 3;
    private short[] screenData;
    private Timer timer;

    public Model() {

        loadImages();
        initVariables();
        addKeyListener(new TAdapter());
        setFocusable(true);
        initGame();
    }


    private void loadImages() {
        down = new ImageIcon("down.gif").getImage();
        up = new ImageIcon("up.gif").getImage();
        left = new ImageIcon("left.gif").getImage();
        right = new ImageIcon("right.gif").getImage();
        ghost = new ImageIcon("holmerRainbow.gif").getImage();
        heart = new ImageIcon("heart.png").getImage();

    }
    private void initVariables() {

        screenData = new short[numBlocks * numBlocks];
        d = new Dimension(400, 400);
        ghostX = new int[maxGhosts];
        ghostDX = new int[maxGhosts];
        ghostY = new int[maxGhosts];
        ghostDY = new int[maxGhosts];
        ghostSpeed = new int[maxGhosts];
        dx = new int[4];
        dy = new int[4];

        timer = new Timer(40, this);
        timer.start();
    }

    private void playGame(Graphics2D g2d) {

        if (dying) {

            death();

        } else {

            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }
    }

    private void showIntroScreen(Graphics2D g2d) {

        String start = "Press SPACE to start and Use arrows to move!";
        g2d.setColor(Color.yellow);
        g2d.drawString(start, (screenSize)/16, 150);
    }

    private void drawScore(Graphics2D g) {
        g.setFont(smallFont);
        g.setColor(new Color(5, 181, 79));
        String s = "Score: " + score;
        g.drawString(s, screenSize / 2 + 96, screenSize + 16);

        for (int i = 0; i < lives; i++) {
            g.drawImage(heart, i * 28 + 8, screenSize + 1, this);
        }
    }

    private void checkMaze() {

        int i = 0;
        boolean finished = true;

        while (i < numBlocks * numBlocks && finished) {

            if ((screenData[i]) != 0) {
                finished = false;
            }

            i++;
        }

        if (finished) {

            score += 50;

            if (numGhosts < maxGhosts) {
                numGhosts++;
            }

            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }

            initLevel();
        }
    }

    private void death() {

        lives--;

        if (lives == 0) {
            inGame = false;
        }

        continueLevel();
    }

    private void moveGhosts(Graphics2D g2d) {

        int pos;
        int count;

        for (int i = 0; i < numGhosts; i++) {
            if (ghostX[i] % blockSize == 0 && ghostY[i] % blockSize == 0) {
                pos = ghostX[i] / blockSize + numBlocks * (int) (ghostY[i] / blockSize);

                count = 0;

                if ((screenData[pos] & 1) == 0 && ghostDX[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 2) == 0 && ghostDY[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[pos] & 4) == 0 && ghostDX[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 8) == 0 && ghostDY[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screenData[pos] & 15) == 15) {
                        ghostDX[i] = 0;
                        ghostDY[i] = 0;
                    } else {
                        ghostDX[i] = -ghostDX[i];
                        ghostDY[i] = -ghostDY[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    ghostDX[i] = dx[count];
                    ghostDY[i] = dy[count];
                }

            }

            ghostX[i] = ghostX[i] + (ghostDX[i] * ghostSpeed[i]);
            ghostY[i] = ghostY[i] + (ghostDY[i] * ghostSpeed[i]);
            drawGhost(g2d, ghostX[i] + 1, ghostY[i] + 1);

            if (pacmanX > (ghostX[i] - 12) && pacmanX < (ghostX[i] + 12)
                    && pacmanY > (ghostY[i] - 12) && pacmanY < (ghostY[i] + 12)
                    && inGame) {

                dying = true;
            }
        }
    }

    private void drawGhost(Graphics2D g2d, int x, int y) {
        g2d.drawImage(ghost, x, y, this);
    }

    private void movePacman() {

        int pos;
        short ch;

        if (pacmanX % blockSize == 0 && pacmanY % blockSize == 0) {
            pos = pacmanX / blockSize + numBlocks * (int) (pacmanY / blockSize);
            ch = screenData[pos];

            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);
                score++;
            }

            if (redDX != 0 || redDY != 0) {
                if (!((redDX == -1 && redDY == 0 && (ch & 1) != 0)
                        || (redDX == 1 && redDY == 0 && (ch & 4) != 0)
                        || (redDX == 0 && redDY == -1 && (ch & 2) != 0)
                        || (redDX == 0 && redDY == 1 && (ch & 8) != 0))) {
                    pacmanDX = redDX;
                    pacmanDY = redDY;
                }
            }

           
            if ((pacmanDX == -1 && pacmanDY == 0 && (ch & 1) != 0)
                    || (pacmanDX == 1 && pacmanDY == 0 && (ch & 4) != 0)
                    || (pacmanDX == 0 && pacmanDY == -1 && (ch & 2) != 0)
                    || (pacmanDX == 0 && pacmanDY == 1 && (ch & 8) != 0)) {
                pacmanDX = 0;
                pacmanDY = 0;
            }
        }
        pacmanX = pacmanX + pacmanSpeed * pacmanDX;
        pacmanY = pacmanY + pacmanSpeed * pacmanDY;
    }

    private void drawPacman(Graphics2D g2d) {

        if (redDX == -1) {
            g2d.drawImage(left, pacmanX + 1, pacmanY + 1, this);
        } else if (redDX == 1) {
            g2d.drawImage(right, pacmanX + 1, pacmanY + 1, this);
        } else if (redDY == -1) {
            g2d.drawImage(up, pacmanX + 1, pacmanY + 1, this);
        } else {
            g2d.drawImage(down, pacmanX + 1, pacmanY + 1, this);
        }
    }

    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;

        for (y = 0; y < screenSize; y += blockSize) {
            for (x = 0; x < screenSize; x += blockSize) {

                g2d.setColor(new Color(0,72,251));
                g2d.setStroke(new BasicStroke(5));

                if ((levelData[i] == 0)) {
                    g2d.fillRect(x, y, blockSize, blockSize);
                }

                if ((screenData[i] & 1) != 0) {
                    g2d.drawLine(x, y, x, y + blockSize - 1);
                }

                if ((screenData[i] & 2) != 0) {
                    g2d.drawLine(x, y, x + blockSize - 1, y);
                }

                if ((screenData[i] & 4) != 0) {
                    g2d.drawLine(x + blockSize - 1, y, x + blockSize - 1,
                            y + blockSize - 1);
                }

                if ((screenData[i] & 8) != 0) {
                    g2d.drawLine(x, y + blockSize - 1, x + blockSize - 1,
                            y + blockSize - 1);
                }

                if ((screenData[i] & 16) != 0) {
                    g2d.setColor(new Color(255,255,255));
                    g2d.fillOval(x + 10, y + 10, 6, 6);
                }

                i++;
            }
        }
    }

    private void initGame() {

        lives = 3;
        score = 0;
        initLevel();
        numGhosts = 6;
        currentSpeed = 3;
    }

    private void initLevel() {

        int i;
        for (i = 0; i < numBlocks * numBlocks; i++) {
            screenData[i] = levelData[i];
        }

        continueLevel();
    }

    private void continueLevel() {

        int dx = 1;
        int random;

        for (int i = 0; i < numGhosts; i++) {

            ghostY[i] = 4 * blockSize; 
            ghostX[i] = 4 * blockSize;
            ghostDY[i] = 0;
            ghostDX[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            ghostSpeed[i] = validSpeeds[random];
        }

        pacmanX = 7 * blockSize;  
        pacmanY = 11 * blockSize;
        pacmanDX = 0;	
        pacmanDY = 0;
        redDX = 0;		
        redDY = 0;
        dying = false;
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        drawMaze(g2d);
        drawScore(g2d);

        if (inGame) {
            playGame(g2d);
        } else {
            showIntroScreen(g2d);
        }

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }


    
    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (inGame) {
                if (key == KeyEvent.VK_LEFT) {
                    redDX = -1;
                    redDY = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    redDX = 1;
                    redDY = 0;
                } else if (key == KeyEvent.VK_UP) {
                    redDX = 0;
                    redDY = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    redDX = 0;
                    redDY = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                }
            } else {
                if (key == KeyEvent.VK_SPACE) {
                    inGame = true;
                    initGame();
                }
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

}