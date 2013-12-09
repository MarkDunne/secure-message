package secure_message;

import secure_message.ServerChannelMessage.GetRoomReply;
import secure_message.ServerChannelMessage.GetRoomRequest;

public class ServerCoreConnector extends CoreConnector {
	
	public interface CoreRoomRequestListener{
		public void onCoreRoomRequest(GetRoomRequest request);
	}
	private final CoreRoomRequestListener coreRoomRequestListener;
	
	public ServerCoreConnector(CoreRoomRequestListener coreRoomRequestListener) {
		this.coreRoomRequestListener = coreRoomRequestListener;
	}
	
	@Override
	public void onGetRoomRequest(GetRoomRequest request) {
		coreRoomRequestListener.onCoreRoomRequest(request);
	}

	@Override
	public void onGetRoomReply(GetRoomReply reply) {

	}
}
