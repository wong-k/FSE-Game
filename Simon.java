import java.util.*;
import java.awt.*;
import javax.swing.Timer;
public class Simon{
    private Timer myTimer;
    private Rectangle simon;
    private Polygon[]colours;
    private Color[]painting;
    private boolean defused;
    private Random colour=new Random();
    private ArrayList<Integer>pattern=new ArrayList<Integer>();//This is the pattern of 0,1,2,3 that determines which if the colours needs to be clicked
    private int complexity,clicks,time;//Complexity is the total amount of times the colour will flash, Clicks keeps up to date with how many times the user has clicked colours to keep up with the pattern
    private int[]invert; //This makes the inverted effect that the reader of the manual has to tell the person to click (So maybe 1 or like red has to actually be 3 AKA green)
    public Simon(int complexity,Rectangle mod,int[]inverts){
        time=15*complexity;
        this.complexity=complexity;
        simon=mod;
        invert=inverts;
        colours=new Polygon[4];
        painting=new Color[]{new Color(255f,0f,0f),new Color(0f,0f,255f),new Color(0f,255f,0f),new Color(255f,255f,0f)};
        for(int i=0;i<4;i++){
            int[]xcoords={simon.x+30+35*(i<2 ? i:i-1),simon.x+65+35*(i<2 ? i:i-1),simon.x+65+35*(i<2 ? i:i-1),simon.x+100+35*(i<2 ? i:i-1)};
            int[]ycoords={simon.y+100-35*(i<2 ? i:i-3),simon.y+65-35*(i<2 ? i:i-3),simon.y+135-35*(i<2 ? i:i-3),simon.y+100-35*(i<2 ? i:i-3)};//NEED 0,+1,-1,0
            colours[i]=new Polygon(xcoords,ycoords,4);
        }
    }
    /*---------------------------------
    *This is the interact function that will be used for each of the modules (Specific to each module)
    *Might need a parameter of the mouse's position or something along those lines
    *Returns a boolean so that if they make a mistake, we can return false in the main and display the mistake.
    -----------------------------------*/
    public boolean interact(int mousex,int mousey){
        //pattern(); Need to figure out a way to call this either with graphics g being passed down as a parameter or outside of interract and in draw
        if(colours[invert[pattern.get(clicks)]].contains(mousex,mousey)) { //We check the part of the pattern they're at, followed by the "correct" inverted pattern which then tells us which polygon to check for collision
            clicks += 1;//The user has gone one further into their pattern
            if (pattern.size() == clicks) { //We check if they've finished the pattern
                if (clicks == complexity) { //And check if they've gotten to the largest complexity
                    defused = true;
                    return true;
                }
                clicks = 0;
                pattern.add(colour.nextInt(4));
            }
            return true;
        }
        else{
            return false;
        }
    }
    public boolean isdefused(){
        return defused;
    }
    public int getAllottedTime(){return time;}
    public void reset(){
        pattern.clear();
    }
    public void draw(Graphics g){
        g.setColor(new Color(192,192,192));
        g.fillRect((int)(simon.getX()),(int)(simon.getY()),(int)(simon.getWidth()),(int)(simon.getHeight()));
        g.setColor(new Color(255f,0f,0f,0.4f));
        g.fillPolygon(colours[0]);
        g.setColor(new Color(0f,0f,255f,0.4f));
        g.fillPolygon(colours[1]);
        g.setColor(new Color(0f,255f,0f,0.4f));
        g.fillPolygon(colours[2]);
        g.setColor(new Color(255f,255f,0f,0.4f));
        g.fillPolygon(colours[3]);
    }
    private void pattern(Graphics g){
        Timer myTimer=new Timer(50,null);
        myTimer.start();
        for(int i:pattern){
            int tickcount=100;
            while(true){
                tickcount-=10;
                if(tickcount==0){
                    tickcount=100;
                    break;
                }
            }
            while(true){
                g.setColor(painting[pattern.get(i)]);
                g.fillPolygon(colours[pattern.get(i)]);
                tickcount-=10;
                if(tickcount==0){
                    break;
                }
            }
        }
    }
}
