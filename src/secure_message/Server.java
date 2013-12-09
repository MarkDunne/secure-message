package secure_message;

import java.net.MalformedURLException;
import java.util.HashMap;

import javax.jms.JMSException;

import org.exolab.jms.administration.AdminConnectionFactory;
import org.exolab.jms.administration.JmsAdminServerIfc;

import secure_message.ServerChannelMessage.GetRoomReply;
import secure_message.ServerChannelMessage.GetRoomRequest;
import secure_message.ServerCoreConnector.CoreRoomRequestListener;

public class Server implements CoreRoomRequestListener {
	private JmsAdminServerIfc coreAdmin;
	private ServerCoreConnector serverCoreConnector;
	private final HashMap<String, Room> activeRooms;
	public Server() {
		serverCoreConnector = new ServerCoreConnector(this);
		activeRooms = new HashMap<String, Room>();
		try {
			coreAdmin = AdminConnectionFactory.create(CoreConnector.getCoreServerURL());
		} catch (MalformedURLException | JMSException e) {
			e.printStackTrace();
		}
		
		System.out.println("Server online");
	}
	
	private boolean validRoomName(String roomName){
		return roomName != null && !roomName.isEmpty();
	}
	
	@Override
	public void onCoreRoomRequest(GetRoomRequest request) {
		String roomName = request.getRoomName();
		if(!activeRooms.containsKey(roomName)){
			try {
				if(validRoomName(roomName)){
					if(!coreAdmin.destinationExists(roomName)){
						coreAdmin.addDestination(roomName, false);
						System.out.println("Server created room: " + roomName);
					}
					activeRooms.put(roomName, new Room(roomName));
				}
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		final GetRoomReply reply = new GetRoomReply(activeRooms.get(roomName));
		serverCoreConnector.sendMessage(reply);
	}
	
	public static void main(String[] args){
		new Server();
	}
}
