
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;


public class Server extends javax.swing.JFrame {

    /**
     * Creates new form Server
     */
    static String username;
    static DataInputStream dis;
    static DataOutputStream dos;

    public Server() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Start");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Stop");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(303, 303, 303)
                .addComponent(jButton1)
                .addGap(107, 107, 107)
                .addComponent(jButton2)
                .addContainerGap(480, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jTextArea1.append("Server started...\n");
        jButton1.setEnabled(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        for (ClientHandler ch : Server.activeClients) {
            if (ch.isLoggedIn == true) {
                try {
                    ch.dos.writeUTF("exit");
                } catch (IOException ex) {
                }
            }
        }
        jTextArea1.append("Shutting down Server...\n");
        JOptionPane.showMessageDialog(null, "Server shutting down...\n");
        System.exit(0);
    }//GEN-LAST:event_jButton2ActionPerformed

    // Vector to store active clients 
    static Vector<ClientHandler> activeClients = new Vector<>();

    // counter for clients 
    static int i = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception {
       
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server().setVisible(true);

            }
        });

        try {
            // server is listening on port 9000
            ServerSocket serverSocket = new ServerSocket(9000);

            Socket socket;

            // running infinite loop for getting 
            // client request 
            while (true) {
                // Accept the incoming request 
                socket = serverSocket.accept();

                // obtain input and output streams 
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());

                username = dis.readUTF();

                jTextArea1.append("New client request received : " + username + "\n");

                // Create a new handler object for handling this request. 
                ClientHandler mtch = new ClientHandler(socket, username, dis, dos);

                // Create a new Thread with this object. 
                Thread t = new Thread(mtch);

                jTextArea1.append("Adding this client to active client list\n");

                // add this client to active clients list 
                activeClients.add(mtch);
                // start the thread. 
                t.start();

                // increment i for new client. 
                // i is used for naming only, and can be replaced 
                // by any naming scheme 
                i++;

            }
        } catch (Exception e) {
            System.err.println("");
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    static javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables

}

// ClientHandler class 
class ClientHandler implements Runnable {

    Scanner scn = new Scanner(System.in);
    public String clientUsername;
    final DataInputStream dis;
    final DataOutputStream dos;
    public Socket socket;
    public boolean isLoggedIn;
    public String received;
    public String httpMsgOk;
    public String httpMsgNotok;
    public String messageToSend;
    public String recipient;
    public StringTokenizer st;

    // constructor 
    public ClientHandler(Socket socket, String clientUsername, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.clientUsername = clientUsername;
        this.socket = socket;
        this.isLoggedIn = true;
    }

    @Override
    public void run() {

        while (this.isLoggedIn) {
            try {
                int localport = socket.getLocalPort();
                String msgOk = "POST HTTP/1.0 200 OK\n";
                String msgNotok = "POST HTTP/1.0 404 NOT FOUND\n";
                String userAgent = "\nUser-Agent : Mozilla/5.0 "
                        + "(Windows 10.0; Win64; x64; rv:47:0) "
                        + "AppleWebKit/537.36 (KHTML, like Gecko) "
                        + "Chrome/51.0.2704.103 Safari/537.36\n";
                String contentType = "\nContent-Type: text/html; charset=utf-8";
                String contentLength = "\nContent-Length: ";

                // receive the string 
                received = dis.readUTF();

                if (received.toLowerCase().equals("exit")) {
                    this.isLoggedIn = false;
                    for (ClientHandler mc : Server.activeClients) {
                        for (int i = 0; i <= Server.activeClients.size() - 1; i++) {
                            mc.dos.writeUTF(this.clientUsername + " disconnected from the Server");
                            break;
                        }
                    }
                    this.socket.close();
                }

                // break the string into message and recipient part 
                st = new StringTokenizer(received, "#");
                while (st.hasMoreTokens()) {
                    messageToSend = st.nextToken();
                    recipient = st.nextToken();
                    break;
                }

                int strLength = messageToSend.length();
                String timeStamp = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(Calendar.getInstance().getTime());
                httpMsgOk = msgOk + "Host: localhost:" + localport + userAgent + "Message from Client is: " + messageToSend
                        + contentType + contentLength + strLength + "\nDate: " + timeStamp;

                httpMsgNotok = msgNotok + "Host: localhost:" + localport + userAgent + "Message from Client is: " + messageToSend
                        + contentType + contentLength + strLength + "\nDate: " + timeStamp;

                for (ClientHandler ch : Server.activeClients) {
                    if (ch.clientUsername.equals(recipient) && ch.isLoggedIn == true) {
                        Server.jTextArea1.append(httpMsgOk + "\n");
                    }

                    if (ch.clientUsername.equals(recipient) && ch.isLoggedIn != true) {
                        Server.jTextArea1.append(httpMsgNotok + "\n");
                    }
                }

                if (recipient.equals("allclients") && !Server.activeClients.isEmpty()) {
                    Server.jTextArea1.append(httpMsgOk + "\n");
                }

                // search for the recipient in the connected devices list. 
                // ar is the vector storing client of active users 
                for (ClientHandler mc : Server.activeClients) {
                    // if the recipient is found, write on its 
                    // output stream 
                    if (mc.clientUsername.equals(recipient) && mc.isLoggedIn == true) {
                        mc.dos.writeUTF(this.clientUsername + " : " + messageToSend);
                        break;
                    }
                    if (recipient.equals("allclients")) {
                        for (int i = 0; i <= Server.activeClients.size(); i++) {
                            if (isLoggedIn == true) {
                                mc.dos.writeUTF(this.clientUsername + " : " + messageToSend);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("");
            }
        }
        try {
            // closing resources 
            this.dis.close();
            this.dos.close();

        } catch (Exception e) {
            System.out.println("");
        }
    }
}
