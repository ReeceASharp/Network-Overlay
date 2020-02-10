package cs455.overlay.transport;

public class TCPServerThread implements Runnable {
	

	@Override
	public void run() {
		System.out.println("TCPReceiverThread::run::");
		boolean exit = false;
		
		//listen for new connections to this program
		while (!false) {
			
			//if something tries to connect, spawn a thread to handle that socket
			Thread connection = new Thread(new TCPReceiverThread(0));
			connection.start();
		}
	}

}
