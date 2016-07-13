/**
 * @author Stian Masserud
 * @version 1.0
 */

import java.util.Scanner;
import java.util.HashMap;
import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.BufferedWriter;

/** 
 * Class for reading in file, topsort, calculating latestStart.
 * All info printed to terminal are also printed to 'output.txt' throughout the program.
 */
class Graph {

	// Used to keep track of tasks to start, working and finished
	private LinkedList<Task> qeue, workingTasks, finishedWorker;
	// Amount of tasks
	private int numberOfTasks;
	// Amount of mapower used to all times
	private int currentStaff;
	// Time used for project
	private int timeUsed;
	// Print to file
	private File out;
	private FileWriter pw;
	private BufferedWriter bw;
	// Stores each task
	protected HashMap<Integer, Task> taskMap;

	/**
	 * Constructor
	 * @param a txt-file containing a project
	 * @return
	 */	 
	public Graph(String filename) {
		taskMap = new HashMap<Integer, Task>();
		currentStaff = 0;
		out = new File("output.txt");
		readFromFile(filename);
		addEgdeses();
		try{
			if(!out.exists()) {
				out.createNewFile();
			}
			pw = new FileWriter(out, true);
			bw = new BufferedWriter(pw);
			bw.write("\n\t\t" + filename + "\n");
			topologicalSort();
			findLatestStart();
			printSlack();
			bw.close();
		} catch (Exception e){
			System.out.println("Something wrong with writing to file. Terminating program");
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Prints al info of all task to terminal
	 * @return
	 */
	private void printSlack() throws Exception{
		String tmp;
		System.out.println("ID|Name|Time|Manpower|earliestStart|latestStart|Slack|List of task(s)");
		bw.write("\nID|Name|Time|Manpower|earliestStart|latestStart|Slack|List of task(s)");
		for(int i = 1; i < taskMap.size() +1; i++) {
			tmp = "(";
			Task t = taskMap.get(i);
			for(int j = 1; j < taskMap.size() + 1; j++) {
				if(taskMap.get(j).outEdges.contains(t)) {
					tmp += taskMap.get(j).id + " ";
				}
			}
			System.out.println(taskToString(t) + tmp + ")");
			bw.write("\n" + taskToString(t) + tmp + ")");
		}
	}

	/**
	 * Helper to 'printSlack'. Prints basic info on all tasks
	 * @param Task to be printed.
	 * @return String containing info about task
	 */
	private String taskToString(Task t) {
		return (t.id + " " + t.name + " " + t.time + " " + t.manpower + " " + t.earliestStart + " " + t.latestStart + " " + (t.latestStart - t.earliestStart) + " ");
	}

	/**
	 * Finds latest start by subtracting the lowest lastestStart (from task(s) that is dependent the current one) by the current time's time to finish.
	 * @return
	 */
	private void findLatestStart() {
		// Used to store tasks which latestStart has been calculated
		workingTasks = new LinkedList<Task>();
		int tmp;
		int i = finishedWorker.size();
		while(i != -1) {
			// First case
			if(i == finishedWorker.size()) {
				i--;
				finishedWorker.get(i).latestStart = (timeUsed - finishedWorker.get(i).time);
				workingTasks.add(finishedWorker.get(i));
			} else {
				tmp = (timeUsed - finishedWorker.get(i).time);
				for(int j = 0; j < workingTasks.size(); j++) {
					if(workingTasks.get(j).outEdges.contains(finishedWorker.get(i))) {
						if((workingTasks.get(j).latestStart - finishedWorker.get(i).time) < tmp) {
							tmp = (workingTasks.get(j).latestStart - finishedWorker.get(i).time); 
						}
					}
				}
				finishedWorker.get(i).latestStart = tmp;
				workingTasks.add(finishedWorker.get(i));
			}
			i--;
		}
	}

	/**
	 * Calculates the edges of all tasks
	 * @return
	 */
	private void addEgdeses() {
		for(int i = 1; i < numberOfTasks +1; i++) {
			for(int j = 0; j < taskMap.get(i).dependencies.length; j++){
				taskMap.get(i).outEdges.add(taskMap.get(taskMap.get(i).dependencies[j]));
			}
		}
	}

	/**
	 * Topological sorting method.
	 * Heavily inspired by the lecture on topsort.
	 * Method puts tasks that is ready to start of the bat into 'qeue'.
	 * Stops and starts tasks by calling support-methods 'printEndingWorkers' and ' printStartingWorkers'
	 * @return
	 */
	private void topologicalSort() throws Exception{
		qeue = new LinkedList<Task>();
		workingTasks = new LinkedList<Task>();
		finishedWorker = new LinkedList<Task>();
		int counter = 0;

		for(int i = 1; i < taskMap.size() +1; i++) {
			if(taskMap.get(i).dependencies.length == 0) {
				qeue.add(taskMap.get(i));
			}
		}
		System.out.println("Time: 0");
		bw.write("\nTime: 0");
		while(qeue.size() > 0 || workingTasks.size() > 0) {
			printEndingWorkers(counter);
			printStartingWorkers(counter);
			counter++;
		}
		System.out.println("**** Shortest possible project execution is " + --counter + " ****");
		bw.write("\n**** Shortest possible project execution is " + counter + " ****");
		timeUsed = counter;
		if(counter < numberOfTasks) { 
			System.out.println("loop found!");
			bw.write("\nloop found!");
			bw.close();
			System.exit(2);
		}
	}

	/**
	 * Helper to 'printEndingWorkes'. Updates the dependecy-list for other tasks which contain the finished task.
	 * A new task can start when cntPredecessors == 0, and is therefore added to the qeue.
	 * @param ID for the finished worker.
	 * @return
	 */
	private void updateDependencies(int workerId) {
		for(int i = 1; i < taskMap.size() +1; i++) {
			for(int j = 0; j < taskMap.get(i).dependencies.length; j++) {
				if(taskMap.get(i).dependencies[j] == workerId) {
					taskMap.get(i).cntPredecessors--;
					if(taskMap.get(i).cntPredecessors == 0 && !finishedWorker.contains(taskMap.get(i))) {
						qeue.add(taskMap.get(i));
					}
				}
			}
		}
	}

	/**
	 * Starts tasks that is in 'qeue', adds them to the list 'workingTasks' and prints info to terminal.
	 * @param The time the tasks starts.
	 * @return
	 */
	private void printStartingWorkers(int currentTime) throws Exception{
		while(qeue.size() != 0) {
			Task t = qeue.getFirst();
			qeue.removeFirst();
			t.earliestStart = currentTime;
			t.startTime = currentTime;
			currentStaff += t.manpower;
			workingTasks.add(t);
			System.out.println("Starting: " + t.id);
			bw.write("\nStarting: " + t.id);
		}
	}

	/**
	 * Checks if any tasks is completed. If so, prints them to termnial, update other task dependencies and removes the task(s) from 'workingTasks'.
	 * @param Current loop time. Used to check when a task is finished.
	 * @return
	 */
	private void printEndingWorkers(int currentTime) throws Exception{
		if(workingTasks.size() == 0) {
			return;
		}
		LinkedList<Task> itemsToRemove = new LinkedList<Task>();
		for(int i = 0; i < workingTasks.size(); i++) {
			if((currentTime - workingTasks.get(i).time) == workingTasks.get(i).startTime) {
				if(itemsToRemove.size() == 0) {
					System.out.println("\nTime: " + currentTime);
					bw.write("\n\nTime: " + currentTime);
				}
				System.out.println("Finished: " + workingTasks.get(i).id);
				bw.write("\nFinished: " + workingTasks.get(i).id);
				currentStaff -= workingTasks.get(i).manpower;
				updateDependencies(workingTasks.get(i).id);
				itemsToRemove.add(workingTasks.get(i));	
				finishedWorker.add(workingTasks.get(i));	
			}
		}
		for(int i = 0; i < itemsToRemove.size(); i++) {
			workingTasks.remove(itemsToRemove.get(i));
		}
	}

	/**
	 * Reads the project from a txt-file and puts the task into a Hashmap.
	 * @param Name of the txt-file containing the project.
	 * @return
	 */
	private void readFromFile(String filename){
		File inFile = new File(filename);
		int tmpDep;
		// Used for counting depedencies for each task
		int numberOfDep = 0;
		int loopCounter = 0;
		String[] tmpArray;
		try{
			// Attributes used to make new task
			int id, time, manpower;
			String name;
			int[] dependencies;

			// Start the reading
			Scanner scIn = new Scanner(inFile);
			numberOfTasks = scIn.nextInt();
			// Jump over blanc line in textfile
			scIn.nextLine();

			while(loopCounter < numberOfTasks) {
				// Length = max dependencies of a task, since I do not know the length before reading.
				dependencies = new int[numberOfTasks-1];
				id = scIn.nextInt();
				name = scIn.next();
				time = scIn.nextInt();
				manpower = scIn.nextInt();
				tmpDep = scIn.nextInt();

				numberOfDep = 0;
				while(tmpDep != 0){
					dependencies[numberOfDep] = tmpDep;
					numberOfDep++;
					tmpDep = scIn.nextInt();
				}
				Task t = new Task(id, time, manpower, name, dependencies, numberOfDep);
				taskMap.put(id, t);
				loopCounter++;
			}
			scIn.close();
		} catch (FileNotFoundException e){
		    e.printStackTrace();
		    System.exit(2);
		}
	}
}
 /**
  * Objectification of tasks in a project
  */
class Task {
	protected int id, time , manpower, startTime;
	protected String name;
	protected int earliestStart, latestStart;
	protected LinkedList<Task> outEdges;
	// Used to keep track of how many dependencies a task has before it can start working. Task starts when cntPredecessors == 0
	protected int cntPredecessors;
	// Functions as a variable for indegrees to.
	protected int[] dependencies;

	public Task(int id, int time, int manpower, String name, int[] dep, int depLength) {
		this.id = id;
		this.time = time;
		this.name = name;
		this.manpower = manpower;
		dependencies = new int[depLength];
		cntPredecessors = depLength;
		outEdges = new LinkedList<Task>();
		int counter = 0;
		for(int i = 0; i < dep.length; i++){
			if(dep[i] != 0) {
				dependencies[counter] = dep[i];
				counter++;
			}
		}
	}
}