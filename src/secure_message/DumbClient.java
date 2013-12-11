package secure_message;

import javax.jms.Message;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import messages.RoomMessage;
import messages.RoomMessage.NewPartialsPackage;
import messages.RoomMessage.PartialsPackageReply;
import messages.RoomMessage.PartialsPackageRequest;
import messages.RoomMessage.RoomMessageListener;
import messages.RoomMessage.TextMessage;
import connectors.Connector;
import connectors.RoomConnector.RoomOutputListener;

public class DumbClient implements RoomOutputListener, RoomMessageListener {

	private JTextArea outputArea;

	public DumbClient() {
		buildGui();
		new Connector("chat_room_1") {
			@Override
			public void onMessage(Message message) {
				RoomMessage.unwrap(message, DumbClient.this);
			}
		};
	}
	
	@Override
	public void onTextMessage(String message) {
		outputArea.append(message + "\n");
	}

	public void buildGui(){
		final JFrame frame = new JFrame();
		frame.setTitle("Dumb Client");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		outputArea = new JTextArea();
		outputArea.setRows(15);
		outputArea.setColumns(40);
		outputArea.setEditable(false);
		final JScrollPane scroll = new JScrollPane(outputArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scroll);

		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new DumbClient();
	}

	@Override
	public void onTextMessage(TextMessage message) {
		outputArea.append(new String(message.getContent()) + "\n");
	}

	@Override
	public void onPartialsPackageRequest(PartialsPackageRequest partialsPackageRequest) {
		outputArea.append("Partials request \n");
	}

	@Override
	public void onPartialsPackageReply(PartialsPackageReply partialsPackageReply) {
		outputArea.append("Partials package reply: " + partialsPackageReply.getPartials() + "\n");
	}

	@Override
	public void onNewPartialsPackage(NewPartialsPackage newPartialsPackage) {
		outputArea.append("new partials package: " + newPartialsPackage.getPartials() + "\n");
	}
}