import java.util.*;
import java.net.*;
import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.security.*;
import javax.crypto.*;

public class Cliente {
	static final int DEFAULT_PORT=2000;
	static final String DEFAULT_HOST="127.0.0.1"; 
	static final String SERVICE_NAME ="PrivateMessaging";
	
	private static void bindRMI(PrivateMessaging priv, String IP) throws RemoteException {
		
		System.getProperties().put( "java.security.policy", "./server.policy");
		
		if( System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try { 
			LocateRegistry.createRegistry(1099);
		} catch( RemoteException e) {
			
		}
		try {
			LocateRegistry.getRegistry(IP, 1099).rebind(SERVICE_NAME, priv);
		  } catch(Exception e) {
		  	System.out.println("Erro ao registrar no RMI");
			System.exit(1);
		  }
	}
	
	public static void main(String[] args) {
		String SERVER_SERVICE_NAME="/PresencesRemote";
		String servidor = DEFAULT_HOST, msg, option1=null, IpServer="127.0.0.1";
		int porto=DEFAULT_PORT,option=0;
		String request= null;
		Scanner scan = new Scanner(System.in);
		BufferedReader in=null;
		PrintWriter out=null;
		PrivateMessaging cl = null;
		boolean erros = true;
		boolean inside = true;
		PresencesInterface presences = null;
		PublicKey publicKey=null;
		PrivateKey privKey=null;
		
		
		if (args.length==0){
			System.out.println("Introduza o seu IP e nick como argumentos ");
			System.exit(1);
		}else if(args.length==1){
			System.out.println("Introduza o seu IP e nick como argumentos ");
			System.exit(1);
		}else if(args.length==3 || args.length>4){
			System.out.println("Introduza o seu IP, nick, IP server e Porta server como argumentos ");
			System.exit(1);
		}
		 String condicao = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

                    while (!args[0].matches(condicao)) { 
                        System.out.println("IP do Cliente invalido! \n Introduza um IP valido: ");
                        args[0] = scan.nextLine();
                    }
					
					if(args.length==4){
						while (!args[2].matches(condicao)) { 
                        System.out.println("IP do Servidor invalido! \n Introduza um IP valido: ");
                        args[2] = scan.nextLine();
                    }
						erros=true;
						while(erros) { 
						
						try{
						Integer.parseInt(args[3]);
						erros=false;
						}catch(Exception e){
							System.out.println("Porta do Servidor invalida! \n Introduza uma Porta valida: ");
							args[3] = scan.nextLine();
						}
                    }
					}
					
		try{
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
			keyPairGen.initialize(2048);
			KeyPair pair = keyPairGen.generateKeyPair();
			privKey = pair.getPrivate();
			publicKey= pair.getPublic();
			cl = new PrivateMessaging(privKey);
		}catch(Exception e){
			System.out.println("Erro na geracao das chaves");
			System.exit(1);
		}
		
		while(true){
			System.out.println("Ativar a possibilidade de receber mensagens privadas?\n1-Sim\n2-Não");
			option1 = scan.nextLine();
			
			if(option1.equals("2"))
			{
			
				if (args.length == 2){
					Socket ligacao = null;
					try{
						InetAddress serverAddress = InetAddress.getByName(servidor); 
						ligacao = new Socket(serverAddress, porto);
						Thread2 leitor = new Thread2(ligacao);
						leitor.start();
					}catch(IOException e){
						System.out.println("Erro na geracao do socket");
						System.exit(1);
					}
					
					try {
						in = new BufferedReader(new InputStreamReader(ligacao.getInputStream()));
						out = new PrintWriter(ligacao.getOutputStream(), true);
						request = "SESSION_UPDATE_REQUEST" + " " + args[0] + " " + args[1];
						out.println(request);
						Thread.sleep(500);
						} catch (Exception e) {
						System.out.println("Erro ao comunicar com o servidor");
						System.exit(1);
					}
						while(true){
							
							erros=true;
							while(erros){
								try{
									option1 = scan.nextLine();
									option= Integer.parseInt(option1);
									erros=false;
								}catch(Exception e){
									System.out.println("Insira uma opcao valida!");
								}
							}
							try{
								if(option == 1){
									request = "SESSION_UPDATE" + " " + args[0] + " " + args[1];
									out.println(request);
									Thread.sleep(500);
									
								}else if(option == 2){
								
									msg = scan.nextLine();
									request = "AGENT_POST" + " " + args[1] + " " + msg;
									out.println(request);
									Thread.sleep(500);
									
								}else if(option == 3){
									System.exit(0);
								}else{
									System.out.println("Insira uma opcao valida!");
								}
							}catch(Exception e){
								System.out.println("Erro ao comunicar com o servidor");
								System.exit(1);
							}	
						}
					
					}
					else
					{
						Socket ligacao = null;
						String HOST = args[2];
						int PORT = Integer.parseInt(args[3]);
						try{
							InetAddress serverAddress = InetAddress.getByName(HOST); 
							ligacao = new Socket(serverAddress, PORT);
							Thread2 leitor = new Thread2(ligacao);
							leitor.start();
						}catch(IOException e){
							System.out.println("Erro ao comunicar com o servidor");
							System.exit(1);
						}
						try {
							in = new BufferedReader(new InputStreamReader(ligacao.getInputStream()));
							out = new PrintWriter(ligacao.getOutputStream(), true);
							request = "SESSION_UPDATE_REQUEST" + " " + args[0] + " " + args[1];
							out.println(request);
							Thread.sleep(500);
						} catch (Exception e) {
							System.out.println("Erro ao comunicar com o servidor");
							System.exit(1);
						}
							
						while(true){
							erros=true;
							while(erros){
								try{
									option1 = scan.nextLine();
									option= Integer.parseInt(option1);
									erros=false;
								}catch(Exception e){
									System.out.println("Insira uma opcao valida!");
								}
							}
							try{
								if(option == 1){
									request = "SESSION_UPDATE" + " " + args[0] + " " + args[1];
									out.println(request);
									Thread.sleep(500);
								}
								else if(option == 2){
									msg = scan.nextLine();
									request = "AGENT_POST" + " " + args[1] + " " + msg;
									out.println(request);
									Thread.sleep(500);
								}else if(option == 2){
									System.exit(0);
								}else if(option == 3){
									System.exit(1);
								}else{
									System.out.println("Insira uma opcao valida!");
								}
							}catch(Exception e){
								System.out.println("Erro ao comunicar com o servidor");
								System.exit(1);
							}
						}
					}	
								
			}else if(option1.equals("1")){
				if (args.length == 2){ 
					Socket ligacao = null;
					try{
						InetAddress serverAddress = InetAddress.getByName(servidor); 
						ligacao = new Socket(serverAddress, porto);
						Thread4 leitor = new Thread4(ligacao);
						leitor.start();
						
						in = new BufferedReader(new InputStreamReader(ligacao.getInputStream()));
						out = new PrintWriter(ligacao.getOutputStream(), true);
						request = "SESSION_UPDATE_REQUEST" + " " + args[0] + " " + args[1];
						out.println(request);
						presences = (PresencesInterface) LocateRegistry.getRegistry(IpServer).lookup(SERVER_SERVICE_NAME);
						bindRMI(cl, args[0]);
						presences.getPresences2(args[0], args[1],publicKey, cl);
					}catch(Exception e){
						System.out.println("Erro ao comunicar com o servidor");
						System.exit(1);
					}
					
					try {
							in = new BufferedReader(new InputStreamReader(ligacao.getInputStream()));
							out = new PrintWriter(ligacao.getOutputStream(), true);
						} catch (Exception e) {
							System.out.println("Erro ao comunicar com o servidor: "+e);
							System.exit(1);
						}
						while(true){
							erros = true;
							while(erros){
									try{
										option1 = scan.nextLine();
										option= Integer.parseInt(option1);
										erros = false;
									}catch(Exception e){
										System.out.println("Insira uma opcao valida!");
									}
								}
							
							if(option == 1){
								try{
								request = "SESSION_UPDATE" + " " + args[0] + " " + args[1];
								out.println(request);
								Thread.sleep(500);
								}catch(Exception e){
									System.out.println("Erro a comunicar com o servidor");
								}
							}
							else if(option == 2){
								try{
								msg = scan.nextLine();
								request = "AGENT_POST" + " " + args[1] + " " + msg;
								out.println(request);
								Thread.sleep(500);
								}catch(Exception e){
									System.out.println("Erro a comunicar com o servidor");
								}
							}else if(option == 3){
								try{
								PrivateMessagingInterface PMI;
								PublicKey pk;
								HashMap<String, PrivateMessagingInterface> PMIS = cl.getMap();
								System.out.println("Qual o nickname do utilizador?");
								msg = scan.nextLine();
								pk = presences.getPublicKeyRMI(msg);
								msg = presences.getIPRMI(msg);
								if(!PMIS.containsKey(msg)){
									PMI = (PrivateMessagingInterface) LocateRegistry.getRegistry(msg).lookup(SERVICE_NAME);
								}else{
									PMI = PMIS.get(msg);
								}
								PMIS.put(msg, PMI);
								System.out.print("Escreva a menssagem: ");
								msg = scan.nextLine();
								System.out.println("Deseja encriptar e assinar a mensagem?\n1-Sim\n2-Não");
								option1 = scan.nextLine();
								if(option1.equals("1")){
									byte[] input = msg.getBytes();
									MessageDigest md = MessageDigest.getInstance("SHA-256");
									md.update(input);
									byte[] resumomsg = md.digest(); 
									Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
									cipher.init(Cipher.ENCRYPT_MODE, pk);	  
									cipher.update(input);
									byte[] cipherText = cipher.doFinal();
									Signature sign = Signature.getInstance("SHA1withRSA");
									sign.initSign(privKey);
									sign.update(resumomsg);
									byte[] signature = sign.sign();
									PMI.sendMessageSecure(args[0], args[1], cipherText, cl,signature,publicKey);									
								}
								else{
									PMI.sendMessage(args[0], args[1], msg, cl);
								}
							}catch(Exception e){
								System.out.println("Erro no envio da mensagem");
								System.out.println("====================");
								System.out.println("|1 - Session_Update|");
								System.out.println("|2 - Escrever Post |");
								System.out.println("|3 - Chat Privado  |");
								System.out.println("====================");
							}
							}else if(option==4){
								System.exit(0);
							}else{
								System.out.println("Insira uma opcao valida!");
							}
						}	
					
				}
				else
				{
						Socket ligacao = null;
						String HOST = args[2];
						int PORT = Integer.parseInt(args[3]);
								
					try{
						InetAddress serverAddress = InetAddress.getByName(HOST); 
						ligacao = new Socket(HOST, PORT);
						Thread4 leitor = new Thread4(ligacao);
						leitor.start();
						
						in = new BufferedReader(new InputStreamReader(ligacao.getInputStream()));
						out = new PrintWriter(ligacao.getOutputStream(), true);
						request = "SESSION_UPDATE_REQUEST" + " " + args[0] + " " + args[1];
						out.println(request);
						presences = (PresencesInterface) LocateRegistry.getRegistry(HOST).lookup(SERVER_SERVICE_NAME);
						bindRMI(cl, args[0]);
						presences.getPresences2(args[0], args[1],publicKey, cl);
					}catch(Exception e){
						System.err.println("Error");
						e.printStackTrace();
					}
					
					try {
							in = new BufferedReader(new InputStreamReader(ligacao.getInputStream()));
							out = new PrintWriter(ligacao.getOutputStream(), true);
						} catch (Exception e) {
							System.out.println("Erro ao comunicar com o servidor");
							System.exit(1);
					}
						while(true){
							erros=true;
							while(erros){
									try{
										option1 = scan.nextLine();
										option= Integer.parseInt(option1);
										erros = false;
									}catch(Exception e){
										System.out.println("Insira uma opcao valida!");
										
									}
								}
							
							if(option == 1){
								try{
								request = "SESSION_UPDATE" + " " + args[0] + " " + args[1];
								out.println(request);
								Thread.sleep(500);
								}catch(Exception e){
									System.out.println("Erro ao comunicar com o servidor");
									System.exit(1);
								}
							}
							else if(option == 2){
								try{
								msg = scan.nextLine();
								request = "AGENT_POST" + " " + args[1] + " " + msg;
								out.println(request);
								Thread.sleep(500);
								}catch(Exception e){
									System.out.println("Erro ao comunicar com o servidor");
									System.exit(1);
								}
							}else if(option == 3){
								try{
								PrivateMessagingInterface PMI;
								PublicKey pk;
								HashMap<String, PrivateMessagingInterface> PMIS = cl.getMap();
								System.out.println("Qual o nickname do utilizador?");
								msg = scan.nextLine();
								pk = presences.getPublicKeyRMI(msg);
								msg = presences.getIPRMI(msg);
								if(!PMIS.containsKey(msg)){
									PMI = (PrivateMessagingInterface) LocateRegistry.getRegistry(msg).lookup(SERVICE_NAME);
								}else{
									PMI = PMIS.get(msg);
								}
								PMIS.put(msg, PMI);
								System.out.print("Escreva a menssagem: ");
								msg = scan.nextLine();
								System.out.println("Deseja encriptar e assinar a mensagem?\n1-Sim\n2-Não");
								option1 = scan.nextLine();
								if(option1.equals("1")){
									byte[] input = msg.getBytes();
									MessageDigest md = MessageDigest.getInstance("SHA-256");
									md.update(input);
									byte[] resumomsg = md.digest(); 
									Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
									cipher.init(Cipher.ENCRYPT_MODE, pk);	  
									cipher.update(input);
									byte[] cipherText = cipher.doFinal();Signature sign = Signature.getInstance("SHA1withRSA");
									sign.initSign(privKey);
									sign.update(resumomsg);
									byte[] signature = sign.sign();
									PMI.sendMessageSecure(args[0], args[1], cipherText, cl,signature,publicKey);									
								}
								else{
									PMI.sendMessage(args[0], args[1], msg, cl);
								}
								}catch(Exception e){
									System.out.println("Erro no envio da mensagem");
									System.out.println("====================");
									System.out.println("|1 - Session_Update|");
									System.out.println("|2 - Escrever Post |");
									System.out.println("|3 - Chat Privado  |");
									System.out.println("|4 - Sair          |");
									System.out.println("====================");
								}
						}else if(option==4){
							System.exit(0);
						}else{
							System.out.println("Insira uma opcao valida!");

							System.exit(0);
						}	
					
					}
				}
			}else{
				System.out.println("Insira uma opcao valida!");
			}
		}
	}
}

