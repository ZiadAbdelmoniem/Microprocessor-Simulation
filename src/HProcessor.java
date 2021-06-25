import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HProcessor {
    static String [] InstructionMemory=new String[1024]; //each block is 2 byte 1 word
    static byte [] DataMemory=new byte[2048]; //each is block is 1 byte 1 word
    static byte [] Registers=new byte[63]; //each is block is 1 byte 1 word

    // 0th=Z, 1th=S, 2th=N, 3th=V, 4th=C, 567=0, update each cycle
    static int[] SREG=new int[8];


    static short PC=0;
    static int clk_cycles=1;



    //important variables

    static String toBeDecoded="";
    static boolean toDecode=false;


    static String opToBeExec="";
    static int rsToBeExec=0;
    static int rtToBeExec=0;
    static boolean toExecute=false;


    static int noOfInstr=0;





    public static String [] fromFileToArray(String s) {
        String[] arr = null;
        List<String> itemsSchool = new ArrayList<String>();

        try {
            FileInputStream fstream_school = new FileInputStream(s);
            DataInputStream data_input = new DataInputStream(fstream_school);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(data_input));
            String str_line;

            while ((str_line = buffer.readLine()) != null) {
                str_line = str_line.trim();
                if ((str_line.length() != 0)) {
                    itemsSchool.add(str_line);
                }
            }

            arr = (String[]) itemsSchool.toArray(new String[itemsSchool.size()]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arr;
    }
    public static void executeIt(String s) throws IOException {
        String [] base=fromFileToArray(s);
        noOfInstr= base.length;
        for (int i = 0; i < base.length; i++) {
            String [] x=base[i].split("\\W+");
            executeIt2(x,i);
        }
    }
    public static void executeIt2(String[] x,int u){
        String b="";
        String instrType="";
        x[0].toUpperCase();

        //first element + identifying instruction type
        switch(x[0]){
            case "ADD":b=b+"0000"; instrType="r";
                break;
            case "SUB":b=b+"0001"; instrType="r";
                break;
            case "MUL":b=b+"0010"; instrType="r";
                break;
            case "MOVI":b=b+"0011"; instrType="i";
                break;
            case "BEQZ":b=b+"0100"; instrType="i";
                break;
            case "ANDI":b=b+"0101"; instrType="i";
                break;
            case "EOR":b=b+"0110"; instrType="r";
                break;
            case "BR":b=b+"0111"; instrType="r";
                break;
            case "SAL":b=b+"1000"; instrType="i";
                break;
            case "SAR":b=b+"1001"; instrType="i";
                break;
            case "LDR":b=b+"1010"; instrType="i";
                break;
            case "STR":b=b+"1011"; instrType="i";
                break;

            default: break;
        }

        //second element
            String g=x[1];
            String c=g.substring(1);
            int e=Integer.parseInt(c);
            String bin=Integer.toBinaryString(e);
            while(bin.length()<6){
                bin="0"+bin;
            }
//            System.out.println(bin);
            b=b+bin;

        //third element
        if(instrType.equals("r")) {
            String g1 = x[2];
            String c1 = g1.substring(1);
            int e1 = Integer.parseInt(c1);
            String bin1 = Integer.toBinaryString(e1);
            while (bin1.length() < 6) {
                bin1 = "0" + bin1;
            }
//           System.out.println(bin1);
            b = b + bin1;
        }
        else if(instrType.equals("i")){
            String g2=x[2];
            int e2=Integer.parseInt(g2);
            String e3= Integer.toBinaryString(e2);
            while(e3.length()<6){
                e3="0"+e3;
            }
            b=b+e3;
        }
            InstructionMemory[u]=b;
//System.out.println(instrType);
    }

    public static void fetch(int pc){
        String instruction =InstructionMemory[pc];
        toBeDecoded=instruction;
        toDecode=true;
        //decode(instruction);
        //PC++;
        printFetch();
    }


    public static void decode(String instruction){
        String op=instruction.substring(0,4);
        String rst=instruction.substring(4,10);
        String rtt=instruction.substring(10,16);
        int rs=Integer.parseInt(rst,2);
        int rt=Integer.parseInt(rtt,2);
        //System.out.println(instruction);
        opToBeExec=op;
        rsToBeExec=rs;
        rtToBeExec=rt;
        toExecute=true;
        //execute(op, rs, rt);
        printDecode();
    }

    public static void execute(String op, int rs, int rt){
        String f="";
        switch (op){
            case "0000": int x=Registers[rs];
            f="Add";
            System.out.println(f+" R"+rs+" R"+rt);
            int y=Registers[rt];
            byte z;
                z= (byte) (x+y);
            //updating the C flags
            if(z>127 || z<-128){
                SREG[4]=1;
            }
            else{
                SREG[4]=0;
            }
            //updating the V flag
                if((x>=0 && y>=0 && z<0)||(x<0 && y<0 && z>0)){
                    SREG[3]=1;
                }
                else{
                    SREG[3]=0;
                }
            //updating the N flag
                if(z<0){
                    SREG[2]=1;
                }
                else{
                    SREG[2]=0;
                }
            //updating the S flag
                if(SREG[3]!=SREG[2]){
                    SREG[1]=1;
                }
                else{
                    SREG[1]=0;
                }
            //updating the Z flag
                if(z==0){
                    SREG[0]=1;
                }
                else{
                    SREG[0]=0;
                }
            Registers[rs]=(byte)z;
                System.out.println("Register R"+rs+"="+Registers[rs]);
                System.out.print("SREG Changes: "); printSreg();
            break;

            case "0001":int x1=Registers[rs];
                int y1=Registers[rt];
                f="SUB";
                System.out.println(f+" R"+rs+" R"+rt);
                byte z1;
                z1= (byte) (x1-y1);
                //updating the C flags
                if(z1>127 || z1<-128){
                    SREG[4]=1;
                }
                else{
                    SREG[4]=0;
                }
                //updating the V flag
                if((x1>=0 && y1>=0 && z1<0)||(x1<0 && y1<0 && z1>0)){
                    SREG[3]=1;
                }
                else{
                    SREG[3]=0;
                }
                //updating the N flag
                if(z1<0){
                    SREG[2]=1;
                }
                else{
                    SREG[2]=0;
                }
                //updating the S flag
                if(SREG[3]!=SREG[2]){
                    SREG[1]=1;
                }
                else{
                    SREG[1]=0;
                }
                //updating the Z flag
                if(z1==0){
                    SREG[0]=1;
                }
                else{
                    SREG[0]=0;
                }
                Registers[rs]=z1;
                System.out.println("Register R"+rs+"="+Registers[rs]);
                System.out.print("SREG Changes: "); printSreg();
                break;
            case "0010":int x2=Registers[rs];
                int y2=Registers[rt];
                f="MUL";
                System.out.println(f+" R"+rs+" R"+rt);
                byte z2;
                z2= (byte) (x2*y2);
                //updating the C flags
                if(z2>127 || z2<-128){
                    SREG[4]=1;
                }
                else{
                    SREG[4]=0;
                }
                //updating the N flag
                if(z2<0){
                    SREG[2]=1;
                }
                else{
                    SREG[2]=0;
                }
                //updating the Z flag
                if(z2==0){
                    SREG[0]=1;
                }
                else{
                    SREG[0]=0;
                }
                Registers[rs]=z2;
                System.out.println("Register R"+rs+"="+Registers[rs]);
                System.out.print("SREG Changes: ");
                System.out.print(" Carry Flag="+ SREG[4]);
                System.out.print(" Negative Flag="+SREG[2]);
                System.out.print(" Zero Flag="+SREG[0]);
                break;
            case "0011": Registers[rs]=(byte)rt;
            f="MOVI";
                System.out.println(f+" R"+rs+" R"+rt);
                System.out.println("Register R"+rs+"="+Registers[rs]);
                break;
            case "0100": int xsx=Registers[rs];
           int xsy=Registers[rt];
           f="BEQZ";
                System.out.println(f+" R"+rs+" "+rt);
                if(xsx==0){
                PC= (short) (PC-1+xsy);
                toExecute=false;
                toDecode=false;
                }
                else{
                    PC++;
                }
                break;
            case "0101":int x5=Registers[rs];
                int z5 = x5 & rt;
                f="ANDI";
                System.out.println(f+" R"+rs+" "+rt);
                //updating the N flag
                if(z5<0){
                    SREG[2]=1;
                }
                else{
                    SREG[2]=0;
                }
                //updating the Z flag
                if(z5==0){
                    SREG[0]=1;
                }
                else{
                    SREG[0]=0;
                }
                Registers[rs]=(byte)z5;
                System.out.println("Register R"+rs+"="+Registers[rs]);
                System.out.print("SREG Changes: ");
                System.out.print(" Negative Flag="+SREG[2]);
                System.out.print(" Zero Flag="+SREG[0]);
                break;
            case "0110":int x6=Registers[rs];
                int z6 = rs | rt;
                f="EOR";
                System.out.println(f+" R"+rs+" R"+rt);
                //updating the N flag
                if(z6<0){
                    SREG[2]=1;
                }
                else{
                    SREG[2]=0;
                }
                //updating the Z flag
                if(z6==0){
                    SREG[0]=1;
                }
                else{
                    SREG[0]=0;
                }
                Registers[rs]=(byte)z6;
                System.out.println("Register R"+rs+"="+Registers[rs]);
                System.out.print("SREG Changes: ");
                System.out.print(" Negative Flag="+SREG[2]);
                System.out.print(" Zero Flag="+SREG[0]);
                break;
            case "0111":int xe=Registers[rs];
                int xee=Registers[rt];
                f="BR";
                System.out.println(f+" R"+rs+" "+rt);
                String xeee=Integer.toBinaryString(xe);
                String xeeee=Integer.toBinaryString(xee);
                String conc=xeee+xeeee;
                PC= (short) Integer.parseInt(conc,2);
                toDecode=false;
                toExecute=false;
                break;
            case "1000":String x7=Integer.toBinaryString(rs);
            f="SAL";
                System.out.println(f+" R"+rs+" "+rt);
            while(x7.length()<6){
                x7="0"+x7;
            }
            String s7=x7.substring(rt);
                while(s7.length()<6){
                    s7=s7+"0";
                }
                //updating the N flag
                if(Integer.parseInt(s7)<0){
                    SREG[2]=1;
                }
                else{
                    SREG[2]=0;
                }
                //updating the Z flag
                if(s7.equals("000000")){
                    SREG[0]=1;
                }
                else{
                    SREG[0]=0;
                }
                Registers[rs]= (byte) Integer.parseInt(s7);
                System.out.println("Register R"+rs+"="+Registers[rs]);
                System.out.print("SREG Changes: ");
                System.out.print(" Negative Flag="+SREG[2]);
                System.out.print(" Zero Flag="+SREG[0]);
                break;
            case "1001":String x8=Integer.toBinaryString(rs);
            f="SAR";
                System.out.println(f+" R"+rs+" "+rt);
                while(x8.length()<6){
                    x8="0"+x8;
                }
                String s8=x8.substring(0,6-rt);
                while(s8.length()<6){
                    s8=s8.charAt(0)+s8;
                }
                //updating the N flag
                if(Integer.parseInt(s8)<0){
                    SREG[2]=1;
                }
                else{
                    SREG[2]=0;
                }
                //updating the Z flag
                if(s8.equals("000000")){
                    SREG[0]=1;
                }
                else{
                    SREG[0]=0;
                }
                Registers[rs]= (byte) Integer.parseInt(s8);
                System.out.println("Register R"+rs+"="+Registers[rs]);
                System.out.print("SREG Changes: ");
                System.out.print(" Negative Flag="+SREG[2]);
                System.out.print(" Zero Flag="+SREG[0]);
                break;
            case "1010":Registers[rs]=DataMemory[rt];
                f="LDR";
                System.out.println(f+" R"+rs+" "+rt);
                System.out.println("Register R"+rs+"="+Registers[rs]);
                break;
            case "1011":DataMemory[rt]=Registers[rs];
                f="STR";
                System.out.println(f+" R"+rs+" "+rt);
                System.out.println("Memory at"+rt+"="+DataMemory[rt]);
                break;
            default: break;
        }
        printExec(f);
    }

    //pipelining
    public static void execution(){
        //int totalClk=noOfInstr+2;
        for (PC=0; PC < noOfInstr; PC++) {
            printClk();
            printPc();
            if(PC==0){
                fetch(PC);
                clk_cycles++;
            }
            else if(PC==1 && toDecode==false) {
                //decode(toBeDecoded);
                fetch(PC);
                clk_cycles++;
            }
           else if(PC==1 && toDecode==true) {
                decode(toBeDecoded);
                fetch(PC);
                clk_cycles++;
            }
             else if(toDecode==false && toExecute==false){
                //execute(opToBeExec, rsToBeExec, rsToBeExec);
                //decode(toBeDecoded);
                fetch(PC);
                clk_cycles++;
            }
            else if(toDecode==true && toExecute==false){
                //execute(opToBeExec, rsToBeExec, rsToBeExec);
                decode(toBeDecoded);
                fetch(PC);
                clk_cycles++;
            }
              else {
                execute(opToBeExec, rsToBeExec, rtToBeExec);
                decode(toBeDecoded);
                fetch(PC);
                clk_cycles++;
            }
            }
        if(toExecute){
            printClk();
            printPc();
            execute(opToBeExec, rsToBeExec, rtToBeExec);
            decode(toBeDecoded);
            clk_cycles++;
            printClk();
            execute(opToBeExec, rsToBeExec, rtToBeExec);
//            clk_cycles++;
  //          printClk();
        }
        else{
            printClk();
            printPc();
            decode(toBeDecoded);
            clk_cycles++;
            printClk();
            printPc();
            execute(opToBeExec, rsToBeExec, rtToBeExec);
            //clk_cycles++;
            //printClk();
          //  printPc();

        }
        System.out.println("Horrraaayy! the program ended!!!!!!!!");
        printRegisters();
        printDataMemory();
        printInstMemory();

        printSreg();
        //printClk();
        //printPc();
        }


    //printing statements
    public static void printRegisters(){ System.out.println("Register= "+Arrays.toString(Registers)); }

    public static void printInstMemory(){System.out.println("Instruction Memory"+Arrays.deepToString(InstructionMemory)); }

    public static void printDataMemory(){ System.out.println("Data Memory"+Arrays.toString(DataMemory)); }

    public static void printSreg(){
        // 0th=Z, 1th=S, 2th=N, 3th=V, 4th=C, 567=0, update each cycle
        System.out.print(" Carry Flag="+ SREG[4]);
        System.out.print(" Overflow Flag="+SREG[3]);
        System.out.print(" Negative Flag="+SREG[2]);
        System.out.print(" Sign Flag="+SREG[1]);
        System.out.print(" Zero Flag="+SREG[0]);
    }

    public static void printPc(){ System.out.println("PC="+PC);}

    public static void printClk(){ System.out.println("#############################################Clock Cycle= "+ clk_cycles+"#####################################");}

    public static void printFetch(){System.out.println("Fetch input="+PC);}

    public static void printDecode(){System.out.println("Decode input="+toBeDecoded);}

    public static void printExec(String f){
        if(f.equals("ADD") || f.equals("SUB") ||f.equals("MUL") ||f.equals("MOVI") ||f.equals("EOR") ||f.equals("BR"))
        System.out.println("Parameters passed to Excute: Opcode= "+opToBeExec+",Register= "+rsToBeExec+",Register= "+rtToBeExec);
        else{
            System.out.println("Parameters passed to Excute: Opcode= "+opToBeExec+" ,Register= "+rsToBeExec+" ,Immediate= "+rtToBeExec);
        }
    }




    public static void Exec(String s) throws IOException {
        executeIt(s);
        execution();
    }




    public static void main(String args[]) throws IOException {
//executeIt("test1.txt");
//      System.out.println(PC);
        Exec("test1.txt");
    }
}
