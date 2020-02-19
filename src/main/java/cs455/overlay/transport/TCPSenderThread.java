package cs455.overlay.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSenderThread implements Runnable {
	private Socket socket;
	private byte[] msg;
	private Object lock = new Object();
	
	public TCPSenderThread(Socket socket, byte[] msg) {
		this.socket = socket;
		this.msg = msg;
	}

	@Override
	public void run() {
		//using the established socket to send information
		
		int msgLength = msg.length;
		try {
			DataOutputStream dout = new DataOutputStream(this.socket.getOutputStream());
			
			
			synchronized(socket) {
			//write message to buffer
				dout.writeInt(msgLength);
				dout.write(msg, 0, msgLength);
				
				dout.flush();
			}
			System.out.println("Successfully wrote " + msgLength + " bytes to " + socket);
		} catch (IOException e) {
			System.out.println("TCPSender::run::writing_to_output: " + e);
		}
		//System.out.println("TCPSenderThread done");
	}

}
