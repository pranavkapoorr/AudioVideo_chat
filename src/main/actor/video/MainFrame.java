package main.actor.video;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.opencv.core.Core;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.io.Tcp;
import akka.io.TcpMessage;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.border.EtchedBorder;
import javax.swing.border.BevelBorder;
import java.awt.Font;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JTextField serverIp;
	private JTextField serverPort;
	private final ActorSystem system =  ActorSystem.create("main_system");
	public volatile static ActorRef server_client = null;
	protected volatile static JLabel screen1;
	protected volatile static JLabel screen2;
	private JPanel panel_1;
	private JPanel panel_2;
	private JTextField clientIp;
	private JTextField clientPort;
	public JButton stopServerbtn;
	public JButton startServerbtn;
	public JButton startClientbtn;
	public JButton stopClientbtn;
	private ActorRef videoActor;
	
	
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1428, 910);
		contentPane = new JPanel();
		contentPane.setFont(new Font("Tahoma", Font.BOLD, 20));
		contentPane.setBackground(new Color(0, 191, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		serverIp = new JTextField();
		serverIp.setName("server ip");
		serverIp.setFont(new Font("Tahoma", Font.PLAIN, 18));
		serverIp.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		serverIp.setText("localhost");
		serverIp.setBounds(42, 640, 160, 30);
		contentPane.add(serverIp);
		serverIp.setColumns(10);
		
		serverPort = new JTextField();
		serverPort.setName("server port");
		serverPort.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		serverPort.setBounds(217, 640, 161, 30);
		contentPane.add(serverPort);
		serverPort.setColumns(10);
		
		startServerbtn = new JButton("start");
		startServerbtn.setFont(new Font("Tahoma", Font.BOLD, 17));
		startServerbtn.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		startServerbtn.setBounds(75, 690, 76, 29);
		startServerbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				startServer();
				
			}
		});
		contentPane.add(startServerbtn);
		
		stopServerbtn = new JButton("stop");
		stopServerbtn.setFont(new Font("Tahoma", Font.BOLD, 17));
		stopServerbtn.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		stopServerbtn.setBounds(266, 690, 76, 29);
		stopServerbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				stopServer();
				
			}
		});
		contentPane.add(stopServerbtn);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setBackground(new Color(255, 255, 255));
		panel.setBounds(32, 33, 1338, 587);
		contentPane.add(panel);
		panel.setLayout(null);
		
		panel_1 = new JPanel();
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(192, 192, 192), new Color(224, 255, 255), null, null));
		panel_1.setBounds(15, 16, 642, 555);
		panel.add(panel_1);
		panel_1.setLayout(null);
		
		screen1 = new JLabel("");
		screen1.setBounds(15, 16, 612, 523);
		panel_1.add(screen1);
		screen1.setBackground(Color.WHITE);
		
		panel_2 = new JPanel();
		panel_2.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(220, 220, 220), new Color(224, 255, 255), null, null));
		panel_2.setBounds(672, 16, 651, 555);
		panel.add(panel_2);
		panel_2.setLayout(null);
		
		screen2 = new JLabel("");
		screen2.setBounds(15, 16, 621, 523);
		panel_2.add(screen2);
		screen2.setBackground(Color.WHITE);
		
		clientIp = new JTextField();
		clientIp.setText("localhost");
		clientIp.setName("server ip");
		clientIp.setFont(new Font("Tahoma", Font.PLAIN, 18));
		clientIp.setColumns(10);
		clientIp.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		clientIp.setBounds(966, 643, 160, 30);
		contentPane.add(clientIp);
		
		clientPort = new JTextField();
		clientPort.setName("server port");
		clientPort.setColumns(10);
		clientPort.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		clientPort.setBounds(1141, 644, 161, 30);
		contentPane.add(clientPort);
		
		startClientbtn = new JButton("start");
		startClientbtn.setFont(new Font("Tahoma", Font.BOLD, 17));
		startClientbtn.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		startClientbtn.setBounds(991, 690, 76, 29);
		startClientbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				startClient();
			}
		});
		contentPane.add(startClientbtn);
		
		stopClientbtn = new JButton("stop");
		stopClientbtn.setFont(new Font("Tahoma", Font.BOLD, 17));
		stopClientbtn.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		stopClientbtn.setBounds(1182, 690, 76, 29);
		stopClientbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				stopClient();
			}
		});
		contentPane.add(stopClientbtn);
	}
	private void startServer(){
	    startClientbtn.hide();
	    stopClientbtn.hide();
		ActorRef tcpMnager =Tcp.get(system).manager();
		server_client = system.actorOf(TcpServerActor.props(tcpMnager, new InetSocketAddress(serverIp.getText(), Integer.parseInt(serverPort.getText()))),"SERVER");
		videoActor = system.actorOf(VideoCaptureActor.props(server_client));
	}
	private void stopServer(){
	    startClientbtn.show();
	    stopClientbtn.show();
		if(server_client!=null){
			server_client.tell(TcpMessage.close(),server_client);
			system.stop(server_client);
			server_client = null;
		}	
	}
	private void startClient(){
		server_client = system.actorOf(TcpClientActor.props(new InetSocketAddress(clientIp.getText(), Integer.parseInt(clientPort.getText()))),"Client");
		videoActor = system.actorOf(VideoCaptureActor.props(server_client));
		startServerbtn.hide();
		stopServerbtn.hide();
	}
	private void stopClient(){
	    startServerbtn.show();
	    stopServerbtn.show();
		if(server_client!=null){
			server_client.tell(TcpMessage.close(),server_client);
			system.stop(server_client);
			server_client = null;
		}	
	}
	
	
}
