package connectors;

import messages.CoreMessage.GetRoomReply;
import messages.CoreMessage.GetRoomRequest;

public class ServerCoreConnector extends CoreConnector {

	public interface CoreRoomRequestListener {
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
