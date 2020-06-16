package com.codegym.games.game2048;
import com.codegym.engine.cell.*;
import java.util.Arrays;
public class Game2048 extends Game {
    private static final int SIDE = 4;
    private int[][] gameField = new int[SIDE][SIDE];
    private boolean isGameStopped = false;
    private int score = 0;
    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
        drawScene();
    }
    @Override
    public void onKeyPress(Key key) {
        if (isGameStopped) {
            if (key == Key.SPACE) {
                isGameStopped = false;
                createGame();
                drawScene();
            }
            return;
        }
        if (!canUserMove()) {
            gameOver();
            if (key == Key.SPACE) {
                isGameStopped = false;
                createGame();
                drawScene();
            }
            return;
        }
        if (key == Key.UP) {
            moveUp();
            drawScene();
        } else if (key == Key.DOWN) {
            moveDown();
            drawScene();
        } else if (key == Key.LEFT) {
            moveLeft();
            drawScene();
        } else if (key == Key.RIGHT) {
            moveRight();
            drawScene();
        }
    }
    private int getMaxTileValue() {
        int maxTileValue = gameField[0][0];
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (gameField[i][j] > maxTileValue) {
                    maxTileValue = gameField[i][j];
                }
            }
        }
        return maxTileValue;
    }
    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.GRAY, "You did it! You won!", Color.RED, 10);
    }
    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.GRAY, "You lost!", Color.RED, 10);
    }
    private boolean canUserMove() {
        boolean canUserMove = false;
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (gameField[i][j] == 0) {
                    canUserMove = true;
                }
                if ((i - 1) > 0 && (gameField[i][j] == gameField[i - 1][j])) {
                    canUserMove = true;
                }
                if ((i + 1) < SIDE && (gameField[i][j] == gameField[i + 1][j])) {
                    canUserMove = true;
                }
                if ((j - 1) > 0 && (gameField[i][j] == gameField[i][j - 1])) {
                    canUserMove = true;
                }
                if ((j + 1) < SIDE && (gameField[i][j] == gameField[i][j + 1])) {
                    canUserMove = true;
                }
            }
        }
        return canUserMove;
    }
    private void moveLeft() {
        int moved = 0;
        boolean compress;
        boolean merged;
        boolean compressed;
        for (int i = 0; i < SIDE; i++) {
            compress = compressRow(gameField[i]);
            merged = mergeRow(gameField[i]);
            compressed = compressRow(gameField[i]);
            if (compress || merged || compressed) {
                moved++;
            }
        }
        if (moved != 0) {
            createNewNumber();
        }
    }
    private void moveRight() {
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
    }
    private void moveUp() {
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
    }
    private void moveDown() {
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }
    private void rotateClockwise() {
        for (int i = 0; i < SIDE / 2; i++) {
            for (int j = i; j < SIDE - i - 1; j++) {
                int temp = gameField[i][j];
                gameField[i][j] = gameField[SIDE - 1 - j][i];
                gameField[SIDE - 1 - j][i] = gameField[SIDE - 1 - i][SIDE - 1 - j];
                gameField[SIDE - 1 - i][SIDE - 1 - j] = gameField[j][SIDE - 1 - i];
                gameField[j][SIDE - 1 - i] = temp;
            }
        }
    }
    private void createGame() {
        gameField = new int[SIDE][SIDE];
        score = 0;
        setScore(score);
        createNewNumber();
        createNewNumber();
    }
    private void createNewNumber() {
        int rand1 = getRandomNumber(SIDE);
        int rand2 = getRandomNumber(SIDE);
        int rand3 = getRandomNumber(10);
        while (gameField[rand1][rand2] != 0) {
            rand1 = getRandomNumber(SIDE);
            rand2 = getRandomNumber(SIDE);
        }
        if (rand3 == 9) {
            gameField[rand1][rand2] = 4;
        } else {
            gameField[rand1][rand2] = 2;
        }
        int maxTileValue = getMaxTileValue();
        if (maxTileValue == 2048) {
            win();
        }
    }
    private boolean compressRow(int[] row) {
        int temp = 0;
        int[] rowCopy = row.clone();
        boolean isArrayChanged = false;
        for (int i = 0; i < row.length; i++) {
            for (int j = 0; j < row.length - i - 1; j++) {
                if (row[j] == 0) {
                    temp = row[j];
                    row[j] = row[j + 1];
                    row[j + 1] = temp;
                }
            }
        }
        if (!Arrays.equals(row, rowCopy)) {
            isArrayChanged = true;
        } else {
            isArrayChanged = false;
        }
        return isArrayChanged;
    }
    private boolean mergeRow(int[] row) {
        boolean isArrayMerged = false;
        for (int i = 0; i < row.length - 1; i++) {
            if (row[i] == row[i + 1] && row[i] != 0) {
                row[i] = 2 * row[i];
                row[i + 1] = 0;
                isArrayMerged = true;
                score += row[i];
                setScore(score);
            }
        }
        return isArrayMerged;
    }
    private void setCellColoredNumber(int y, int x, int value) {
        Color cellColor = getColorByValue(value);
        if (value != 0) {
            setCellValueEx(y, x, cellColor, Integer.toString(value));
        } else {
            setCellValueEx(y, x, cellColor, "");
        }
    }
    private Color getColorByValue(int value) {
        if (value == 0) {
            return Color.WHITE;
        } else if (value == 2) {
            return Color.PURPLE;
        } else if (value == 4) {
            return Color.ORANGE;
        } else if (value == 8) {
            return Color.GREEN;
        } else if (value == 16) {
            return Color.BLACK;
        } else if (value == 32) {
            return Color.RED;
        } else if (value == 64) {
            return Color.PINK;
        } else if (value == 128) {
            return Color.VIOLET;
        } else if (value == 256) {
            return Color.YELLOW;
        } else if (value == 512) {
            return Color.BLUE;
        } else if (value == 1024) {
            return Color.BROWN;
        } else if (value == 2048) {
            return Color.GRAY;
        } else {
            return Color.NONE;
        }
    }
    private void drawScene() {
        for (int x = 0; x < SIDE; x++) {
            for (int y = 0; y < SIDE; y++) {
                setCellColoredNumber(x, y, gameField[y][x]);
            }
        }
    }