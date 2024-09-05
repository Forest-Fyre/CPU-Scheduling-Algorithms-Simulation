/*
* myProcess.java
* Modified : 27/08/2023
*
* This class represents the process object used within the CPU scheduling
* algorithms FCFS, SRT, FBV & LTR, with all attributes for correct function
* This file is used in conjunction with Assignment1 for COMP2240.
*/
public class myProcess {
  private String PID;       //Process Identification
  private int ArrTime;      //Process arrival Time 
  private int SrvTime;      //Process service time
  private int strtTime;     //Process start time
  private int endTime;      //Process end time
  private int servedTime;   //Processes served time
  private int ageTime;      //Processes aged time
  private int qtTime;       //Process quantum time
  private int qtTimeSrved;  //Processes served quantum time
  private int waitingTime;  //Processes served waiting time
  private int priorityLvl;  //Processes priority level
  private int tickets;      //Process ticket count
  private boolean srvStrted;//Process service started status

  //default constructor
  public myProcess() {
    PID = "";
    ArrTime = 0;
    SrvTime = 1;
    strtTime = 0;
    endTime = 0;
    servedTime = 0;
    ageTime = 0;
    qtTime = 0;
    waitingTime = 0;
    qtTimeSrved = 0;
    srvStrted = false;
    tickets = 0;
    priorityLvl = 0;
  }
 
  //constructor with parameters
  public myProcess(String newPID, int newArrTime, int newSrvTime, int newStrtTime,
      int newEndTime, int newServedTime, int newWaitingTime, int newAgeTime,
      int newQtTime, int newQtTimeSrved,int newPriorityLvl,
      int newTickets, boolean newSrvStrted) {
    PID = newPID;
    ArrTime = newArrTime;
    SrvTime = newSrvTime;
    strtTime = newStrtTime;
    endTime = newEndTime;
    servedTime = newServedTime;
    srvStrted = newSrvStrted;
    waitingTime = newWaitingTime;
    ageTime = newAgeTime;
    qtTime = newQtTime;
    priorityLvl = newPriorityLvl;
    qtTimeSrved = newQtTimeSrved;
    tickets = newTickets;
  }

  //function used to create deepCopy of self for duplication
  public myProcess deepCopy() {
    return new myProcess(this);
  }

  //constructor used for creating deepCopies of self
  public myProcess(myProcess other) {
    this.PID = other.PID;
    this.ArrTime = other.ArrTime;
    this.SrvTime = other.SrvTime;
    this.strtTime = other.strtTime;
    this.endTime = other.endTime;
    this.servedTime = other.servedTime;
    this.ageTime = other.ageTime;
    this.qtTime = other.qtTime;
    this.qtTimeSrved = other.qtTimeSrved;
    this.waitingTime = other.waitingTime;
    this.priorityLvl = other.priorityLvl;
    this.tickets = other.tickets;
    this.srvStrted = other.srvStrted;
  }

 public int getRemainingSrvTime() {
   return this.getSrvTime() - this.getServedTime();
 }
 /*
  * REST OF CODE IS ALL SETTERS AND GETTERS FOR VARIABLES
 */
 public void setTickets(int newTickets) {
   tickets = newTickets;
 }

 public int getTickets() {
   return tickets;
 }

 public void setPriorityLvl(int newPriorityLvl) {
   priorityLvl = newPriorityLvl;
 }

 public int getPriorityLvl() {
   return priorityLvl;
 }

 public int getQtTimeSrved() {
   return qtTimeSrved;
 }

 public void setQtTimeSrved(int newQtTimeSrved) {
   qtTimeSrved = newQtTimeSrved;
 }

 public int getQtTime() {
   return qtTime;
 }

 public void setQtTime(int newQtTime) {
   qtTime = newQtTime;
 }

 public void setWaitingTime(int newWaitingTime) {
   waitingTime = newWaitingTime;
 }

 public int getWaitingTime() {
   return waitingTime;
 }

 public void setPID(String newPID) {
   PID = newPID;
 }

 public String getPID() {
   return PID;
 }
 public boolean serviceStarted() {
   return srvStrted;
 }

 public void setSrvStrted(boolean newSrvStrted) {
   srvStrted = newSrvStrted;
 }
 
 public int getArrTime() {
   return ArrTime;
 }

 public int getServedTime() {
   return servedTime;
 }

 public void setServedTime(int newServedTime) {
   servedTime = newServedTime;
 }

 public int getAgeTime() {
   return ageTime;
 }

 public void setAgeTime(int newAgeTime) {
   ageTime = newAgeTime;
 }

 public void setArrTime(int newArrTime) {
   ArrTime = newArrTime;
 }

 public int getSrvTime() {
   return SrvTime;
 }

 public void setSrvTime(int newSrvTime) {
   SrvTime = newSrvTime;
 }

 public int getStrtTime() {
   return strtTime;
 }

 public void setStrtTime(int newStrtTime) {
   strtTime = newStrtTime;
 }

 public int getEndTime() {
   return endTime;
 }

 public void setEndTime(int newEndTime) {
   endTime = newEndTime;
 }

}
