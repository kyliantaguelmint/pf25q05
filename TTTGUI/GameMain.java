package TTTGUI; // Make sure to include the package declaration

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L;

    // --- Constants ---
    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = new Color(239, 105, 80);
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225);
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    // --- Enum for Game Mode ---
    private enum GameMode { PVP, PVB }
    private GameMode gameMode;

    // --- Game Objects ---
    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private JLabel statusBar;

    public GameMain() {
        // --- Game Mode Selection Dialog ---
        Object[] options = {"Player vs. Player", "Player vs. Bot"};
        int choice = JOptionPane.showOptionDialog(null, "Choose your game mode:",
                "Tic-Tac-Toe", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        this.gameMode = (choice == 1) ? GameMode.PVB : GameMode.PVP;

        // --- Mouse Click Handler ---
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentState == State.PLAYING) {
                    int row = e.getY() / Cell.SIZE;
                    int col = e.getX() / Cell.SIZE;

                    if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                            && board.cells[row][col].content == Seed.NO_SEED) {
                        if (gameMode == GameMode.PVB && currentPlayer == Seed.NOUGHT) {
                            return; // Don't allow click during bot's turn
                        }
                        processMove(row, col);

                        if (gameMode == GameMode.PVB && currentState == State.PLAYING && currentPlayer == Seed.NOUGHT) {
                            Timer timer = new Timer(500, event -> {
                                makeBotMove();
                                repaint();
                            });
                            timer.setRepeats(false);
                            timer.start();
                        }
                    }
                } else {
                    newGame();
                }
                repaint();
            }
        });

        // --- UI Setup ---
        setupUI();

        // --- Game Initialization ---
        initGame();
        newGame();
    }

    private void setupUI() {
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        super.setLayout(new BorderLayout());
        super.add(statusBar, BorderLayout.PAGE_END);
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));
    }

    public void initGame() {
        board = new Board(); //
    }

    public void newGame() {
        board.newGame(); //
        currentPlayer = Seed.CROSS; //
        currentState = State.PLAYING; //
    }

    private void processMove(int row, int col) {
        currentState = board.stepGame(currentPlayer, row, col); //
        if (currentState == State.PLAYING) {
            SoundEffect.EAT_FOOD.play(); //
        } else {
            SoundEffect.DIE.play(); //
        }
        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS; //
    }

    private void makeBotMove() {
        java.util.List<int[]> validMoves = new java.util.ArrayList<>();
        for (int r = 0; r < Board.ROWS; r++) {
            for (int c = 0; c < Board.COLS; c++) {
                if (board.cells[r][c].content == Seed.NO_SEED) { //
                    validMoves.add(new int[]{r, c});
                }
            }
        }
        if (!validMoves.isEmpty()) {
            int[] move = validMoves.get(new java.util.Random().nextInt(validMoves.size()));
            processMove(move[0], move[1]);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); //
        setBackground(COLOR_BG); //
        board.paint(g); //

        // Update status bar text based on game state
        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK); //
            if (gameMode == GameMode.PVB) {
                statusBar.setText((currentPlayer == Seed.CROSS) ? "Your Turn (X)" : "Bot's Turn (O)");
            } else {
                statusBar.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn"); //
            }
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED); //
            statusBar.setText("It's a Draw! Click to play again."); //
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED); //
            statusBar.setText("'X' Won! Click to play again."); //
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED); //
            statusBar.setText("'O' Won! Click to play again."); //
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(TITLE);
            frame.setContentPane(new GameMain());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}