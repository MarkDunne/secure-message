package secure_message;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import connectors.ClientCoreConnector;
import connectors.ClientCoreConnector.CoreRoomReplyListener;
import connectors.RoomConnector.RoomOutputListener;

public class Client implements RoomOutputListener {

	private Room chatRoom;
	private final ClientCoreConnector clientCoreConnector;
	private JTextArea outputArea;
	private JTextArea inputArea;
	
	private final ActionListener sendAction = new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			chatRoom.sendTextMessage(inputArea.getText());
			inputArea.setText("");
		}
	};

	public Client() {
		buildGui();
		clientCoreConnector = new ClientCoreConnector();
		clientCoreConnector.getRoom("chat_room_1", new CoreRoomReplyListener() {
			@Override
			public void onCoreRoomReply(Room room) {
				chatRoom = room;
				chatRoom.attachClient(Client.this);
			}
		});
	}
	
	@Override
	public void onTextMessage(String message) {
		outputArea.append(message + "\n");
	}

	public void buildGui(){
		final JFrame frame = new JFrame();
		frame.setTitle("Client");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		outputArea = new JTextArea();
		outputArea.setRows(10);
		outputArea.setEditable(false);
		final JScrollPane scroll = new JScrollPane(outputArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scroll);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		inputArea = new JTextArea();
		inputArea.setColumns(40);
		inputArea.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		panel.add(inputArea);
				
		inputArea.registerKeyboardAction(sendAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_FOCUSED);
		final JButton sendButton = new JButton("Send");
		sendButton.addActionListener(sendAction);
		panel.add(sendButton);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new Client();
	}
}
