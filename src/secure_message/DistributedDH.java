package secure_message;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import messages.RoomMessage.NewPartialsPackage;
import messages.RoomMessage.PartialsPackageReply;
import messages.RoomMessage.PartialsPackageRequest;
import connectors.RoomConnector;

public class DistributedDH {

	private BigInteger clientID;
	private BigInteger privateKey;
	private DistributedDHPartial sharedKey;
	private boolean communicationSafe;
	private boolean hasLatestPartialSet;
	private boolean waitingOnPartialsPackageReply;
	private List<DistributedDHPartial> knownPartials;
	private RoomConnector roomConnector;
	
	public DistributedDH(RoomConnector roomConnector){
		this.roomConnector = roomConnector;
	}
	
	public void addClientToRoom(){	
		final SecureRandom rand = new SecureRandom();
		communicationSafe = false;
		roomConnector.setCommunicationSafe(false);
		hasLatestPartialSet = true;
		waitingOnPartialsPackageReply = true;
		clientID = new BigInteger(256, rand);
		privateKey = new BigInteger(256, rand);
		knownPartials = new ArrayList<DistributedDHPartial>();
		knownPartials.add(new DistributedDHPartial());
		roomConnector.sendMessage(new PartialsPackageRequest());	
	}
	
	public void onPartialsPackageRequest(PartialsPackageRequest partialsPackageRequest) {
		communicationSafe = false;
		roomConnector.setCommunicationSafe(false);
		if(hasLatestPartialSet){
			hasLatestPartialSet = false;
			System.out.println("returning set of size: " + knownPartials.size());
			roomConnector.sendMessage(new PartialsPackageReply(knownPartials));
		}
	}

	public void onPartialsPackageReply(PartialsPackageReply partialsPackageReply) {
		final List<DistributedDHPartial> partials = partialsPackageReply.getPartials();
		if(waitingOnPartialsPackageReply){
			waitingOnPartialsPackageReply = false;
			for(int i = 0; i < partials.size(); i++){
				DistributedDHPartial partial = partials.get(i);
				if(i < partials.size() - 1){
					partial.applyPrivateKey(privateKey, clientID);
				}
			}
			hasLatestPartialSet = true;
			knownPartials = partials;
			roomConnector.sendMessage(new NewPartialsPackage(partials));
		}
	}

	public void onNewPartialsPackage(NewPartialsPackage newPartialsPackage) {
		final List<DistributedDHPartial> partials = newPartialsPackage.getPartials();
		for(DistributedDHPartial partial : partials){
			if(!partial.hasClientContributed(clientID)){
				partial.applyPrivateKey(privateKey, clientID);
				sharedKey = partial;
				knownPartials.add(sharedKey);
				communicationSafe = true;
				roomConnector.setCommunicationSafe(true);
				break;
			}
		}
	}
	
	public boolean getCommunicationSafe(){
		return communicationSafe;
	}
	
	public BigInteger getSharedKey(){
		if(communicationSafe){
			return sharedKey.getValue();
		}else{
			return null;
		}
	}
}
