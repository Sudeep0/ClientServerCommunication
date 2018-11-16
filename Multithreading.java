//Author: Sudeep Neupane
//Device: Macbook Pro i5






package com.company;

import java.io.*;
import java.net.Socket;
interface function1 {
    void newUsers() throws IOException;
}
//Inmplementing interface
class multithreading implements Runnable {

    String Username, Password;
    public String pass=null;
    //Give the options to the client
    public String firstpage="1. New User\n2. Existing User\n3. Disconnect";
    //Further options for the existing users
    public String secondpage="1. Download\n2. Upload\n3. File List\n4. Disconnect";
    private Socket s;
    // Stream the message
    public DataOutputStream output;
    public DataInputStream input;

    public multithreading(Socket s,DataOutputStream response,DataInputStream input)  {
        //Super
        this.s=s;
        this.output=response;
        this.input=input;

    }

    public void run() {

        try {
            String str="";
            firstWin(str);
        }



        catch (IOException e1) {
            e1.printStackTrace ();
        }





    }


    public void firstWin(String strings) throws IOException {
        //DataOutputStream response=new DataOutputStream (s.getOutputStream ());
        String newUser="Please send new id and password!!!";
        output.writeUTF (strings+firstpage);
        String ans=input.readUTF ();
        int answer=Integer.parseInt (ans);
        //Implement the switch function for the given cases
        switch(answer) {
            //Case 1 for the new User
            case 1:

                output.writeUTF ("READY" + "*" + newUser);
                pass=newUsers ();
                Username=getUsername (pass);
                secondWin (Username,pass);
                break;

            case 2:
                int i = 0;
                output.writeUTF ("READY");
                pass = input.readUTF ();
                Username = getUsername (pass);
                while ((userValidation (pass) == false) & i < 2) {

                    output.writeUTF ("LOGINERROR");
                    pass = input.readUTF ();

                    i++;
                }
                //Check the user input value
                if ((userValidation (pass) == false)&i==2) {
                    output.writeUTF("LOGINERROR");
                    firstWin ("READY"+"*");

                }
                break;

            //case 3 for the exiting clients
            case 3:
                output.writeUTF ("Bye bye");
                input.close ();
                output.close ();
                s.close ();
                break;


        }
        //Pass the username and the password
        secondWin(Username,pass);
    }
    //For the existing users
    public void secondWin(String uname,String upass) throws IOException{
        int answer = 0;
        Username=uname;
        pass=upass;


        //Send the message
        output.writeUTF (secondpage);
        answer = Integer.parseInt (input.readUTF ());
        //Switch statement for the given cases
        switch(answer)
        {
            case 1:
                output.writeUTF ("READY");
                String filename=input.readUTF ();
                if(fileExist (filename,Username)==true)
                {
                    output.writeUTF ("FOUND");
                    if(input.readUTF().equals ("READY"))
                        send(s,Username,filename);
                }
                else{
                    output.writeUTF ("FILEDOWNLOADFAILED"+"*"+secondpage);
                }
                if(input.readUTF ().equals (""))
                    secondWin (Username,pass);
                break;


            case 2:
                output.writeUTF ("READY");
                String fu=input.readUTF ();
                if(fu.equals ("ERROR")){
                    secondWin(Username,pass);
                }
                else {

                    if ((filename = fu) != null) {
                        int i=1;
                        while(fileExist (filename,Username)==true){
                            filename=filename+"_copy_"+i+".txt";
                            i++;

                        }
                        output.writeUTF ("CONTINUE");
                        recieve (s, Username, filename);


                    }
                }
                break;

            case 3:

                BufferedReader outfile=new BufferedReader (new FileReader ("/Users/sudeepneupane/IdeaProjects/Project3/account.txt"));
                String line=outfile.readLine ();
                while(line!=null){
                    if(line.contains (pass)) {
                        String[] files = line.split (" ");
                        int i = 1;
                        line = "";
                        for (i=1;i<(files.length);i++) {

                            line += files[i] + " ";

                        }
                        output.writeUTF (line);
                        if(input.readUTF ().equals ("SUCCESSFUL")){
                            secondWin(Username,pass);

                        }

                    }
                    else {
                        line = outfile.readLine ();
                    }
                }
                break;

            case 4:
                output.writeUTF ("Disconnected");
                input.close ();
                output.close ();
                s.close ();
                break;
        }






    }
    //Retrieve the username and password for the new users
    public String getUsername(String password){
        String[] user=password.split ("\\*");
        Username=user[0];
        return Username;
    }
    public String newUsers() throws IOException {
        pass = input.readUTF ();
        Username = getUsername (pass);
        while (checkUsername (Username)) {
            output.writeUTF ("CREATEFAILED");

            newUsers ();
        }

        return pass;
    }


    //Check if the username exists
    public boolean checkUsername(String username) throws IOException {
        BufferedReader outfile=new BufferedReader (new FileReader ("/Users/sudeepneupane/IdeaProjects/Project3/account.txt"));
        String line=outfile.readLine ();
        while(line!=null){

            String[] files = line.split (" ");
            if(files[0].contains (username)){
                return true;
            }
            line=outfile.readLine ();

        }
        return false;
    }
    //Checking the existence of the file
    public boolean fileExist(String filename,String username) throws IOException
    {
        BufferedReader infile=new BufferedReader (new FileReader ("/Users/sudeepneupane/IdeaProjects/Project3/account.txt"));
        String add;
        while((add=infile.readLine ())!=null)
        {
            if(add.startsWith (username)&add.contains (filename))
            {
                return true;
            }
        }
        return false;

    }

    public boolean userValidation(String userpassword) throws IOException {
        BufferedReader outfile=new BufferedReader (new FileReader ("/Users/sudeepneupane/IdeaProjects/Project3/account.txt"));
        String line=outfile.readLine ();
        while(line!=null){

            String[] files = line.split (" ");
            if(files[0].equals (userpassword)){
                return true;
            }
            line=outfile.readLine ();


        }
        return false;

    }
    // Method for sending and reading the file
    public void send(Socket sock, String user, String filename) throws IOException {
        new File("/Users/sudeepneupane/IdeaProjects/Project3" + user).mkdir();
        File newFile = new File("/Users/sudeepneupane/IdeaProjects/Project3" + user + "/" + filename);
        byte[] mybytearray = new byte[(int) newFile.length()];
        FileInputStream fis = new FileInputStream(newFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(mybytearray, 0, mybytearray.length);
        output.write(mybytearray, 0, mybytearray.length);
        output.flush();
        System.out.println("File Transferred");
    }
    // Method for receiving and making individual directories
    public void recieve(Socket client,String user,String filename) throws IOException {
        new File("/Users/sudeepneupane/IdeaProjects/Project3"+user).mkdir ();
        File file = new File("/Users/sudeepneupane/IdeaProjects/Project3"+user+"/"+filename);
        byte[] bytes = new byte[1000000];
        FileOutputStream in = new FileOutputStream (file);
        input.read ();
        in.write(bytes);
        System.out.println("File Recieved");

    }
}
















