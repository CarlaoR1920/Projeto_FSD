import java.net.*;
import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.lang.SecurityManager;
import java.rmi.registry.LocateRegistry;

public class Server {
	static int DEFAULT_PORT=2000;
	static String SERVICE_NAME="/PresencesRemote";
	
	private static void bindRMI(Presences presences) throws RemoteException {
		
		System.getProperties().put( "java.security.policy", "./server.policy");
		
		
		if( System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try { 
			LocateRegistry.createRegistry(1099);
		} catch( RemoteException e) {
			
		}
		try {
		  LocateRegistry.getRegistry("127.0.0.1",1099).rebind(SERVICE_NAME, presences);
		  } catch(Exception e) {
		  	System.out.println("Registry not found" + e);
		  }
	}
	
	public static void main(String[] args) {


		int port=DEFAULT_PORT;
		ServerSocket servidor = null;
		Presences presences = null;
		
		if(args.length != 1){}
		else{
			port = Integer.parseInt(args[0]);
		}
		
		try {
			presences = new Presences();
			servidor = new ServerSocket(port);
		} catch (Exception e1) {
			System.err.println("unexpected error...");
			e1.printStackTrace();
		}
		
		try {
			bindRMI(presences);
		} catch (RemoteException e1) {
			System.err.println("erro ao registar o stub...");
			e1.printStackTrace();
		}
		

		while(true) {
			try {
				Socket ligacao = servidor.accept();
				Thread1 atendedor = new Thread1(ligacao,presences);
				atendedor.start();
			} catch (IOException e) {
				System.out.println("Erro na execucao do servidor: "+e);
			}
		}
	}
}
