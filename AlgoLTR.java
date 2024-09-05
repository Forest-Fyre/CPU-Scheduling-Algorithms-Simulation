/*
* AlgoLTR.java
* Modified : 27/08/2023
*
* This class represents the implementation of the Lottery CPU scheduling
* proportional-share algorithm, using a process time quantum of 3
* This file is used in conjunction with Assignment1 for COMP2240.
*/

import java.util.*;

public class AlgoLTR {
  private LinkedList<Integer> randNums; //stores random lottery nums for usage
  private LinkedList<myProcess> startList; //stores initial process
  private LinkedList<myProcess> finishList; //stores completed processes
  private myProcess currentP; //stores selected running process
  private int time; //stores time to tack algorithm lifetime
  private int DIP; //stores global dispatcher time unit

  //default constructor
  public AlgoLTR() {
    randNums = new LinkedList<>();
    startList = new LinkedList<>();
    finishList = new LinkedList<>();
    time = 0;
    DIP = 0; 
    currentP = new myProcess();
  }

  //constructor with parameters
  public AlgoLTR(LinkedList<Integer> newRandNums, int newTime, int newDIP,
      LinkedList<myProcess> newStartList, LinkedList<myProcess> newEndList,
      myProcess newCurrentP) {
    randNums = newRandNums;
    startList = newStartList;
    finishList = newEndList;
    time = newTime;
    DIP = newDIP;
    currentP = newCurrentP;
  }

  //main function that algorithm runs through
  public void algoMain() {
    System.out.println("LTR:");
    time = 0; //sets time to 0 to track algorithm lifetime
    int initStartSize = startList.size();
    int totalTickets = getTotalTickets(startList); //adds up all tickets 
    while (initStartSize != finishList.size()) {
      //checks the currentProcess, adjusting attributes & status
      time = checkCurrent(time);
      //checks startList, reselecting current proces if needed
      time = checkList(time, totalTickets);
      //increments waiting values for startList processes
      incrementListWaiting(startList, time);
    }
    printResults();
  }

  //Iterates through processes & adds up their ticket attribute values
  public int getTotalTickets(LinkedList<myProcess> sList) {
    int totalT = 0; //value incremented with ticket nums
    if (sList == null) return totalT;
    Iterator<myProcess> iter = sList.iterator();
    while (iter.hasNext()) {
      myProcess current = iter.next();
      totalT += current.getTickets();
    }
    return totalT;
  }

  // checks that parameter proces is ready to be selected as currentP
  public boolean processReady(myProcess currentLP, int time) {
    if(currentLP == null || currentLP.getArrTime() > time) return false;
    return true;
  }

  //returns the top element from the randNum list
  public int generateNum(int time) {
    if (randNums == null || randNums.isEmpty()) return time;
    int head = randNums.removeFirst();
    randNums.add(head); //switch head to end of list
    return head;
  }

  //Iterate through start list, calculating a lottery winner if currentP null
  public int checkList(int time, int totalTickets) {
    if(currentP == null && startList != null && !startList.isEmpty()) {
      int randNum = generateNum(time); //takes random num from randNum list
      int winner = randNum ;//% totalTickets; //winner from randNum mod tickets
      int counter = 0; //counter to track when winner found

      ListIterator<myProcess> iter = startList.listIterator();
      //while loop continues, until lottery winner found
      while (true) {
        if(!iter.hasNext()) iter = startList.listIterator();
        myProcess currentLP = iter.next();
        if(processReady(currentLP, time)) {
          counter = counter + currentLP.getTickets();
          //If lottery winner found, select as current process
          if(counter > winner) {
            currentP = currentLP;
            iter.remove();
            currentP.setQtTime(3);
            time = time + DIP;
            //outPrint new current process to terminal
            System.out.println("T"+time+": " + currentP.getPID());
            break;
          }
        }
      }
    }
    return time;
  }

  //increments all of a queue element's waiting time values by 1 second
  public void incrementListWaiting(LinkedList<myProcess> cPList, int time) {
    if (cPList == null) return;
    Iterator<myProcess> iter = cPList.iterator();
    while (iter.hasNext()) {
      myProcess currentLP = iter.next();
      if(currentLP.getArrTime() <= time) {
        int waitTime = currentLP.getWaitingTime() + 1;
        currentLP.setWaitingTime(waitTime);
      }
    }
  }

  //checks the current running process during algorithm, adjusting its status
  //incrementing served service time & quantum time
  public int checkCurrent(int time) {
    if (currentP == null) return time;
    //increment served time by 1
    int currentST = currentP.getServedTime() + 1;
    currentP.setServedTime(currentST);
    //increment process time quantum by 1
    int currentQT = currentP.getQtTimeSrved() + 1;
    currentP.setQtTimeSrved(currentQT);
    time = time + 1;

    if (currentP.getSrvTime() == currentST) {
      currentP.setEndTime(time);
      finishList.add(currentP);
      currentP = null;
    } else if (currentQT == currentP.getQtTime()) {
      currentP.setQtTimeSrved(0);
      startList.add(currentP);
      currentP = null;
    }
    return time;
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
