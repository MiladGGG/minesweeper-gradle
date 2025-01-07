package minesweeper;

import processing.core.PImage;
import processing.core.PApplet;
import processing.core.PFont;

public class Tile{

    protected int x;
    protected int y;
    private PImage sprite;

    public App appInfo;

    public boolean isMine;


    public boolean isHovered;
    public boolean isRevealed;
    public boolean isFlagged;

    public int array_num;
    public int count;

    public int adj_bombs;
    public boolean checked;

    public boolean isExploding;
    public int phase;
    public int frameCount;

    public PFont f;

    /**
     * Creates a new Shape object.
     * 
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */


    public Tile(int x, int y, App appInfo) {
        this.x = x;
        this.y = y;
        this.appInfo = appInfo;
    }

    /**
     * Sets the shape's sprite.
     * 
     * @param sprite The new sprite to use.
     */
    public void setSprite(PImage sprite) {
        this.sprite = sprite;
    }


    /**
     * @param app The window to draw onto.
     */
    public void draw(PApplet app) {
        //Draw box
        app.image(this.sprite, this.x, this.y);
        
        if(isHovered == true){
            isHovered = false; 
            
            if(isRevealed == false){
                this.setSprite(App.defImg);
            }

        }
        if(isFlagged && isRevealed == false){
            app.image(App.flaImg, this.x, this.y);
        }
        if(isRevealed == true && isMine == false){
            if(adj_bombs > 0){
            appInfo.drawFont(x,y,adj_bombs);
            }
        }

        if(isExploding){
            explode();
        }
        

    }

    /**
     * Gets the x-coordinate.
     * @return The x-coordinate.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Returns the y-coordinate.
     * @return The y-coordinate.
     */
    public int getY() {
        return this.y;
    }

    public void hover(){
        if(isRevealed == false){
        isHovered = true;
        
        this.setSprite(App.hovImg);
        
        }
    }


    public void click(){    
        if(isFlagged== false){
        //NO BOMB
        if(isHovered && isRevealed == false && isMine == false){
            isRevealed = true;
            this.setSprite(App.revImg);
            appInfo.rev_tiles++;
            adj_bombs = get_adjacent_bombs();
            if(adj_bombs == 0){
                reveal_adjacent_blanks();
            }    
        }
        
        //BOMB
        if(isHovered && isRevealed == false && isMine){
            isRevealed = true;
            appInfo.isDead = true;
            isExploding = true;
            explode();
        }
        }
    }


    public void right_click(){
        //FLAG
        if(isHovered && isRevealed == false && isFlagged == false){
            isFlagged = true;
        }
        //NOFLAG
        else{
            if(isFlagged){
            isFlagged = false;
            this.setSprite(App.defImg);}
        }


    }


    public int get_adjacent_bombs(){
        count = 0;
        
        int[] surrounding_arr = new int[8];
        
        surrounding_arr[1] = array_num - 27 -0;
        

        surrounding_arr[3] = array_num -1;
        
        surrounding_arr[4] = array_num + 1;

        surrounding_arr[0] = array_num - 27 -1;
        surrounding_arr[2] = array_num - 27 +1;

        surrounding_arr[5] = array_num + 27 -1;
        surrounding_arr[7] = array_num + 27 +1;

        if((surrounding_arr[5]+1) % 27 == 0){ surrounding_arr[5] = -1;}
        if((surrounding_arr[7]) % 27 == 0){ surrounding_arr[7] = -1;}//Ignore off values

        if((surrounding_arr[0]+1) % 27 == 0){ surrounding_arr[0] = -1;}
        if((surrounding_arr[2]) % 27 == 0){ surrounding_arr[2] = -1;}//Ignore off values

        if((surrounding_arr[3]+1) % 27 == 0){ surrounding_arr[3] = -1;}
        if((surrounding_arr[4]) % 27 == 0){ surrounding_arr[4] = -1;}//Ignore off values



        surrounding_arr[6] = array_num + 27 -0;


        for(int ind: surrounding_arr){
            if(ind <= 485 && ind >=0){
                if(appInfo.my_tile[ind].isMine){
                    count++;
                }
            }
        }

        adj_bombs = count;
        return count;
    }

    public int reveal_adjacent_blanks(){
        checked = true;
        int[] surrounding_arr = new int[4];
        surrounding_arr[0] = array_num - 27 -0;

        surrounding_arr[1] = array_num -1;
        
        surrounding_arr[2] = array_num + 1;

        if((surrounding_arr[1]+1) % 27 == 0){ surrounding_arr[1] = -1;}
        if((surrounding_arr[2]) % 27 == 0){ surrounding_arr[2] = -1;}//Ignore off values

        surrounding_arr[3] = array_num + 27 -0;

        for(int ind: surrounding_arr){
            if(ind <= 485 && ind >=0){
                if(appInfo.my_tile[ind].isMine == false){
                    if(appInfo.my_tile[ind].get_adjacent_bombs() > 0){
                        if(appInfo.my_tile[ind].isRevealed == false){
                            appInfo.my_tile[ind].isRevealed = true;
                            appInfo.rev_tiles++;
                        }
                        appInfo.my_tile[ind].setSprite(App.revImg);
                    }

                                       
                    
                    if(appInfo.my_tile[ind].get_adjacent_bombs() == 0){
                        if(appInfo.my_tile[ind].isRevealed == false){
                            appInfo.my_tile[ind].isRevealed = true;
                            appInfo.rev_tiles++;
                        }
                        
                        

                        appInfo.my_tile[ind].setSprite(App.revImg);
                        if(appInfo.my_tile[ind].checked == false){
                            appInfo.my_tile[ind].reveal_adjacent_blanks();
                        } 

                    }

                }
            }
        }

        
        return count;
    }

    public void explode(){
        isFlagged = false;
        if(phase <= 9){
            if(frameCount < 1){
                frameCount++;
            }
            else{
                frameCount = 0;
                
                this.setSprite(App.minesImg[phase]);
                phase++;

            }
    }
        else{
            isExploding = false;
        }
    }

}
