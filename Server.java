/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package network.project.server;


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
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Taylor & Jeremiah
 */
public class Server extends JPanel
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
    boolean firstActivate = false;
    
    ServerSocket socketProvider;
    Socket connection;
    
    ObjectInputStream in;
    ObjectOutputStream out;
    
    
//////////////////////////////////////////////////////////////////////////////
    
    /**
     * The constructor for the Server class.
     */
    public Server(ObjectInputStream in, ObjectOutputStream out)
    {
        
        this.in = in;
        this.out = out;
        
        
        this.scroll = new JScrollPane(display);
        setLayout(new MigLayout(
                "",
                "[]15[]15[grow]15[grow]",
                "[][][]"));
        guiFunc();
        login.addActionListener(new StartServerListener());
        display.setEditable(false);
        enterKey.addActionListener(new EnterListener());
        enterKey.addKeyListener(new EnterListener());
        cmdLine.addKeyListener(new EnterListener());
        port.addKeyListener(new StartServerListener());
        login.addKeyListener(new StartServerListener());
        port.setToolTipText("Type in the server port #");
        cmdLine.setToolTipText("Type your Command Arguments");
        enterKey.setToolTipText("Enter Key");
//        scroll.addMouseWheelListener(new MouseWhListener());
        
        connection = null;
        serverInitialized = 0;
        display.append("Server> Please input a port number to create an inactive server.");
        
        //portL.addComponentListener(new ConnectionListener());
        
    }//end Client Contructor
    
//////////////////////////////////////////////////////////////////////////////
    
    /**
     * The method that adds in the GUI parts.
     */
    private void guiFunc()
    {
        
        add(portL,"span 1, growx");
        add(port,"span 1, w 80!, growx");
        add(login,"span 1, w 80!, wrap");
        add(scroll,"span 4, growx, growy, push, wrap");
        add(cmdLine, "span 3, growx, growy");
        add(enterKey, "span 1, w 80!, wrap");
        
    }//end guiFunc() method

//////////////////////////////////////////////////////////////////////////////
    
    /**
     * This method appends a message to the main display of the 
     * Server.
     * 
     * <p><b>KEY:</b></p>
     * <p>type "server" to add Server's built in tag to message.</p>
     * <p>type "client" to add Client's built in tag to message.</p>
     * <p>type "admin" to add admin's current tag to message.</p>
     * 
     * @param tag name of the tag to appear on the scroll pane.
     * @param message the message to appear in the pane.
     */
    private void append(String tag, String message)
    {

        String sTag = "\nServer> ";
        String aTag = "\n" + adminName + "> ";
        String cTag = "\nClient> ";

        if(tag.equalsIgnoreCase("server"))
            display.append(sTag + message);
        else if(tag.equalsIgnoreCase("admin"))
            display.append(aTag + message);
        else if(tag.equalsIgnoreCase("client"))
            display.append(cTag + message);
    }//end append() method
    
    
//////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
    
    /**
     * This class listens to the ">>" button.
     */
    private class EnterListener implements ActionListener, KeyListener 
    {
        @Override
        public void actionPerformed(ActionEvent event)
        {
            
            enterAction();
            
        }//end actionPerformed
        
//////////////////////////////////////////////////////////////////////////////
        
        @Override
        public void keyPressed(KeyEvent ke) 
        {
            if (ke.getKeyCode() == KeyEvent.VK_ENTER)
            {
                
                enterAction();
                
            }//end if
        }//end keyPressed() method
        
        @Override
        public void keyReleased(KeyEvent ke) 
        {
            
            //Unused but required field.

        }//end keyReleased() method

        @Override
        public void keyTyped(KeyEvent ke) 
        {
            
            //Unused but required field.
            
        }//end keyTyped() method
        
//////////////////////////////////////////////////////////////////////////////
        
        public void enterAction()
        {
            
            cmd = cmdLine.getText();
            display.setLineWrap(false);
            display.setWrapStyleWord(false);
            //display.append(cmd + " " + adminName + " " + portNum + "\n");
            
            boolean yesno = false;
            //int temp1 = 0;
            
            if(cmd.equalsIgnoreCase("--current time") 
                    || cmd.equalsIgnoreCase("--time") 
                    || cmd.equalsIgnoreCase("-t"))
            {
                
                Calendar calen = Calendar.getInstance();
                calen.getTime();
                //Format by Day, Hour, min, sec
                SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
                
                append("server","Current time is " + format1.format(calen.getTime()) + ".");
                System.out.println("Server> "+ format1.format(calen.getTime()) + ".");
                
            }//end if
            else if(cmd.equalsIgnoreCase("--activate"))
            {
                
                if(serverInitialized==1)
                {
                    
                    //append this statement to server display.
                    append("server", "Conection activated. "
                            + "Waiting on client(s)...");
                    
                    System.out.println("Conection activated. Opening server "
                            + "connection to public... Waiting on client...");
                    //open the server to the public

                    //make runnable object
                    Runnable run = new Runnable() 
                    {
                         public void run() 
                         {
                             openServer();
                         }//end run() method
                    };
                    //new Thread(r).start();
                    ExecutorService executor = Executors.newCachedThreadPool();
                    executor.submit(run);
                    
                }//end if
                
            }//end else if
            
            
            //if the admin enters a non-command (a message)
            if(!cmd.startsWith("-"))
            {
                messageOut("admin", cmd);
                //append("admin", cmdLine.getText());
            }//end if
            
            cmdLine.setText("");
            
        }//end enterAction() method
        
//////////////////////////////////////////////////////////////////////////////
        
        /**
         * The main "run" method of Server.java.
         */
        public void openServer()
        {
            //boolean exit = false;
            while(true)
            {
            
                int portNumber = 0;

                try
                {
                    portNumber = Integer.parseInt(portNum);
                    
                    if(firstActivate==true)
                        firstActivate = false;
                    else
                        socketProvider = new ServerSocket(portNumber, 50);

                    //waiting for connection
                    connection = socketProvider.accept();
                    
                    System.out.println("Connection aquired from:\n " +
                            "Host Name: "+ connection.getInetAddress().getHostName() + "\n" +
                            "Host Address:" + connection.getInetAddress().getHostAddress() );
                    
                    //create output stream
                    out = new ObjectOutputStream(connection.getOutputStream());
                    //flush stream
                    out.flush();
                    //create input stream.
                    in = new ObjectInputStream(connection.getInputStream());
                    
//                    System.out.println(" (After thread initialized) Connection:"
//                            + " " + connection + "; in: " + in.readUTF() + "; out: " 
//                            + out);
                    
                    System.out.print("In openServer(): ");
                    if(out != null)
                        System.out.print("out is not null and ");
                    else
                        System.out.print("out is null and ");
                    if(in != null)
                        System.out.println("in is not null.");
                    else
                        System.out.println("in is null.");
                    
                    messageOut("server", "Hello client. Connection is successful.");

                    do
                    {
                        try 
                        {
                            //read in client command
                            clientcmd = in.readObject().toString();
                            
                            System.out.println("clientcmd: " + clientcmd);
                            
                            append("client", clientcmd);
                            if(clientcmd.equalsIgnoreCase("--exit"))
                            {
                                messageOut("server", "Goodbye.");
                                
//                                in.close();
//                                out.close();
//                                connection.close();
                                
                            }//end if
                            if(clientcmd.equalsIgnoreCase("--time"))
                            {
                                Calendar calen = Calendar.getInstance();
                                calen.getTime();
                                //Format by Day, Hour, min, sec
                                SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");

                                messageOut("server", "Current time is " + format1.format(calen.getTime()) + ".");
                                display.repaint();

                            }//end if
                            
                        }//end try
                        catch(BindException e)
                        {
                            append("server", "It seems the port you issued is already in use. Try another one.");
                            append("server", "Some commonly unused port numbers include: 204-208, 176, 178, 410-420.");
                            
                        }//end catch
                        catch (ClassNotFoundException ex) 
                        {
                            System.out.println("Unknown format of client input "
                                    + "reported.");
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }//end catch
                        catch(SocketException ex)
                        {
                            
                            System.out.println("Seems the client disconnected.");
                            append("server", "Connection to client lost.");
                            
                            //close old conection
//                            in.close();
//                            out.close();
//                            connection.close();
                        
                        }
//                        catch(IOException ex)
//                        {
//                            
//                            System.out.println();
//                            
//                            in.close();
//                            out.close();
//                            connection.close();
//                            
//                        }
                        
                    }//end do
                    while(!clientcmd.equalsIgnoreCase("--exit"));
                    
                    //if(clientcmd.equalsIgnoreCase("exit"))
                    //    append("server", "Client has left.");


                }//end try

                catch (IOException ex)
                {
                    
                    System.out.println("I/O in initializeServer().");

                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//                    try 
//                    {
//                        in.close();
//                        out.close();
//                        connection.close();
//                    } //end try
//                    catch (IOException ex1) 
//                    {
//                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex1);
//                    }//end catch

                }//end catch
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
                        Logger.getLogger(Server.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }//end catch
                
                }//end fianlly
                
//                if(clientcmd.equalsIgnoreCase("--exit"))
//                {
//                    
////                  break;
//                    
//                }//end if
                
            }//end while loop
            
        }//end openServer() method
        
    }//end subclass

//////////////////////////////////////////////////////////////////////////////
    
    /**
     * Send a message out to all clients.
     * 
     * <p><b>KEY:</b></p>
     * <p>type "server" to add Server's built in tag to message.</p>
     * <p>type "admin" to add admin's current tag to message.</p>
     */
    public void messageOut(String tag, String message)
    {

        String sTag = "\nServer> ";
        String aTag = "\n" + adminName + "> ";

        try 
        {

            if(tag.equalsIgnoreCase("admin"))
            {
                //append to display
                System.out.println(aTag + message);
                append("admin", message);
                
                System.out.print("In messageOut(): ");
                if(out == null)
                    System.out.println("out is null.\n");
                if(out != null)
                    System.out.println("out is not null.\n");
                
                //if out stream isn't null:
                if(out != null)
                {
                    //write out a message to client.
                    out.writeObject(message);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    //flush output stream
                    out.flush();
                }//end if
                
            }//end if
            else if(tag.equalsIgnoreCase("server"))
            {

                //append to display
                System.out.println(sTag + message);
                append("server", message);
                //write out a message to client.
                out.writeObject(message);
            }//end else if

            
        }//end try
        catch (IOException ex) 
        {
            System.out.println("I/O error while sending message out to "
                    + "client.");
            append("server", "I/O error occoured while sending message to "
                    + "client.");
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }//end catch


    }//end messageOut() method
    
//////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
    
    /**
     * The action listener for starting the server. 
     * 
     * (Was originally called LoginListener.)
     * 
     */
    private class StartServerListener implements ActionListener, KeyListener
    {
        
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
        public void keyPressed(KeyEvent ke) 
        {
            if (ke.getKeyCode() == KeyEvent.VK_ENTER)
            {
                
                startAction();
                
            }//end if
        }//end keyPressed() method
        
        @Override
        public void keyReleased(KeyEvent ke) 
        {
            
            //Unused but required field.

        }//end keyReleased() method

        @Override
        public void keyTyped(KeyEvent ke) 
        {
            
            //Unused but required field.
            
        }//end keyTyped() method
        
//////////////////////////////////////////////////////////////////////////////
        
        /**
         * The main action called by the Overrided methods.
         */
        public void startAction()
        {
            
            if(serverInitialized == 0)
            {
                
                //get port number
                portNum = port.getText().trim();
                
                if(portNum.matches(empty.trim()))
                    append("server", "Server port number not set. Set a port "
                            + "number to start the server.");
                else
                {
                    
                    //append("server", "Waiting for a client to connect...");
                    //initializeServer();
                    
                    if(initializeServer() == true)
                    {
                        System.out.println("Server created successfully.");
                        serverInitialized++;
                    }//end if
                    else
                    {
                        //do nothing
                    }//end else clause
                    
                    
                    
                }//end else
                
                
            }//end if clause
            else if(serverInitialized == 1)
            {
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
        private boolean initializeServer()
        {
            
            boolean serverCreated = false;
            
//            boolean serverStarted = false;
            
            int portNumber = 0;
            
            try
            {
                //get port number integer
                portNumber = Integer.parseInt(portNum);
                System.out.println("portNumber = " + portNumber);
                
                portNum = portNumber + "";
                
                //make server socket
                socketProvider = new ServerSocket(portNumber, 50);//10
                
                firstActivate = true;
                
                //System.out.println("Waiting for connection...");

                System.out.println("An inactive server was created by admin.");
                append("server", "Inactive server created."); 
                append("server", "To active it, type in \"--activate\" (no quotes) to the command bar and press the \">>\" button.");

                serverCreated = true;

            }
            
            catch(NumberFormatException e)
            {
                append("server", "Notice: Only type in numbers for a port number.");
                System.out.println("User entered letters for port number.");
            }
            catch(BindException e)
            {
                append("server", "It seems the port you issued is already in use. Try another one.");
                append("server", "Some commonly unused port numbers include: 204-208, 176, 178, 410-420.");
            }//end catch
            
            catch (IOException ex)
            {
                System.out.println("I/O in initializeServer().");
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                
            }//end catch
            
           
            

            return serverCreated;
            
        }//end startServer() method
        
    }//end subclass
    
}//end Server class
