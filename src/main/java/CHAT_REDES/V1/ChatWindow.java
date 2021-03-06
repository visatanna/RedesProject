package CHAT_REDES.V1;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ChatWindow  implements Runnable   {
	JFrame tela;
	JTextArea textWriteableArea;
	JTextPane textReadableArea;
	JScrollPane WrittableScrollPanel;
	JScrollPane ReadableScrollPanel;
	JButton sendButton;
	Socket connection;
	TCPReceiver tcpReceiver;
	TCPSender   tcpSender;
	String ipAddress;
	Thread threadReceiver;
	Thread threadSender;
	ArrayBlockingQueue<String> Queue;
	StyledDocument doc;
	SimpleAttributeSet specialKeyWord;
	SimpleAttributeSet normalKeyWord;
	JScrollBar verticalScrollReadeable;
	
	
	public ChatWindow(Socket connection){
		tela = new JFrame();
		ipAddress =	connection.getInetAddress().getHostAddress();
		tela.setTitle("Conversa com IP " + ipAddress);
		tela.getContentPane().setLayout(null);
		tela.setSize(400, 300 );
		textWriteableArea = new JTextArea(5, 40);
		textReadableArea = new JTextPane();
		WrittableScrollPanel = new JScrollPane(textWriteableArea);
		ReadableScrollPanel = new JScrollPane(textReadableArea);
		ReadableScrollPanel.setBounds( 30 , 11 , 320 , 150);
		WrittableScrollPanel.setBounds( 30 , 180 , 260 , 70  );
		WrittableScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		ReadableScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		WrittableScrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		ReadableScrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		textReadableArea.setEditable(false);
		sendButton = new JButton();
		sendButton.setText("Envio");
		sendButton.setBounds(300, 210 , 70 , 20);
		doc = textReadableArea.getStyledDocument();
		verticalScrollReadeable = ReadableScrollPanel.getVerticalScrollBar();

		specialKeyWord = new SimpleAttributeSet();
		StyleConstants.setForeground(specialKeyWord, new Color(35, 76, 142));
		StyleConstants.setBold(specialKeyWord, true);
		
		normalKeyWord = new SimpleAttributeSet();
		StyleConstants.setAlignment(normalKeyWord,StyleConstants.ALIGN_LEFT );
		
		tela.getContentPane().add(WrittableScrollPanel);
		tela.getContentPane().add(ReadableScrollPanel);
		tela.getContentPane().add(sendButton);
		tela.setVisible(true);
		this.connection = connection;
		try {
			Queue = new ArrayBlockingQueue<String>(10);
			
			threadReceiver = new Thread(tcpReceiver = new TCPReceiver(connection , this));
			threadReceiver.start();
			
			threadSender = new Thread(tcpSender = new TCPSender(connection, (BlockingQueue<String>)Queue));
			threadSender.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		
	}

	public void run() {
		init();
		
	}

	private void init() {
		sendButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				sendMessage(textWriteableArea.getText());
			}
			
		});
		textWriteableArea.addKeyListener(new KeyListener(){

			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == e.VK_ENTER){
					sendMessage(textWriteableArea.getText());
				}
				
			}

			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		
	}
	public void messageRecieved(String message){
		try {
			this.doc.insertString(doc.getLength() , ipAddress + ": "  ,specialKeyWord );
			this.doc.insertString(doc.getLength() , message + "\n" , normalKeyWord );
			verticalScrollReadeable.setValue(verticalScrollReadeable.getMaximum());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	public void sendMessage(String message){
		try {
			Queue.put(message);
			System.out.println("Mensagem colocada na Fila " + message);
			textWriteableArea.setText("");
			try {
				this.doc.insertString(doc.getLength(),"localhost: ", specialKeyWord);
				
				this.doc.insertString(doc.getLength(),message + "\n" , normalKeyWord);
				verticalScrollReadeable.setValue(verticalScrollReadeable.getMaximum());
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
}
