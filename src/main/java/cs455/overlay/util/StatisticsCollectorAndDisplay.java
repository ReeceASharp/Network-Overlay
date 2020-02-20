package cs455.overlay.util;

public class StatisticsCollectorAndDisplay {
	
	NodeResult[] results;
	int[] idList;
	
	private class NodeResult {
		
		int sentPackets = 0;
		int receivedPackets = 0;
		int relayedPackets = 0;
		long sentSum = 0;
		long receivedSum = 0;
		boolean ready = false;
		
		public NodeResult(int sp, int rp, int rlp, long ss, long rs) {
			sentPackets = sp;
			receivedPackets = rp;
			relayedPackets = rlp;
			sentSum = ss;
			receivedSum = rs;
			ready = true;
		}
		
		@Override
		public String toString() {
			return String.format("%10s %10d %10d %15d %15d", 
					sentPackets, receivedPackets, relayedPackets, sentSum, receivedSum);
		}
		
	}
	
	
	
	public StatisticsCollectorAndDisplay(int size, int[] idList) {
		results = new NodeResult[size];
		this.idList = idList;
	}
	
	public synchronized void setResult(int i, int sp, int rp, int rlp, long ss, long rs) {
		results[i] = new NodeResult(sp, rp, rlp, ss, rs);
	}

	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("%10s %10s %10s %10s %15s %15s%n", 
				"Node ID","Sent", "Received", "Relayed"," Sum Sent","Sum Received"));
		
		for (int i = 0; i < results.length; i++) {
			sb.append(String.format("%10d %s%n", idList[i], results[i]));
		}
		
		sb.append(sumOutput() + "\n");
		return sb.toString();
	}
	
	public String sumOutput() {
		//could also be done 
		int totalSent = 0;
		int totalReceived = 0;
		int totalRelayed = 0;
		long totalSumSent = 0;
		long totalSumReceived = 0;
		
		for (NodeResult r : results) {
			totalSent += r.sentPackets;
			totalReceived += r.receivedPackets;
			totalRelayed += r.relayedPackets;
			totalSumSent += r.sentSum;
			totalSumReceived += r.receivedSum;
		}
		
		
		return String.format("%10s %10s %10s %10s %15s %15s",
				"SUM", totalSent, totalReceived, totalRelayed, totalSumSent, totalSumReceived);
	}
	
	public synchronized boolean isMissingData() {
		for (NodeResult r : results) {
			if (r == null) {
				return false;
			}
			if (!r.ready) {
				return false;
			}
		}
		return true;
	}
	
	//reset to an empty array, stops other threads from attempting to display
	public void reset() {
		for (int i = 0; i < results.length; i++) {
			results[i] = null;
		}
	}
}

