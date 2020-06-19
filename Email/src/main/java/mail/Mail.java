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
    //标准输入流

    
    /*
     * 用户输入邮箱账号
     */
    System.out.println("请输入你的邮箱账号");
    String from=in.nextLine();  //邮箱账号
//    String from="2737793330@qq.com";
    
    mail.setFromMail(from);
    mail.setUserName(from);
    
    
    /*
     * 辨别出用户输入的是什么类型的邮箱
     */
    String[] strArr = from.split("@");
    String temp = strArr[1];
    String[] serverName=temp.split("\\.");
    
    
    /*
     * 初始化smtp服务器地址
     */
    String smtpServer = "smtp."+serverName[0]+".com";
    mail.setSmtpServer(smtpServer);
    
    
    /*
     * 初始化pop3服务器地址
     */
    String pop3Server = "pop."+serverName[0]+".com";
    mail.setPop3Server(pop3Server);
    
    
    /* SMTP 服务器端口（ qq默认：587   其他：25） */
    if(serverName[0].equals("qq"))
    	mail.setSmtpPort(587);
    else
    	mail.setSmtpPort(25);

    
    /*
     * 用户输入邮箱密码（校验码）
     * 确保邮箱已经开启smtp/imap服务
     */
    System.out.println("请输入你的邮箱密码（校验码）");
    String password = in.nextLine();
//    String password = "lipbeahsvaghdggc" ;
    mail.setPassword(password);
    
    
    
    System.out.println("发送邮件请选1\n接受邮件请选2");
    String choice = in.nextLine();
    
    
    //发送邮件
    if(choice.equals("1")) {
    	/*
         * 输入receiver的邮箱
         * */
        System.out.println("请输入你要发送的邮箱地址：");
        String ToMail = in.nextLine(); 
        mail.addToMail(ToMail);
        
        
        /*
         * 输入邮箱标题
         * */
        System.out.println("请输入邮件标题：");
        String subject = in.nextLine(); 
        mail.setSubject(subject);
        
        
        /*
         * 输入邮件正文
         * */
        System.out.println("请输入邮件正文：");
        String content = in.nextLine(); 
        mail.setContent(content);
        
        mail.setShowLog(true);
        mail.send();
        System.out.println("程序结束");
    }
    
    //接收邮件
    else if(choice.equals("2")) {
    	mail.receive();
    }
    
    
    else {
    	 System.out.println("输入错误    \n程序结束");
    }
  }
  
  /** 邮件主题 **/
  private String subject;
  /** 从此地址发出 **/
  private String fromMail;
  /** 用户名 **/
  private String userName;
  /** 登录密码 **/
  private String password;
  /** SMTP 服务器地址 **/
  private String smtpServer;
  /** POP3 服务器地址 **/
  private String pop3Server;
  /** SMTP 服务器端口（163默认：25  qq默认：587） **/
  private int smtpPort ;
  /** POP3 服务器端口  默认110 **/
  private int pop3Port = 110;
  /** 发送到 toMail 中的所有地址 **/
  private List<String> toMail;
  /** 邮件内容 **/
  private String content;
  /** 是否显示日志 **/
  private boolean showLog = true;
  
  public void addToMail(String mail) {
    if (toMail == null)
      toMail = new ArrayList<String>();
    toMail.add(mail);
  }
  
  
  public void receive(){
	 
	 if (pop3Server == null) {
	      throw new RuntimeException("pop3Server 不能为空");
	    }
	 if (userName == null) {
	      throw new RuntimeException("userName 不能为空");
	    }
	    if (password == null) {
	      throw new RuntimeException("password 不能为空");
	    }
	    
	    Socket socket = null;
	    try {
	      socket = new Socket(pop3Server, pop3Port);
	      socket.setSoTimeout(3000);
	    } catch (IOException e) {
	      throw new RuntimeException("连接到 " + pop3Server + ":" + pop3Port + " 失败", e);
	    }
	    
	    
	    try {
	    	BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    	BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	    	
	    	
	    	user(userName ,in,out);	    	
	    	pass(password,in,out);//输入密码    	
	    	stat(in,out);	    	                
	    	list(in,out);

            Scanner inPut = new Scanner(System.in);
	    	               
	    	System.out.println("你想要下载哪封邮件？");
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
 				 System.out.println("服务器返回状态："+ line);
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
 			System.out.println("已发送命令："+str);
 			
 		}
 		return getReturn(in);
 	}
 	
 	//发送user命令
 	public void user(String user,BufferedReader in,BufferedWriter out)throws IOException{
 		String result = null;
 		result = getResult(getReturn(in));
 		
 		if(!"+OK".equals(result)) {
 			throw new IOException("连接服务器失败！");
 			
 		}
 			
 		//发送pop3的user命令  注意格式为  user<SP>username<CRLF>
 		result = getResult(sendServer("user "+user, in, out));
 		
 		if(!"+OK".equals(result)) {
 			throw new IOException("用户名错误！");
 			
 		}
 	}
 	
 	//发送pass命令
 	public void pass(String password,BufferedReader in,BufferedWriter out) throws IOException {
 		String result = null;
 		
 		//发送pop3的pass命令  注意格式为  pass<SP>password<CRLF>
 		result = getResult(sendServer("pass "+password, in, out));
 		
 		if(!"+OK".equals(result)) {
 			throw new IOException("密码错误！");
 			
 		}
	}
 	
 	//发送stat命令
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
 
            throw new IOException("查看邮箱状态出错!");
        }
 
        System.out.println("共有邮件"+mailNum+"封");
        return mailNum;
 }

 	//无参数list命令
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
    
     //带参数list命令
    public void list_one(int mailNumber ,BufferedReader in,BufferedWriter out) throws IOException{

                String result = null;
                result = getResult(sendServer("list "+mailNumber,in,out)); 
         
                if(!"+OK".equals(result)){

                    throw new IOException("list错误!");
                }
         }

     //得到邮件详细信息
     
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
  
     //retr命令
    public void retr(int mailNum,BufferedReader in,BufferedWriter out) throws IOException, InterruptedException{
   
             String result = null;
    
             result=getResult(sendServer("retr "+mailNum,in,out));
   
             if(!"+OK".equals(result)){
   
                 throw new IOException("接收邮件出错!");
             }
             
             System.out.println("第"+mailNum+"封");
             System.out.println(getMessagedetail(in));
             Thread.sleep(3000);
     }
  
     //退出
    public void quit(BufferedReader in,BufferedWriter out) throws IOException{
  
         String result;
  
         result=getResult(sendServer("QUIT",in,out));
  
         if(!"+OK".equals(result)){
   
             throw new IOException("未能正确退出");
         }
     }
  
 

 	public void send() {
    if (smtpServer == null) {
      throw new RuntimeException("smtpServer 不能为空");
    }
    if (userName == null) {
      throw new RuntimeException("userName 不能为空");
    }
    if (password == null) {
      throw new RuntimeException("password 不能为空");
    }
    if (fromMail == null) {
      throw new RuntimeException("fromMail 不能为空");
    }
    if (toMail == null || toMail.isEmpty()) {
      throw new RuntimeException("toMail 不能为空");
    }
    if (content == null || toMail.isEmpty()) {
      throw new RuntimeException("content 不能为空");
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
      throw new RuntimeException("连接到 " + smtpServer + ":" + smtpPort + " 失败", e);
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
      System.err.println("发送邮件完成，关闭 Socket 出错：" + e.getMessage());
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
          throw new RuntimeException("响应信息错误 : " + line);
        } else if (num >= 400) {
          throw new RuntimeException("发送邮件失败 : " + line);
        }
        if (showResponse) {
          System.out.println(line);
        }
      } catch (IOException e) {
        System.out.println("获取响应失败");
      }
    }
  
  }
}