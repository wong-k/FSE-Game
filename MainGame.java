/*
 * MainGame.java
 * Upon running, a main menu is displayed. Users can access an instructions JFrame, or select level screen.
 * On select level screen, user can click through 10 JPanels and click play to start a game.
 * When the bomb explodes after 10 seconds (the puzzles don't work right now), a game over frame is shown.
 * From game over frame, user can return to main menu or play again.
 * Keith Wong
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.Timer;		//import Timer specifically to avoid conflict with util Timer
public class MainGame extends JFrame implements ActionListener {
    private JButton playBut=new JButton("Select Level");				//buttons that bring user to level selection and instruction pages
    private JButton infoBut=new JButton("Instructions");

    /*------------------------------------
    Constructor which makes the main menu
     ------------------------------------*/
    public MainGame() {
        super("Main Menu");
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        playBut.addActionListener(this);
        infoBut.addActionListener(this);

        ImageIcon background=new ImageIcon("images/main menu back.png");		//change image later
        JLabel menuBack=new JLabel(background);
        JLayeredPane mainPage=new JLayeredPane();
        mainPage.setLayout(null);

        menuBack.setSize(900,750);			//adding the background image onto the menu page
        menuBack.setLocation(-100,-300);
        mainPage.add(menuBack,1);

        playBut.setSize(155,60);			//setting size of play and instruction buttons and adding them to main menu
        playBut.setLocation(355,300);
		/*playBut.setContentAreaFilled(false);			//the buttons themselves don't need to be visible because the player can just click words such as "Play" on the background image
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
     This method changes the frame when a button is clicked
     *-----------------------------------------------------*/
    public void actionPerformed(ActionEvent evt) {
        Object source=evt.getSource();
        if(source==playBut){				//when play button is clicked, the main menu is no longer visible and the level selection frame is shown
            setVisible(false);
            SelectLevelPage page=new SelectLevelPage(0);
            page.start();
        }
        else if(source==infoBut){			//when instructions button is clicked, instructions page is shown
            setVisible(false);
            new InstructionsPage();
        }
    }
    public static void main(String[]args){
        MainGame main=new MainGame();
    }
}
/*---------------------------------------------------------------------------------------
 This class creates the frame where players select a level to play
 The frame uses cardLayout, where each panel is a BookPage Object that represents a level.
 *This class is called in the main class, when user clicks the "Select Level" button.
 *--------------------------------------------------------------------------------------*/
class SelectLevelPage extends JFrame implements ActionListener{
    private JPanel completeBook;							//JPanel that stores all the other panels
    private CardLayout cLayout;
    private BookPage[] pages;								//The Objects that represent the pages of the book. The Array is used to update the displayed panel when a button is clicked
    private BookPage currentPage;							//The current page being shown. This is used to update the panel's interface.
    private Timer myTimer;
    private JButton returnBut;								//A button that brings user back to main menu. This is a field because it has its own if statement in actionPerformed()
    private JButton playBut;
    private JButton[] levelBut;								//Array that stores the next/previous buttons to flip between pages
    private int level;

    /*-----------------------------------------------------------------------------------------
     Constructor which makes the card layout, buttons, and BookPage Objects
     "displayedLevel" is an index of levelBut and pages. It controls which level page is shown.
     displayedLevel+1 equals a level number ranging from 1 to 10.
     *-----------------------------------------------------------------------------------------*/
    public SelectLevelPage(int displayedLevel){
        super("Select Level");						//the following lines assign values to the fields and create the frame
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        cLayout=new CardLayout();
        level=displayedLevel;
        completeBook=new JPanel(cLayout);
        pages=new BookPage[10];
        myTimer=new Timer(10,this);
        levelBut=new JButton[10];

        returnBut=new JButton("Main menu");
        returnBut.addActionListener(this);
        returnBut.setSize(200,50);			//the location of the return and play buttons is constant for all pages, whereas the buttons for the levels changes location depending on the displayed panel
        returnBut.setLocation(300,510);			//since return and play buttons remain constant, they're created here

        playBut=new JButton("Play");
        playBut.addActionListener(this);
        playBut.setSize(200,50);
        playBut.setLocation(300,0);

        for(int i=0;i<10;i++){							//creating a page for each level and 10 buttons that bring the player to specific level pages
            JButton newButton=new JButton("Level "+(i+1));
            newButton.addActionListener(this);
            newButton.setSize(200,50);						//the size of all buttons is constant
            levelBut[i]=newButton;

            BookPage newPage=new BookPage(i+1);
            pages[i]=newPage;
            completeBook.add(newPage,(i+1)+"");	            //the String assigned to each level is a number from 1 to 10 for clarity. It is not an index
        }

        currentPage=pages[displayedLevel];		                        //displaying the page indicated by the constructor's parameter
        if(displayedLevel==0){                                         //the first and last level pages are special cases because they're missing a previous/next button
            currentPage.addButtons(null,levelBut[1],returnBut,playBut);
        }
        else if(displayedLevel==9){
            currentPage.addButtons(levelBut[8],null,returnBut,playBut);
        }
        else{
            currentPage.addButtons(levelBut[displayedLevel-1],levelBut[displayedLevel+1],returnBut,playBut);
        }

        getContentPane().add(completeBook);
        cLayout.show(completeBook,(displayedLevel+1)+"");
        currentPage=pages[displayedLevel];
        setVisible(true);
    }
    /*--------------------------------------------------------------------------------------
     *This method starts the timer, causing the interface to be updated in actionPerformed()
     *-------------------------------------------------------------------------------------*/
    public void start(){
        myTimer.start();
    }
    /*-----------------------------------------------------------------------------
     *This method changes the page that's shown whenever the player clicks a button
     *----------------------------------------------------------------------------*/
    public void actionPerformed(ActionEvent e){
        Object source=e.getSource();
        if(source==returnBut){				//the main menu is shown if the return to main menu button is clicked
            setVisible(false);
            new MainGame();
        }
        if(source==playBut){              //game frame is in charge of updating and drawing the bomb
            setVisible(false);
            GameFrame actualGame=new GameFrame(currentPage.getBomb(),level);
            actualGame.start();
        }
        if(source==myTimer){                //updating the level page's graphics
            currentPage.repaint();
        }
        else{								//detecting which level button is clicked and showing the corresponding level page
            for(int i=0;i<10;i++){
                if(source==levelBut[i]){											    //From testing, it was found that buttons occasionally disappear from panels when clicking back and forth.
                    if(i==0){														    //Therefore, all the buttons must be added whenever a new page is displayed
                        pages[i].addButtons(null,levelBut[1],returnBut,playBut);	//The first page is unique because it has no previous button; similarly, the last page lacks a next button
                    }
                    else if(i==9){
                        pages[i].addButtons(levelBut[8],null,returnBut,playBut);
                    }
                    else{
                        pages[i].addButtons(levelBut[i-1],levelBut[i+1],returnBut,playBut);
                    }
                    currentPage=pages[i];
                    cLayout.show(completeBook,(i+1)+"");
                }
            }
        }
    }
}
/*---------------------------------------------------------------------------------------------------------------------------------
 *This class makes a panel designated to a specific level. The 10 level pages are created in SelectLevelPage constructor.
 *Custom Objects are required because every page has different buttons. These Objects facilitate the process of adding unique buttons.
 *---------------------------------------------------------------------------------------------------------------------------------*/
class BookPage extends JPanel{
    private int pageNum;				//the level that the page represents (1 to 10)
    private JPanel thisPanel;			//variable that represents this JPanel. Used for clarity so we don't keep typing "this"
    private BombPanel bomb;             //the bomb that's played for a specific level

    /*----------------------------------------------------------
     *Constructor where "level" is the level the page represents
     *----------------------------------------------------------*/
    public BookPage(int level){
        pageNum=level;
        thisPanel=this;
        thisPanel.setLayout(null);                           //allows us to add buttons wherever we want
        bomb=new BombPanel(3,11000);        //make a bomb, possibly pass in array of Bombs as argument
    }
    /*----------------------------------------------------------------------
    This method returns the bomb that belongs to a certain level.
    Used to create GameFrame in SelectLevelPage once play button is clicked
     ----------------------------------------------------------------------*/
    public BombPanel getBomb(){
        return bomb;
    }
    /*---------------------------------------------------------------------------------------------------------------------
     *This method adds specific buttons to the page
     *"prev" goes back a level, "next" advances a level,"returnBut" returns player to main menu, "playBut" starts the game
     *Called in SelectLevelPage actionPerformed() whenever the displayed panel changes
     *--------------------------------------------------------------------------------------------------------------------*/
    public void addButtons(JButton prev,JButton next,JButton returnBut,JButton playBut){
        if(!thisPanel.isAncestorOf(returnBut)){			//the buttons are added only if the JPanel doesn't already have the button
            thisPanel.add(returnBut);                   //Credit goes to Zulaikha, who told Keith he has to check if the panel already has a button
        }
        if(!thisPanel.isAncestorOf(playBut)){
            thisPanel.add(playBut);
        }
        if(prev!=null){									//the first level page lacks a previous button, so this avoids a null pointer exception
            if(!thisPanel.isAncestorOf(prev)){
                prev.setLocation(0,510);
                thisPanel.add(prev);
            }
        }
        if(next!=null){									//the last level page lacks a next button
            if(!thisPanel.isAncestorOf(next)){
                next.setLocation(600,510);
                thisPanel.add(next);
            }
        }
    }
    /*----------------------------------------------------------------------------------------------------------------------------
     *This method is used to display information about the bomb for a level: time required to complete, modules, current best time
     *Since bombs are randomly created every time the program is run, we need a general method of displaying information,
     *rather than blitting a picture that contains information about the bomb on each page of the book.
     *---------------------------------------------------------------------------------------------------------------------------*/
    @Override
    public void paintComponent(Graphics g){
        g.setColor(new Color(255,255,255));			        //make the bombs in main class. Pass around array of bombs as parameter in constructors.
        //get info about the bombs and display the info here
        g.fillRect(0,0,getWidth(),getHeight());
        g.setFont(new Font("Arial",Font.PLAIN,50));
        g.setColor(new Color(0,0,0));
        g.drawString(pageNum+"",375,150);                   //displaying level number
    }
}
/*----------------------------------------------------------------
This class controls the gameplay by displaying and updating a bomb
 -----------------------------------------------------------------*/
class GameFrame extends JFrame implements ActionListener{
    private Timer myTimer;                      //controls when the bomb is updated
    private int tickCount,levelIndex;           //tickCount is a countdown. When it reaches 0, the game stops.
    private BombPanel bomb;

    /*--------------------------------------------------------------
    Constructor which makes the frame
    "thisBomb" is the bomb that belongs to the level being played
    "index" is an index of BookPages. index+1 is the level (1 to 10)
     ---------------------------------------------------------------*/
    public GameFrame(BombPanel thisBomb, int index){
        super("Game Screen");
        System.out.println(index);
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        myTimer=new Timer(10,this);
        tickCount=11000;                                        //this can be adjusted by making a getTime() method for the bomb
        bomb=thisBomb;
        add(bomb);
        levelIndex=index;
        setVisible(true);
    }
    /*-----------------------------------------------------------------
    This method starts the Timer, which enables the game to be updated
     ----------------------------------------------------------------*/
    public void start(){
        myTimer.start();
    }
    /*------------------------------------------------------------------------------------------
    This method updates the game whenever myTimer fires, and stops the game once the time is up
     ------------------------------------------------------------------------------------------*/
    public void actionPerformed(ActionEvent evt){
        Object source=evt.getSource();
        if(source==myTimer){
            tickCount-=10;
            bomb.updateState();
            bomb.repaint();
        }
        if(tickCount==0){
            myTimer.stop();
            new GameOverFrame(bomb,levelIndex);
        }
    }
}
/*----------------------------------------------------------
This class makes a frame after user completes a level
From this frame, user can play again or return to main menu
 ----------------------------------------------------------*/
class GameOverFrame extends JFrame implements ActionListener{
    private BombPanel bomb;                      //necessary field because if user clicks play again, GameFrame constructor needs a BombPanel Object
    private int levelIndex;                      //potentially necessary if we want return button to return player to select level page
    private JButton returnBut,playAgainBut;     //buttons that allow user to return to main menu or play again

    /*---------------------------------------------------------------------------------------------------------------
    Constructor that makes the frame.
    "justPlayed" is the bomb that was completed, necessary because it's used to recreate the bomb if user plays again
    "level" is the level that was just played, necessary to recreate the bomb
     ----------------------------------------------------------------------------------------------------------------*/
    public GameOverFrame(BombPanel justPlayed,int level){	//add score argument
        super("Game Over");
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        returnBut=new JButton("Main menu");
        playAgainBut=new JButton("Play again");
        returnBut.addActionListener(this);
        playAgainBut.addActionListener(this);
        bomb=justPlayed;
        //ImageIcon background=new ImageIcon("Simple game images/game over back.png");
        //JLabel back=new JLabel(background);
        JLabel scoreLabel=new JLabel("Final Score: 01:30");			//displays final score
        JLayeredPane thisPage=new JLayeredPane();
        thisPage.setLayout(null);

        //back.setSize(800,600);
        //back.setLocation(0,0);
        scoreLabel.setSize(500,50);
        scoreLabel.setFont(new Font("Arial",Font.PLAIN,40));
        //scoreLabel.setForeground(new Color(185,122,85));
        scoreLabel.setLocation(325,100);
        thisPage.add(scoreLabel,1);
        //thisPage.add(back,1);

        playAgainBut.setSize(250,100);
        playAgainBut.setLocation(0,0);
        //playAgainBut.setContentAreaFilled(false);
        //playAgainBut.setFocusPainted(false);
        //playAgainBut.setBorderPainted(false);

        returnBut.setSize(250,100);
        returnBut.setLocation(0,300);
        //returnBut.setContentAreaFilled(false);
        //returnBut.setFocusPainted(false);
        //returnBut.setBorderPainted(false);

        thisPage.add(returnBut,2);
        thisPage.add(playAgainBut,2);
        add(thisPage);
        setVisible(true);
    }
    /*-------------------------------------------------------------------
     This method changes frames when a button is pressed
     When the game is over, you can either go to main menu or play again.
     *-------------------------------------------------------------------*/
    public void actionPerformed(ActionEvent evt){
        Object source=evt.getSource();
        if(source==returnBut){
            setVisible(false);
            new MainGame();
        }
        if(source==playAgainBut){               //a new game is started when player clicks play again
            setVisible(false);
            bomb.reset();
            GameFrame actualGame=new GameFrame(bomb,levelIndex);
            actualGame.start();
        }
    }
}
/*--------------------------------------------------------------------------------------------------
 *This class makes the JPanel that's the game screen. It contains a  bomb with modules as attributes
 *-------------------------------------------------------------------------------------------------*/
class BombPanel extends JPanel implements MouseListener{
    private int mouseX,mouseY;          //coordinates of mouse
    private TimeModule timer;           //a countdown
    private WireModule wires;           //the wires puzzle

    /*--------------------------------------------------------
    Constructor which makes a timer and wire module.
    "numWires" specifies how many wires are in the wire module.
    "timeLeft" is the time the player has to complete the level
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
    /*--------------------------------------------------------
    This method is called whenever player clicks "Play again"
    It resets all the modules so the level can be played again
     --------------------------------------------------------*/
    public void reset(){
        timer.resetTime();
    }
    /*----------------------------------------------------------------------
    This method updates the game whenever the Timer fires in GameFrame class
     ----------------------------------------------------------------------*/
    public void updateState(){
        timer.subtractTime();						//displaying a count down
        for(Rectangle rect:wires.getWires()){		//checks if user clicks on wire
            if(rect.contains(mouseX,mouseY)){
                System.out.println("true");
            }
        }
    }

    /*-------------------------------------------------------------------------
    Draws the bomb on screen, called at same time as updateState() in GameFrame
     --------------------------------------------------------------------------*/
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
    private int x,y,time,originalTime;		//time is in milliseconds, x and y are where the module is displayed
                                            //originalTime is how much time is assigned to the level
    /*----------------------------------------
    Constructor, module is made in Bomb class
     ----------------------------------------*/
    public TimeModule(int xCoord, int yCoord, int timeLeft){
        x=xCoord;
        y=yCoord;
        time=originalTime=timeLeft;
    }
    /*-------------------------------------------------------------------------
    This method allows players to play a level again by resetting the countdown
     -------------------------------------------------------------------------*/
    public void resetTime(){
        time=originalTime;
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
/*---------------------------------------------------------------------------------
 *this class makes a frame for an instructions page
 *implements ActionListener because there's a button that returns user to main menu
 *---------------------------------------------------------------------------------*/
class InstructionsPage extends JFrame implements ActionListener{
    private JButton returnBut=new JButton("Main menu");		//button used to return to main menu

    /*---------------------------------------------------------------------
     *constructor method which makes buttons and a background for the frame
     *"g" is a SimpleGame Object
     *-------------------------------------------------------------------*/
    public InstructionsPage(){
        super("Instructions");
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        returnBut.addActionListener(this);

        ImageIcon back=new ImageIcon("images/info back.png");
        JLabel infoBack=new JLabel(back);
        JLayeredPane infoPage=new JLayeredPane();
        infoPage.setLayout(null);

        infoBack.setSize(800,600);
        infoBack.setLocation(0,0);
        infoPage.add(infoBack,1);

        returnBut.setSize(150,55);						//creating a "return to main menu" button and adding it to infoPage
        returnBut.setLocation(0,500);
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
            new MainGame();
        }
    }
}