/*
* A1.java
* Modified : 27/08/2023
*
* This class represents the implementation main class that all CPU scheduling
* algorithms (FCFS, SRT, FBV, & LTR) run with inputted text file data.
* This file is used in conjunction with Assignment1 for COMP2240.
*/
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class A1 {
  private LinkedList<myProcess> processList;  //process list read from file
  private LinkedList<Integer> randLottoNums;  //lottoNum list read from file
  private int DISP; //Dispatcher value read from file
  
  //default constructor
  public A1() {
    processList = new LinkedList<>();
    randLottoNums = new LinkedList<>();
    DISP = 0;
  }

  public void setDISP(int newDISP) {
    DISP = newDISP;
  }

  //returns class processList
  public LinkedList<myProcess> getProcessList() {
    return processList;
  }

  //sets class processList
  public void setProcessList(LinkedList<myProcess> newPList) {
    processList = newPList;
  }

  //sets class lottoNums
  public void setRandLottoNums(LinkedList<Integer> newLottoNums) {
    randLottoNums = newLottoNums;
  }

  //function used within fileReading(), to specifically scan the lotto nums
  public void readRandLottoNums(Scanner fReader) {
    LinkedList<Integer> newNList = new LinkedList<>();
    while(fReader.hasNext()) {
      String randLine =  fReader.nextLine().trim();
      if (randLine.equals("ENDRANDOM")) {
        this.setRandLottoNums(newNList);
        break;
      } else {
        newNList.add(Integer.parseInt(randLine));
      }
    }
  }

  //function used within fileReading(), to specifically scan processes
  public myProcess ReadProcess(Scanner fReader) {
    myProcess fProcess = new myProcess();
    while (fReader.hasNext()) {
      String line = fReader.nextLine();
      if (line.trim().equals("END")) break;
      String[] splitLine = line.split(":");
      String title = splitLine[0].trim();
      String value = splitLine[1].trim();
      if(title.equals("ArrTime")) {
        int newArrTime = Integer.parseInt(value);
        fProcess.setArrTime(newArrTime);
      } else if (title.equals("SrvTime")) {
        int newSrvTime = Integer.parseInt(value);
        fProcess.setSrvTime(newSrvTime);
      } else if (title.equals("Tickets")) {
        int newTicketCount = Integer.parseInt(value);
        fProcess.setTickets(newTicketCount);
      }
    }
    return fProcess;
  }

  //main function used to read file
  //reads dispatcher, random lottery numbers and process list
  public void fileReading(Scanner fReader) {
    LinkedList<myProcess> newPList = new LinkedList<>();
    while (fReader.hasNext()) {
      String line = fReader.nextLine().trim();
      //If end of file break from loop
      if (line.equals("EOF")) {
        this.setProcessList(newPList);
        break;
      //Accounts for random lotto numbers in file
      } else if (line.equals("BEGINRANDOM")) {
        readRandLottoNums(fReader);
      //Accounts for whitespace lines
      } else if (line.equals("") || line.equals("BEGIN") || line.equals("END")) {
        continue;
      //Accounts for Dispatcher & Process file reading
      } else {
        String[] splitLine = line.split(":");
        //Accounts for Dispatcher file reading
        if(splitLine[0].trim().equals("DISP")) {
           this.setDISP(Integer.parseInt(splitLine[1].trim())); 
        //Accounts for process file reading
        } else if(splitLine[0].trim().equals("PID")) {
          String newPID = splitLine[1].trim();
          myProcess currentProcess = ReadProcess(fReader);
          currentProcess.setPID(newPID);
          newPList.add(currentProcess);
        } 
      }
    }
  }

  //used to create deep copies of class process list, copying all process to 
  //new list. Was needed because couldnt use same list for all algorithms
  public static LinkedList<myProcess> copyProcessList(LinkedList<myProcess> ogList) {
    LinkedList<myProcess> newList = new LinkedList<>();
    for (myProcess process : ogList) {
      newList.add(process.deepCopy());
    }
    return newList;
  }

  //main method, reads file, runs algorithms, and displays results
  public static void main(String args[]) {
    //check file exists
    if (args.length < 1) {
      System.out.println("No file to scan");
      return;
    }
    String fName = args[0];
    A1 A1 = new A1(); //create instance of class
    try {
      File dataFile = new File(fName);
      Scanner fReader = new Scanner(dataFile); 
      A1.fileReading(fReader);  //reads file & 
      fReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("File Not Found");
    }

    int algoDISP = A1.DISP;   //Dispatcher value
    
    //data required for First Come First Serve Parameters
    LinkedList<myProcess> sListFCFS = copyProcessList(A1.getProcessList());
    LinkedList<myProcess> eListFCFS = new LinkedList<>();

    //creation and running of FCFS algorithm
    AlgoFCFS a1FCFS = new AlgoFCFS(sListFCFS, eListFCFS, null, algoDISP);
    a1FCFS.algoMain();
    System.out.println("\n");

    //data required for Shorest Remaining Time Parameters
    LinkedList<myProcess> sListSRT = copyProcessList(A1.getProcessList());
    LinkedList<myProcess> eListSRT = new LinkedList<>();

    //creation and running of SRT algorithm
    AlgoSRT a1SRT = new AlgoSRT(sListSRT, eListSRT, null, algoDISP);
    a1SRT.algoMain();
    System.out.println("\n");

    //data required for FBV Parameters
    LinkedList<myProcess> sListFBV = copyProcessList(A1.getProcessList()); 
    LinkedList<myProcess> eListFBV = new LinkedList<>();
    LinkedList<myProcess> hpL = new LinkedList<>();
    LinkedList<myProcess> mpL = new LinkedList<>();
    LinkedList<myProcess> lpL = new LinkedList<>();

    //creation and running of FBV algorithm
    AlgoFBV a1FBV = new AlgoFBV(sListFBV, eListFBV, hpL, mpL, lpL, algoDISP, 0);
    a1FBV.algoMain();
    System.out.println("\n");

    //data required for LTR Parameters
    LinkedList<Integer> lottoNums = A1.randLottoNums;
    LinkedList<myProcess> sListLTR = copyProcessList(A1.getProcessList());
    LinkedList<myProcess> eListLTR = new LinkedList<>();

    //creation and running of FBV algorithm
    AlgoLTR a1LTR = new AlgoLTR(lottoNums, 0, algoDISP, sListLTR, eListLTR, null);
    a1LTR.algoMain();
    System.out.println("\n");

    System.out.println("Algorithm   Average Turnaround Time   Waiting Time");
    System.out.printf("%-11s %-25.2f %-15.2f%n", "FCFS", a1FCFS.getAvgTT(), a1FCFS.getAvgWT());
    System.out.printf("%-11s %-25.2f %-15.2f%n", "SRT", a1SRT.getAvgTT(), a1SRT.getAvgWT());
    System.out.printf("%-11s %-25.2f %-15.2f%n", "FBV", a1FBV.getAvgTT(), a1FBV.getAvgWT());
    System.out.printf("%-11s %-25.2f %-15.2f%n", "LTR", a1LTR.getAvgTT(), a1LTR.getAvgWT());
    /*
    System.out.println("FCFS        "+a1FCFS.getAvgTT()+"       "+a1FCFS.getAvgWT());
    System.out.println("SRT        "+a1SRT.getAvgTT()+"       "+a1SRT.getAvgWT());
    System.out.println("FBV        "+a1FBV.getAvgTT()+"       "+a1FBV.getAvgWT());
    System.out.println("LTR        "+a1LTR.getAvgTT()+"       "+a1LTR.getAvgWT());
    */
  }
}
