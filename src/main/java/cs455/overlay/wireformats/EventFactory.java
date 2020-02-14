package cs455.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class EventFactory {
	
	private static final EventFactory instance = new EventFactory();
	
	private EventFactory() { }
	
	public static EventFactory getInstance() {
		return instance;
	}
	
	public Event createEvent(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream bin = new ByteArrayInputStream(marshalledBytes);
		int value = ByteBuffer.wrap( marshalledBytes ).getInt();
		System.out.printf("EventFactory::createEvent: Value = %d%n", value);
		
		switch (value) {
			case 2:
				return new OverlayNodeSendsRegistration(marshalledBytes);
			case 3:
				return new RegistryReportsRegistrationStatus(marshalledBytes);
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
				System.err.printf("EventFactory::createEvent: 'Could not create Error': %d%n", value);
				return null;
		}
	}

}
