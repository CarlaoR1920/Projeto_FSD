import java.net.*;
import java.io.*;
import java.util.*;

public class Thread4 extends Thread {
	Socket ligacao;
	BufferedReader in;
	PrintWriter out;
	String metodo, print;
	String[] all =null;
	

	public Thread4(Socket ligacao) {
		this.ligacao = ligacao;
		try
		{	
			this.in = new BufferedReader (new InputStreamReader(ligacao.getInputStream()));
			
			this.out = new PrintWriter(ligacao.getOutputStream());
		} catch (IOException e) {
			System.out.println("Erro na execucao do servidor: " + e);
			System.exit(1);
		}
	}
	
	public void run() {
		while(ligacao.isConnected())
		{
			try{
				metodo = in.readLine();
			}
			catch(IOException e){
				System.out.println("Erro na execucao do servidor: " + e);
				System.exit(1);
			}
			
			switch(metodo){
				
				case "SESSION_UPDATE_REQUEST":
				try{
				for (int i=0;i<2;i++){
							print = in.readLine();
							all = print.split("\n");
							System.out.println(Arrays.toString(all));
				}
				}catch(IOException e){
					System.out.print("Erro");
				}
				System.out.println("====================");
				System.out.println("|1 - Session_Update|");
				System.out.println("|2 - Escrever Post |");
				System.out.println("|3 - Chat Privado  |");
				System.out.println("|4 - Sair          |");
				System.out.println("====================");
				break;
				case "SESSION_UPDATE":
				try{
				int i=0;
				System.out.println(System.lineSeparator().repeat(50));
				while(i==0)
				{
					print = in.readLine();
					if(!print.equals("fim"))
					{
						System.out.println(print);
					}else{
						i=1;
					}
				}
				}catch(IOException e){
					System.out.print("Erro");
				}
				System.out.println("====================");
				System.out.println("|1 - Session_Update|");
				System.out.println("|2 - Escrever Post |");
				System.out.println("|3 - Chat Privado  |");
				System.out.println("|4 - Sair          |");
				System.out.println("====================");
				break;
				
				case "SESSION_TIMEOUT":
				try{
					in.close();
					out.close();
					ligacao.close();
					System.exit(1);
				}catch(IOException e){
					System.out.println("Erro");
				}
				
				break;
				
				default:
				System.out.println("Erro");
			}
		}
	}
}