import javax.swing.Timer;
public class Button {
    Timer myTimer;
    boolean defused;
    int bat;
    int[]colour;
    String word;
    public Button(String word,int[]colour,int bat){
        this.word=word;
        this.colour=colour;
        this.bat=bat;
    }

    public boolean isdefused(){
        return defused;
    }
    public boolean interact(){
    }

}
