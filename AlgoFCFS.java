/*
* AlgoFCFS.java
* Modified : 27/08/2023
*
* This class represents the implementation of the First Come First Serve
* CPU scheduling algorithm
* This file is used in conjunction with Assignment1 for COMP2240.
*/
import java.util.*;

public class AlgoFCFS {
  private Queue<myProcess> arrivalQueue; //stores init processes for scheduling
  private Queue<myProcess> finishQueue; //stores completed processes
	private myProcess currentP; //stores selected running process
  private int DIP; //stores global dispatcher time unit

  //default constructor
  public AlgoFCFS() {
    arrivalQueue =  new LinkedList<>();
    finishQueue =  new LinkedList<>();
    currentP = null;
		DIP = 0;
  }

  //constructor with parameters
  public AlgoFCFS(Queue<myProcess> newArrivalQueue, Queue<myProcess> newReadyQueue
      , myProcess newCurrentP, int newDIP) {
    arrivalQueue = newArrivalQueue;
    finishQueue = newReadyQueue;
    currentP = newCurrentP;
    DIP = newDIP;
  }

  //main function that algorithm runs through
  public void algoMain() {
    System.out.println("FCFS:");
    int time = 0;  //sets time to 0 to track algorithm lifetime
    this.sortAQueue(); //pre-sort arrivalQueue for correct ordering
    int aQueueSize = arrivalQueue.size();
    while(aQueueSize != finishQueue.size()) {
      //sets correct attributes for currentP during round
      setCurrent(time, DIP);
      //checks if currentP needs to be reselected
      checkCurrent(time);
      time++;
    }
    printResults();
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

  //prints process results of algorithm (TurnAround & Waiting Times)
  public void printResults() {
    sortQueuebyPID();
    System.out.println("Process   Turnaround Time   Waiting Time");
    for (myProcess process : finishQueue) {
      int taTime = process.getEndTime() - process.getArrTime();
      int wTime = taTime - process.getSrvTime();
      System.out.printf("%-9s %-17d %-12d%n", process.getPID(), taTime, wTime);
    }
  }

  //checks that current process has completed it's service time
  public boolean processServiced() {
    if(currentP.getSrvTime() == currentP.getServedTime()) return true;
    return false;
  }

  //checks current process, adjusting its status accordingly
  public void checkCurrent(int time) {
    if (currentP == null) return;
    //if process service time fin, adjust & add to finish queue
    if (processServiced()) {
      currentP.setEndTime(time+1);
      finishQueue.add(currentP);
      currentP = null;
    //if not finished, increment service time
    } else {
      int currentPST =  currentP.getServedTime();
      currentPST++;
      currentP.setServedTime(currentPST);
    }
  }

  //sorts the arrival queue by ascending arrival time 
  public void sortAQueue() {
    Queue<myProcess> sortedQueue = new LinkedList<>();
    while (arrivalQueue.size() != 0) { //loops through until old order is 0
      Iterator<myProcess> iterWQ = arrivalQueue.iterator();
      int currentArrTime = Integer.MAX_VALUE;
      //finds shortest arrival time process & adds it to ordered queue
      myProcess lowestP = new myProcess();
      while(iterWQ.hasNext()) {
        myProcess currentP = iterWQ.next();
        if (currentArrTime > currentP.getArrTime()) {
          currentArrTime = currentP.getArrTime();
          lowestP = currentP;
        }
      }
      sortedQueue.add(lowestP);
      //Iterates through queue again & removes shortest arrival time process
      Iterator<myProcess> rIter = arrivalQueue.iterator();
      while(rIter.hasNext()) {
        myProcess removalP = rIter.next();
        if(removalP == lowestP) { rIter.remove(); }
      }
    }
    //set arrival queue to newly sorted queue
    arrivalQueue = sortedQueue;
  }

    //checks that arrival queue's head can be current process
   public boolean AQueueReady(int time) {
     if (currentP != null) { return false; }
     myProcess headP = arrivalQueue.element();
     int headTime = headP.getArrTime();
     //if aQueue head arrTime hasn't come, return false
     if (headTime > time) { return false; } 
     return true;
   } 

  //checks if current running process is null replace with aQueue head
  public void setCurrent(int time, int DIP) {
    if (AQueueReady(time)) { 
      if (arrivalQueue.element() != null) {
        currentP = arrivalQueue.poll();
        time += DIP; // add DIP to current time
        //outPrint newly selected process to terminal
        System.out.println("T"+time+": "+ currentP.getPID());
        currentP.setStrtTime(time);
      } 
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
