import java.net.*;
import java.io.*;
import java.util.*;

public class Thread1 extends Thread {
	Socket ligacao;
	Presences presences;
	BufferedReader in;
	PrintWriter out, out2;
	String msg, response, ip, nickname;
	Vector<String> nickList;
	ArrayList<Socket> socketList;
	ArrayList<String> RMIList;

	public Thread1(Socket ligacao, Presences presences) {
		this.ligacao = ligacao;
		this.presences = presences;
		
		try
		{	
			this.in = new BufferedReader (new InputStreamReader(ligacao.getInputStream()));
			
			this.out = new PrintWriter(ligacao.getOutputStream());
		} catch (IOException e) {
			System.out.println("Erro na execucao do servidor: " + e);
		}
	}
	
	public void run() {
		while(ligacao.isConnected()){
			try{
				msg = in.readLine();
			}
			catch(IOException e){
				System.out.println("Erro na execucao do servidor: " + e);
				break;
			}
			System.out.println("Request=" + msg);
			StringTokenizer tokens = new StringTokenizer(msg);
			String metodo = tokens.nextToken();
			
			switch(metodo){
				case "SESSION_UPDATE_REQUEST":
						response = "SESSION_UPDATE\n";
						ip = tokens.nextToken();
						nickname = tokens.nextToken();
						nickList = presences.getPresences(ip,nickname,ligacao);
						Thread3 t3 = new Thread3(ligacao,presences,nickname);
						t3.start();
						RMIList= presences.getRMIUsers();
						response += "Lista de Clientes Ativos:" + "\n";
						for (Iterator<String> it = nickList.iterator(); it.hasNext();){
							String next = it.next(); 
							response += next + "\n"; 
						}
						response += "Lista de Clientes a Usar RMI:" + "\n";
						for (Iterator<String> it = RMIList.iterator(); it.hasNext();){
							String next = it.next(); 
							response += next + "\n"; 
						}
						response += "\nUltimos 10 Posts:\n";
						response += presences.Get10UltPosts() + "fim";
						System.out.println(response);
						out.println(response);
						out.flush();
						socketList=presences.getSocketList();
						System.out.println(response);
						for(int i=0;i<socketList.size();i++)
						{
							try{
								out2 = new PrintWriter(socketList.get(i).getOutputStream());
								out2.println(response);
								out2.flush();
							}catch(Exception e){
								System.out.println("Erro");
							}
							
						}
					break;
					
					case"SESSION_UPDATE":
					
						response = "SESSION_UPDATE\n";
						ip = tokens.nextToken();
						nickname = tokens.nextToken();
						nickList = presences.getPresences(ip,nickname,ligacao);
						RMIList= presences.getRMIUsers();
						response += "Lista de Clientes Ativos:" + "\n";
						for (Iterator<String> it = nickList.iterator(); it.hasNext();){
							String next = it.next(); 
							response += next + "\n"; 
						}
						response += "Lista de Clientes a Usar RMI:" + "\n";
						for (Iterator<String> it = RMIList.iterator(); it.hasNext();){
							String next = it.next(); 
							response += next + "\n"; 
						}
						response += "\nUltimos 10 Posts:\n";
						response += presences.Get10UltPosts() + "fim";
						System.out.println(response);
						out.println(response);
						out.flush();
						
					break;
					case"AGENT_POST":
						String p="", Post=null;
						nickname = tokens.nextToken();
						presences.AtualizarPres(nickname);
						int inteiro = tokens.countTokens();
						for(int i=0; i<inteiro;i++)
						{
							if(i==0){
								Post=tokens.nextToken();
							}else{
								p=tokens.nextToken();
								Post += " " + p ; 
							}
							
						}
						System.out.println(Post);
						presences.SavePost(Post, nickname);
						
						response = "SESSION_UPDATE\n";
						nickList = presences.getNickList();
						response += "Lista de Clientes Ativos:" + "\n";
						RMIList= presences.getRMIUsers();
						for (Iterator<String> it = nickList.iterator(); it.hasNext();){
							String next = it.next(); 
							response += next + "\n"; 
						}
						response += "Lista de Clientes a Usar RMI:" + "\n";
						for (Iterator<String> it = RMIList.iterator(); it.hasNext();){
							String next = it.next(); 
							response += next + " "; 
						}
						response += "\nUltimos 10 Posts:\n";
						response += presences.Get10UltPosts() + "fim";
						System.out.println(response);
						socketList=presences.getSocketList();
						for(int i=0;i<socketList.size();i++)
						{
							try{
								out2 = new PrintWriter(socketList.get(i).getOutputStream());
								out2.println(response);
								out2.flush();
							}catch(Exception e){
								System.out.println("Erro");
							}
						}
					break;
					default:
					response = "SESSION_UPDATE\n";
					response += "Metodo Invalido\n";
					response += "fim";
					out.println(response);
					out.flush();
			}
		}
	}		
}

