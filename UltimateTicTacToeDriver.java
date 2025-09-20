import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * Authors: Maxim Beauchemin and Bardia Basiri
 * Date: 2024-10-29
 * Subject: A game of ultimate tic tac toe for the final project. Call the main method to start the game
 */

// Driver class with only a main method
public class UltimateTicTacToeDriver {
    // Main method to start the game
    public static void main(String[] args) {
        // Starts the game
        UltimateTicTacToe ult = new UltimateTicTacToe(); // Creates an instance of the UltimateTicTacToe class
        JFrame f = new JFrame("UltimateTicTacToe"); // Initialises JFrame 
        f.setSize(700, 900); // Sets frame size
        f.setVisible(true); // Sets frame to be visible
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Terminates the program once the frame is closed
        f.add(ult); // Adds the object of UltimateTicTacToe class to frame
    }
}

// Class that runs the game itself using buttons and a mouse listener
class UltimateTicTacToe extends JPanel implements ActionListener, MouseListener{
    private static int[][] posX, posY; // 2D arrays of the x and y coordinates of all the corners and intersections on the board
    private int lastSquare; // Variable to record the last board played in, -1 means the next player can play anywhere
    private char screen; // t for title screen, p for how to play, and s for the game itself
    private char win; // Represents who has won: 0 for no win, x for x wins, o for o wins, t for a tie
    private char turn; // Represents which person's turn it is, x for Player 1 and o for Player 2
    
    private Mini[] bigBoard; // Array of objects of the mini board class. each element represents a mini board
    // the whole array represents the whole board (a collection of 9 smaller boards)
    // simpler to use a 1D array than a 2D array
    
    private JButton start, how, title; // A button to 1. start game, 2. send you to the how to play screen, 3. send back to the title screen
    private JLabel howTo; // JLabel which contains the instructions for how to play the game
    
    private Timer time = new Timer(100, this); // Timer object to repaint every 100 milliseconds
    private Color back = new Color(210, 191, 224); // Creates a color object with specific RGB values to make setting the background easier
    private ImageIcon logo = new ImageIcon("Logo.png"); // ImageIcon object logo which is the logo for our game exported as png placed in the same java file as our class
    
    // Default constructor to initialise variables
    public UltimateTicTacToe() {
        bigBoard = new Mini[9]; // Creates an array of 9 instances of the mini board class, each representing one of the 9 boards
        lastSquare = -1; // Starts the last square so that the plyaer can pick any mini board
        screen = 't'; // Initialises to the title screen
        win = '0'; // Initialising to no one winning
        turn = 'x'; // Initialising to x's turn, then swaps betweem x and o for each turn
        
        // Both arrays of positions represent a 4 x 4 grid
        posX = new int[4][4];
        posY = new int[4][4];
        // Filling up the arrays with values for x and y coordinates
        for (int i = 0; i < posX.length; i++) {
            for (int j = 0; j < posX[0].length; j++) {
                posX[i][j] = j * 200 + 50;
                posY[i][j] = i * 200 + 250;
            }    
        }
        
        // Filling up the array of instances and providing the x and y coordinates of the top left position
        for (int i = 0; i < bigBoard.length; i++) {
            bigBoard[i] = new Mini(posX[i/3][i%3], posY[i/3][i%3]);
        }
        
        time.start(); // Start the timer
        addMouseListener(this);
        setLayout(null);
        
        // Initialising the buttons
        
        // Creates the start game button
        start = new JButton("Start Game"); 
        start.addActionListener(this);
        start.setBounds(50, 500, 250, 150);
        start.setFont(new Font("Sans Serif", Font.PLAIN, 40));
        start.setBackground(new Color(80, 83, 212));
        
        // Creates the how to play button
        how = new JButton("How to Play");
        how.addActionListener(this);
        how.setBounds(400, 500, 250, 150);
        how.setFont(new Font("Sans Serif", Font.PLAIN, 40));
        how.setBackground(new Color(204, 86, 82));
        
        // Creates the return to title sceen button
        title = new JButton("Return to Title Screen");
        title.addActionListener(this);
        title.setBounds(10, 10, 250, 30);
        title.setFont(new Font("Sans Serif", Font.PLAIN, 20));
        title.setBackground(new Color(177, 113, 222));
        
        // Filling the JLabel with the rules of the game
        howTo=new JLabel("<html>"
        + "The Rules of Ultimate Tic Tac Toe"
        + "<br>"
        + "<br>Setup:"
        + "<br>Ultimate Tic-Tac-Toe is played with two players, one for X, and one for O"
        + "<br>There is a larger tic-tac-toe board that consists of 9 smaller tic-tac-toe boards which each consist of 9 squares"
        + "<br>"
        + "<br>Goal:"
        + "<br>The goal is to own 3 boards in a row, column, or diagonal"
        + "<br>Each smaller boards is won like a regular game of Tic-Tac-Toe"
        + "<br>"
        + "<br>How To Play:"
        + "<br>1. On they’re first turn, Player X clicks one of the smaller squares on the board, it will show up as an X"
        + "<br>"
        + "<br>2. Player O then clicks a square, but it has to be in the corresponding board to the square where Player X just played. For example, if Player X clicks the top right square of the top left board, Player O must click a square in the top right board. Play then alternates back and forth with each player clicking on the board corresponding to the square clicked by the other player." 
        + "<br>"
        + "<br>3. Play continues until a player makes 3 squares in a row, column, or diagonal in a board. The player then owns the square. From then on, if a player clicks on a square that corresponds to a board a player already owns, then the next player gets to choose which board to play in."
        + "<br>"
        + "<br>4. The game is over when a player owns 3 boards in a row, column, or diagonal"
        + "<br>"
        + "<br>5. A tie occurs when every board has an owner, but no player has won."
        + "</html>");
        howTo.setBounds(10, 50, 680, 750);
        howTo.setFont(new Font("Sans Serif", Font.PLAIN, 18));
        howTo.setLayout(null);
        
        // Adds the start and how buttons to the start screen
        add(start);
        add(how);
        
    }
    
    // Checks all win cases
    public void winCheck() {
        char case1; // The base case that ensures all other board required for a win are matching
        
        // Checking two diagonals
        case1 = bigBoard[0].owner; // Base case is top left
        if (bigBoard[4].owner == case1 && bigBoard[8].owner == case1 && case1 != '0') { // Top left to bottom right boards
            win = case1;
            return;
        } else {
            case1 = bigBoard[2].owner; // Base case is top right
            if (bigBoard[4].owner == case1 && bigBoard[6].owner == case1 && case1 != '0') { // Top right to bottom right boards
                win = case1; // Sets the win to whoever owns the 3 boards
                return;
            }
        }
        
        // Checking rows and columns
        for (int i = 0; i < Math.sqrt(bigBoard.length); i++) {
            case1 = bigBoard[i*3].owner; // Sets base case to the left board
            if (bigBoard[i*3+1].owner == case1 && bigBoard[i*3+2].owner == case1 && case1 != '0') { // Checking each of the 3 rows
                win = case1; // Sets the win to whoever owns the 3 board
                return;
            } else {
                case1 = bigBoard[i].owner; // Sets base case to the top board
                if (bigBoard[i+3].owner == case1 && bigBoard[i+6].owner == case1 && case1 != '0') { // Checking each of the 3 columns
                    win = case1; // Sets the win to whoever owns the 3 board
                    return;
                }
            }
        }
    }
    
    // If a button is pressed, checks which button is pressed
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == start) { 
            screen = 's'; // Sets the char screen to 's' meaning the game has started
            // Adds and removes buttons
            remove(start);
            remove(how);
            add(title);
        } else if (e.getSource() == how) {
            screen = 'p'; // Sets the char screen to 'p' how to play screen has started
            // Adds and removes buttons and JLabel
            remove(start);
            remove(how);
            add(title);
            add(howTo);
        } else if (e.getSource() == title) {
            screen = 't'; // Sets the char screen to 't' title screen is being displayed
            // Adds and removes buttons
            add(start);
            add(how);
            remove(title);
            remove(howTo);
        }
        repaint(); // Reapplies the graphics by calling paintComponent method again
    }

    // If the mouse is clicked, determines the x and y coordinates of the mouse and calculates which board and square it is on
    @Override
    public void mouseClicked(MouseEvent e) {
        if (screen == 's' && win == '0') {
            int x = e.getX(), y = e.getY(); // Getting the x and y positions of the mouse
            int I = 0; // Setting a variable that with loop through the bigBoard array
            
            // Looping through each square on the board
            for (int r = 0; r < posX.length-1; r++) {
                for (int c = 0; c < posX[r].length-1; c++) {
                    // Checking if the mouse is within one of the smaller boards, no one owns the square, and it corresponds to the last square clicked
                    if (x > posX[r][c]+25 && x < posX[r][c+1]-25 && y > posY[r][c]+25 && y < posY[r+1][c]-25
                    && bigBoard[I].owner == '0' && (lastSquare == I || lastSquare == -1)) {
                            bigBoard[I].squareClicked(x, y); // Calls the squareClicked method using the object of the mini board that was clicked
                            return;
                    }
                    I++; // Incrementing the variable that loops through the bigBoard array
                }
            }
        }
    }
    @Override public void mouseExited(MouseEvent e) {} 
    @Override public void mouseEntered(MouseEvent e) {} 
    @Override public void mouseReleased(MouseEvent e) {} 
    @Override public void mousePressed(MouseEvent e) {}
    
    // Paints on the JFrame
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Call to the super class to ensure the entire component will be painted
        Graphics2D g2 = (Graphics2D) g; // Creating a 2D graphics object
        
        // Switch statement telling the paintComponent what graphics to generate depending on the current screen
        switch(screen){
            case 's':// Case is the game screen
                g.setColor(back); // Sets to the background color 
                g.fillRect(0, 0, 700, 900); // Fills a rectangle in the background
                g2.setStroke(new BasicStroke(5)); // Sets stroke size to 5
                
                // Highlights the square where the next player must play
                if (lastSquare != -1) {
                    if (turn == 'o') {
                        g.setColor(new Color(191, 200, 224)); // highlights light blue if it's O's turn
                    } else if (turn == 'x') {
                        g.setColor(new Color(224, 191, 191)); // highlight light red if it's X's turn
                    }
                    g.fillRect(posX[lastSquare/3][lastSquare%3], posY[lastSquare/3][lastSquare%3], 200, 200);
                }
                
                // Changes color to black and draws the lines for the big board
                g.setColor(Color.black);
                g.drawLine(250, 250, 250, 850);
                g.drawLine(450, 250, 450, 850);
                g.drawLine(50, 450, 650, 450);
                g.drawLine(50, 650, 650, 650);
                
                for (int i = 0; i < bigBoard.length; i++) {
                    bigBoard[i].draw(g); // Calls the method that draws the mini boards
                    switch(bigBoard[i].owner) { // Switch case for if x or o owns a square
                        case 'x':// Draws a large blue x over that particular square
                            g2.setStroke(new BasicStroke(8));
                            g.setColor(Color.red);
                            g.drawLine(posX[i/3][i%3]+20, posY[i/3][i%3]+20, posX[i/3+1][i%3+1]-20, posY[i/3+1][i%3+1]-20);
                            g.drawLine(posX[i/3][i%3+1]-20, posY[i/3][i%3+1]+20, posX[i/3+1][i%3]+20, posY[i/3+1][i%3]-20);
                            break;
                        case 'o':// Draws a large o over that particular board
                            g2.setStroke(new BasicStroke(8));
                            g.setColor(Color.blue);
                            g.drawOval(posX[i/3][i%3]+20, posY[i/3][i%3]+20, 160, 160);
                            break;
                        default: break; // If no one owns the board then nothing happens
                    }
                }
                logo.paintIcon(this, g, 5, 50); // Paints the logo at (5, 50)
                
                // Switch case checking if x or o wins the game
                switch(win) {
                    case 'x':// Draws massive x over entier board
                        g2.setStroke(new BasicStroke(40));
                        g.setColor(Color.red);
                        g.drawLine(40, 240, 660, 860);
                        g.drawLine(40, 860, 660, 240);
                        break;
                    case 'o':// Draws massive o over entier board
                        g2.setStroke(new BasicStroke(40));
                        g.setColor(Color.blue);
                        g.drawOval(40, 240, 620, 620);
                        break;
                    default: break; // Breaks if no one has won or it is a tie. If the game is a tie, you will not be able to play anyways
                }
                break;
            case 'p': // Creates the blue background by creating a painted rectangle the exact same size as the frame
                g.setColor(back);
                g.fillRect(0, 0, 700, 900);
                break;
            case 't': // Creates a blue background with the game logo
                g.setColor(back);
                g.fillRect(0, 0, 700, 900);
                logo.paintIcon(this, g, 5, 150); // Paints the logo at (5, 150)
                break;
        }       
    }
    
    // Each instance of the mini board class represents one of the nine mini boards
    class Mini extends JPanel {
        private char[] board; // Array of characters that represents who clicked a square, can be 'x', 'o', or '0' for no one
        private char owner; // Represents the owner of the board, 'x', 'o', or '0' for no one
        private int[][] posX, posY; // The positions of every corner on the board
            
        // Overloaded constructor with two parameters: the x and y coordinates of the top left position of mini board
        public Mini(int x, int y) {
            board = new char[9]; // Initialising the board
            owner = '0'; // The board has no owner yet
            posX = new int[4][4];
            posY = new int[4][4];
            
            // The board starts with no one owning any square
            for (int i = 0; i < board.length; i++) {
                board[i] = '0';
            }
            
            // Filling up the position arrays with the X and Y positions of the corners
            for (int i = 0; i < posX.length; i++) {
                for (int j = 0; j < posX[0].length; j++) {
                    posX[i][j] = j * 50 + x + 25;
                    posY[i][j] = i * 50 + y + 25;
                }
            }
        }
        
        // Called in the MouseListener method, checks which square out of the nine was clicked within the mini board
        public void squareClicked(int x, int y) {
            int i = 0; // Variable that loops through the whole board array
            
            // Checking what square was just clicked by looping through the position arrays
            for (int r = 0; r < posX.length-1; r++) {
                for (int c = 0; c < posX[r].length-1; c++) {
                    if (x > posX[r][c] && x < posX[r][c+1] && y > posY[r][c] && y < posY[r+1][c]) { 
                        if (board[i] == '0') { // Ensures the square that was just clicked is empty
                            board[i] = turn; // Sets the owner of a square to whichever player just clicked
                            checkOwner(); // Checking if any player owns the square
                            winCheck(); // Checking if any player has won
                            lastSquare = (bigBoard[i].owner == '0')? i: -1; // Sets the last square to whichever square was clicked, if the corresponding square has an owner, sets to -1
                            turn = (turn == 'x')? 'o': 'x'; // Swapping turns
                        }
                        return;
                    }
                    i++; // Increments the variable that moves through the board array
                }
            }
        }
    
        // After every turn, checks the owner of the mini board by looking for 3 in a row
        public void checkOwner() { 
            // Mirrors the winCheck method, but checks for an owner instead
            char case1; // The base case that ensures all other board required for a win are matching
            
            // Checking diagonals
            case1 = board[0]; // Sets base case to the top left square
            if (board[4] == case1 && board[8] == case1 && case1 != '0') { // Checking top left to bottom right squares
                owner = case1; // Sets the win to whoever owns the 3 squares
                return;
            } else {
                case1 = board[2]; // Sets base case to the top right square
                if (board[4] == case1 && board[6] == case1 && case1 != '0') { // Checking top right to bottom left squares
                    owner = case1; // Sets the win to whoever owns the 3 squares
                    return;
                }
            }
            
            // Checking rows and columns
            for (int i = 0; i < 3; i++) {
                case1 = board[i*3]; // Sets base case to the left board
                if (board[i*3+1] == case1 && board[i*3+2] == case1 && case1 != '0') { // Checking each row
                    owner = case1; // Sets the win to whoever owns the 3 squares'
                    return;
                } else {
                    case1 = board[i]; // Sets base case to the top board
                    if (board[i+3] == case1 && board[i+6] == case1 && case1 != '0') { // Checking each column
                        owner = case1; // Sets the win to whoever owns the 3 squares
                        return;
                    }
                }
            }
        }
        
        // Draws the mini board including the Xs and Os
        public void draw(Graphics g) {
            // Sets the x and y values to the top left corner of the board itself
            int x = posX[0][0];
            int y = posY[0][0];
            
            // Creating the mini board
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(3)); // Setting line thickness to 3 pixels
            g.setColor(Color.black);
            g.drawLine(x+50, y, x+50, y+150);
            g.drawLine(x+100, y, x+100, y+150);
            g.drawLine(x, y+50, x+150, y+50);
            g.drawLine(x, y+100, x+150, y+100);
            
            // Filling each square in the board with either an X or an O
            for (int i = 0; i < board.length; i++) {
                switch (board[i]) {
                    case 'x':
                        g2.setStroke(new BasicStroke(4)); // Setting line thickness to 5 pixels
                        g.setColor(Color.red);
                        g.drawLine(posX[i/3][i%3]+10, posY[i/3][i%3]+10, posX[i/3+1][i%3+1]-10, posY[i/3+1][i%3+1]-10); // Line going from left to right
                        g.drawLine(posX[i/3][i%3+1]-10, posY[i/3][i%3+1]+10, posX[i/3+1][i%3]+10, posY[i/3+1][i%3]-10); // Line going from right to left
                        break;
                    case 'o': 
                        g2.setStroke(new BasicStroke(4)); // Setting line thickness to 5 pixels
                        g.setColor(Color.blue);
                        g.drawOval(posX[i/3][i%3]+6, posY[i/3][i%3]+6, 38, 38); // Creating a red hollow circle
                        break;
                    default: break; // If no player owns the square, it stays empty
                }
            }
        }
    }
}