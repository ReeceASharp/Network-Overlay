package cs455.overlay.wireformats;

import java.nio.ByteBuffer;

public class EventFactory {
	private static final EventFactory instance = new EventFactory();
	
	private EventFactory() {}
	
	public static EventFactory getInstance() {
		return instance;
	}
	
	public Event createEvent(byte[] marshalledBytes) {
		switch (ByteBuffer.wrap(marshalledBytes).getInt()) {
			case 2:
				return new OverlayNodeSendsRegistration();
			case 3:
				return new RegistryReportsRegistrationStatus();
			case 4:
				return new OverlayNodeSendsDeregistration();
			case 5:
				return new RegistryReportsDeregistrationStatus();
			case 6:
				return new RegistrySendsNodeManifest();
			case 7:
				return new NodeReportsOverlaySetupStatus();
			case 8:
				return new RegistryRequestsTaskInitiate();
			case 9:
				return new OverlayNodeSendsData();
			case 10:
				return new OverlayNodeReportsTaskFinished();
			case 11:
				return new RegistryRequestsTrafficSummary();
			case 12:
				return new OverlayNodeReportsTrafficSummary();
			default:
				System.err.println("EventFactory::createEvent: 'Could not create Error'");
				return null;
		}
	}

}
