package network.project.server;

import java.awt.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import net.miginfocom.swing.MigLayout;

/**
 * The main class for the Server Controller (SController).
 * 
 * @author Taylor (GUI layout)
 * @author Jeremiah Doody (Main coding, GUI layout)
 */
public class SController 
{
    JFrame mainframe;
    
    JLabel portL = new JLabel("Port Number:");
    JTextField port = new JTextField(5);
    JTextField cmdLine = new JTextField();
    JScrollPane scroll;
    JTextArea display = new JTextArea();//(200,400)
    
    JButton login = new JButton("Create");
    JButton enterKey = new JButton(">>");
    String adminName = "Admin";//userName
    String portNum;
    String cmd = ""; 
    String clientcmd;
    
    //the nitty-gritty:
    int serverInitialized;
    String empty = "";
    boolean firstActivate = false;
    
    
    /**
     * Constructor for SController class.
     * 
     * @param none
     * @return none
     */
    public SController()
    {
        
        System.out.println("Debug Server System Message Board: \n");
        
        mainframe = new JFrame("Server");
        mainframe.setDefaultCloseOperation(3);
        mainframe.setPreferredSize(new Dimension(500,400));
        mainframe.setMinimumSize(new Dimension(393,200));//350,200
        Server server = new Server();//(in, out)
        JPanel serverPanel = server;
        mainframe.getContentPane().add(serverPanel);
        mainframe.pack();
        mainframe.setVisible(true);
    }//end SController constructor method
    
//////////////////////////////////////////////////////////////////////////////
    
    /**
     * Sets the icon for the project.
     * 
     * @param iconLocation the location of the icon image
     * @return none
     */
    public void setIcon(String iconLocation)
    {
        
        try
        {
            mainframe.setIconImage(ImageIO.read(new File(iconLocation)));
        }//end try
        
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }//end catch
        
    }//end setIcon


//////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
    

    
    /**
     * This class defines the GUI of the Server 
     * 
     * @author Jeremiah
     * @author Taylor
     */
    private class Server extends JPanel
    {

        JLabel portL = new JLabel("Port Number:");
        JTextField port = new JTextField(5);
        JTextField cmdLine = new JTextField();
        JScrollPane scroll;
        JTextArea display = new JTextArea();//(200,400)

        JButton login = new JButton("Create");
        JButton enterKey = new JButton(">>");
        String adminName = "Admin";//userName
        String portNum;
        String cmd = ""; 
        String clientcmd;

        //the nitty-gritty:
        int serverInitialized;
        String empty = "";
    //    boolean firstActivate = false;

        ServerSocket socketProvider;
        Socket connection;

    //    BufferedReader in;
    //    PrintWriter out;
    
    
//////////////////////////////////////////////////////////////////////////////
    
    /**
     * The constructor for the Server subclass.
     */
    public Server() //(ObjectInputStream in, ObjectOutputStream out)
    {


        this.scroll = new JScrollPane(display);
        setLayout(new MigLayout(
                "",
                "[]15[]15[grow]15[grow]",
                "[][][]"));
        guiFunc();
        login.addActionListener(new SController.Server.StartServerListener());
        display.setEditable(false);
        enterKey.addActionListener(new SController.Server.EnterListener());
        enterKey.addKeyListener(new SController.Server.EnterListener());
        cmdLine.addKeyListener(new SController.Server.EnterListener());
        port.addKeyListener(new SController.Server.StartServerListener());
        login.addKeyListener(new SController.Server.StartServerListener());
        port.setToolTipText("Type in the server port #");
        cmdLine.setToolTipText("Type your Command Arguments");
        enterKey.setToolTipText("Enter Command");
        
        
        connection = null;
        serverInitialized = 0;
        display.append("Server> Please input a port number to create an inactive server.");

        //portL.addComponentListener(new ConnectionListener());

    }//end Client Contructor

//////////////////////////////////////////////////////////////////////////////
    /**
     * The method that adds in the GUI parts.
     */
    private void guiFunc() {

        add(portL, "span 1, growx");
        add(port, "span 1, w 80!, growx");
        add(login, "span 1, w 100!, wrap");
        add(scroll, "span 4, growx, growy, push, wrap");
        add(cmdLine, "span 3, growx, growy");
        add(enterKey, "span 1, w 100!, wrap");

    }//end guiFunc() method

//////////////////////////////////////////////////////////////////////////////
    /**
     * This method appends a message to the main display of the Server.
     *
     * <p><b>KEY:</b></p>
     * <p>type "server" to add Server's built in tag to message.</p>
     * <p>type "client" to add Client's built in tag to message.</p>
     * <p>type "admin" to add admin's current tag to message.</p>
     *
     * @param tag name of the tag to appear on the scroll pane.
     * @param message the message to appear in the pane.
     */
    private void append(String tag, String message) {

        String sTag = "\nServer> ";
        String aTag = "\n" + adminName + "> ";
        String cTag = "\nClient> ";
        String tTag = "\n\t";

        if (tag.equalsIgnoreCase("server")) 
        {
            display.append(sTag + message);
        }
        else if (tag.equalsIgnoreCase("admin")) 
        {
            display.append(aTag + message);
        } 
        else if (tag.equalsIgnoreCase("client")) 
        {
            display.append(cTag + message);
        }
        else if (tag.equalsIgnoreCase("tab"))
        { 
            display.append(tTag + message);
        }
    }//end append() method
    
//////////////////////////////////////////////////////////////////////////////
    
    /**
     * Return the current time of day.
     * 
     * @return String of the time of the day.
     */
    public String getTime()
    {
        
        //initialize resulting date string.
        String result = "";

        //create simple date format of specified parts.
        SimpleDateFormat month = new SimpleDateFormat("MM");
        SimpleDateFormat day = new SimpleDateFormat("dd");
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat hour = new SimpleDateFormat("HH");
        SimpleDateFormat min = new SimpleDateFormat("mm");
        SimpleDateFormat sec = new SimpleDateFormat("ss");
        SimpleDateFormat ampm = new SimpleDateFormat("aa");

        // Get current date
        Date today = Calendar.getInstance().getTime();   
        
        // Use SimpleDateFormat format to make a string...
        String monthString = month.format(today);
        String dayString = day.format(today);
        String yearString = year.format(today);
        String hourString = hour.format(today);
        String minString = min.format(today);
        String secString = sec.format(today);
        String ampmString = ampm.format(today);
        
        //Parse hour string.
        int hourInt = Integer.parseInt(hourString);
        
        //if the hour time goes into army time...
        if(hourInt > 12)
            hourInt = hourInt-12;
        
        //set resulting String
        result = monthString + "/" + dayString + "/" + yearString + ";  " 
                + hourInt + ":" + minString + ":" + secString + " " 
                + ampmString;
        
        //return the resulting date/time String
        return result;
        
    }//end getTime()
    
//////////////////////////////////////////////////////////////////////////////
    
    /**
     * 
     * 
     */
    public void getUptime()
    {
        
        
    }//end getUptime() method
    
//////////////////////////////////////////////////////////////////////////////
    
    /**
     * 
     */
    public void getMemory()
    {
    
    
    }//end getMemory() method
    
//////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns and prints Netstat.
     */
    public String[] getNetstat()
    {
        
        System.out.println("getNetstat() method accessed.");
        
        //create String array of executed code "netstat"
        String[] messageArr = execute("netstat");
        
        //return the array. Note the info will have been printed on screen.
        return messageArr;
        
    }//end getNetstat()
    
//////////////////////////////////////////////////////////////////////////////
    
    /**
     * 
     */
    public String[] getRunningProcesses()
    {
        
        System.out.println("getRunningProcesses() method accessed.");
        
        //create String array of executed code "netstat"
        String[] messageArr = execute("tasklist");
        
        //return the array. Note the info will have been printed on screen.
        return messageArr;
        
    }//end getRunningProcesses() method
    
/////////////////////////////////////////////////////////////////////////////
    
    /**
     * 
     */
    public void getCurrentUsers()
    {
    
    
    }//end getCurrentUsers()
    
//////////////////////////////////////////////////////////////////////////////
    
    private String[] execute(String cmd)
    {
        
        //set up String array (Highest number is 2147483647.)
        int arraySize = 20000;
        String[] messageArr =new String[arraySize];
        
        //create cmd message to print netstat data
        String message;
        
        //create buffered reader for input stream
        BufferedReader inStream = null;
        
        try 
        {
            
            //initialize process netstat
            Process process = Runtime.getRuntime().exec(cmd);
            InputStream is = process.getInputStream();
            
            //initialzie in-coming stream's buffered reader
            inStream = new BufferedReader(new InputStreamReader(is));
            
            //set temp array index number
            int messageIndex = 0;
            
            //loop through and read each line of the netstat
            while (!((message = inStream.readLine()) == null))
            {
                
                //Debug display netstat
                System.out.println("\t" + message);
                //append netstat to display one line at a time
                append("tab", message);
                
                //If there's room left in the array:
                if(messageIndex <= arraySize)
                {
                    //add message to message array
                    messageArr[messageIndex] = message;
                    //increment index number of array by 1.
                    messageIndex++;
                }//end if
                else
                {
                    append("server", "There was too much info to process. Only "
                            + arraySize + " lines of" + cmd + " were printed.");
                    break;
                }//end else
                
            }//end while loop
            
        }//end try
        catch (Exception ex) 
        {
            Logger.getLogger(SController.class.getName()).log(Level.SEVERE, null, ex);
        }//end catch
        finally 
        {
            if (inStream != null) 
            {
                try 
                {
                    //close in-coming stream
                    inStream.close();
                }
                catch (IOException ex) 
                {
                    Logger.getLogger(SController.class.getName()).log(Level.SEVERE, null, ex);
                }//end catch
                
            }//end if
            
        }//end finally
        
        //return the array. Note the info will have been printed on screen.
        return messageArr;
        
    }//end execute()
    
//////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
        /**
         * This class listens to the ">>" button.
         */
        private class EnterListener implements ActionListener, KeyListener 
        {
            
//            BufferedReader in;
//            PrintWriter out;
            
            @Override
            public void actionPerformed(ActionEvent event) {

                enterAction();

            }//end actionPerformed

//////////////////////////////////////////////////////////////////////////////
            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {

                    enterAction();

                }//end if
            }//end keyPressed() method

            @Override
            public void keyReleased(KeyEvent ke) {
                //Unused but required field.
            }//end keyReleased() method

            @Override
            public void keyTyped(KeyEvent ke) {
                //Unused but required field.
            }//end keyTyped() method

//////////////////////////////////////////////////////////////////////////////
            public void enterAction() {

                cmd = cmdLine.getText();
                display.setLineWrap(false);
                display.setWrapStyleWord(false);
                //display.append(cmd + " " + adminName + " " + portNum + "\n");

                boolean yesno = false;
                //int temp1 = 0;

                if (cmd.equalsIgnoreCase("--current time")
                        || cmd.equalsIgnoreCase("--time")
                        || cmd.equalsIgnoreCase("-t")) 
                {

//                    append("server", "Current time is " + format1.format(calen.getTime()) + ".");
                    append("server", "Current date/time is:  " + getTime() + ".");
                    System.out.println("Server> Date/Time is:  " + getTime() + ".");

                }//end if
                else if (cmd.equalsIgnoreCase("--port")
                        || cmd.equalsIgnoreCase("--portnumber")
                        || cmd.equalsIgnoreCase("--port number")
                        || cmd.equalsIgnoreCase("-pn")) {
                    if (portNum == null) {

                        System.out.println("Server>Port number is currently null.");
                        append("server", "No port number has been selected.");

                    }//end if
                    else {

                        System.out.println("Server> Port number is: " + portNum);
                        append("server", "The most recently used port number is: "
                                + portNum);

                    }//end else clause

                }//end else if
                else if(cmd.equalsIgnoreCase("--netstat")
                        || cmd.equalsIgnoreCase("-n"))
                {
                    
                    getNetstat();
                    
                }//end else if
                else if(cmd.equalsIgnoreCase("--processes")
                        || cmd.equalsIgnoreCase("-p"))
                {
                    
                    getRunningProcesses();
                    
                }//end else if
                else if (cmd.equalsIgnoreCase("--activate")) 
                {

                    if (serverInitialized == 1) {


                        //open the server to the public

                        //make runnable object
                        Runnable run = new Runnable() 
                        {
                            public void run() 
                            {
                                
                                
                                while (true) 
                                {
                                    try 
                                    {
                                        append("server", "Conection activated. "
                                            + "Waiting on client(s)...");

                                        System.out.println("Conection activated. Opening server "
                                            + "connection to public... Waiting on client...");
                                        
                                        connection = socketProvider.accept();

                                        final ServerThread se = new ServerThread(connection);
                                        se.start();
                                        
                                    } //end while loop
                                    catch (IOException ex) 
                                    {
                                        Logger.getLogger(SController.class.getName()).log(Level.SEVERE, null, ex);
                                    }//end catch
                                }//end while loop


                            }//end run() method
                        };
                        new Thread(run).start();

                    }//end if

                }//end else if


                //if the admin enters a non-command (a message)
                if (!cmd.startsWith("-")) {
                    //messageOut("admin", cmd);
                    append("admin", cmdLine.getText());
                }//end if

                cmdLine.setText("");

            }//end enterAction() method
            
//////////////////////////////////////////////////////////////////////////////
//        /**
//         * The main "run" method of Server.java.
//         */
//        public void openServer()
//        {
//            //boolean exit = false;
//            while(true)
//            {
//            
//                
//            }//end while loop
//            
//        }//end openServer() method
//        
//    }//end subclass
            
            //////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
            /**
             *
             */
            private class ServerThread extends Thread 
            {

                Socket socket;
                
                BufferedReader in;
                PrintWriter out;

                public ServerThread(Socket socket) 
                {

                    this.socket = socket;

                }//end ServerThread constructor method.

//////////////////////////////////////////////////////////////////////////////
                
                /**
                 * The run method
                 */
                public void run() 
                {
                    
                    //If a time stamp should go anywhere for the server, it would be here.
                    
                    int portNumber = 0;

                    try 
                    {
                        portNumber = Integer.parseInt(portNum);


                        System.out.println("Connection aquired from:\n "
                                + "Host Name: " + connection.getInetAddress().getHostName() + "\n"
                                + "Host Address:" + connection.getInetAddress().getHostAddress());

                        //create output stream
                        in= new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        out=new PrintWriter(connection.getOutputStream());
                        out.flush();
                        
                        //send success message to Client
                        messageOut("server", "Hello client. Connection is successful.");
                        
//                        Runnable adminMessage = new Runnable() 
//                        {
//                            @Override
//                            public void run() 
//                            {
//                                
//                                cmd = "";
//                                
//                                int i = 0;
//                                
//                                while(true)
//                                {
//                                    
//                                    //If the user has entered something worth sending
//                                    if(!cmd.equals(empty.trim()))
//                                        i=1;
//                                    
//                                    while(i==1)
//                                    {
//                                        //send out if not a command
//                                        if (!((cmd.toString()).startsWith("-"))) 
//                                        {
//                                            messageOut("admin", cmd.toString());
//                                            System.out.println("Admin message \"cmd\": " + cmd.toString());
//                                            i=0;
//                                        }//end if
//                                        else if (((cmd.toString()).startsWith("-"))) 
//                                        {
//                                            System.out.println("Admin message \"cmd\": " + cmd.toString());
//                                            i=0;
//                                        }//end if
//                                    }//end while loop
//                                    
//                                     cmd = "";
//                                    
//                                }//end while loop
//
//                            }//end run() method
//                        };
//                        new Thread(adminMessage).start();
                        
                        
                        
                        
                        
                        do 
                        {
                            try 
                            {
                                //Note: to send messages from admin to client,
                                //could put a runnable with a run() here to work in background.
                                
                                //read in client command
                                clientcmd = in.readLine();

                                System.out.println("clientcmd: " + clientcmd);

                                append("client", clientcmd);
                                if (clientcmd.equalsIgnoreCase("--connection close")) 
                                {
                                    messageOut("server", "Goodbye.");


                                }//end if
                                
                                else if (clientcmd.equalsIgnoreCase("--time") 
                                        || clientcmd.equalsIgnoreCase("-t")) 
                                {
                                    
                                    messageOut("server", "Current date and time"
                                            + " is:  " + getTime() + ".");

                                }//end if
                                else if(clientcmd.equalsIgnoreCase("--netstat")
                                        ||clientcmd.equalsIgnoreCase("-n"))
                                {
                                    
                                    append("server", "Client accessed netstat.");
                                    
                                    messageOut("server", "Sending Netstat data "
                                            + "to Client. Please wait...");
                                    
                                    String[] netArr = getNetstat();
                                    
                                    System.out.println("Test Netstat Array:");
                                    
                                    int index = 0;
                                    
                                    while(netArr[index] != null)
                                    {
                                        //send out netstat line
                                        messageOut("tab", netArr[index]);
                                        
                                        System.out.println(netArr[index]);
                                        
                                        //increment index #
                                        index++;
                                    
                                    }//end while
                                
                                }//end else if
                                else if(clientcmd.equalsIgnoreCase("--processes")
                                        ||clientcmd.equalsIgnoreCase("-p"))
                                {
                                    
                                    append("server", "Client accessed running processes.");
                                    
                                    messageOut("server", "Sending Running Process data "
                                            + "to Client. Please wait...");
                                    
                                    String[] netArr = getRunningProcesses();
                                    
                                    System.out.println("Test Processes Array:");
                                    
                                    int index = 0;
                                    
                                    while(netArr[index] != null)
                                    {
                                        //send out netstat line
                                        messageOut("tab", netArr[index]);
                                        
                                        System.out.println(netArr[index]);
                                        
                                        //increment index #
                                        index++;
                                    
                                    }//end while
                                
                                }//end else if

                            }//end try
                            
                            catch (SocketException ex) 
                            {
                                clientcmd = this.getName();
                                System.out.println("Seems the Client " + clientcmd + " disconnected.");
                                append("server", "Connection to client lost.");

                                //The client was lost, so this works to end the loop
                                clientcmd = "--connection close";

                            }//end catch

                            
                            
                        }//end do
                        while (!clientcmd.equalsIgnoreCase("--connection close"));


                    }//end try
                    catch (BindException e) 
                    {
                        append("server", "It seems the port you issued is already in use. Try another one.");
                        append("server", "Some commonly unused port numbers include: 204-208, 176, 178, 410-420.");

                    }//end catch
                    catch (IOException ex) 
                    {
                        clientcmd = this.getName();
                        System.out.println("I/O in initializeServer(). Client " + clientcmd + " terminated unexpectedly.");

                        Logger.getLogger(SController.Server.class.getName()).log(Level.SEVERE, null, ex);

                    }//end catch
                    catch(NullPointerException e)
                    {
                        clientcmd =this.getName(); //reused String line for getting thread name
                        System.out.println("Client "+ clientcmd +" quit.");
                    }
                    finally 
                    {

                        try 
                        {

                            in.close();
                            out.close();
                            connection.close();

                        }//end catch
                        catch (IOException ex) 
                        {
                            System.out.println("I/O Exception error in finally "
                                    + "clause.");
                            Logger.getLogger(SController.Server.class.getName())
                                    .log(Level.SEVERE, null, ex);
                        }//end catch

                    }//end fianlly

                }//end run() method
                
                //////////////////////////////////////////////////////////////////////////////
            /**
             * Send a message out to all clients.
             *
             * <p><b>KEY:</b></p>
             * <p>type "server" to add Server's built in tag to message.</p>
             * <p>type "admin" to add admin's current tag to message.</p>
             * <p>type "tab" to add a tab to outward message.</p>
             */
            public void messageOut(String tag, String message) 
            {

                String sTag = "\nServer> ";
                String aTag = "\n" + adminName + "> ";
                String tTag = "\n\t";
                
                
                if (tag.equalsIgnoreCase("admin")) 
                {
                    //append to display
                    System.out.println(aTag + message);
                    append("admin", message);

                    System.out.print("In messageOut(): ");
                    if (out == null) {
                        System.out.println("out is null.\n");
                    }//end if clause
                    else if (out != null) {
                        System.out.println("out is not null.\n");
                    }//end else if clause
                    
                    //if out stream isn't null:
                    if (out != null) 
                    {
                        //write out a message to client.
                        out.println(message);
                        //flush output stream
                        out.flush();
                        try 
                        {
                            Thread.sleep(10);
                        }//end catch
                        catch (InterruptedException ex) 
                        {
                            Logger.getLogger(SController.Server.class.getName()).log(Level.SEVERE, null, ex);
                        }//end catch


                    }//end if

                }//end if
                else if (tag.equalsIgnoreCase("server")) 
                {

                    //append to display
                    System.out.println(sTag + message);
                    append("server", message);
                    //write out a message to client.
                    out.println(message);
                    out.flush();

                }//end else if
                
                else if(tag.equalsIgnoreCase("tab"))
                {
                    //append to display
                    System.out.println(tTag + message);
                    append("tab", message);
                    //write out a message to client.
                    out.println(message);
                    out.flush();
                }//end else if
                
                out.flush();



            }//end messageOut() method

        }//end ServerThread inner subclass

    }//end EnterListener subclass

        //////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
        /**
         * The action listener for starting the server.
         *
         * (Was originally called LoginListener.)
         *
         */
        private class StartServerListener implements ActionListener, KeyListener {

            public StartServerListener() 
            {
            }//end StartServerListener() constructor method

            @Override
            public void actionPerformed(ActionEvent event) 
            {


                startAction();


            }//end actionPerformed

//////////////////////////////////////////////////////////////////////////////
            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {

                    startAction();

                }//end if
            }//end keyPressed() method

            @Override
            public void keyReleased(KeyEvent ke) {
                //Unused but required field.
            }//end keyReleased() method

            @Override
            public void keyTyped(KeyEvent ke) {
                //Unused but required field.
            }//end keyTyped() method

//////////////////////////////////////////////////////////////////////////////
            /**
             * The main action called by the Overrided methods.
             */
            public void startAction() {

                if (serverInitialized == 0) {

                    //get port number
                    portNum = port.getText().trim();

                    if (portNum.matches(empty.trim())) {
                        append("server", "Server port number not set. Set a port "
                                + "number to start the server.");
                    } else {

                        //append("server", "Waiting for a client to connect...");
                        //initializeServer();

                        if (initializeServer() == true) {
                            System.out.println("Server created successfully.");
                            serverInitialized++;
                        }//end if
                        else {
                            //do nothing
                        }//end else clause



                    }//end else


                }//end if clause
                else if (serverInitialized == 1) {
                    append("server", "The server has already started. Restart program to start new session.");
                    System.err.println(adminName + " clicked to start new server "
                            + "session. However, this isn't allowed.");
                }//end else clause

            }//end startAction() method

//////////////////////////////////////////////////////////////////////////////
            /**
             *
             * @return
             */
            private boolean initializeServer() {

                boolean serverCreated = false;

//            boolean serverStarted = false;

                int portNumber = 0;

                try {
                    //get port number integer
                    portNumber = Integer.parseInt(portNum);
                    System.out.println("portNumber = " + portNumber);

                    portNum = portNumber + "";

                    //make server socket
                    socketProvider = new ServerSocket(portNumber);//(portNumber, 50)

//                    firstActivate = true;

                    //System.out.println("Waiting for connection...");

                    System.out.println("An inactive server was created by admin.");
                    append("server", "Inactive server created.");
                    append("server", "To active it, type in \"--activate\" (no quotes) to the command bar and press the \">>\" button.");

                    serverCreated = true;

                } catch (NumberFormatException e) {
                    append("server", "Notice: Only type in numbers for a port number.");
                    System.out.println("User entered letters for port number.");
                } catch (BindException e) {
                    append("server", "It seems the port you issued is already in use. Try another one.");
                    append("server", "Some commonly unused port numbers include: 204-208, 176, 178, 410-420.");
                }//end catch
                catch (IOException ex) {
                    System.out.println("I/O in initializeServer().");
                    Logger.getLogger(SController.Server.class.getName()).log(Level.SEVERE, null, ex);

                }//end catch

                return serverCreated;

            }//end startServer() method
        }//end StartServerListener subclass
    }//end Server class
    
    class UpdateText extends SwingWorker<String, String> 
    {
        @Override
        public String doInBackground() 
        {
//            for (int i = 0; i < 1000; i++) {
//                publish("Hello-" + i);
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
            return null;
        }

        @Override
        public void process(java.util.List<String> chunks) {
//            for (String s : chunks) {
//                if (display.getDocument().getLength() > 0) {
//                    display.append("\n");
//                }
//                display.append(s);
//            }
            try {
                display.setCaretPosition(display.getLineStartOffset(display.getLineCount() - 1));
            } 
            catch (BadLocationException e) 
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void done() {

        }
    }
    
}//end SConstructor
