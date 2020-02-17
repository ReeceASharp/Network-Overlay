package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OverlayNodeSendsData implements Event {
	private static final int type = Protocol.OVERLAY_NODE_SENDS_DATA;

	int destinationID;
	int sourceID;
	
	int payload;
	
	int nodesVisited;
	int[] visitedList;
	
	public OverlayNodeSendsData(int destinationID, int sourceID, int payload,
			int nodesVisited, int[] visitedList) {
		
		this.destinationID = destinationID;
		this.sourceID = sourceID;
		this.payload = payload;
		this.nodesVisited = nodesVisited;
		this.visitedList = visitedList;
	}
	
	public OverlayNodeSendsData(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		din.readInt();
		
		destinationID = din.readInt();
		sourceID = din.readInt();
		
		payload = din.readInt();
		
		nodesVisited = din.readInt();
		
		//int visitedLength = din.readInt();
		visitedList = new int[nodesVisited];
		
		for (int i = 0; i < nodesVisited; i++) 
			visitedList[i] = din.readInt();
		
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
		byte[] marshalledBytes = null;
		
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(byteOutStream));
		try {
			
			//get type
			dout.writeInt(type);
			//get Destination node ID
			dout.writeInt(destinationID);
			//get Source node ID
			dout.writeInt(sourceID);
			//get payload Value
			dout.writeInt(payload);
			//get visit ID list length
			dout.writeInt(nodesVisited);
			//populate ID list
			for (int i = 0; i < nodesVisited; i++)
				dout.writeInt(visitedList[i]);
			
			
		} catch (IOException e) {
			//failed for some reason
			System.out.println(e);
		}
		return marshalledBytes;
	}
	
	public int getDesitinationID() {
		return destinationID;
	}
	
	public int getSourceID() {
		return sourceID;
	}
	
	public int getPayload() {
		return payload;
	}
	
	public int getNodesVisited() {
		return nodesVisited;
	}
	
	public int[] getVisitedList() {
		return visitedList;
	}

}
