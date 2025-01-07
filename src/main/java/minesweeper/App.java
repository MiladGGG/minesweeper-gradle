package minesweeper;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.*;
// import java.awt.desktop.SystemEventListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;

@SuppressWarnings("unused")
public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 864; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int FPS = 30;

    public Tile[] my_tile = new Tile[486];


    public String configPath;

    public static Random random = new Random();
	
	public static int[][] mineCountColour = new int[][] {
            {0,0,0}, // 0 is not shown
            {0,0,255},
            {0,133,0},
            {255,0,0},
            {0,0,132},
            {132,0,0},
            {0,132,132},
            {132,0,132},
            {32,32,32}
    };
	
    public boolean endSequence;

    public boolean clickFlag;
    public boolean r_clickFlag;

    public boolean isWon;
    public boolean isDead;
    public int phaseInd;
    public int tick_time;

    public int rev_tiles;

    int curTime;
    int timeReset;
    int saved_time;

    public int mosX;
    public int mosY;

    public static PImage defImg;
    public static PImage hovImg;
    public static PImage revImg;

    public static PImage flaImg;



    public static PImage[] minesImg = new PImage[10];






    public static int set_count = 100;
    public int mine_count = 0;

    public int[] confirmed_mineInd;

    public App() {
        
        this.configPath = "config.json";

        for(int i = 0; i < 18; i++){
            for(int j = 0; j < 27; j++){
                this.my_tile[j + (i*27)] = new Tile(CELLSIZE*(j),TOPBAR + (i *CELLHEIGHT), this);
            }
        }
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }


    

    private int find_mineless(){
        int rand = random.nextInt(486);
        if(my_tile[rand].isMine == true){
            return find_mineless();
        }
        return rand;
    }
    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
	@Override
    public void setup() {
        frameRate(FPS);

        defImg = loadImage("src/main/resources/minesweeper/tile1.png");
        hovImg = loadImage("src/main/resources/minesweeper/tile2.png");
        revImg = loadImage("src/main/resources/minesweeper/tile.png");

        flaImg = loadImage("src/main/resources/minesweeper/flag.png");

        {     
            for(int i = 0; i < 10; i++){
                minesImg[i] = loadImage("src/main/resources/minesweeper/mine"+Integer.toString(i)+".png");;
            }
        }


        for(int i = 0; i < 486; i++){
            this.my_tile[i].setSprite(this.loadImage("src/main/resources/minesweeper/tile1.png"));
            this.my_tile[i].array_num = i;
        }


        //Setup mines
        mine_count = set_count;
        confirmed_mineInd = new int[mine_count];
        for(int i = 0; i <mine_count; i++){
            int index = find_mineless();
        
            my_tile[index].isMine = true;
            confirmed_mineInd[i] = index;
        }
        

    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        if(event.getKey() == 'r'){
            for(Tile t: my_tile){
                t.isExploding = false;
                t.isMine = false;
                t.isHovered = false;
                t.isFlagged = false;
                t.phase = 0;
                t.adj_bombs = 0;
                t.isRevealed = false;
                t.checked = false;
                t.count = 0;
            }
            clickFlag = false;
            r_clickFlag = false;
            isDead =false;
            isWon = false;
            timeReset += curTime;
            saved_time = 0;
            phaseInd = 0;
            tick_time = 0;
            rev_tiles = 0;
            setup();
        }
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == LEFT){ 
        clickFlag = true;
        }
        if(e.getButton() == RIGHT){ 
            r_clickFlag = true;
            }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
        mosX = e.getX();
        mosY = e.getY();
    }


    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        //draw game board
        background(255, 255, 255);

        if(isDead == false && isWon == false){
        int chosenSquare = mosX/CELLSIZE + (mosY/CELLSIZE)*27 -  ((TOPBAR/CELLSIZE)*27);

        if(chosenSquare < 486 && chosenSquare >= 0){
        my_tile[chosenSquare].hover();
        }

        if(clickFlag){
            if(chosenSquare < 486 && chosenSquare >= 0){
                my_tile[chosenSquare].click();
                }
            clickFlag = false;
        }
        if(r_clickFlag){
            if(chosenSquare < 486 && chosenSquare >= 0){
                my_tile[chosenSquare].right_click();
                }
            r_clickFlag = false;
        } 
        }

        if(rev_tiles + mine_count == 486){
            isWon = true;
        }
        
        for(int i = 0; i < 486; i++){
            my_tile[i].draw(this);
            }


        drawTime();

        if(isDead){
            mines_explode();
            PFont f = createFont("Arial",CELLSIZE*2,true);
            textFont(f,CELLSIZE*2);           // STEP 3 Specify font to be used
            fill(40,40,40);
            textAlign(CENTER, CENTER);
            text( "YOU LOST!",WIDTH- (6*WIDTH/8),TOPBAR/2);

        }
        if(isWon){
            PFont f = createFont("Arial",CELLSIZE*2,true);
            textFont(f,CELLSIZE*2);           // STEP 3 Specify font to be used
            fill(40,220,40);
            textAlign(CENTER, CENTER);
            text( "YOU WON!",WIDTH- (6*WIDTH/8),TOPBAR/2);

        }
    }

    public void drawFont(int x, int y, int count){
        PFont f = createFont("Arial",CELLSIZE,true);
        textFont(f,CELLSIZE);           // STEP 3 Specify font to be used
        fill(mineCountColour[count][0], mineCountColour[count][1],mineCountColour[count][2]);
        textAlign(CENTER, CENTER);
        text(count,x+16,y+14);

    }

    public void drawTime(){
        PFont f = createFont("Arial",CELLSIZE*2,true);
        textFont(f,CELLSIZE*2);           // STEP 3 Specify font to be used
        fill(200,200,200);
        textAlign(RIGHT, CENTER);
        curTime = millis() - timeReset;
        
        if(isDead || isWon){
            if(saved_time == 0){
            saved_time = curTime;
            }
            text( "Time: "+saved_time/1000,WIDTH- WIDTH/8,TOPBAR/2);

        }
        else{
            text( "Time: "+curTime/1000,WIDTH- WIDTH/8,TOPBAR/2);
        }
    }

    public void mines_explode(){
        if(phaseInd < mine_count){
        if(tick_time <3){
            tick_time++;
        }
        else{
            tick_time = 0;
            my_tile[confirmed_mineInd[phaseInd]].isExploding = true;
            my_tile[confirmed_mineInd[phaseInd]].explode();
            phaseInd++;
        }
    }
    }

    public static void main(String[] args) {



        
        
        PApplet.main("minesweeper.App");
        System.out.println("Working Directory = " + System.getProperty("user.dir"));


        try{
            if(Integer.parseInt(args[0]) > 0 && Integer.parseInt(args[0]) <486){
        set_count = Integer.parseInt(args[0]);
            }
        }
        catch(Exception e){

        }
    }

}
