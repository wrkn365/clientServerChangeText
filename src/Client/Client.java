package Client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author Jon Janet 6701505 IT315 5/30/12
 * Class Client opens a socket and input and output streams to that socket. 
 * It then reads from and writes to the streams according to the server's protocol. 
 * It sends the name of a text file that was entered into the JTextField to the server. 
 * It waits for the server to send back the contents of the file or an error message,
 * which is displayed in the JtextArea. The client can then modify the contents 
 * of the file and then send it back to the server to store.
 * It then closes the streams and the socket.  
 */
public class Client extends JFrame implements ActionListener, Runnable
{

    private JTextArea displayArea;
    private JTextField jTextField1;
    private JButton jButton1;
    private JButton jButton2;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private Socket connect;
    private ObjectOutputStream writer;
    private ObjectInputStream reader;
    private String message;
    private Thread thread1;

    /**
     * Constructor Client creates the GUI, Opens a socket and opens an input and
     * output stream to the socket.
     */
    public Client()
    {
        JFrame jFrame1 = new JFrame("Client");
        jFrame1.setLayout(null);
        jLabel1 = new JLabel();
        jLabel1.setText("Enter file name");
        addElement(jFrame1, jLabel1, 18, 0, 100, 20);
        jLabel2 = new JLabel();
        jLabel2.setText("Information from Server");
        addElement(jFrame1, jLabel2, 15, 65, 180, 100);
        jTextField1 = new JTextField();
        addElement(jFrame1, jTextField1, 15, 25, 237, 25);
        jButton1 = new JButton("Get File");
        addElement(jFrame1, jButton1, 15, 55, 100, 40);
        jButton1.setText("Get File");
        jButton2 = new JButton("Save Changes");
        addElement(jFrame1, jButton2, 125, 55, 125, 40);
        jButton2.setText("Save Changes");

        jButton1.addActionListener(new java.awt.event.ActionListener()
        {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                getFileActionPerformed(evt);

            }
        });
        jButton2.addActionListener(new java.awt.event.ActionListener()
        {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                getChangeActionPerformed(e);
            }
        });
        displayArea = new JTextArea(380, 200);
        displayArea.setBackground(Color.white);
        addElement(jFrame1, displayArea, 15, 125, 360, 200);
        jFrame1.addWindowListener(new jWin());
        pack();

        try
        {
            connect = new Socket("localhost", 10987);
            writer = new ObjectOutputStream(connect.getOutputStream());
            writer.flush();
            reader = new ObjectInputStream(connect.getInputStream());
        } catch (IOException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        thread1 = new Thread(this);
        thread1.setDaemon(true);
        thread1.start();
        jFrame1.setSize(400, 400);
        jFrame1.setLocation(10, 10);
        jFrame1.setVisible(true);
        jFrame1.validate();
    }// end constructor Client

    /**
     * method actionPerformed implements ActionLisnener
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        throw new UnsupportedOperationException();
    }// end method actionPerformed

    /**
     * method getFileActionPerformed sends the input from the text field to the
     * server when the "Get File" button is pressed. It then clears the display area.
     *
     * @param evt
     */
    private void getFileActionPerformed(java.awt.event.ActionEvent evt)
    {
        String inputFile = jTextField1.getText();
        sendMessage(inputFile);
        displayArea.setText("");
    }// end method getFileActionPerformed

    /**
     * method getChangeActionPerformed sends 3 messages to the server when the 
     * "Save File" button is pressed. Save the file, the file name, and the new 
     * text in the display area.
     * 
     * @param e
     */
    public void getChangeActionPerformed(java.awt.event.ActionEvent e)
    {
        String enteredText = displayArea.getText();
        sendMessage("SAVE");
        sendMessage(jTextField1.getText());
        sendMessage(enteredText);
    }
    private void addElement(Container c, Component e, int x, int y, int h, int w)
    {
        e.setBounds(x, y, h, w);
        c.add(e);
    }

    /**
     *
     * method run receives the information from the server, and displays the
     * information in the text area. It then closes the input and output streams
     * and the socket.
     */
    @Override
    public void run()
    {
        try
        {
            do
            {
                try
                {

                    message = (String) reader.readObject();
                    displayArea.append(message + "\n");
                } catch (ClassNotFoundException ex)
                {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            } while (true);

        } catch (UnknownHostException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } finally
        {
            try
            {
                reader.close();
                writer.close();
                connect.close();
            } catch (IOException ex)
            {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }// end method run

    private class jWin extends WindowAdapter
    {

        @Override
        public void windowClosing(WindowEvent we)
        {
            System.exit(0);
        }
    }

    /**
     * method sendMessage sends a message to the server.
     *
     * @param message
     */
    public void sendMessage(String message)
    {
        try
        {
            writer.writeObject(message);
            writer.flush();
        } catch (IOException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }// end method sendMessage    

    /**
     *
     * @param args
     */
    public static void main(String args[])
    {
        Client myClient = new Client();
    }// end method main
}
