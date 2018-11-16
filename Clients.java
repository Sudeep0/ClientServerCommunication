//Author: Sudeep Neupane
//Device: Macbook Pro i5


package com.company;
import java.util.*;
import java.io.*;
import java.net.Socket;

//Main function for the client
public class Main
{
    public static BufferedReader reader = new BufferedReader (new InputStreamReader (System.in));
    public static void main(String[] args) throws IOException, InterruptedException {
        String ipaddress;
        String portnumber;
        //Retrieve the ip address and port of the server
        System.out.println("Please enter the ipaddress");
        ipaddress= reader.readLine();
        System.out.println("Please enter the portnumber");
        portnumber= reader.readLine();
        // Change the strint port number into the integer
        Socket client = new Socket (ipaddress, Integer.parseInt(portnumber));
        //Start 
        while (true) {

            firstPage (client, "");



        }
    }

    
    
    //New User
    public static void firstPage(Socket s,String start) throws IOException {
        Socket sockets=s;
        InputStream in = s.getInputStream ();
        DataInputStream enter = new DataInputStream (in);
        OutputStream out = s.getOutputStream ();
        DataOutputStream output = new DataOutputStream (out);
        //Use of UTF type to communicate
        System.out.println (start+enter.readUTF ());
        System.out.println ("How can I help you today?");
        String c = reader.readLine ();
        String a="";

        output.writeUTF (c);
        boolean set = true;
        System.out.println (enter.readUTF ());
        String reply=null;
        //Use of switch statement for the possible cases
        switch (c){
            //Case 1 to read the username, password and check the validation
            case "1":
                while (set==true) {
                    System.out.println ("Enter the username");
                    a = reader.readLine ();
                    while (!checkUsername (a)) {
                        System.out.println ("Username can only be alphanumeric.Please enter again.");
                        a = reader.readLine ();
                    }


                    System.out.println ("Enter the password");
                    String b = reader.readLine ();
                    while (!checkPassword (b)) {
                        System.out.println ("Password format not supported. Please enter again with no asterisk");
                        b = reader.readLine ();
                    }
                    output.writeUTF (a + "*" + b);
                    a=enter.readUTF ();
                    if (a.equals ("CREATEFAILED")) {
                        set = true;
                    } else
                        set = false;
                }
                reply=a;
                break;
            case "2":
                int i=0;
                while (set==true) {
                    System.out.println ("Enter the username");
                    a = reader.readLine ();
                    while (!checkUsername (a)) {
                        System.out.println ("Username can only be alphanumeric.Please enter again.");
                        a = reader.readLine ();
                    }


                    System.out.println ("Enter the password");
                    String b = reader.readLine ();
                    while (!checkPassword (b)) {
                        System.out.println ("Password format not supported. Please enter again with no asterisk");
                        b = reader.readLine ();
                    }
                    output.writeUTF (a+"*"+b);
                    reply=enter.readUTF ();
                    String[] exUser=a.split("\\*");
                    i++;
                    if ((exUser[0].equals ("LOGINERROR"))&i<3) {
                        set = true;
                    }
                    else if(exUser[0].equals("LOGINERROR")&i==3){
                        firstPage (sockets,exUser[0]);
                    }
                    else
                        set = false;
                }
                break;

            case "3":

                System.out.println(enter.readUTF ());
                s.close ();
                break;
        }
        page(sockets,reply);


    }


    
    
    
    public static void page(Socket s, String str) throws IOException {
        Socket sockets =s;
        String filename;
        String reply=str;
        InputStream in = s.getInputStream ();
        DataInputStream input = new DataInputStream (in);
        OutputStream out = s.getOutputStream ();
        DataOutputStream response = new DataOutputStream (out);
        System.out.println(str);
        String a=reader.readLine ();
        if(a.equals("1")) {
            response.writeUTF (a);
            String fd=input.readUTF ();
            if(fd.equals("READY")) {
                System.out.println ("Enter the filename");
                filename = reader.readLine ();
                response.writeUTF (filename);
                String[] found=input.readUTF ().split ("\\*");
                if((found[0]).equals ("FOUND")) {
                    response.writeUTF ("READY");
                    recieve (s, filename);
                    response.writeUTF ("DOWNLOADCOMPLETED");
                    page (sockets,reply);
                }
                else if(found[0].equals ("FILEDOWNLOADFAILED")){
                    page (sockets,found[1]);

                }
            }


        }
        else if(a.equals("2"))
        {
            response.writeUTF (a);
            if(input.readUTF ().equals ("READY")) {
                System.out.println ("Enter the filename");
                filename= reader.readLine ();
                int i=0;
                while(haveFile (filename)==false&i<2)
                {
                    System.out.println ("Re-enter the filename");
                    filename= reader.readLine ();
                    i++;
                }
                if(i==2)
                {
                    response.writeUTF ("ERROR");
                    page (sockets,reply);
                }
                response.writeUTF (filename);
                if(input.readUTF ().equals("CONTINUE")) {
                    send (s, filename);

                }
            }
        }
        else if(a.equals("3"))
        {
            response.writeUTF(a);
            String fileList=input.readUTF ();
            if(fileList!=null) {
                System.out.println (input.readUTF ());
                response.writeUTF ("SUCCESSFUL");
            }
            else{
                System.out.println ("No files.");

            }

        }



    }
    //If the file already exists
    public static boolean haveFile(String filename) throws IOException
    {
        try {
            File newFile = new File ("/Users/sudeepneupane/Clients/src/com/company/"+filename);
            Scanner input=new Scanner (newFile);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Not Found. Sorry");
            return false;
        }
        return true;
    }


    public static void recieve(Socket client,String filename) throws IOException {
        File file = new File("/Users/sudeepneupane/Clients/src/com/company/"+filename);
        long length = file.length();
        byte[] bytes = new byte[1000000];
        FileOutputStream in = new FileOutputStream (file);
        DataInputStream out = new DataInputStream (client.getInputStream());
        out.read (bytes);
        in.write(bytes);

        System.out.println("File Recieved Successfully");

    }


    //Read and write from and to the file
    public static void send(Socket sock, String filename) throws IOException {

        File newFile=new File("/Users/sudeepneupane/Clients/src/com/company/"+filename);
        byte [] mybytearray  = new byte [(int)newFile.length()];
        FileInputStream fis = new FileInputStream(newFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(mybytearray,0,mybytearray.length);
        DataOutputStream os = new DataOutputStream (sock.getOutputStream());
        os.write(mybytearray,0,mybytearray.length);
        os.flush();
        System.out.println("File transmission successful");

    }
    //Validation
    public static boolean checkUsername(String a) {
        boolean isAlphaNumeric = a != null &&
                a.chars().allMatch(Character::isLetterOrDigit);
        return isAlphaNumeric;
    }
    //Password Validation
    public static boolean checkPassword(String a)
    {

        if(a.contains ("*")) {
            return false;
        }
        return true;
    }
}

