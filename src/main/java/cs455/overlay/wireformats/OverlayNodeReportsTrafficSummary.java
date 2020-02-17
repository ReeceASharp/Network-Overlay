package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OverlayNodeReportsTrafficSummary implements Event {
	private static final int type = Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY;
	
	int id;
	int sentPackets;
	int relayedPackets;
	long payloadSentSum;
	int receivedPackets;
	long payloadReceivedSum;
	
	public OverlayNodeReportsTrafficSummary(int id, int sentPackets, int relayedPackets,
			long payloadSentSum, int receivedPackets, long payloadReceivedSum) {
		this.id = id;
		this.sentPackets = sentPackets;
		this.relayedPackets = relayedPackets;
		this.payloadSentSum = payloadSentSum;
		this.receivedPackets = receivedPackets;
		this.payloadReceivedSum = payloadReceivedSum;
	}
	

	public OverlayNodeReportsTrafficSummary(byte[] marshalledBytes) throws IOException {
		// TODO Auto-generated constructor stub
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		//disregard type
		din.readInt();
		
		id = din.readInt();
		sentPackets = din.readInt();
		relayedPackets = din.readInt();
		payloadSentSum = din.readLong();
		receivedPackets = din.readInt();
		payloadReceivedSum = din.readLong();
		
		//close wrapper streams
		baInputStream.close();
		din.close();		
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public byte[] getBytes() {
		
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(byteOutStream));
		try {
			dout.writeInt(type);
			dout.writeInt(sentPackets);
			dout.writeInt(relayedPackets);
			dout.writeLong(payloadSentSum);
			dout.writeInt(receivedPackets);
			dout.writeLong(payloadReceivedSum);
		} catch (IOException e) {
			//failed for some reason
			System.out.println(e);
		}
		return null;
	}
	
	public int getID() {
		return id;
	}

	public int getSentPackets() {
		return sentPackets;
	}
	
	public int getRelayedPackets() {
		return relayedPackets;
	}
	
	public long getPayloadSentSum() {
		return payloadSentSum;
	}
	
	public int getReceivedPackets() {
		return receivedPackets;
	}
	
	public long getPayloadReceivedSum() {
		return payloadReceivedSum;
	}
	
}



















