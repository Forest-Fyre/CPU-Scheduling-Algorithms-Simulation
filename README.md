# CPU Scheduling Algorithms Simulation

## Project Overview
This project is a simulation of four CPU scheduling algorithms: First-Come, First-Served (FCFS), Shortest Remaining Time (SRT), Multilevel Feedback (Variable) (FBV), and Lottery (LTR). The program simulates the process of scheduling tasks and calculates key performance metrics, including the turnaround time and waiting time for each process, along with their averages. It then consolidates the average values into a comparison table for easy analysis.

## Features
- **Simulated Scheduling Algorithms:**
  - **FCFS (First-Come, First-Served):** Standard first-come, first-served scheduling.
  - **SRT (Shortest Remaining Time):** Preemptive scheduling based on the shortest remaining processing time.
  - **FBV (Multilevel Feedback with Variable Time Quanta):** A three-level feedback queue with variable time quanta (2, 4, 4 ms) and process aging.
  - **LTR (Lottery Scheduling):** A proportional-share scheduling algorithm using random numbers to determine process selection.

- **Dispatcher Simulation:** 
  - The program accounts for dispatcher time (context switching) between processes.

- **Process Attributes:**
  - **Process ID** (PID): A unique identifier for each process.
  - **Arrival Time**: The time when a process enters the ready queue.
  - **Service Time**: The time required by a process to complete.
  - **Tickets**: Used in the LTR algorithm to determine the probability of a process being selected.

- **Performance Metrics:**
  - Turnaround time for each process.
  - Waiting time for each process.
  - Average turnaround time and average waiting time.

- **Input:** The program reads from input files containing:
  - Dispatcher time.
  - Process details (PID, arrival time, service time, tickets).
  - Random numbers for the lottery scheduling algorithm.

- **Output:** 
  - The order and time of processes being loaded into the CPU.
  - Turnaround time and waiting time for each process.
  - Average turnaround time and average waiting time.
  - A comparison table showing the average metrics for all algorithms.

## Input Data Format
Each input file should contain:
1. **Dispatcher Time (DISP):** The time for running the dispatcher (a non-negative integer).
2. **Processes:** Each process entry should include the following:
   - `PID`: The process ID, e.g., `p1`, `p2`, etc.
   - `ArrTime`: The process arrival time (non-negative integer).
   - `SrvTime`: The service time of the process (positive integer).
   - `Tickets`: The number of tickets for the LTR algorithm (positive integer).
3. **Random Numbers:** A list of random numbers to be used for the Lottery scheduling algorithm.
   
## Running Program
1. Compile files with 'javac *.java'
2. Run with : 'java A1' followed by the input file
