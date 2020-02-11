package cs455.overlay.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSender implements Runnable {
	private Socket socket;
	private DataOutputStream dout;
	private byte[] msg;
	
	public TCPSender(Socket socket, byte[] msg) throws IOException {
		this.socket = socket;
		dout = new DataOutputStream(this.socket.getOutputStream());
		this.msg = msg;
	}

	@Override
	public void run() {
		System.out.println("TCPSender::run");
		
		//Will implement Event system to package the data more effectively
		int msgLength = msg.length;
		try {
			//write message to buffer
			dout.writeInt(msgLength);
			dout.write(msg, 0, msgLength);
			dout.flush();
			
		} catch (IOException e) {
			System.out.println("TCPSender::run::writing_to_output: " + e);
		}

		
	}

}
