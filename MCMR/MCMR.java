#Important modules of the mini-project- Enforcing Minimum Cost Multicast Routing against Selfish Information Flows
#Enforcing minimum cost multicast routing against selfish information flows

/* Setting database connection */
static void dbconnect()
{
    try
    {
        Class.forName("sun.jdbc.odbc.JdbcOdbcdriver");
        conn = DriverManager.getConnection("jdbc:odbc:MCMR","","");
        conn1 = DriverManager.getConnection("jdbc:odbc:MCMR","","");
        conn2 = DriverManager.getConnection("jdbc:odbc:MCMR","","");
        conn3 = DriverManager.getConnection("jdbc:odbc:MCMR","","");
        conn4 = DriverManager.getConnection("jdbc:odbc:MCMR","","");
        System.out.println("Connected to the database");
    }
    catch
    {
        System.out.println("DB Connection error:" +e);
        
        
    }

}

/*Sending Message*/
try
    {
        senddata = "Node3"+":"+jtf1.getText().trim()+":"+"20"+"----"+cb1+"&"+cb2+"&"+bc3+"&";
        senddata = senddata.trim();
        Socket ds = new Socket("localhost",2001);
        DataInputStream din = new DataInputStream(ds.getInputStream());
        DataOutputStream dout = new DataInputStream(ds.getInputStream());
        dout.writeUTF("node22dest");
        dout.writeUTF("senddata");
        System.out.println("Data send"+senddata);
    }
catch(Exception e)
    {
        System.out.println(e);
        e.printStackTrace();
    }

/*Sending an attachment*/
if(ob == jb3)
{
    System.out.println("browse");
    JFileChooser jf = new JFileChooser();
    int m = jf.showOpenDialog(null);
    if(m == jFilechooser.APPROVE_OPTION)
    {
        f = jf.getSelectedFile();
        path = f.getAbsolutePath();
        System.out.println(path)
    }
    try
    {
        FileInputStream fis = new FileInputStream(path);
        byte b[] = new byte[fis.available()];
        fis.read(b);
        String str = new String(b);
        
        jtf1.setText(str);
        fis.close();
    }
    catch(Exception e1)
    {
        System.out.println(e1);
    }
}

/*Server Module*/
/*Receiving message from the sender*/

if(req.equals(node13dest))
{
    String data = din.readUTF();
    System.out.println("data is"+ data);
    String dtarr[] = data.split("---");
    String ndet = dtarr[0];
    System.out.println("node det is"+ndet);
    String dest = dtarr[1];
    System.out.println("dest is"+dest);
    String narr[] = ndet.split(":");
    node = narr[0];
    mesg = narr[1];
    nodecost = nar[2];
    String rec1 = rec[0];
    String rec2 = rec[1];
    String rec3 = rec[2];
    System.out.println("rec2"+rec2);
    System.out.println("rec3"+rec3);
    if(rec1.equals("Node2 selected"))
    {
        dest1 = "node2";
    }
    if(rec1.equals("Node3 selected"))
    {
        dest1 = "node3";
    }
    if(rec1.equals("Node4 selected"))
    {
        dest1 = "node4";
    }
    if(rec2.equals("Node2 selected"))
    {
        dest2 = "node2";
    }
    if(rec2.equals("Node3 selected"))
    {
        dest2 = "node3";
    }
    if(rec2.equals("Node4 selected"))
    {
        dest2 = "node4";
    }
    if(rec3.equals("Node2 selected"))
    {
        dest3 = "node2";
    }
    if(rec3.equals("Node3 selected"))
    {
        dest3 = "node3";
    }
    if(rec3.equals("Node4 selected"))
    {
        dest3 = "node4";
    }
    System.out.println("dest"+dest1);
    System.out.println("dest2"+dest2);
    System.out.println("dest3"+dest3);
    jbtax.setEnabled(true);
    jbsend.setEnabled(true);
    jbgraph.setEnabled(true);
}

public static String vcg(String a, String b, String c)
{
    System.out.println("in vcg calculation....");
    System.out.println("in vcg Part A"+a);
    System.out.println("in vcg Part B"+b);
    System.out.println("in vcg Part C"+c);
    int a1=Integer.parseInt(a);
    int b1=Integer.parseInt(b);
    int c1=Integer.parseInt(c);
    int sample = ((b1+0)-(c1+0));
    sampleA = Math.abs(sample);
    System.out.println("Tax of a..."+sampleA);
    int sample1 = ((a1+0)-(c1+0));
    sample1B = Math.abs(sample1);
    System.out.println("Tax of b..."+sample1B);
    int sample2 = ((a1+0)-(b1+0));
    sample2C = Math.abs(sample2);
    System.out.println("Tax of c..."+sample2c);
    int sample3 = ((a1+0)-(b1+0)-(c1+0));
    sample3D = Math.abs(sample3);
    System.out.println("Tax of d..."+sample3D);
    String all = sampleA+","+sample1B+","+sample2C+",";
    return(all);
}

/*Graph.java*/
if(source.equals("Node1"))
{
g.setColor(Color.blue);
g.drawString("Node1",25,25);
g.fillOval(25,25,12,12);
g.drawString(mina,26,45)
if(!(firstd.equals("")) && firstd.equals("node2"))
{
    g.setColor(Color.blue);
    g.drawString("Node2",250,25);
    g.fillOval(250,25,12,12);
    g.drawLine(25,25,250,25);
    System.out.println("drawn");
    g.drawString(minb,256,45);
    g.drawString("tax"+taxa,250,35);
}
else
{
    g.setColor(Color.blue);
    g.drawString("Node2",250,25);
    g.fillOval(250,25,12,12);
}

if(!(firstd.equals("")) && firstd.equals("node3"))
{
    g.setColor(Color.blue);
    g.drawString("Node3",250,175);
    g.fillOval(250,175,12,12);
    g.drawLine(25,28,250,173);
    System.out.println("drawn");
    g.drawString(minc,256,165);
    g.drawString("taxb"+taxb,260,185);
}
else
{
    g.setColor(Color.blue);
    g.drawString("Node3",250,175);
    g.fillOval(250,175,12,12);
}
}

/*Message received notification*/
while(true)
{
    DataInputStream din = new DataInputStream(s.getInputStream());
    DataOutputStream dout = new DataOutputStream(s.getOutputStream());
    req=din.readUTF();
    if(req.equals("ServiceTax"))
    {
        String txvalue= din.readUTF();
        System.out.println("txvalue"+txvalue);
        int n = JOptionPane.showConfirmDialog(panel1,"Do you want to pay"+txvalue
        +"$ to receive message?","Acknowledgment",JoptionPane.YES_NO_OPTION);
        if(n == JOptionPane.YES_OPTION)
        {
            senddata = "Yes, node3 wants to receive message";
            senddata = senddata.trim();
            dout.writeUTF(senddata);
        }
    }
    if(req.equals("Ack"))
    {
        string ack = din.readUTF();
        JOptionPane.showMessageDialog((Component)null,""+ack,"Click Ok",JOptionPane.INFORMATION_MESSAGE);
    }
    if(req.equals("serverdata"))
    {
        String data = din.readUTF();
        String sender = din.readUTF();
        System.out.println("Sender is"+sender);
        System.out.println("Received Data"+data);
        jtf2.setText(data);
        System.out.println("Received Data"+data);
        if(sender.equals(node1))
        {
            try
            {
                Socket sc = new Socket("localhost",2000);
                DataInputStream din = new DataInputStream(s.getInputStream());
                DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                dout.writeUTF("Ack");
                dout1.writeUTF("Data received");
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
        }
    }
                
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
        
    
        
        

    


            

