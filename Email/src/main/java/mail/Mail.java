package mail;
 


import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;


  
public class Mail {
  
  public static void main(String[] args) throws IOException {
    Mail mail = new Mail();
    
    Scanner in = new Scanner(System.in);
    //��׼������

    
    /*
     * �û����������˺�
     */
    System.out.println("��������������˺�");
    String from=in.nextLine();  //�����˺�
//    String from="2737793330@qq.com";
    
    mail.setFromMail(from);
    mail.setUserName(from);
    
    
    /*
     * �����û��������ʲô���͵�����
     */
    String[] strArr = from.split("@");
    String temp = strArr[1];
    String[] serverName=temp.split("\\.");
    
    
    /*
     * ��ʼ��smtp��������ַ
     */
    String smtpServer = "smtp."+serverName[0]+".com";
    mail.setSmtpServer(smtpServer);
    
    
    /*
     * ��ʼ��pop3��������ַ
     */
    String pop3Server = "pop."+serverName[0]+".com";
    mail.setPop3Server(pop3Server);
    
    
    /* SMTP �������˿ڣ� qqĬ�ϣ�587   ������25�� */
    if(serverName[0].equals("qq"))
    	mail.setSmtpPort(587);
    else
    	mail.setSmtpPort(25);

    
    /*
     * �û������������루У���룩
     * ȷ�������Ѿ�����smtp/imap����
     */
    System.out.println("����������������루У���룩");
    String password = in.nextLine();
//    String password = "lipbeahsvaghdggc" ;
    mail.setPassword(password);
    
    
    
    System.out.println("�����ʼ���ѡ1\n�����ʼ���ѡ2");
    String choice = in.nextLine();
    
    
    //�����ʼ�
    if(choice.equals("1")) {
    	/*
         * ����receiver������
         * */
        System.out.println("��������Ҫ���͵������ַ��");
        String ToMail = in.nextLine(); 
        mail.addToMail(ToMail);
        
        
        /*
         * �����������
         * */
        System.out.println("�������ʼ����⣺");
        String subject = in.nextLine(); 
        mail.setSubject(subject);
        
        
        /*
         * �����ʼ�����
         * */
        System.out.println("�������ʼ����ģ�");
        String content = in.nextLine(); 
        mail.setContent(content);
        
        mail.setShowLog(true);
        mail.send();
        System.out.println("�������");
    }
    
    //�����ʼ�
    else if(choice.equals("2")) {
    	mail.receive();
    }
    
    
    else {
    	 System.out.println("�������    \n�������");
    }
  }
  
  /** �ʼ����� **/
  private String subject;
  /** �Ӵ˵�ַ���� **/
  private String fromMail;
  /** �û��� **/
  private String userName;
  /** ��¼���� **/
  private String password;
  /** SMTP ��������ַ **/
  private String smtpServer;
  /** POP3 ��������ַ **/
  private String pop3Server;
  /** SMTP �������˿ڣ�163Ĭ�ϣ�25  qqĬ�ϣ�587�� **/
  private int smtpPort ;
  /** POP3 �������˿�  Ĭ��110 **/
  private int pop3Port = 110;
  /** ���͵� toMail �е����е�ַ **/
  private List<String> toMail;
  /** �ʼ����� **/
  private String content;
  /** �Ƿ���ʾ��־ **/
  private boolean showLog = true;
  
  public void addToMail(String mail) {
    if (toMail == null)
      toMail = new ArrayList<String>();
    toMail.add(mail);
  }
  
  
  public void receive(){
	 
	 if (pop3Server == null) {
	      throw new RuntimeException("pop3Server ����Ϊ��");
	    }
	 if (userName == null) {
	      throw new RuntimeException("userName ����Ϊ��");
	    }
	    if (password == null) {
	      throw new RuntimeException("password ����Ϊ��");
	    }
	    
	    Socket socket = null;
	    try {
	      socket = new Socket(pop3Server, pop3Port);
	      socket.setSoTimeout(3000);
	    } catch (IOException e) {
	      throw new RuntimeException("���ӵ� " + pop3Server + ":" + pop3Port + " ʧ��", e);
	    }
	    
	    
	    try {
	    	BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    	BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	    	
	    	
	    	user(userName ,in,out);	    	
	    	pass(password,in,out);//��������    	
	    	stat(in,out);	    	                
	    	list(in,out);

            Scanner inPut = new Scanner(System.in);
	    	               
	    	System.out.println("����Ҫ�����ķ��ʼ���");
	    	String cho = inPut.nextLine();
	        retr(Integer.parseInt(cho),in,out);
	    	quit(in,out);
	    	                
	    

	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
  }
 
 
 	public String getReturn(BufferedReader in) {
 		String line = "";
 		 try {
 			 line = in.readLine();
 			 if(showLog) {
 				 System.out.println("����������״̬��"+ line);
 			 }
 		 }catch(Exception e) {
 				 e.printStackTrace();
 			 }
 		return line;
 		 }
 	
 	public String getResult(String line) {
 		//StringTokenizer st = new StringTokenizer(line,"");
 		
 		String str = line.substring(0, 3);
 		return str;
 	}
  
 	public String sendServer (String str,BufferedReader in,BufferedWriter out) throws IOException {
 		out.write(str);
 		out.newLine();
 		out.flush();
 		if(showLog) {
 			System.out.println("�ѷ������"+str);
 			
 		}
 		return getReturn(in);
 	}
 	
 	//����user����
 	public void user(String user,BufferedReader in,BufferedWriter out)throws IOException{
 		String result = null;
 		result = getResult(getReturn(in));
 		
 		if(!"+OK".equals(result)) {
 			throw new IOException("���ӷ�����ʧ�ܣ�");
 			
 		}
 			
 		//����pop3��user����  ע���ʽΪ  user<SP>username<CRLF>
 		result = getResult(sendServer("user "+user, in, out));
 		
 		if(!"+OK".equals(result)) {
 			throw new IOException("�û�������");
 			
 		}
 	}
 	
 	//����pass����
 	public void pass(String password,BufferedReader in,BufferedWriter out) throws IOException {
 		String result = null;
 		
 		//����pop3��pass����  ע���ʽΪ  pass<SP>password<CRLF>
 		result = getResult(sendServer("pass "+password, in, out));
 		
 		if(!"+OK".equals(result)) {
 			throw new IOException("�������");
 			
 		}
	}
 	
 	//����stat����
 	public int stat(BufferedReader in,BufferedWriter out) throws IOException{
 		  
        String result = null;
 
        String line = null;
  
        int mailNum = 0;
 
        line=sendServer("stat",in,out); 
 
        StringTokenizer st=new StringTokenizer(line," ");
  
        result=st.nextToken();
  
        if(st.hasMoreTokens())
 
            mailNum=Integer.parseInt(st.nextToken());
 
        else{
            
            mailNum=0;
            
        }
        
        if(!"+OK".equals(result)){
 
            throw new IOException("�鿴����״̬����!");
        }
 
        System.out.println("�����ʼ�"+mailNum+"��");
        return mailNum;
 }

 	//�޲���list����
    public void list(BufferedReader in,BufferedWriter out) throws IOException{
 
        String message = "";
 
        String line = null;
 
        line=sendServer("list",in,out); 
 
        while(!".".equalsIgnoreCase(line)){
   
            message=message+line+"\n";    
        
            line=in.readLine().toString();
            }
        
            System.out.println(message);
 }
    
     //������list����
    public void list_one(int mailNumber ,BufferedReader in,BufferedWriter out) throws IOException{

                String result = null;
                result = getResult(sendServer("list "+mailNumber,in,out)); 
         
                if(!"+OK".equals(result)){

                    throw new IOException("list����!");
                }
         }

     //�õ��ʼ���ϸ��Ϣ
     
    public String getMessagedetail(BufferedReader in) throws UnsupportedEncodingException{
  
         String message = "";
   
         String line = null;
  
         try{
             line=in.readLine().toString();
   
             while(!".".equalsIgnoreCase(line)){
    
                 message=message+line+"\n";
    
                 line=in.readLine().toString();
             }
         }catch(Exception e){
   
             e.printStackTrace();
         }
     
             return message;
     }
  
     //retr����
    public void retr(int mailNum,BufferedReader in,BufferedWriter out) throws IOException, InterruptedException{
   
             String result = null;
    
             result=getResult(sendServer("retr "+mailNum,in,out));
   
             if(!"+OK".equals(result)){
   
                 throw new IOException("�����ʼ�����!");
             }
             
             System.out.println("��"+mailNum+"��");
             System.out.println(getMessagedetail(in));
             Thread.sleep(3000);
     }
  
     //�˳�
    public void quit(BufferedReader in,BufferedWriter out) throws IOException{
  
         String result;
  
         result=getResult(sendServer("QUIT",in,out));
  
         if(!"+OK".equals(result)){
   
             throw new IOException("δ����ȷ�˳�");
         }
     }
  
 

 	public void send() {
    if (smtpServer == null) {
      throw new RuntimeException("smtpServer ����Ϊ��");
    }
    if (userName == null) {
      throw new RuntimeException("userName ����Ϊ��");
    }
    if (password == null) {
      throw new RuntimeException("password ����Ϊ��");
    }
    if (fromMail == null) {
      throw new RuntimeException("fromMail ����Ϊ��");
    }
    if (toMail == null || toMail.isEmpty()) {
      throw new RuntimeException("toMail ����Ϊ��");
    }
    if (content == null || toMail.isEmpty()) {
      throw new RuntimeException("content ����Ϊ��");
    }
  
    Socket socket = null;
    InputStream in = null;
    OutputStream out = null;
    try {
      socket = new Socket(smtpServer, smtpPort);
      socket.setSoTimeout(3000);
      in = socket.getInputStream();
      out = socket.getOutputStream();
    } catch (IOException e) {
      throw new RuntimeException("���ӵ� " + smtpServer + ":" + smtpPort + " ʧ��", e);
    }
  
    BufferedReaderProxy reader = new BufferedReaderProxy(new InputStreamReader(in), showLog);
    PrintWriterProxy writer = new PrintWriterProxy(out, showLog);
  
    reader.showResponse();
    writer.println("HELO " + smtpServer);
    reader.showResponse();
    writer.println("AUTH LOGIN");
    reader.showResponse();
    byte[] encodeUsernameBase64 = Base64.encodeBase64(userName.getBytes());
    writer.println(new String(encodeUsernameBase64));
    reader.showResponse();
    writer.println(new String(Base64.encodeBase64(password.getBytes())));
    reader.showResponse();
    writer.println("MAIL FROM:<" + fromMail+">");
    reader.showResponse();
    for (String mail : toMail) {
      writer.println("RCPT TO:<" + mail+">");
      reader.showResponse();
    }
    writer.println("DATA");
    writer.println("Content-Type: text/plain;charset=\"gb2312\"");
    if (subject != null) {
      writer.println("Subject:" + subject);
    }
    writer.println("From:" + fromMail);
    writer.print("To:");
    for (String mail : toMail) {
      writer.print(mail + "; ");
    }
    writer.println();
    writer.println(content);
    writer.println(".");
    writer.println();
    reader.showResponse();
    writer.println("QUIT");
    reader.showResponse();
    try {
      socket.close();
    } catch (IOException e) {
      System.err.println("�����ʼ���ɣ��ر� Socket ����" + e.getMessage());
    }
  }
  
  
  public String getSubject() {
    return subject;
  }
  
  public void setSubject(String subject) {
    this.subject = subject;
  }
  
  public String getFromMail() {
    return fromMail;
  }
  
  public void setFromMail(String fromMail) {
    this.fromMail = fromMail;
  }
  
  public String getSmtpServer() {
    return smtpServer;
  }
  
  public void setSmtpServer(String smtpServer) {
    this.smtpServer = smtpServer;
  }
  
  public String getpop3Server() {
	    return pop3Server;
  }
  
  public void setPop3Server(String pop3Server) {
	    this.pop3Server = pop3Server;
  }
  
  public int getSmtpPort() {
    return smtpPort;
  }
  
  public void setSmtpPort(int smtpPort) {
    this.smtpPort = smtpPort;
  }
  
  public String getContent() {
    return content;
  }
  
  public void setContent(String content) {
    this.content = content;
  }
  
  public List<String> getToMail() {
    return toMail;
  }
  
  public void setToMail(List<String> toMail) {
    this.toMail = toMail;
  }
  
  public String getUserName() {
    return userName;
  }
  
  public void setUserName(String userName) {
    this.userName = userName;
  }
  
  public String getPassword() {
    return password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
  
  public boolean getShowLog() {
    return showLog;
  }
  
  public void setShowLog(boolean showLog) {
    this.showLog = showLog;
  }
  
  static class PrintWriterProxy extends PrintWriter {
    private boolean showRequest;
  
    public PrintWriterProxy(OutputStream out, boolean showRequest) {
      super(out, true);
      this.showRequest = showRequest;
    }
  
    @Override
    public void println() {
      if (showRequest)
        System.out.println();
      super.println();
    }
  
    public void print(String s) {
      if (showRequest)
        System.out.print(s);
      super.print(s);
    }
  }
  
  static class BufferedReaderProxy extends BufferedReader {
    private boolean showResponse = true;
  
    public BufferedReaderProxy(Reader in, boolean showResponse) {
      super(in);
      this.showResponse = showResponse;
    }
  
    public void showResponse() {
      try {
        String line = readLine();
        String number = line.substring(0, 3);
        int num = -1;
        try {
          num = Integer.parseInt(number);
        } catch (Exception e) {
        }
        if (num == -1) {
          throw new RuntimeException("��Ӧ��Ϣ���� : " + line);
        } else if (num >= 400) {
          throw new RuntimeException("�����ʼ�ʧ�� : " + line);
        }
        if (showResponse) {
          System.out.println(line);
        }
      } catch (IOException e) {
        System.out.println("��ȡ��Ӧʧ��");
      }
    }
  
  }
}