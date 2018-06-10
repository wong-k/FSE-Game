/*
 * MainGame.java
 * Upon running, a main menu is displayed. Users can access an instructions JFrame, or select level screen.
 * On select level screen, user can click through 10 JPanels and click play to start a game.
 * When the bomb explodes after 10 seconds (the puzzles don't work right now), a game over frame is shown.
 * From game over frame, user can return to main menu or play again.
 * Keith Wong
 */
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.Timer;		//import Timer specifically to avoid conflict with util Timer
public class MainGame extends JFrame implements ActionListener,MouseListener {
    private JButton playBut,infoBut;				//buttons that bring user to level selection and instruction pages
    private Bomb[] allBombs;
    private SelectLevelPage selectLevel;
    /*---------------------------------------------------------------------------------------------------------------------------
    Constructor which makes the main menu
    Rather than having to create a new SelectLevelPage whenever user clicks "Play", a SelectLevelPage is passed in as an argument
     ----------------------------------------------------------------------------------------------------------------------------*/
    public MainGame(SelectLevelPage selectPage) {
        super("Main Menu");
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        allBombs=new Bomb[10];
        selectLevel=selectPage;

        ImageIcon background=new ImageIcon("images/main menu back.png");		//adding a background to main menu
        JLabel menuBack=new JLabel(background);
        JLayeredPane mainPage=new JLayeredPane();
        mainPage.setLayout(null);
        menuBack.setSize(800,600);
        menuBack.setLocation(0,0);

        JLabel scoreLabel=new JLabel("Final Score: 01:30");			//displays final score
        scoreLabel.setSize(500,200);
        scoreLabel.setFont(new Font("Special Elite",Font.BOLD,40));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setLocation(400,0);

        playBut=new JButton("Bombs");                                        //making a play button that changes colour when hovering over it
        playBut.addActionListener(this);
        playBut.addMouseListener(this);
        playBut.setFont(new Font("Special Elite",Font.BOLD,50));
        playBut.setBackground(new Color(98,28,32));
        playBut.setForeground(Color.BLACK);
        playBut.setSize(220,60);
        playBut.setLocation(450,365);
		playBut.setFocusPainted(false);
		playBut.setBorderPainted(false);

        infoBut=new JButton("Manual");                                      //creating a button that opens html manual
        infoBut.addActionListener(this);
        infoBut.addMouseListener(this);
        infoBut.setFont(new Font("Special Elite",Font.BOLD,45));
        infoBut.setBackground(new Color(45,60,100));
        infoBut.setForeground(Color.BLACK);
        infoBut.setSize(220,60);
        infoBut.setLocation(125,365);
        infoBut.setFocusPainted(false);
        infoBut.setBorderPainted(false);

        mainPage.add(menuBack,JLayeredPane.DEFAULT_LAYER);
        mainPage.add(scoreLabel,JLayeredPane.DRAG_LAYER);
        mainPage.add(playBut,JLayeredPane.DRAG_LAYER);
        mainPage.add(infoBut,JLayeredPane.DRAG_LAYER);
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
            selectLevel.start();            //this makes the select level page visible
        }
        else if(source==infoBut){			//when instructions button is clicked, html manual is opened
            try{                            //instructional code found at: https://stackoverflow.com/questions/20517434/how-to-open-html-file-using-java
                File htmlFile=new File("sample.html");
                Desktop.getDesktop().browse(htmlFile.toURI());
            }
            catch(IOException e){
                System.out.println("Can't find html file");
            }
        }
    }
    public void generateBombs(){

    }
    /*-------------------------------------------------------------------------------
    This method makes the button text black when mouse isn't hovering over the buttons
    ---------------------------------------------------------------------------------*/
    public void mouseExited(MouseEvent e) {
        Object source = e.getSource();
        if (source == playBut) {
            playBut.setForeground(Color.BLACK);     //setForeground() controls text colour
        }
        if(source==infoBut){
            infoBut.setForeground(Color.BLACK);
        }
    }
    /*-------------------------------------------------------------------
    This method makes button text white when mouse hovers over the button
     -------------------------------------------------------------------*/
    public void mouseEntered(MouseEvent e){
        Object source=e.getSource();
        if(source==playBut){
            playBut.setForeground(Color.WHITE);
        }
        if(source==infoBut){
            infoBut.setForeground(Color.WHITE);
        }
    }
    /*-------------------------------------------------------------------------
    The following methods must be included in order to implement MouseListener
     -------------------------------------------------------------------------*/
    public void mouseClicked(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mousePressed(MouseEvent e){}

    public static void main(String[]args){                  //create random array of integers 1 to 4
        int[] moduleTypes=new int[1];                       //pass that Array into Bomb constructor, it'll  create the modules from there
        Bomb[] allBombs=new Bomb[10];
        Modules wireTest=new Modules(2,100,100);
        moduleTypes[0]=wireTest.getType();
        allBombs[0]=new Bomb("",0,moduleTypes);
        SelectLevelPage selectPage=new SelectLevelPage(0,allBombs);
        new MainGame(selectPage);
    }
}
/*---------------------------------------------------------------------------------------
 This class creates the frame where players select a level to play
 The frame uses cardLayout, where each panel is a BookPage Object that represents a level.
 *This class is called in the main class, when user clicks the "Select Level" button.
 *--------------------------------------------------------------------------------------*/
class SelectLevelPage extends JFrame implements ActionListener,MouseListener{
    private JPanel completeBook;							//JPanel that stores all the other panels
    private CardLayout cLayout;
    private BookPage[] pages;								//The Objects that represent the pages of the book. The Array is used to update the displayed panel when a button is clicked
    private BookPage currentPage;							//The current page being shown. This is used to update the panel's interface.
    private Timer myTimer;
    private JButton returnBut,playBut;						//A button that brings user back to main menu. This is a field because it has its own if statement in actionPerformed()
    private JButton[] levelBut;								//Array that stores the next/previous buttons to flip between pages
    private int level;
    private Bomb[] allBombs;
    /*-----------------------------------------------------------------------------------------
     Constructor which makes the card layout, buttons, and BookPage Objects
     "displayedLevel" is an index of levelBut and pages. It controls which level page is shown.
     displayedLevel+1 equals a level number ranging from 1 to 10.
     *-----------------------------------------------------------------------------------------*/
    public SelectLevelPage(int displayedLevel,Bomb[] bombs){
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
        allBombs=bombs;

        returnBut=new JButton("Main menu");
        returnBut.addActionListener(this);
        returnBut.addMouseListener(this);
        returnBut.setSize(200,50);			//the location of the return and play buttons is constant for all pages, whereas the buttons for the levels changes location depending on the displayed panel
        returnBut.setLocation(300,510);			//since return and play buttons remain constant, they're created here
        returnBut.setFont(new Font("Special Elite",Font.BOLD,20));
        returnBut.setBackground(Color.WHITE);
        returnBut.setForeground(Color.BLACK);
        returnBut.setFocusPainted(false);
        returnBut.setBorderPainted(false);

        playBut=new JButton("Play");
        playBut.addActionListener(this);
        playBut.addMouseListener(this);
        playBut.setSize(200,50);
        playBut.setLocation(300,0);
        playBut.setFont(new Font("Special Elite",Font.BOLD,35));
        playBut.setBackground(Color.WHITE);
        playBut.setForeground(Color.BLACK);
        playBut.setFocusPainted(false);
        playBut.setBorderPainted(false);

        for(int i=0;i<10;i++){							                //creating a page for each level and 10 buttons that bring the player to specific level pages
            JButton newBut=new JButton("Level "+(i+1));
            newBut.addActionListener(this);
            newBut.addMouseListener(this);
            newBut.setFont(new Font("Special Elite",Font.BOLD,35));
            newBut.setBackground(Color.WHITE);
            newBut.setForeground(Color.BLACK);
            newBut.setSize(200,50);						//the size of all buttons is constant
            newBut.setFocusPainted(false);
            newBut.setBorderPainted(false);
            levelBut[i]=newBut;

            BookPage newPage=new BookPage(i+1,allBombs[i]);
            pages[i]=newPage;
            completeBook.add(newPage,(i+1)+"");            //the String assigned to each level is a number from 1 to 10 for clarity. It is not an index
        }
        unlockLevel(0);                                    //the first level is unlocked by default
        showPage(displayedLevel);                                   //displaying the level indicated by displayedLevel
        getContentPane().add(completeBook);
    }
    /*--------------------------------------------------------------------------------------------------------
    This method unlocks a level to play, called whenever user completes a level and returns to SelectLevelPage.
     ---------------------------------------------------------------------------------------------------------*/
    public void unlockLevel(int pageIndex){
        pages[pageIndex].unlock();
    }
    /*-------------------------------------------------------------
    This method shows a specific page and adds buttons to the page
     ------------------------------------------------------------*/
    public void showPage(int pageIndex){
        currentPage=pages[pageIndex];
        if(pageIndex==0){                     //the first and last level pages are special cases because they're missing a previous/next button
            currentPage.addButtons(null,levelBut[1],returnBut,playBut);
        }
        else if(pageIndex==9){
            currentPage.addButtons(levelBut[8],null,returnBut,playBut);
        }
        else{
            currentPage.addButtons(levelBut[pageIndex-1],levelBut[pageIndex+1],returnBut,playBut);
        }
        cLayout.show(completeBook,(pageIndex+1)+"");
    }
    /*--------------------------------------------------------------------------------------
     *This method starts the timer, causing the interface to be updated in actionPerformed()
     *-------------------------------------------------------------------------------------*/
    public void start(){
        setVisible(true);
        myTimer.start();
    }
    /*-------------------------------------------------------------------------------
    This method makes the button text black when mouse isn't hovering over the buttons
    ---------------------------------------------------------------------------------*/
    public void mouseExited(MouseEvent e) {
        Object source = e.getSource();
        if (source == playBut) {
            playBut.setForeground(Color.BLACK);     //setForeground() controls text colour
        }
        if(source==returnBut){
            returnBut.setForeground(Color.BLACK);
        }
        for(JButton but:levelBut){
            if(source==but){
                but.setForeground(Color.BLACK);
            }
        }
    }
    /*-------------------------------------------------------------------
    This method makes button text white when mouse hovers over the button
     -------------------------------------------------------------------*/
    public void mouseEntered(MouseEvent e){
        Object source=e.getSource();
        if(source==playBut){
            playBut.setForeground(Color.RED);
        }
        if(source==returnBut){
            returnBut.setForeground(Color.RED);
        }
        for(JButton but:levelBut){
            if(source==but){
                but.setForeground(Color.RED);
            }
        }
    }
    /*-------------------------------------------------------------------------
    The following methods must be included in order to implement MouseListener
     -------------------------------------------------------------------------*/
    public void mouseClicked(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    /*-----------------------------------------------------------------------------
     *This method changes the page that's shown whenever the player clicks a button
     *----------------------------------------------------------------------------*/
    public void actionPerformed(ActionEvent e){
        Object source=e.getSource();
        if(source==returnBut){				//the main menu is shown if the return to main menu button is clicked
            setVisible(false);
            new MainGame(this);
        }
        if(source==playBut && currentPage.getLockedStatus().equals("Unlocked")){              //game frame is in charge of updating and drawing the bomb
            setVisible(false);
            GameFrame actualGame=new GameFrame(currentPage.getBomb(),level,this);
            actualGame.start();
        }
        if(source==myTimer){                //updating the level page's graphics
            currentPage.repaint();
        }
        else{								//detecting which level button is clicked and showing the corresponding level page
            for(int i=0;i<10;i++){
                if(source==levelBut[i]){    //From testing, it was found that buttons occasionally disappear from panels when clicking back and forth.
                    showPage(i);            //Therefore, all the buttons must be added whenever a new page is displayed, which is what showPage does.
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
    private Bomb bomb;             //the bomb that's played for a specific level
    private String locked;              //String because paintComponent writes either "Locked" or "Unlocked" on the panel

    /*----------------------------------------------------------
     *Constructor where "level" is the level the page represents
     *----------------------------------------------------------*/
    public BookPage(int level,Bomb inputBomb){
        pageNum=level;
        setLayout(null);                           //allows us to add buttons wherever we want
        bomb=inputBomb;
        locked="Locked";
    }
    public String getLockedStatus(){
        return locked;
    }
    public void unlock(){
        locked="Unlocked";
    }
    /*----------------------------------------------------------------------
    This method returns the bomb that belongs to a certain level.
    Used to create GameFrame in SelectLevelPage once play button is clicked
     ----------------------------------------------------------------------*/
    public Bomb getBomb(){
        return bomb;
    }
    /*---------------------------------------------------------------------------------------------------------------------
     *This method adds specific buttons to the page
     *"prev" goes back a level, "next" advances a level,"returnBut" returns player to main menu, "playBut" starts the game
     *Called in SelectLevelPage actionPerformed() whenever the displayed panel changes
     *--------------------------------------------------------------------------------------------------------------------*/
    public void addButtons(JButton prev,JButton next,JButton returnBut,JButton playBut){
        if(!isAncestorOf(returnBut)){			//the buttons are added only if the JPanel doesn't already have the button
            add(returnBut);
        }
        if(!isAncestorOf(playBut)){
            add(playBut);
        }
        if(prev!=null){									//the first level page lacks a previous button, so this avoids a null pointer exception
            if(!isAncestorOf(prev)){
                prev.setLocation(0,510);
                add(prev);
            }
        }
        if(next!=null){									//the last level page lacks a next button
            if(!isAncestorOf(next)){
                next.setLocation(600,510);
                add(next);
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
        g.setFont(new Font("Special Elite",Font.PLAIN,50));
        g.setColor(new Color(0,0,0));
        g.drawString(pageNum+"",375,150);                   //displaying level number
        g.drawString("Status: "+locked,375,250);
    }
}
/*----------------------------------------------------------------
This class controls the gameplay by displaying and updating a bomb
 -----------------------------------------------------------------*/
class GameFrame extends JFrame implements ActionListener{
    private Timer myTimer;                      //controls when the bomb is updated
    private int tickCount,levelIndex;           //tickCount is a countdown. When it reaches 0, the game stops.
    private Bomb bomb;
    private SelectLevelPage selectLevel;

    /*--------------------------------------------------------------
    Constructor which makes the frame
    "thisBomb" is the bomb that belongs to the level being played
    "index" is an index of BookPages. index+1 is the level (1 to 10)
     ---------------------------------------------------------------*/
    public GameFrame(Bomb thisBomb, int index,SelectLevelPage levelPage){
        super("Game Screen");
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        myTimer=new Timer(10,this);
        //tickCount=thisBomb.getTotalTime();                                        //this can be adjusted by making a getTime() method for the bomb
        bomb=thisBomb;
        add(bomb);
        levelIndex=index;
        selectLevel=levelPage;
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
        if(tickCount==0 || bomb.getStrikes()==3){                   //game ends if time runs out, or player makes 3 mistakes
            myTimer.stop();
            new GameOverFrame(bomb,levelIndex,selectLevel);
        }
    }
}
/*---------------------------------------------------------------------------------------------------
This class makes a bomb Object that has an Array of modules as an attribute
The Bomb detects when the player clicks on a module, and then tells the module to handle interactions
The Bomb also tells all its modules to draw themselves using methods in Modules class
 ----------------------------------------------------------------------------------------------------*/
class Bomb extends JPanel implements MouseListener{
    private String serial;                  //Serial code used for more complex defusing rules
    private int numBat;                     //number of batteries for same purpose as above
    private int numMod;                     //the number of modules on this bomb
    private Modules[][] minigames;          //2D Array that contains the modules located on front and back of the Bomb
    private int face;                       //Controls which side of the Bomb is shown. 0 for the front, 1 for the back.
    private int mouseX,mouseY,strikes;      //mouseX and mouseY are the mouse coordinates. strikes is how many mistakes the player has made. The game ends if strikes==3
    private Modules currentInteract;        //the module that is being interacted with right now

    /*-----------------------------------------------------------------------------------------------------
    This is the constructor of the bomb
    "serial" is a serial code, "bat" is number of batteries
    "modTypes" contains numbers from 1-4 that're interpreted as constants when a Modules Object is created
    ------------------------------------------------------------------------------------------------------*/
    public Bomb(String serialCode,int bat,int[] modTypes){
        addMouseListener(this);
        serial=serialCode;
        numBat=bat;
        numMod=modTypes.length;
        face=strikes=0;                                                                   //the front of the bomb is shown by default
        int[][] cornerCoord={{100,100},{100,300},{300,100},{500,100},{500,300}};          //each module box is drawn as a rectangle, so these are the rectangles' coordinates

        if(numMod>5){                                           //If there are more than 5 modules including the countdown
            minigames=new Modules[6][2];                        //Each element has two spots: the first one contains a module on the front of the bomb, the second one is for the back
        }
        else {
            minigames = new Modules[6][1];
        }
        for(int i=0;i<numMod;i++) {                             //assigning modules to the new 2D Array
            if (i<=5) {
                minigames[i][0] = new Modules(modTypes[i],cornerCoord[i][0],cornerCoord[i][1]);
            }
            else {                                              //the front of the bomb only has room for 5 modules, so we go back and start adding modules to the back
                minigames[i - 5][1] = new Modules(modTypes[i],cornerCoord[i-5][0],cornerCoord[i-5][1]);
            }
        }
    }
    public void reset(){
        strikes=face=0;
        for(Modules[] modList:minigames){
            if(modList[0]!=null) {                          //remove this once all modules are made
                modList[0].reset();
            }
            if(modList.length==2){
                modList[1].reset();
            }
        }
    }
    /*----------------------------------------------------------------------------------------------------------
    This method is used by GameFrame to see how many mistakes the player has made and end the game if strikes==3
    -----------------------------------------------------------------------------------------------------------*/
    public int getStrikes(){
        return strikes;
    }
    /*-----------------------------------------------------------------------------------------
    This method changes the displayed side of the bomb when flip button is clicked in GameFrame.
    Changing "face" will change the modules that're drawn
    ------------------------------------------------------------------------------------------*/
    public void changeFace(){
        face=1-face;                    //if face is currently 0, it must change to 1 and vice versa
    }
    /*----------------------------------------------------------------------
    This method updates the Bomb whenever the Timer fires in GameFrame class
     ----------------------------------------------------------------------*/
    public void updateState(){
    }
    /*---------------------------------------------------------------------------------------------------------
    This method draws a 3 x 2 grid that represents the bomb. It also tells all the modules to draw themselves.
    This is called whenever the Timer fires in GameFrame
    ----------------------------------------------------------------------------------------------------------*/
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

        for(Modules[] mod:minigames){                       //drawing the modules on the current face
            Modules facingMod=mod[face];
            if(facingMod!=null) {
                facingMod.draw(g);
            }
        }
    }
    /*------------------------------------------------------------------------------------------------------
    This method verifies if the user wants to interact with a module, or if they just clicked on empty space
    It also checks if the user is defusing a module correctly
    -------------------------------------------------------------------------------------------------------*/
    public void mousePressed(MouseEvent e){
        mouseX=e.getX();
        mouseY=e.getY();
        for(Modules[] mod:minigames){
            Modules facingMod=mod[face];
            if(facingMod!=null) {                                         //remove this line once all bombs have been made
                if (!facingMod.alreadyDefused(mouseX, mouseY)) {          //user has clicked on a module that has not been defused
                    currentInteract=facingMod;
                    facingMod.startInteraction();                         //this enables the actual gameplay with the module
                }
                else {
                    facingMod.setUnfocused();
                }
            }
        }
        if(currentInteract!=null) {
            if (!currentInteract.correctPlayerAction()) {                  //this can't be called in updateState() because "strikes" would increase every 10 milliseconds, ending the game instantly
                strikes++;
            }
        }
    }
    /*--------------------------------------------------------------------
    The following method must be implemented in order to use MouseListener
     --------------------------------------------------------------------*/
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseClicked(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
}
/*-------------------------------------------------------------------------------------------------------
This class creates a generic module which is then assigned a specific module, such as wires or simon says
It tells modules to draw themselves and determines when player has clicked on a module
 -------------------------------------------------------------------------------------------------------*/
class Modules {
    public final int BUTTON=1;               //Constants for clarity when dealing with modules
    public final int WIRES=2;
    public final int SYMBOLS=3;
    public final int SIMON=4;

    private Rectangle mod;                  //the module's hitbox
    private int type,x,y;                   //type is one of the constants which indicate the type of module. x and y are coordinates of the hitbox
    private boolean defused,isFocused;      //defused indicates if this module has been solved or not. isFocused indicates if user has clicked a particular module
    private boolean correctAction;          //this verifies if the user is playing the module correctly
    private int mouseX,mouseY;
    private Button click;                   //only one of these is assigned a value, the rest are null
    private WireModule cut;
    //private Symbols press;
    //private Simon pattern;
    /*--------------------------------------------------------------------
    Constructor that makes the module
    "modType" indicates what type of module is made based on the constants
    "x" and "y" are the coordinates of where the module hitbox is
    ---------------------------------------------------------------------*/
    public Modules(int modType,int x,int y){
        type=modType;
        /*if(type==1){
            click=new Button();             //add arguments to constructors if necessary
        }*/
        if(type==WIRES){
            cut=new WireModule(x,y);
        }
        /*if(type==3){
            press=new Symbols();
        }
        if(type==4){
            pattern=new Simon();
        }*/
        this.x=x;
        this.y=y;
        mod=new Rectangle(x,y,200,200);
        isFocused=false;                                //by default, the module has not been defused yet and it hasn't been clicked on
        defused=false;
        correctAction=true;
    }
    /*---------------------------------------------------------------------------------------
    This method is used by Bomb class to see if user is interacting correctly with the module.
    If it's false, a strike will be added in Bomb class.
     --------------------------------------------------------------------------------------*/
    public boolean correctPlayerAction(){
        return correctAction;
    }
    /*----------------------------------------------------------------
    Remove this method once we add the code to randomly generate bombs
    -----------------------------------------------------------------*/
    public int getType(){
        return type;
    }
    /*------------------------------------------------------------------------------------
    This method changes the isFocused value, meaning user isn't interacting with this box
     ------------------------------------------------------------------------------------*/
    public void setUnfocused(){
        isFocused=false;
    }
    /*---------------------------------------------------------------------------------
    This method checks if the user clicked on a module that hasn't been defused yet
    Called in mousePressed() in Bomb class
    Returns a boolean that tells the game whether or not the module is already defused
    ----------------------------------------------------------------------------------*/
    public boolean alreadyDefused(int mouseX,int mouseY){
        if(mod.contains(mouseX,mouseY)) {
            this.mouseX=mouseX;
            this.mouseY=mouseY;
            isFocused = true;                       //this enables interaction with the module
            return defused;
        }
        return false;
    }
    /*-----------------------------------------------------------------------------------------------------------------
    This method calls the interaction methods of each module.
    This enables gameplay, such as checking if correct wires were cut, or if Simon says pattern was repeated correctly
    This is called in mousePressed() in Bomb class once it has been verified that the player clicked on a valid module
     ------------------------------------------------------------------------------------------------------------------*/
    public void startInteraction(){
        if(type==WIRES){                    //this is where you create anything that needs to be passed in as an argument in the modules' interact()
            correctAction=cut.interact(mouseX,mouseY);
            System.out.println(correctAction);
        }
    }
    /*------------------------------------------------------------------------------
    This method outlines the module box if it's currently focused
    It also calls the draw methods of each module, which makes them draw themselves
     ------------------------------------------------------------------------------*/
    public void draw(Graphics g){
        if(isFocused){                      //if this module is currently being focused on, it's outlined in green
            g.setColor(Color.GREEN);
        }
        else{
            g.setColor(Color.BLACK);
        }
        g.drawRect((int)mod.getX(),(int)mod.getY(),200,200);       //drawing the box for the module
        if(type==WIRES){
            cut.draw(g);
        }
    }
    public void reset(){
        if(type==WIRES){
            cut.reset();
        }
    }
}
/*--------------------------------------------------------------
This class makes a wire module with a specified number of wires
*-------------------------------------------------------------*/
class WireModule{
    private SingleWire[] wires;				//each wire is a rectangle so collisions with the mouse can be detected
    private int[][]colours;					//the colours of the wires
    private int[] correctOrder;             //contains codes in the correctOrder
    private int numWires,allottedTime,startIndex;

    /*--------------------------------------------------------------------------------------------
    Constructor which creates a specified number of Rectangles and rgb values that represent wires
    "coord" is a list of y coordinates that paintComponent uses to draw the wires.
    "wireCount" is how many wires there will be
     -------------------------------------------------------------------------------------------*/
    public WireModule(int startX,int startY){
        Random rand=new Random();
        numWires=2+rand.nextInt(4);         //2 - 5 possible number of wires
        allottedTime=10000*numWires;               //20 - 50 seconds to solve the module
        int[][]allColours={{255,0,0},{0,255,0},{0,0,255},{255,255,0},{255,0,255},{0,255,255}};  //possible wire colours: red, blue, green, yellow, magenta,light blue
        correctOrder=new int[numWires];
        wires=new SingleWire[numWires];		                            //creating the Rectangles that represent the wire hitboxes
        int spaceBetween=(200-numWires*10)/(numWires+1);                //space between wires in order for them to be evenly spaced
        startIndex=0;

        for(int i=0;i<numWires;i++){
            int index=rand.nextInt(numWires);							//choosing a random colour out of all the possible colours and assigning it to a wire
            int[] rgbSet=allColours[index];
            correctOrder[i]=rgbSet[0]*numWires+rgbSet[1]*2+rgbSet[2]*3;
            System.out.println("colour: "+Arrays.toString(rgbSet)+" code: "+correctOrder[i]);

            int YCoord=startY+spaceBetween*(i+1)+10*i;
            wires[i]=new SingleWire(startX,YCoord,rgbSet,correctOrder[i]);
        }
        Arrays.sort(correctOrder);					//wires must be cut in ascending order
        System.out.println(Arrays.toString(correctOrder));
    }
    public void draw(Graphics g){
        for(SingleWire wire:wires) {
            int[] rgb = wire.getColour();
            Rectangle hitbox = wire.getRect();
            if (!wire.alreadyCut()) {
                g.setColor(new Color(rgb[0], rgb[1], rgb[2]));
            }
            else{
                g.setColor(Color.BLACK);
            }
            g.fillRect((int) hitbox.getX(), (int) hitbox.getY(), 200, 10);
        }
    }
    public void reset(){
        startIndex=0;
        for(SingleWire wire:wires){
            if(wire.alreadyCut())
                wire.setCut();
        }
    }
    /*-------------------------------------------------------------------------------------------------------
    User has clicked on a wire, determine which wire was clicked and compare it to the index of correctOrder
    The value returned determines if the player gets a strike or not
     ------------------------------------------------------------------------------------------------------*/
    public boolean interact(int x,int y){
        SingleWire selected=null;                   //if this is still null at the end of the process, that means user clicked empty space
        for(SingleWire wire:wires){                 //first, we have to determine if user clicked a wire or empty space
            if(wire.contains(x,y)){
                selected=wire;
            }
        }
        if(selected!=null) {
            if (selected.getCode() == correctOrder[startIndex] && !selected.alreadyCut()) {         //check if the correct wire was cut
                startIndex++;
                selected.setCut();
                return true;
            }
        }
        if(selected==null || selected.alreadyCut()){        //player isn't penalized for clicking empty space, or a wire that was already cut
            return true;
        }
        return false;                                       //this means user clicked the incorrect wire
    }
    public int getAllottedTime(){
        return allottedTime;
    }
}
class SingleWire{
    private Rectangle hitbox;
    private int code;
    private int colour[];
    private boolean cut;                //different graphics shown for cut and uncut wires

    public SingleWire(int x,int y, int[] rgb,int colourCode){
        hitbox=new Rectangle(x,y,200,10);
        code=colourCode;
        colour=rgb;
        cut=false;
    }
    public boolean alreadyCut(){
        return cut;
    }
    public void setCut(){
        cut=!cut;
    }
    public int[] getColour(){
        return colour;
    }
    public Rectangle getRect(){
        return hitbox;
    }
    public int getCode(){
        return code;
    }
    public boolean contains(int x,int y){
        return hitbox.contains(x,y);
    }
}
/*--------------------------------------------------------------------------------------------------
 *This class makes the JPanel that's the game screen. It contains a  bomb with modules as attributes
 *-------------------------------------------------------------------------------------------------
class BombPanel extends JPanel implements MouseListener{
    private int mouseX,mouseY;          //coordinates of mouse
    private TimeModule timer;           //a countdown
    private WireModule wires;           //the wires puzzle
    private int totalTime;
    /*--------------------------------------------------------
    Constructor which makes a timer and wire module.
    "numWires" specifies how many wires are in the wire module.
    "timeLeft" is the time the player has to complete the level
     ---------------------------------------------------------
    public BombPanel(int numWires){
        addMouseListener(this);
        mouseX=mouseY=0;
        int[] wireYCoord=new int[numWires];
        int spaceBetweenWires=(200-10*numWires)/(numWires+1);		//adjust: 200, which is the height of the squares on the grid.
        for(int i=0;i<numWires;i++){
            int nextYCoord=100+spaceBetweenWires*(i+1)+10*i;		//adjust: 100, which is y-coord where the module box starts. 10, which is current width of wires
            wireYCoord[i]=nextYCoord;
        }
        wires=new WireModule(wireYCoord);					//creating a wire module
        totalTime=3000;//wires.getAllottedTime()+1000;
        timer=new TimeModule(340,425,totalTime);		//creating a timer
    }
    public int getTotalTime(){
        return totalTime;
    }
    /*--------------------------------------------------------
    This method is called whenever player clicks "Play again"
    It resets all the modules so the level can be played again
     --------------------------------------------------------
    public void reset(){
        timer.resetTime();
    }
    /*----------------------------------------------------------------------
    This method updates the game whenever the Timer fires in GameFrame class
     ----------------------------------------------------------------------
    public void updateState(){
        timer.subtractTime();						//displaying a count down
        for(Rectangle rect:wires.getWires()){		//checks if user clicks on wire
            if(rect.contains(mouseX,mouseY)){
                System.out.println("true");
            }
        }
    }

    /*--------------------------------------------------------------------------------------------
    Draws a 3 x 2 grid that represents the bomb, called at same time as updateState() in GameFrame
     --------------------------------------------------------------------------------------------
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
     ---------------------------------------------------------
    public void mousePressed(MouseEvent e){
        mouseX=e.getX();
        mouseY=e.getY();
    }
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseClicked(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
}*/
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
        int seconds=(time-(min*60000))/1000;
        String output=String.format("%2d:%2d",(int)min,seconds).replace(" ","0");		//replacing blank spaces with 0's so it looks like a timer
        return output;
    }
}
/*--------------------------------------------------------------------
This class makes a frame after user completes a level
From this frame, user can play again or return to select level screen
 --------------------------------------------------------------------*/
class GameOverFrame extends JFrame implements ActionListener,MouseListener {
    private Bomb bomb;                      //necessary field because if user clicks play again, GameFrame constructor needs a BombPanel Object
    private int levelIndex;                      //potentially necessary if we want return button to return player to select level page
    private JButton returnBut, playAgainBut;     //buttons that allow user to return to select level screen or play again
    private SelectLevelPage selectLevel;
    /*---------------------------------------------------------------------------------------------------------------
    Constructor that makes the frame.
    "justPlayed" is the bomb that was completed, necessary because it's used to recreate the bomb if user plays again
    "level" is the level that was just played, necessary to recreate the bomb
     ----------------------------------------------------------------------------------------------------------------*/
    public GameOverFrame(Bomb justPlayed, int level,SelectLevelPage levelPage) {    //add score argument
        super("Game Over");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        bomb = justPlayed;
        levelIndex = level;
        selectLevel=levelPage;

        ImageIcon background = new ImageIcon("images/game over back.png");
        JLabel back = new JLabel(background);
        JLayeredPane thisPage = new JLayeredPane();
        thisPage.setLayout(null);
        back.setSize(800, 600);
        back.setLocation(0, 0);

        JLabel scoreLabel = new JLabel("Final Score: 01:30");            //displays final score
        scoreLabel.setSize(500, 50);
        scoreLabel.setFont(new Font("Special Elite", Font.BOLD, 40));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setLocation(325, 100);

        playAgainBut = new JButton("Play again");
        playAgainBut.addActionListener(this);
        playAgainBut.addMouseListener(this);
        playAgainBut.setFont(new Font("Special Elite", Font.BOLD, 30));
        playAgainBut.setBackground(new Color(51, 31, 23));
        playAgainBut.setForeground(Color.WHITE);
        playAgainBut.setSize(250, 100);
        playAgainBut.setLocation(0, 460);
        playAgainBut.setFocusPainted(false);
        playAgainBut.setBorderPainted(false);

        returnBut = new JButton("Main menu");
        returnBut.addActionListener(this);
        returnBut.addMouseListener(this);
        returnBut.setFont(new Font("Special Elite", Font.BOLD, 30));
        returnBut.setBackground(new Color(51, 31, 23));
        returnBut.setForeground(Color.WHITE);
        returnBut.setSize(250, 100);
        returnBut.setLocation(550, 460);
        returnBut.setFocusPainted(false);
        returnBut.setBorderPainted(false);

        thisPage.add(back, JLayeredPane.DEFAULT_LAYER);
        thisPage.add(returnBut, JLayeredPane.DRAG_LAYER);
        thisPage.add(playAgainBut,JLayeredPane.DRAG_LAYER);
        thisPage.add(scoreLabel,JLayeredPane.DRAG_LAYER);
        add(thisPage);
        setVisible(true);
    }

    /*-------------------------------------------------------------------
     This method changes frames when a button is pressed
     When the game is over, you can either go to main menu or play again.
     *-------------------------------------------------------------------*/
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == returnBut) {
            setVisible(false);
            new MainGame(selectLevel);
        }
        if (source == playAgainBut) {               //a new game is started when player clicks play again
            setVisible(false);
            bomb.reset();
            GameFrame actualGame = new GameFrame(bomb, levelIndex,selectLevel);
            actualGame.start();
        }
    }

    /*-------------------------------------------------------------------------------
This method makes the button text black when mouse isn't hovering over the buttons
---------------------------------------------------------------------------------*/
    public void mouseExited(MouseEvent e) {
        Object source = e.getSource();
        if (source == playAgainBut) {
            playAgainBut.setForeground(Color.WHITE);     //setForeground() controls text colour
        }
        if (source == returnBut) {
            returnBut.setForeground(Color.WHITE);
        }
    }

    /*-------------------------------------------------------------------
    This method makes button text white when mouse hovers over the button
     -------------------------------------------------------------------*/
    public void mouseEntered(MouseEvent e) {
        Object source = e.getSource();
        if (source == playAgainBut) {
            playAgainBut.setForeground(Color.RED);
        }
        if (source == returnBut) {
            returnBut.setForeground(Color.RED);
        }
    }

    /*-------------------------------------------------------------------------
    The following methods must be included in order to implement MouseListener
     -------------------------------------------------------------------------*/
    public void mouseClicked(MouseEvent e) {
    }
    public void mouseReleased(MouseEvent e) {
    }
    public void mousePressed(MouseEvent e) {
    }
}