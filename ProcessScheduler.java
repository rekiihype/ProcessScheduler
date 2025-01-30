import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

class Process {
    String name;
    int arrivalTime;
    int burstTime;
    int priority;
    int remainingTime;
    int finishTime;
    int turnaroundTime;
    int waitingTime;

    public Process(String name, int arrivalTime, int burstTime, int priority) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
    }
}

public class ProcessScheduler extends JFrame {
    private JTextField arrivalTimeFld, burstTimeFld, priorityFld, quantumFld;
    private JTextArea result;
    private JComboBox<String> algorithmComboBox;
    private JButton addProcessBtn, runBtn;
    private JTable processTable;
    private DefaultTableModel table;
    private List<Process> processes = new ArrayList<>();
    
    private JLabel priorityLbl; 
    private JLabel quantumLbl;  
    private JPanel input;
    private JPanel ganttPanel;

    public ProcessScheduler() {
        setTitle("CPU Scheduling Simulator");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    
        input = new JPanel(new GridLayout(6, 2, 10, 10));
        input.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        priorityLbl = new JLabel("Priority:");
        quantumLbl = new JLabel("Time Quantum (Round Robin):");
    
        input.add(new JLabel("Arrival Time:"));
        arrivalTimeFld = new JTextField();
        input.add(arrivalTimeFld);
    
        input.add(new JLabel("Burst Time:"));
        burstTimeFld = new JTextField();
        input.add(burstTimeFld);
    
        input.add(priorityLbl);
        priorityFld = new JTextField();
        input.add(priorityFld);
    
        input.add(quantumLbl);
        quantumFld = new JTextField();
        input.add(quantumFld);
    
        input.add(new JLabel("Select Algorithm:"));
        String[] algorithms = {"Round Robin", "SRT", "SJN", "Preemptive Priority", "Non-Preemptive Priority"};
        algorithmComboBox = new JComboBox<>(algorithms);
        algorithmComboBox.addActionListener(e -> updateInputFields());
        input.add(algorithmComboBox);
    
        addProcessBtn = new JButton("Add Process");
        addProcessBtn.addActionListener(new AddProcessListener());
        input.add(addProcessBtn);
    
        runBtn = new JButton("Run Simulation");
        runBtn.addActionListener(e -> runSimulation());
        input.add(runBtn);
    
        add(input, BorderLayout.NORTH);
    
        table = new DefaultTableModel(new Object[]{"Process", "Arrival Time", "Burst Time", "Priority"}, 0);
        processTable = new JTable(table);
        add(new JScrollPane(processTable), BorderLayout.CENTER);
    
        result = new JTextArea();
        result.setEditable(false);
        add(new JScrollPane(result), BorderLayout.SOUTH);
    
        ganttPanel = new JPanel();
        ganttPanel.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));
        ganttPanel.setPreferredSize(new Dimension(900, 150)); // Adjusted height to accommodate end times
        add(ganttPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void updateInputFields() {
        String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
        boolean isPriorityAlgorithm = selectedAlgorithm.contains("Priority");
        boolean isRoundRobin = selectedAlgorithm.equals("Round Robin");
    
        priorityLbl.setVisible(isPriorityAlgorithm);
        priorityFld.setVisible(isPriorityAlgorithm);
    
        quantumLbl.setVisible(isRoundRobin);
        quantumFld.setVisible(isRoundRobin);
    
        input.revalidate();
        input.repaint();
    }

    private class AddProcessListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int arrivalTime = Integer.parseInt(arrivalTimeFld.getText()); 
                int burstTime = Integer.parseInt(burstTimeFld.getText());
                if (arrivalTime < 0 || burstTime <= 0) {
                    throw new IllegalArgumentException("Arrival time must be non-negative, and burst time must be positive.");
                }
    
                int priority = 0;
                String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
                if (selectedAlgorithm.contains("Priority")) {
                    priority = Integer.parseInt(priorityFld.getText());
                    if (priority < 0) {
                        throw new IllegalArgumentException("Priority must be a non-negative integer.");
                    }
                }
    
                Process process = new Process("P" + (processes.size()), arrivalTime, burstTime, priority);
                processes.add(process);
    
                String priorityValue = selectedAlgorithm.contains("Priority") ? String.valueOf(priority) : "N/A";
                table.addRow(new Object[]{process.name, arrivalTime, burstTime, priorityValue});

                arrivalTimeFld.setText("");
                burstTimeFld.setText("");
                priorityFld.setText("");
    
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(ProcessScheduler.this, "Invalid input! Enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(ProcessScheduler.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void runSimulation() {
        if (processes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No processes added!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
        result.setText("");
        result.append("Running " + selectedAlgorithm + " Simulation \n");
    
        try {
            switch (selectedAlgorithm) {
                case "Round Robin":
                    int quantum = Integer.parseInt(quantumFld.getText());
                    if (quantum <= 0) {
                        throw new IllegalArgumentException("Quantum must be a positive integer.");
                    }
                    roundRobin(processes, quantum);
                    break;
                case "SRT":
                    srt(processes);
                    break;
                case "SJN":
                    sjn(processes);
                    break;
                case "Preemptive Priority":
                    preemptivePriority(processes);
                    break;
                case "Non-Preemptive Priority":
                    nonPreemptivePriority(processes);
                    break;
            }
            JOptionPane.showMessageDialog(this, "Simulation completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input! Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void roundRobin(List<Process> processes, int quantum) {
        Queue<Process> queue = new LinkedList<>();
        int currentTime = 0;
        int[] waitingTime = new int[processes.size()];
        int[] turnaroundTime = new int[processes.size()];
        int[] remainingTime = new int[processes.size()];
        List<List<Integer>> endTimes = new ArrayList<>();
        boolean[] isReady = new boolean[processes.size()];
        List<String> ganttChart = new ArrayList<>(); 
    
        for (int i = 0; i < processes.size(); i++) {
            endTimes.add(new ArrayList<>());
            remainingTime[i] = processes.get(i).burstTime;
        }
    
        int doneProc = 0;
    
        while (doneProc < processes.size()) {
            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).arrivalTime <= currentTime && !isReady[i]) {
                    queue.add(processes.get(i));
                    isReady[i] = true; 
                }
            }
    
            if (!queue.isEmpty()) {
                Process currentProc = queue.poll();
                int index = processes.indexOf(currentProc);
    
                int executionTime = Math.min(remainingTime[index], quantum);
                ganttChart.add(currentProc.name);
                currentTime += executionTime;
                remainingTime[index] -= executionTime;
                endTimes.get(index).add(currentTime);
    
                for (Process process : queue) {
                    int procIndex = processes.indexOf(process);
                    waitingTime[procIndex] += executionTime;
                }
    
                for (int i = 0; i < processes.size(); i++) {
                    if (processes.get(i).arrivalTime <= currentTime && !isReady[i]) {
                        queue.add(processes.get(i));
                        isReady[i] = true; 
                    }
                }
    
                if (remainingTime[index] > 0) {
                    queue.add(currentProc);
                } else {
                    doneProc++;
                    turnaroundTime[index] = currentTime - processes.get(index).arrivalTime;
                    waitingTime[index] = turnaroundTime[index] - processes.get(index).burstTime;
                }
            } else {
                currentTime++;
            }
        }
    
        displayResults(processes, waitingTime, turnaroundTime, endTimes, ganttChart);
    }

    private void srt(List<Process> processes) {
        int currentTime = 0;
        int[] waitingTime = new int[processes.size()];
        int[] turnaroundTime = new int[processes.size()];
        int[] remainingTime = new int[processes.size()];
        //int[] endTimes = new int[processes.size()];
        List<List<Integer>> endTimes = new ArrayList<>();
        List<String> ganttChart = new ArrayList<>();
    
        for (int i = 0; i < processes.size(); i++) {
            endTimes.add(new ArrayList<>());
            remainingTime[i] = processes.get(i).burstTime;
        }
    
        int doneProc = 0;
    
        while (doneProc < processes.size()) {
            int shortest = -1;
            int minRemainingTime = Integer.MAX_VALUE;
    
            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).arrivalTime <= currentTime && remainingTime[i] > 0) {
                    if (remainingTime[i] < minRemainingTime) {
                        minRemainingTime = remainingTime[i];
                        shortest = i;
                    }
                }
            }
    
            if (shortest == -1) {
                currentTime++;
            } else {
                ganttChart.add(processes.get(shortest).name);
                
                remainingTime[shortest]--;
                currentTime++;
                
                endTimes.get(shortest).add(currentTime);
    
                for (int i = 0; i < processes.size(); i++) {
                    if (i != shortest && processes.get(i).arrivalTime <= currentTime && remainingTime[i] > 0) {
                        waitingTime[i]++;
                    }
                }
    
                if (remainingTime[shortest] == 0) {
                    doneProc++;
                    //endTimes[shortest] = currentTime; 
                    turnaroundTime[shortest] = currentTime - processes.get(shortest).arrivalTime;
                    waitingTime[shortest] = turnaroundTime[shortest] - processes.get(shortest).burstTime;
                }
            }
        }
    
        displayResults(processes, waitingTime, turnaroundTime, endTimes, ganttChart);
    }

    private void sjn(List<Process> processes) {
        int currentTime = 0;
        int[] waitingTime = new int[processes.size()];
        int[] turnaroundTime = new int[processes.size()];
        List<List<Integer>> endTimes = new ArrayList<>();
        List<String> ganttChart = new ArrayList<>();
    
        for (int i = 0; i < processes.size(); i++) {
            endTimes.add(new ArrayList<>());
        }
    
        for (Process proc : processes) {
            proc.remainingTime = proc.burstTime;
        }
    
        while (true) {
            Process shortest = null;
            for (Process process : processes) {
                if (process.arrivalTime <= currentTime && process.remainingTime > 0) {
                    if (shortest == null || process.remainingTime < shortest.remainingTime) {
                        shortest = process;
                    }
                }
            }
    
            if (shortest == null) break;
    
            ganttChart.add(shortest.name);
            currentTime += shortest.remainingTime;
            int index = processes.indexOf(shortest);
            endTimes.get(index).add(currentTime);
            turnaroundTime[index] = currentTime - shortest.arrivalTime;
            waitingTime[index] = turnaroundTime[index] - shortest.burstTime;
            shortest.remainingTime = 0;
        }
    
        displayResults(processes, waitingTime, turnaroundTime, endTimes, ganttChart);
    }
    
    private void preemptivePriority(List<Process> processes) {
        int currentTime = 0;
        int[] waitingTime = new int[processes.size()];
        int[] turnaroundTime = new int[processes.size()];
        int[] remainingTime = new int[processes.size()];
        List<List<Integer>> endTimes = new ArrayList<>();
        List<String> ganttChart = new ArrayList<>();
    
        for (int i = 0; i < processes.size(); i++) {
            endTimes.add(new ArrayList<>());
            remainingTime[i] = processes.get(i).burstTime;
        }
    
        int doneProc = 0;
    
        while (doneProc < processes.size()) {
            int highestPriority = -1;
            int minPriority = Integer.MAX_VALUE;
    
            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).arrivalTime <= currentTime && remainingTime[i] > 0) {
                    if (processes.get(i).priority < minPriority) {
                        minPriority = processes.get(i).priority;
                        highestPriority = i;
                    }
                }
            }
    
            if (highestPriority == -1) {
                currentTime++;
            } else {
                ganttChart.add(processes.get(highestPriority).name);
                remainingTime[highestPriority]--;
                currentTime++;
                
                endTimes.get(highestPriority).add(currentTime);
                
                for (int i = 0; i < processes.size(); i++) {
                    if (i != highestPriority && processes.get(i).arrivalTime <= currentTime && remainingTime[i] > 0) {
                        waitingTime[i]++;
                    }
                }
    
                if (remainingTime[highestPriority] == 0) {
                    doneProc++;
                    turnaroundTime[highestPriority] = currentTime - processes.get(highestPriority).arrivalTime;
                    waitingTime[highestPriority] = turnaroundTime[highestPriority] - processes.get(highestPriority).burstTime;
                }
            }
        }
    
        displayResults(processes, waitingTime, turnaroundTime, endTimes, ganttChart);
    }

    private void nonPreemptivePriority(List<Process> processes) {
        int currentTime = 0;
        int[] waitingTime = new int[processes.size()];
        int[] turnaroundTime = new int[processes.size()];
        List<List<Integer>> endTimes = new ArrayList<>();
        List<String> ganttChart = new ArrayList<>();
    
        for (int i = 0; i < processes.size(); i++) {
            endTimes.add(new ArrayList<>());
        }
    
        int doneProc = 0;
    
        while (doneProc < processes.size()) {
            int highestPriority = -1;
            int minPriority = Integer.MAX_VALUE;
    
            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).arrivalTime <= currentTime && processes.get(i).remainingTime > 0) {
                    if (processes.get(i).priority < minPriority) {
                        minPriority = processes.get(i).priority;
                        highestPriority = i;
                    }
                }
            }
    
            if (highestPriority == -1) {
                currentTime++;
            } else {
                ganttChart.add(processes.get(highestPriority).name);
                currentTime += processes.get(highestPriority).burstTime;
                endTimes.get(highestPriority).add(currentTime);
                turnaroundTime[highestPriority] = currentTime - processes.get(highestPriority).arrivalTime;
                waitingTime[highestPriority] = turnaroundTime[highestPriority] - processes.get(highestPriority).burstTime;
                processes.get(highestPriority).remainingTime = 0;
                doneProc++;
            }
        }
    
        displayResults(processes, waitingTime, turnaroundTime, endTimes, ganttChart);
    }

    private void displayResults(List<Process> processes, int[] waitingTime, int[] turnaroundTime, List<List<Integer>> endTimes, List<String> ganttChart) {
        int totalWaitingTime = Arrays.stream(waitingTime).sum();
        int totalTurnaroundTime = Arrays.stream(turnaroundTime).sum();
        double avgWaitingTime = (double) totalWaitingTime / processes.size();
        double avgTurnaroundTime = (double) totalTurnaroundTime / processes.size();
     
        displayGanttChart(ganttChart, endTimes);
        result.append("\nProcess\tWaiting Time\tTurnaround Time\n");
        for (int i = 0; i < processes.size(); i++) {
            result.append(processes.get(i).name + "\t" + waitingTime[i] + "\t\t" + turnaroundTime[i] + "\n");
        }
    
        result.append("\nAverage Waiting Time: " + String.format("%.2f", avgWaitingTime) + "\n");
        result.append("Average Turnaround Time: " + String.format("%.2f", avgTurnaroundTime) + "\n");
    }

    private void displayGanttChart(List<String> ganttChart, List<List<Integer>> endTimes) {
        ganttPanel.removeAll(); // Clear previous Gantt chart
        ganttPanel.setLayout(new BoxLayout(ganttPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical arrangement
    
        // Create a panel for the Gantt chart
        JPanel chartPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        chartPanel.setPreferredSize(new Dimension(800, 10)); // Adjust size for better visibility
        
        // Create a panel for the end time labels
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); // Align labels to the left
        timePanel.setPreferredSize(new Dimension(800, 30));
        
        // Add the initial label for time 0
        JLabel startTimeLabel = new JLabel("0", SwingConstants.CENTER);
        startTimeLabel.setPreferredSize(new Dimension(90, 20)); 
        timePanel.add(startTimeLabel);
    
        // Group consecutive processes
        if (!ganttChart.isEmpty()) {
            String currentProcess = ganttChart.get(0);
    
            for (int i = 1; i < ganttChart.size(); i++) {
                if (ganttChart.get(i).equals(currentProcess)) {
                    // Find the index of the process with the matching name
                    int processIndex = -1;
                    for (int j = 0; j < processes.size(); j++) {
                        if (processes.get(j).name.equals(currentProcess)) {
                            processIndex = j;
                            break;
                        }
                    }
                    
                    endTimes.get(processIndex).remove(0);
                    continue;
                } else {
                    // Add a box for the current process group
                    JLabel processLabel = new JLabel(currentProcess, SwingConstants.CENTER);
                    processLabel.setOpaque(true);
                    processLabel.setBackground(Color.CYAN);
                    processLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                    processLabel.setPreferredSize(new Dimension(90, 50)); 
                    chartPanel.add(processLabel);
                    
                    // Find the index of the process with the matching name
                    int processIndex = -1;
                    for (int j = 0; j < processes.size(); j++) {
                        if (processes.get(j).name.equals(currentProcess)) {
                            processIndex = j;
                            break;
                        }
                    }
    
                    // Add the end time label below the box
                    if (processIndex != -1 && !endTimes.get(processIndex).isEmpty()) {
                        // Get the end time corresponding to this instance of the process
                        int endTime = endTimes.get(processIndex).get(0);
                        JLabel timeLabel = new JLabel(String.valueOf(endTime), SwingConstants.CENTER);
                        timeLabel.setPreferredSize(new Dimension(90, 20)); 
                        timePanel.add(timeLabel);
                        endTimes.get(processIndex).remove(0);
                    }
    
                    // Reset for the next process
                    currentProcess = ganttChart.get(i);
                }
            }
    
            // Add the last process group
            JLabel processLabel = new JLabel(currentProcess, SwingConstants.CENTER);
            processLabel.setOpaque(true);
            processLabel.setBackground(Color.CYAN);
            processLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            processLabel.setPreferredSize(new Dimension(90, 50)); 
            chartPanel.add(processLabel);
            
            // Find the index of the process with the matching name
            int processIndex = -1;
            for (int j = 0; j < processes.size(); j++) {
                if (processes.get(j).name.equals(currentProcess)) {
                    processIndex = j;
                    break;
                }
            }
    
            // Add the end time label below the box
            if (processIndex != -1 && !endTimes.get(processIndex).isEmpty()) {
                // Get the end time corresponding to this instance of the process
                int endTime = endTimes.get(processIndex).get(0);
                JLabel timeLabel = new JLabel(String.valueOf(endTime), SwingConstants.CENTER);
                timeLabel.setPreferredSize(new Dimension(90, 20)); 
                timePanel.add(timeLabel);
                endTimes.get(processIndex).remove(0);
            }
        }
    
        // Add the Gantt chart panel to the main Gantt panel
        ganttPanel.add(chartPanel);
        ganttPanel.add(timePanel);
       
        // Revalidate and repaint the Gantt panel
        ganttPanel.revalidate();
        ganttPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProcessScheduler());
    }
}
