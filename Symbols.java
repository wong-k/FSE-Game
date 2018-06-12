import java.awt.*;
import java.util.*;
public class Symbols{
    private Rectangle[] symbols; //4 symbol rectangles in the middle of the module.
    private Image[]images;
    private TreeSet<Integer>right,undo;
    private Random ran;
    private Rectangle symbol; //The rectangle of the module itself
    private int time; //This counts how many correct symbols the player presses
    private boolean defused; //Standard defuse boolean depicting whether or not the module has been defused
    private ArrayList<Integer>selected; //List of symbols randomly selected from the "order" of symbols it's being chosen from
    public Symbols(int[]order,Rectangle mod,Image[]options){ //Order is the line of symbols that we are choosing from
        time=50;
        symbol=mod;
        symbols=new Rectangle[4];
        ran=new Random();
        right=new TreeSet<Integer>();
        undo=new TreeSet<Integer>();
        selected=new ArrayList<Integer>();
        while(selected.size()<4){//Randomly selects symbols from order to be the ones we're using
            selected.add(ran.nextInt(order.length));
        }
        while(right.size()<4){
            for(int j=0;j<order.length;j++){ //This will have up to 10
                if(selected.indexOf(order[j])>0 && !right.contains(order[j])){//If the element we're looking at in order is in our arraylist
                    right.add(order[j]); //We input that element into the right sequence
                }
            }
        }//When making symbols rectangles, we are left with a 150X150 square so we make them 75X75. THIS MEANS 25X25 rectangles cut off at edges
        for(int i=0;i<4;i++){
            symbols[i]=new Rectangle(symbol.x+25+76*(i-1<=0 ? 0:1),symbol.y+25+76*(i%2),75,75);//Need (x+25,y+25,75,75),(x+101,y+25,75,75),(x+25,y+101,75,75),(x+101,+101
        }
    }
    /*---------------------------------
    *This is the interact function that will be used for each of the modules
    *No parameters since it will simply be called once it's clicked on
    *Returns a boolean so that if they make a mistake, we can return false in the main and display the mistake.
    -----------------------------------*/
    public boolean interact(int mousex,int mousey){
        for (int i = 0; i < 4; i++) {
            if (symbols[i].contains(mousex, mousey)) {
                if (right.first()==selected.get(i)) {
                    undo.add(right.pollFirst());
                    //Display the button as green/clicked
                    if (right.first()==null) {
                        defused = true;
                        return true;
                    }
                    return true;
                }
            }
        }
        return false;
    }
    public int getAllottedTime(){return time;}
    public boolean isdefused(){
        return defused;
    }
    public void reset(){
        defused=false;
        right.addAll(undo);
        undo.clear();
    }
    public void draw(Graphics g){
        Rectangle[]indicator={new Rectangle((int)(symbols[0].getX())+10,(int)(symbols[0].getY())+5,55,10),new Rectangle((int)(symbols[0].getX())+10,(int)(symbols[0].getY())+80,55,10),new Rectangle((int)(symbols[0].getX())+85,(int)(symbols[0].getY())+5,55,10),new Rectangle((int)(symbols[0].getX())+85,(int)(symbols[0].getY())+80,55,10)};
        int coloured=4-right.size();
        g.setColor(new Color(246,231,206));
        for(int i=0;i<4;i++) {
            g.fillRect((int)(symbols[i].getX()),(int)(symbols[i].getY()),(int)(symbols[i].getWidth()),(int)(symbols[i].getHeight()));
        }
        for(int i=0;i<2;i++){
            for(int j=0;j<2;j++){
                g.setColor(Color.black);
                g.fillRect((int)(indicator[i*2+j].getX()),(int)(indicator[i*2+j].getY()),(int)(indicator[i*2+j].getWidth()),(int)(indicator[i*2+j].getHeight()));
                for(int k=0;k<coloured;k++){
                    ArrayList<Integer>current=new ArrayList<Integer>();
                    current.addAll(selected);
                    current.removeAll(right);
                    g.setColor(Color.green);
                    g.fillRect((int)(indicator[selected.indexOf(current.get(k))].getX()),(int)(indicator[selected.indexOf(current.get(k))].getY()),(int)(indicator[selected.indexOf(current.get(k))].getWidth()),(int)(indicator[selected.indexOf(current.get(k))].getHeight()));
                }
            }
        }
    }
}
