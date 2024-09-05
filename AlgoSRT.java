/*
* AlgoSRT.java
* Modified : 27/08/2023
*
* This class represents the implementation of the Shortest remaining time
* CPU scheduling algorithm, taking the process with least remaining time at 
* designated interruption time.
* This file is used in conjunction with Assignment1 for COMP2240.
*/

import java.util.*;

public class AlgoSRT {
  private Queue<myProcess> arrivalQueue; //stores init processes for scheduling 
  private Queue<myProcess> finishQueue; //stores completed/finalised processes
  private myProcess currentP; //stores the current selected P serving time
  private int time; //stores global time during scheduling process
  private int DIP; //stores the global dispatcher time unit 

  //default constructor
  public AlgoSRT() {
    arrivalQueue = new LinkedList<>();
    finishQueue = new LinkedList<>();
    currentP = null;
    DIP = 0;
  }

  //constructor with all parameters
  public AlgoSRT(Queue<myProcess> newArrivalQueue, Queue<myProcess> newReadyQueue
      , myProcess newCurrentP, int newDIP) {
    arrivalQueue = newArrivalQueue;
    finishQueue = newReadyQueue;
    currentP = newCurrentP;
    DIP = newDIP;
  }

  public void algoMain() {
    System.out.println("SRT:");
    time = 0; //sets time to 0 to track algorithm lifetime
    //sort queue for inital implementation
    this.sortAQueue();
    int aQueueSize = arrivalQueue.size();
    //repeats until the finalQueue contains the elements in arrivalQueue by
    //tracking its inital size
    while(aQueueSize != finishQueue.size()) {
      //if a new P or current P is empty search queue for replacement
      if(checkAvailableP() || currentP == null)  selectShortestP(); 
      //make adjustments to current P
      checkCurrent();     
      //if currentP has completed and now null, reselect from queue
      if(currentP == null) { 
        selectShortestP(); 
        checkCurrent();
      }
      //if a new P interrupts after new P selected replace current with SRT
      if(checkAvailableP()) {
        checkQueueWait();
        time++; //NEW LINE
        selectShortestP();
      }
      //if not just re-adjust queue waiting values
      else {
        checkQueueWait();
        time++;
      }
    }
    //print results of SRT scheduling
    printResults();
  }


  //Checks wait queue, and iterates waitingTime values if required
  public void checkQueueWait() {
      Iterator<myProcess> QIter = arrivalQueue.iterator();
      while(QIter.hasNext()) {
        myProcess iterP = QIter.next();

        if (time >= iterP.getArrTime()) {
          int currentPWT = iterP.getWaitingTime();
          currentPWT++;
          iterP.setWaitingTime(currentPWT);
        }
      }
  }

  //checks that the servedTime is equal to required service time of process
  public boolean processServiced() {
    if(currentP.getSrvTime() == currentP.getServedTime()) return true;
    return false;
  }


  //checks the current Process and adjusts its attributes as required
  public void checkCurrent() {
    if (currentP == null) return;  //if null return
    //if P finished set end time and send to finishQueue
    if (processServiced()) {
      currentP.setEndTime(time);
      finishQueue.add(currentP);
      currentP = null; //remove from current
    //if not finished, incrememnt served time by 1
    } else {
      int currentPST =  currentP.getServedTime();
      currentPST++;
      currentP.setServedTime(currentPST);
    }
  }

  //iterates through queue and checks if the currentTime triggers interruption 
  public boolean checkAvailableP() {
    if (!arrivalQueue.isEmpty()) {
      ///Iterator for queue
      Iterator<myProcess> QIter = arrivalQueue.iterator();
      while(QIter.hasNext()) {
        myProcess iterP = QIter.next();
        //current time equals P time then true
        if (time == iterP.getArrTime()) return true;
      }
    }
    return false;
  }

  public void DIPwaitAdd() {
    Iterator<myProcess> QIter = arrivalQueue.iterator();
     while (QIter.hasNext()) {
         myProcess iterP = QIter.next();
         if (iterP.getArrTime() <= time) {
            int iterWT = iterP.getWaitingTime() + DIP;
            iterP.setWaitingTime(iterWT);
         }
     }

     if (currentP != null) {
        int currentWT = currentP.getWaitingTime() + DIP;
        currentP.setWaitingTime(currentWT);
     }
  }

  public void selectShortestP() {
    if (!arrivalQueue.isEmpty()) {
      Iterator<myProcess> QIter = arrivalQueue.iterator();
      myProcess selectedProcess = currentP;

      while (QIter.hasNext()) {
          myProcess iterP = QIter.next();
          if (iterP.getArrTime() <= time) {
              if (selectedProcess == null || iterP.getRemainingSrvTime() 
                  < selectedProcess.getRemainingSrvTime()) {
                  selectedProcess = iterP;
              }
          }
      }

      if (selectedProcess != null && selectedProcess != currentP) {
          if (currentP != null) {
            arrivalQueue.add(currentP);
          }
          currentP = selectedProcess;
          arrivalQueue.remove(selectedProcess);
          DIPwaitAdd();
          if (!currentP.serviceStarted()) {
            currentP.setSrvStrted(true);
            currentP.setStrtTime(time);
          }
          time = time + DIP;
      }


    }
  if(currentP != null)  System.out.println("T"+time+": "+ currentP.getPID());
 }

  public void sortAQueue() {
    Queue<myProcess> sortedQueue = new LinkedList<>();
    while (arrivalQueue.size() != 0) {
      Iterator<myProcess> iterWQ = arrivalQueue.iterator();
      int currentArrTime = Integer.MAX_VALUE;
      myProcess lowestP = new myProcess();
      while(iterWQ.hasNext()) {
        myProcess currentP = iterWQ.next();
        if (currentArrTime > currentP.getArrTime()) {
          currentArrTime = currentP.getArrTime();
          lowestP = currentP;
        }
      }
      sortedQueue.add(lowestP);
      Iterator<myProcess> rIter = arrivalQueue.iterator();
      while(rIter.hasNext()) {
        myProcess removalP = rIter.next();
        if(removalP == lowestP) { rIter.remove(); }
      }
    }
    arrivalQueue = sortedQueue;
  }

  //sorts finishedQueue by process ID
  public void sortQueuebyPID() {
    //put in Queue in list to use collections framework
    LinkedList<myProcess> ll = new LinkedList<>(finishQueue);
    Collections.sort(ll, new Comparator<myProcess>() {
      @Override
      public int compare(myProcess firstP, myProcess secondP) {
        String[] firstID = firstP.getPID().split("p");
        int idNum1 = Integer.parseInt(firstID[1].trim());
        String[] secondID = secondP.getPID().split("p");
        int idNum2 = Integer.parseInt(secondID[1].trim());
        return Integer.compare(idNum1, idNum2);
      }
    });
    //add all elements back to finishQueue sorted
    finishQueue.clear();
    finishQueue.addAll(ll);
  }

  public void printResults() {
    sortQueuebyPID();
    System.out.println("Process   Turnaround Time   Waiting Time");
    for (myProcess process : finishQueue) {
      int taTime = process.getEndTime() - process.getArrTime();
      int wTime =   process.getWaitingTime() ;
      //int wTime =  process.getWaitingTime();
      System.out.printf("%-9s %-17d %-12d%n", process.getPID(), taTime, wTime);
    }
  }

  //calculates the average wait time across all processes
  public double getAvgWT() {
    int processCount = 0;
    double totalWT = 0;
    for (myProcess process : finishQueue) {
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
    for (myProcess process : finishQueue) {
      totalTT += (process.getEndTime() - process.getArrTime());
      processCount++;
    }
    return totalTT / processCount;
  }
}

