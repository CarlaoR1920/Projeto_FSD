import java.net.*;
import java.io.*;
import java.util.*;

public class Thread3 extends Thread {
	Socket ligacao;
	BufferedReader in;
	PrintWriter out;
	String nick;
	Presences presences;
	

	public Thread3(Socket ligacao,Presences presences, String nick) {
		this.ligacao = ligacao;
		this.nick=nick;
		this.presences=presences;
		try
		{	
			this.in = new BufferedReader (new InputStreamReader(ligacao.getInputStream()));
			
			this.out = new PrintWriter(ligacao.getOutputStream());
		} catch (IOException e) {
			System.out.println("Erro na execucao do servidor: " + e);
		}
	}
	
	public void run() {
		while(ligacao.isConnected())
		{
			if(presences.verTimeOut(nick)){
						out.println("SESSION_TIMEOUT");
						out.flush();
						try{
						presences.atualizarRMI(nick);
						in.close();
						out.close();
						ligacao.close();
						}catch(Exception e){
							System.out.println("Error");
						}
			}
		}
	}
}