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
		//System.out.printf("EventFactory::createEvent: Value = %d%n", value);
		
		switch (value) {
			case 2:
				return new OverlayNodeSendsRegistration(marshalledBytes);
			case 3:
				return new RegistryReportsRegistrationStatus(marshalledBytes);
			case 4:
				return new OverlayNodeSendsDeregistration(marshalledBytes);
			case 5:
				return new RegistryReportsDeregistrationStatus(marshalledBytes);
			case 6:
				return new RegistrySendsNodeManifest(marshalledBytes);
			case 7:
				return new NodeReportsOverlaySetupStatus(marshalledBytes);
			case 8:
				return new RegistryRequestsTaskInitiate(marshalledBytes);
			case 9:
				return new OverlayNodeSendsData(marshalledBytes);
			case 10:
				return new OverlayNodeReportsTaskFinished(marshalledBytes);
			case 11:
				return new RegistryRequestsTrafficSummary(marshalledBytes);
			case 12:
				return new OverlayNodeReportsTrafficSummary(marshalledBytes);
			default:
				System.err.printf("EventFactory::createEvent: 'Could not create Error': %d%n", value);
				return null;
		}
	}

}
