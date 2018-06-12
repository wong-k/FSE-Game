import javax.swing.Timer;
public class Button {
    private Timer myTimer;
    private boolean defused;
    private int bat,time;
    private int[]colour;
    private String word;
    public Button(String word,int[]colour,int bat){
        this.word=word;
        this.colour=colour;
        this.bat=bat;
    }

    public boolean isdefused(){
        return defused;
    }
    public boolean interact(int x,int y){ return false;}
    public int getAllottedTime(){return time;}
    public void reset(){}
}
