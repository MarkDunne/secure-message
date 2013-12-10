package secure_message;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

import messages.RoomMessage.NewPartialsPackage;
import messages.RoomMessage.PartialsPackageReply;
import messages.RoomMessage.PartialsPackageRequest;
import messages.RoomMessage.TextMessage;
import connectors.RoomConnector;
import connectors.RoomConnector.RoomManagementListener;

public class Room implements Serializable, RoomManagementListener {
	
	private static final long serialVersionUID = 7965805336448973934L;
	
	private BigInteger clientID;
	private boolean clientIsfirstInRoom;
	private boolean haveLatestPartialSet;
	private RoomConnector roomConnector;

	private final String name;

	public Room(String name) {
		this.name = name;
		clientIsfirstInRoom = true;
		haveLatestPartialSet = false;
	}

	public void attachClient(Client client) {
		roomConnector = new RoomConnector(name, client, this);
		
		clientID = new BigInteger(256, new Random());
		
		if(getClientIsFirstInRoom()){
			
		}else{
			roomConnector.sendMessage(new PartialsPackageRequest());
		}
	}

	public void sendTextMessage(String message) {
		roomConnector.sendMessage(new TextMessage(message));
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public boolean getClientIsFirstInRoom() {
		return clientIsfirstInRoom;
	}

	public void setClientIsFirstInRoom(boolean clientIsfirstInRoom) {
		this.clientIsfirstInRoom = clientIsfirstInRoom;
	}

	@Override
	public void onPartialsPackageRequest(PartialsPackageRequest partialsPackageRequest) {
	}

	@Override
	public void onPartialsPackageReply(PartialsPackageReply partialsPackageReply) {

	}

	@Override
	public void onNewPartialsPackage(NewPartialsPackage newPartialsPackage) {

	}
}
