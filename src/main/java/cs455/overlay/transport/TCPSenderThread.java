package cs455.overlay.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSenderThread implements Runnable {
	private Socket socket;
	private DataOutputStream dout;
	private byte[] msg;
	
	public TCPSenderThread(Socket socket, byte[] msg) {
		this.socket = socket;
		this.msg = msg;
	}

	@Override
	public void run() {
		//using the established pipeline to send information
		//System.out.println("TCPSender::run::sending_to:" + socket);
		
		int msgLength = msg.length;
		try {
			dout = new DataOutputStream(this.socket.getOutputStream());
			
			//write message to buffer
			dout.writeInt(msgLength);
			dout.write(msg, 0, msgLength);
			dout.flush();
			
		} catch (IOException e) {
			System.out.println("TCPSender::run::writing_to_output: " + e);
		}
		//System.out.println("TCPSenderThread done");
	}

}
