package com.javarush.games.game2048;
import com.javarush.engine.cell.*;

import java.util.Arrays;

public class Game2048 extends Game{
    private static final int SIDE=4;
    private int[][] gameField=new int[SIDE][SIDE];
    private boolean isGameStopped=false;
    private int score;
    
    @Override
    public void initialize(){
        setScreenSize(SIDE,SIDE);
        createGame();
        drawScene();
    }
    
    private void createGame(){
        gameField=new int[SIDE][SIDE];
        score=0;
        setScore(score);
        createNewNumber();
        createNewNumber();

    }
    
    private void drawScene(){
        for(int y=0;y<SIDE;y++)
            for(int x=0;x<SIDE;x++)
                setCellColoredNumber(x,y,gameField[y][x]);
    }
    
    private void setCellColoredNumber(int x, int y, int value){
        String s = Integer.toString(value);
        if (s.equals("0")) {
            setCellValueEx(x, y, getColorByValue(value),  "");
        } else {
            setCellValueEx(x, y, getColorByValue(value), s);
        }
    }
    
    private Color getColorByValue(int value){
        int[] numbers={2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048};
        Color color=Color.WHITE;
        if (value!=0)
            for (int i=0;i<numbers.length;i++)
                if (value==numbers[i]) {
                    color=Color.values()[i+3];
                    break;
                }
        return color;
    }
    
    private void createNewNumber(){
        int x,y;
        do {
            x=getRandomNumber(SIDE);
            y=getRandomNumber(SIDE);
        } while (gameField[y][x]!=0);
        
        gameField[y][x]=getRandomNumber(10)==9?4:2;
        if (getMaxTileValue()==2048)
            win();
    }
    
    private boolean compressRow(int[] row){
        boolean result=false;
        for (int i=0;i<row.length;i++){
            if (row[i]!=0){
                int z=0;
                for (int j=i;j>0;j--){
                    if(row[j-1]==0) z++;
                }
                if (z!=0) {
                    row[i-z]=row[i];
                    row[i]=0;
                    result=true;
                }
            }
        }
        return result;
    }

    private boolean mergeRow(int[] row){
        boolean result=false;
            for (int i=0;i<row.length;i++){
                if (row[i]==0) continue;
                if (i!=row.length-1){
                    if (row[i]==row[i+1]){
                        row[i]+=row[i];
                        row[i+1]=0;
                        result=true;
                        score+=row[i];
                        setScore(score);
                    }
                }
            }
        return result;
    }

    @Override
    public void onKeyPress(Key key){
        if (isGameStopped) {
            if (key == Key.SPACE) {
                isGameStopped = false;
                createGame();
                drawScene();
            }
        }else {
            if (!canUserMove()) {
                gameOver();
                return;
            }
            if (key == Key.LEFT) {
                moveLeft();
                drawScene();
            } else if (key == Key.RIGHT) {
                moveRight();
                drawScene();
            } else if (key == Key.UP) {
                moveUp();
                drawScene();
            } else if (key == Key.DOWN) {
                moveDown();
                drawScene();
            }
        }
    }

    private void moveLeft(){
        boolean result=false;
        for (int i=0;i<gameField.length;i++){
            if (compressRow(gameField[i])) result=true;
            if (mergeRow(gameField[i])) {
                    result=true;
                    compressRow(gameField[i]);
                }
        }
        if (result) createNewNumber();
    }

    private void moveRight(){
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
    }

    private void moveUp(){
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
    }

    private void moveDown(){
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }

    private  void rotateClockwise(){
        int[][] temp=new int[4][4];
        for(int y=0,tx=3;y<4;y++,tx--)
            for (int x=0;x<4;x++)
                temp[x][tx]=gameField[y][x];

        gameField=temp;
    }

    private int getMaxTileValue(){
        int max=Arrays.stream(gameField[0]).max().getAsInt();
        for (int i=1;i<SIDE;i++)
            if (Arrays.stream(gameField[i]).max().getAsInt()>=max)
                max=Arrays.stream(gameField[i]).max().getAsInt();
        return max;
    }

    private void win(){
        isGameStopped=true;
        showMessageDialog(Color.YELLOW,"***!!!WIN!!!***",Color.BLUE,36);
    }

    private boolean canUserMove(){
        boolean result=false;
        boolean isHaveZero=false;
        boolean isHavePair=false;
        int count=0;

        for (int i=0;i<SIDE;i++)
            for (int j=0;j<SIDE;j++)
                if (gameField[i][j]==0)
                    count++;
        if (count!=0) isHaveZero=true;

        count=0;
        for (int y=0;y<SIDE;y++)
            for (int x=1;x<SIDE;x++) {
                if (gameField[y][x] == gameField[y][x - 1])
                    count++;
                if (gameField[x][y]==gameField[x-1][y])
                    count++;
            }
        if (count!=0) isHavePair=true;

        if (isHaveZero)
            result=true;
        else if (isHavePair)
                result=true;

        return result;
    }

    private void gameOver(){
        isGameStopped=true;
        showMessageDialog(Color.YELLOW,"GAME OVER",Color.BLUE,36);
    }
}