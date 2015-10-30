package Server;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

/**
 *
 * @author Jon Janet 
 * Class Server opens a socket and an input and output stream to that socket. 
 * It then listens for a client to connect and send the name of a text file. 
 * If it exists the server sends the contents of the text file back to the client. 
 * If the file does not exist, then it sends back an appropriate error message instead.
 * If the client presses the "Save File" button, the server gets the file name,
 * the changed content, and then saves the file. 
 * It then closes the streams and the socket.
 */
public class Server extends JFrame implements ActionListener, Runnable
{

    private JTextArea displayArea;
    private JLabel jLabel1;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private int PORT = 10987;
    private ObjectOutputStream svrWriter;
    private ObjectInputStream svrReader;
    private BufferedReader file;
    private String message;
    private String fileName;
    String result;
    Thread thread1;

    /**
     * Constructor Server creates the GUI, Opens a socket that supports 10
     * connections, and opens an input and output stream to the socket.
     */
    public Server()
    {
        JFrame jFrame1 = new JFrame("Server");
        jFrame1.setLayout(null);
        jLabel1 = new JLabel();
        jLabel1.setText("Information sent to Client");
        addElement(jFrame1, jLabel1, 15, 35, 180, 100);
        displayArea = new JTextArea(360, 200);
        displayArea.setBackground(Color.white);
        addElement(jFrame1, displayArea, 15, 100, 360, 200);
        jFrame1.addWindowListener(new jWin());
        pack();

        try
        {
            serverSocket = new ServerSocket(PORT, 10);
            clientSocket = serverSocket.accept();
            svrWriter = new ObjectOutputStream(clientSocket.getOutputStream());
            svrWriter.flush();
            svrReader = new ObjectInputStream(clientSocket.getInputStream());

        } catch (IOException ex)
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

        thread1 = new Thread(this);
        //thread1.setDaemon(true);
        thread1.start();
        jFrame1.setSize(400, 400);
        jFrame1.setLocation(450, 10);
        jFrame1.setVisible(true);
        jFrame1.validate();
    }// end constructor Server

    /**
     * method actionPerformed implements ActionLisnener
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
    }//end method actionPerformed

    private void addElement(Container c, Component e, int x, int y, int h, int w)
    {
        e.setBounds(x, y, h, w);
        c.add(e);
    }//end method addElement

    /**
     *
     * method run receives the file name from the client, sends the content back
     * to the client, and displays the content of that file in the text area. 
     * If the client presses the "Save Changes" button, the server gets the file name,
     * the changed content, and then saves the file.It then closes the input and 
     * output streams and the socket.
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
                    message = (String) svrReader.readObject();
                    if (message.equalsIgnoreCase("SAVE"))
                    {
                        message = (String) svrReader.readObject();
                        fileName = message;
                        message = (String) svrReader.readObject();
                        saveFile(fileName, message);
                        displayArea.setText("The file " + fileName + " has been saved");

                    } else
                    {
                        fileName = message;
                        displayArea.setText("");
                        File inputFile = new File(fileName);
                        if (inputFile.exists())
                        {
                            file = new BufferedReader(new FileReader(fileName));
                            displayArea.append("The content of file " + message + " is: " + "\n \n");

                            while ((message = file.readLine()) != null)
                            {
                                svrWriter.writeObject(message);
                                svrWriter.flush();
                                displayArea.append(message + "\n");
                            }
                            file.close();
                        } 
                        else
                        {
                            result = "The file " + message + " can not be found. \n";
                            svrWriter.writeObject(result);
                            svrWriter.flush();
                            displayArea.append("The file " + message + " can not be found. \n");
                        }
                    }
                } catch (ClassNotFoundException ex)
                {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }

            } while (true);

        } catch (IOException ex)
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally
        {
            try
            {
                svrReader.close();
                svrWriter.close();
                serverSocket.close();
                if (file != null)
                {
                    file.close();
                }
            } catch (IOException ex)
            {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
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
     * method sendMessage sends a message to the client, and displays it in the
     * text area.
     *
     * @param message
     */
    public void sendMessage(String message)
    {
        try
        {
            svrWriter.writeObject(message);
            svrWriter.flush();
            displayArea.append(message + "\n");
        } catch (IOException ex)
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }// end method sendMessage

    /**
     * The method saveFile reads the file name and edited content and writes it
     * back to the file.
     * @param myFile
     * @param newText
     */
    public void saveFile(String myFile, String newText)
    {
        File file2 = new File(myFile);
        try
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(file2));
            BufferedReader input = new BufferedReader(new StringReader(newText));

            out.write(newText);
            out.close();
            input.close();
        } catch (Exception ex)
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }// end method saveFile

    /**
     *
     * @param args
     */
    public static void main(String args[])
    {
        Server myServer = new Server();
    }// end method main
}
