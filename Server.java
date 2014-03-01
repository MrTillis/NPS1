/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package network.project.server;


import java.awt.event.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Taylor
 */
public class Server extends JPanel
{
    JLabel portL = new JLabel("Port Number");
    JTextField port = new JTextField(5);
    JTextField cmdLine = new JTextField();
    JScrollPane scroll;
    JTextArea display = new JTextArea(200,400);
    
    JButton login = new JButton("Start");
    JButton enterKey = new JButton(">>");
    String username;
    String hostname;
    String portNum;
    String cmd;
    
    
    
    
    public Server()
    {
        this.scroll = new JScrollPane(display);
        setLayout(new MigLayout("","[grow]15",""));
        guiFunc();
        login.addActionListener(new LoginListener());
        display.setEditable(false);
        enterKey.addActionListener(new EnterListener());
        port.setToolTipText("Type in the port #");
        cmdLine.setToolTipText("Type your Command Arguments");
        enterKey.setToolTipText("Enter Key");
//        scroll.addMouseWheelListener(new MouseWhListener());
        
    }//end Client Contructor
    
    public void guiFunc()
    {
        add(portL,"span 1, growx, wrap");
        add(port,"span 1, growx");
        add(login,"span 1,wrap");
        add(scroll,"span 4, wrap");
        add(cmdLine, "span 3, growx");
        add(enterKey, "span 1, wrap");
    }


    private class EnterListener implements ActionListener 
    {
        @Override
        public void actionPerformed(ActionEvent event)
        {
            cmd = cmdLine.getText();
            display.setLineWrap(true);
            display.setWrapStyleWord(true);
            display.append("\n" + cmd + " " + username + " " + hostname + " " + portNum);
            
        }//end actionPerformed
        
    }//end subclass

    private class LoginListener implements ActionListener 
    {

        @Override
        public void actionPerformed(ActionEvent event)
        {

            portNum = port.getText();
        }//end actionPerformed
    }//end subclass
    
    
}//end Server
