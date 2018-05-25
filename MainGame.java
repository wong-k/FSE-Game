/*
 * MainGame.java
 * Upon running, a 3 x 2 grid is displayed that represents the bomb. There are wires and a countdown.
 * How the program works: main method in MainGame class creates MainGame Object. The constructor there makes
 * a Bomb Object. The Bomb's constructor makes wire and time module Objects,  which are also fields in Bomb.
 * Keith Wong
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.Timer;		//import Timer specifically to avoid conflict with util Timer
public class MainGame extends JFrame implements ActionListener {
    Timer myTimer;
    private int tickCount;		//counter that tracks how much time is left in milliseconds to defuse the bomb
    BombPanel bomb;

    /*----------------------------------------------------------------
    Constructor which makes the window, the Bomb Object, and the Timer
     ----------------------------------------------------------------*/
    public MainGame() {
        super("Main Game");
        setSize(800,600);
        myTimer=new Timer(10,this);
        tickCount=11000;						//counts down, 1000 greater than desired number on screen, in this case, 30 sec
        bomb=new BombPanel(3,tickCount);				//if numWires>5, add colours to allColours[][] line 188
        add(bomb);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    public void start(){
        myTimer.start();
        setVisible(true);
    }
    public void makeMainMenu(){
        MainGame game=new MainGame();
        new MainMenu(game);
    }
    /*-----------------------------------------------------------------
    Updates the game whenever Timer fires and ends game when time is up
     ----------------------------------------------------------------*/
    public void actionPerformed(ActionEvent e){
        Object source=e.getSource();
        if(bomb!=null && source==myTimer){			//updating the game
            tickCount-=10;
            bomb.updateState();
            bomb.repaint();
        }
        if(tickCount==0){							//stop game when time is up
            myTimer.stop();
            tickCount=0;
            System.out.println("done");
        }
    }

    public static void main(String[]args){
        MainGame main=new MainGame();
        main.makeMainMenu();
    }
}
/*--------------------------------------------------------------------------------------------------
 *This class makes the JPanel that's the game screen. It contains a  bomb with modules as attributes
 *-------------------------------------------------------------------------------------------------*/
class BombPanel extends JPanel implements MouseListener{
    private int mouseX,mouseY;					//coordinates of mouse
    private TimeModule timer;
    private WireModule wires;

    /*--------------------------------------------------------
    Constructor which makes a timer and wire module.
    "numWires" specifies how many wires are in the wire module.
     ---------------------------------------------------------*/
    public BombPanel(int numWires, int timeLeft){
        addMouseListener(this);
        mouseX=mouseY=0;
        timer=new TimeModule(340,425,timeLeft);		//creating a timer

        int[] wireYCoord=new int[numWires];
        int spaceBetweenWires=(200-10*numWires)/(numWires+1);		//adjust: 200, which is the height of the squares on the grid.
        for(int i=0;i<numWires;i++){
            int nextYCoord=100+spaceBetweenWires*(i+1)+10*i;		//adjust: 100, which is y-coord where the module box starts. 10, which is current width of wires
            wireYCoord[i]=nextYCoord;
        }
        wires=new WireModule(wireYCoord,numWires);					//creating a wire module
    }

    /*-----------------------------------------------------------------
    This method updates the game whenever the Timer fires in main class
     -----------------------------------------------------------------*/
    public void updateState(){
        timer.subtractTime();						//displaying a count down
        for(Rectangle rect:wires.getWires()){		//checks if user clicks on wire
            if(rect.contains(mouseX,mouseY)){
                System.out.println("true");
            }
        }
    }

    /*------------------------------------------------------------
    Draws the bomb on screen, called at same time as updateState()
     ------------------------------------------------------------*/
    @Override
    public void paintComponent(Graphics g){
        g.setColor(new Color(255,255,255));
        g.fillRect(0,0,getWidth(),getHeight());		//clearing the screen in preparation for new drawings
        g.setColor(new Color(0,0,0));

        for(int i=100;i<800;i+=200){						//drawing a 3 x 2 grid to represent the bomb
            g.drawLine(i,100,i,500);
        }
        for(int i=100;i<600;i+=200){
            g.drawLine(100,i,700,i);
        }

        g.setFont(new Font("Arial",Font.PLAIN,50));			//displaying time
        g.drawString(timer.getTime(),timer.getX(),timer.getY());

        for(int i=0;i<wires.getNumWires();i++){							//drawing the wires on screen
            Rectangle rect=wires.getWires()[i];
            int[] newColour=wires.getColour(i);
            g.setColor(new Color(newColour[0],newColour[1],newColour[2]));
            g.fillRect((int)rect.getX(),(int)rect.getY(),(int)rect.getWidth(),(int)rect.getHeight());
        }
    }
    /*--------------------------------------------------------
    This method updates mouse coordinates whenever user clicks
     ---------------------------------------------------------*/
    public void mousePressed(MouseEvent e){
        mouseX=e.getX();
        mouseY=e.getY();
    }
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseClicked(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
}
/*---------------------------------------------
 This class makes a countdown for a Bomb Object
 --------------------------------------------*/
class TimeModule{
    private int x,y,time;		//time is in milliseconds, x and y are where the module is displayed

    /*----------------------------------------
    Constructor, module is made in Bomb class
     ----------------------------------------*/
    public TimeModule(int xCoord, int yCoord, int timeLeft){
        x=xCoord;
        y=yCoord;
        time=timeLeft;
    }

    /*------------------------------------------------------------------------
     Responsible for the countdown because it alters the time that's displayed
     -------------------------------------------------------------------------*/
    public void subtractTime(){
        time-=10;					//called every 10 milliseconds
    }
    /*---------------------------------------------------------
    Accessor methods used by paintComponent to display the time
     ----------------------------------------------------------*/
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }

    /*--------------------------------------------------------------------------------------
     This method returns the time remaining in the format of minutes:seconds, such as 01:30
     Returns the time as a String so paintComponent can display it
     *-----------------------------------------------------------------------------------*/
    public String getTime(){
        int min=time/60000;
        System.out.println(time);
        int seconds=(time-(min*60000))/1000;
        String output=String.format("%2d:%2d",(int)min,seconds).replace(" ","0");		//replacing blank spaces with 0's so it looks like a timer
        return output;
    }
}
/*--------------------------------------------------------------
 This class makes a wire module with a specified number of wires
 *-------------------------------------------------------------*/
class WireModule{
    private Rectangle[] wires;				//each wire is a rectangle so collisions with the mouse can be detected
    private int[][]colours;					//the colours of the wires
    private int[] codes;
    private int numWires;
    /*--------------------------------------------------------------------------------------------
    Constructor which creates a specified number of Rectangles and rgb values that represent wires
    "coord" is a list of y coordinates that paintComponent uses to draw the wires.
    "wireCount" is how many wires there will be
     -------------------------------------------------------------------------------------------*/
    public WireModule(int[] coord,int wireCount){
        numWires=wireCount;
        colours=new int[numWires][3];	//possible wire colours: red, blue, green, yellow, black
        codes=new int[numWires];
        wires=new Rectangle[numWires];		//creating the Rectangles that represent the wire hitboxes
        int[][]allColours={{255,0,0},{0,0,255},{0,255,0},{255,255,0},{0,0,0}};
        Random rand=new Random();										//generating random colours for the wires

        for(int i=0;i<numWires;i++){
            int index=rand.nextInt(numWires);							//choosing a random colour out of all the possible colours and assigning it to a wire
            int[] rgbSet=allColours[index];
            colours[i]=rgbSet;

            int sum=0;													//assigning a code to the wire that determines when it should be cut
            sum+=rgbSet[0]+rgbSet[1]*2+rgbSet[2]*3;						//each colour value has a certain weighting that contributes to the code
            codes[i]=sum;

            int YCoord=coord[i];
            wires[i]=new Rectangle(100,YCoord,200,10);
        }
        Arrays.sort(codes);					//wires must be cut in ascending order
    }

    /*
    Work in progress, ignore this method for now.
    This method is called whenever a wire is clicked to see if wires are cut in the right order.
    */
    public boolean checkOrder(int[] cutSoFar){
        int[] correctOrder=Arrays.copyOfRange(codes,0,cutSoFar.length);
        return correctOrder.equals(cutSoFar);
    }

    /*----------------------------------------------------
     Used by paintComponent to draw the wires.
     Also used by updateState to see if user clicked a wire.
     ------------------------------------------------------*/
    public Rectangle[] getWires(){
        return wires;
    }
    /*----------------------------------------
     Used by paintComponent to draw the wires.
     ------------------------------------------*/
    public int[] getColour(int index){
        return colours[index];
    }

    /*--------------------------------------------------------------
    Used by paintComponent to display rectangles where the wires are
     --------------------------------------------------------------*/
    public int getNumWires(){
        return numWires;
    }
}
/*-----------------------------------------------------
 *this class makes a frame for the main menu
 *implements ActionListener because user clicks buttons
 *------------------------------------------------------*/
class MainMenu extends JFrame implements ActionListener{
    private MainGame mainGame;							//the SimpleGame Object is used to start the Timer for the game when the player hits "Play"
    private JButton playBut=new JButton("Play");				//buttons that bring user to the game and instructions pages
    private JButton infoBut=new JButton("Instructions");

    /*--------------------------------------------------------------------
     *constructor method which makes buttons and a background for the frame
     *"g" is a SimpleGame Object
     *-------------------------------------------------------------------*/
    public MainMenu(MainGame g){
        super("Main Menu");								//setting size and calling to super class constructor because the main menu is its own frame
        setSize(900,750);
        mainGame=g;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        playBut.addActionListener(this);
        infoBut.addActionListener(this);

        ImageIcon background=new ImageIcon("images/main menu back.png");		//change image later
        JLabel menuBack=new JLabel(background);
        JLayeredPane mainPage=new JLayeredPane();
        mainPage.setLayout(null);												//this allows me to add labels where I want

        menuBack.setSize(900,750);						//adding the background image onto the menu page
        menuBack.setLocation(0,0);
        mainPage.add(menuBack,1);

        playBut.setSize(155,60);						//setting size of play and instruction buttons and adding them to main menu
        playBut.setLocation(355,300);
		/*playBut.setContentAreaFilled(false);						//the buttons themselves don't need to be visible because the player can just click words such as "Play" on the background image
		playBut.setFocusPainted(false);
		playBut.setBorderPainted(false);*/

        infoBut.setSize(155,60);
        infoBut.setLocation(355,425);
		/*infoBut.setContentAreaFilled(false);
		infoBut.setFocusPainted(false);
		infoBut.setBorderPainted(false);*/

        mainPage.add(playBut,2);
        mainPage.add(infoBut,2);
        add(mainPage);
        setVisible(true);
    }
    /*-----------------------------------------------------
     *this method changes the frame when a button is clicked
     *-----------------------------------------------------*/
    public void actionPerformed(ActionEvent evt) {
        Object source=evt.getSource();
        if(source==playBut){				//when play button is clicked, the main menu is no longer visible and the Timer for the game start
            setVisible(false);
            //mainGame.startMusic();
            mainGame.start();
        }
        else if(source==infoBut){			//when instructions button is clicked, instructions page is shown
            setVisible(false);
            new InstructionsPage(mainGame);
        }
    }
}
/*---------------------------------------------------------------------------------
 *this class makes a frame for an instructions page
 *implements ActionListener because there's a button that returns user to main menu
 *---------------------------------------------------------------------------------*/
class InstructionsPage extends JFrame implements ActionListener{
    private MainGame mainGame;						//The constructor for the main menu has a SimpleGame Object as a parameter, so when the return button is clicked on this frame, MainMenu() has to be called with a SimpleGame Object
    private JButton returnBut=new JButton("Main menu");		//button used to return to main menu

    /*---------------------------------------------------------------------
     *constructor method which makes buttons and a background for the frame
     *"g" is a SimpleGame Object
     *-------------------------------------------------------------------*/
    public InstructionsPage(MainGame g){
        super("Instructions");
        setSize(900,750);
        mainGame=g;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        returnBut.addActionListener(this);

        ImageIcon back=new ImageIcon("images/info back.png");
        JLabel infoBack=new JLabel(back);
        JLayeredPane infoPage=new JLayeredPane();
        infoPage.setLayout(null);

        infoBack.setSize(900,700);
        infoBack.setLocation(0,0);
        infoPage.add(infoBack,1);

        returnBut.setSize(250,55);						//creating a "return to main menu" button and adding it to infoPage
        returnBut.setLocation(0,640);
		/*returnBut.setContentAreaFilled(false);
		returnBut.setFocusPainted(false);
		returnBut.setBorderPainted(false);*/

        infoPage.add(returnBut,2);
        add(infoPage);
        setVisible(true);
    }
    /*-------------------------------------------------------------------------
     *this method creates the main menu again when the return button is clicked
     *-------------------------------------------------------------------------*/
    public void actionPerformed(ActionEvent evt){
        Object source=evt.getSource();
        if(source==returnBut){
            setVisible(false);
            mainGame.makeMainMenu();
        }
    }
}
