/*
* AlgoFBV.java
* Modified : 27/08/2023
*
* This class represents the implementation of the Standard Multi-level feedback
* CPU scheduling algorithm, with queue time quants of 2,4,4 (in priority order)
* This file is used in conjunction with Assignment1 for COMP2240.
*/
import java.util.*;

public class AlgoFBV {
  private LinkedList<myProcess> startList;  //list at beginning of algo
  private LinkedList<myProcess> highPriorityList; //top FB queue
  private LinkedList<myProcess> midPriorityList;  //middle FB queue
  private LinkedList<myProcess> lowPriorityList;  //bottom FB queue
  private LinkedList<myProcess> finishList; //list at end of algo
  private myProcess currentP; //holds the currently processing P
  private int DIP;
  private int time;


  //default constructor
  public AlgoFBV() {
    startList = new LinkedList<>();
    highPriorityList = new LinkedList<>();
    midPriorityList = new LinkedList<>();
    lowPriorityList = new LinkedList<>();
    finishList = new LinkedList<>();
    DIP = 0;
    time = 0;
  }

  //constructor with parameters
  public AlgoFBV(LinkedList<myProcess> newSList, LinkedList<myProcess> newFList,
      LinkedList<myProcess> newHPList, LinkedList<myProcess> newMPList,
      LinkedList<myProcess> newLPList, int newDIP, int newTime) {
    startList = newSList;
    finishList = newFList;
    highPriorityList = newHPList;
    midPriorityList = newMPList;
    lowPriorityList = newLPList;
    DIP = newDIP;
    time = newTime;
  }

  //main function that algorithm runs through
  public void algoMain() {
    System.out.println("FBV:");
    time = 0;   //sets time to 0 to track algorithm lifetime
    int initStartSize = startList.size();
    while (initStartSize != finishList.size()) {
      //check arrival times for start list
      time = checkCurrentP(time);
      //move arrived processes to high priority list
      listArrTimeCheck(time, startList, highPriorityList);
      //iterate through priority queues to select a process to run
      time = checkPriorityList(0, time);
      time = checkPriorityList(1, time);
      time = checkPriorityList(2, time);
      //check if lowP queue elements have been there longer then 16 secs
      //move to highP queue if so
      listAgingCheck(lowPriorityList, highPriorityList);
      //increment all queue element's waiting time values
      incrementListWaiting(highPriorityList);
      incrementListWaiting(midPriorityList);
      incrementListWaiting(lowPriorityList);
      //increment aging values for lowP queue elements
      incrementListAging(lowPriorityList);
    }
    //prints the results of the algorithm
    printResults();
  }

  //sorts finishedList by process ID
  public void sortQueuebyPID() {
    //put in Queue in list to use collections framework
    Collections.sort(finishList, new Comparator<myProcess>() {
      @Override
      public int compare(myProcess firstP, myProcess secondP) {
        String[] firstID = firstP.getPID().split("p");
        int idNum1 = Integer.parseInt(firstID[1].trim());
        String[] secondID = secondP.getPID().split("p");
        int idNum2 = Integer.parseInt(secondID[1].trim());
        return Integer.compare(idNum1, idNum2);
      }
    });
  }

  //prints the process results of the algorithm (TurnAround & Waiting times)
  public void printResults() {
    sortQueuebyPID();
    System.out.println("Process   Turnaround Time   Waiting Time");
    for (myProcess process : finishList) {
      int taTime = process.getEndTime() - process.getArrTime();
      int wTime = taTime - process.getSrvTime();
      System.out.printf("%-9s %-17d %-12d%n", process.getPID(), taTime, wTime);
    }
  }

  //checks the current running process during algorithm, adjusting its status
  //incrementing served service time & quantum time
  public int checkCurrentP(int time) {
    if (currentP == null) return time; //if current process null return early
    //increment served time by 1
    int currentST = currentP.getServedTime() + 1;
    currentP.setServedTime(currentST);
    //increment process time quantum by 1
    int currentQT = currentP.getQtTimeSrved() + 1;
    currentP.setQtTimeSrved(currentQT);
    time = time + 1;

    //if service time is complete, remove from current status & add to end List  
    if (currentP.getSrvTime() == currentST) {
      currentP.setEndTime(time);
      finishList.add(currentP);
      currentP = null;
    //if quant time complete, remove from current status & add back to pQueue
    } else if (currentQT == currentP.getQtTime()) {
      currentP.setQtTimeSrved(0);
      //adjust priority queue for required priority level
      int priorityLvl = currentP.getPriorityLvl();
      if(priorityLvl == 0) midPriorityList.add(currentP);
      else lowPriorityList.add(currentP);
      currentP = null;
    }
    return time;
  }

  //Goes through PriorityList, Reselecting currentP if empty/null
  public int checkPriorityList(int level, int time) {
    LinkedList<myProcess> currentList = null;
    int qtTime = 0;

    //set proper attributes for priority lists
    if(level == 0) {
      currentList = highPriorityList;
      qtTime = 2;
    } else if (level == 1) {
      currentList = midPriorityList;
      qtTime = 4;
    } else {
      currentList = lowPriorityList;
      qtTime = 4;
    }
    
    if(currentP == null && currentList != null && !currentList.isEmpty()) {
      //grab process & set current and remove from list
      currentP = currentList.getFirst();
      currentList.removeFirst();
      //set needed attributes 
      currentP.setPriorityLvl(level);
      currentP.setAgeTime(0);
      currentP.setQtTime(qtTime);
      time = time + DIP;
      //Print to terminal when new process is selected as current
      System.out.println("T"+time+": " + currentP.getPID());
    }
    return time;
  }

  //move arrived processes to high priority list 
  public void listArrTimeCheck(int time, LinkedList<myProcess> sList,
      LinkedList<myProcess> fList) {
    if (sList == null) return;
    Iterator<myProcess> iter = sList.iterator();
    //iterate through start queue
    while (iter.hasNext()) {
      myProcess currentFP = iter.next();
      int arrTimeFP = currentFP.getArrTime();
      if (currentFP.getArrTime() <= time) {
        iter.remove();
        currentFP.setStrtTime(time);
        fList.add(currentFP);
      }
    }
  }


  //check if lowP queue elements have been there longer then 16 secs
  //move element to highP queue if true
  public void listAgingCheck(LinkedList<myProcess> lPList, LinkedList<myProcess> hPList) {
    if (lPList == null) return;
    Iterator<myProcess> iter = lPList.iterator();
    while (iter.hasNext()) {
      myProcess currentLP = iter.next();
      if(currentLP.getAgeTime() > 16) {
        iter.remove();
        currentLP.setAgeTime(0);
        hPList.add(currentLP);
      }
    }
  }

  //increments all of a queue element's waiting time values by 1 second
  public void incrementListWaiting(LinkedList<myProcess> cPList) {
    if (cPList == null) return;
    Iterator<myProcess> iter = cPList.iterator();
    while (iter.hasNext()) {
      myProcess currentLP = iter.next();
      int waitTime = currentLP.getWaitingTime() + 1;
      currentLP.setWaitingTime(waitTime);
    }
  }

  //increments age time attributes for priority queue elements
  public void incrementListAging(LinkedList<myProcess> lPList) {
    if(lPList == null) return;
    Iterator<myProcess> iter = lPList.iterator();
    while (iter.hasNext()) {
      myProcess currentLP = iter.next();
      int ageTime = currentLP.getAgeTime() + 1;
      currentLP.setAgeTime(ageTime);
    }
  }

  //calculates the average wait time across all processes
  public double getAvgWT() {
    int processCount = 0;
    double totalWT = 0;
    for (myProcess process : finishList) {
      int taTime = (process.getEndTime() - process.getArrTime());
      totalWT += taTime - process.getSrvTime();
      processCount++;
    }
    return totalWT / processCount;
  }

  //calculates the average turnaround time across all processes
  public double getAvgTT() {
    int processCount = 0;
    double totalTT = 0;
    for (myProcess process : finishList) {
      totalTT += (process.getEndTime() - process.getArrTime());
      processCount++;
    }
    return totalTT / processCount;
  }
}
