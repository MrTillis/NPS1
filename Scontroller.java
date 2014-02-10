/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package network.project.server;
import java.awt.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 *
 * @author Taylor
 */
public class Scontroller 
{
    JFrame mainframe;
    
    public Scontroller()
    {
        mainframe = new JFrame("Server");
        mainframe.setDefaultCloseOperation(3);
        mainframe.setPreferredSize(new Dimension(500,400));
        mainframe.setMinimumSize(new Dimension(350,200));
        Server server = new Server();
        JPanel serverPanel = server;
        mainframe.getContentPane().add(serverPanel);
        mainframe.pack();
        mainframe.setVisible(true);
    }
    
    
    
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
    
}
