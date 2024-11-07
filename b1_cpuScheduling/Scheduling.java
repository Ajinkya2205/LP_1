import java.util.Scanner;

public class Scheduling{
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter the choice for the scheduling algorithm:");
        System.out.println("1. FCFS \t 2. SJF \t 3. Both");
        int choice = sc.nextInt();

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        int[] process = new int[n];
        int[] arrivalTime = new int[n+1];
        int[] burstTime = new int[n+1];
        int[] completionTime = new int[n];
        int[] TAT = new int[n];  // Turn Around Time
        int[] waitingTime = new int[n];

        // Input process details
        for (int i = 0; i < n; i++) {
            process[i] = i + 1;
            System.out.print("Enter Arrival Time for Process " + (i + 1) + ": ");
            arrivalTime[i] = sc.nextInt();
            System.out.print("Enter Burst Time for Process " + (i + 1) + ": ");
            burstTime[i] = sc.nextInt();
        }

        // Choice: FCFS scheduling
        if (choice == 1 || choice == 3) {
            System.out.println("\n*** First Come First Serve Scheduling ***");
            // Sort by arrival time for FCFS
            fcfsSort(arrivalTime, burstTime, process, n);
            calculateTimes(arrivalTime, burstTime, completionTime, TAT, waitingTime, n);
             displayResults(process, arrivalTime, burstTime, completionTime, TAT, waitingTime, n);
        }

        // Choice: SJF scheduling
        if (choice == 2 || choice == 3) {
            System.out.println("\n*** Shortest Job First Scheduling (Non Preemptive) ***");
            sjfSort(arrivalTime, burstTime, process, n);
            calculateTimes(arrivalTime, burstTime, completionTime, TAT, waitingTime, n);
            displayResults(process, arrivalTime, burstTime, completionTime, TAT, waitingTime, n);
        }

        sc.close();
    }

    // Sort for FCFS based on arrival time
    private static void fcfsSort(int[] arrivalTime, int[] burstTime, int[] process, int n) {
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (arrivalTime[i] > arrivalTime[j]) {
                    swap(arrivalTime, i, j);
                    swap(burstTime, i, j);
                    swap(process, i, j);
                }
            }
        }
    }

    // Sort for SJF based on burst time after initial arrival time sorting
    private static void sjfSort(int[] arrivalTime, int[] burstTime, int[] process, int n) {
        fcfsSort(arrivalTime, burstTime, process, n); // Sort by arrival time first

        int time = 0;
        for (int i = 0; i < n; i++) {
            int minIndex = i;

            // Select shortest job among those that have arrived
            for (int j = i; j < n; j++) {
                if (arrivalTime[j] <= time && burstTime[j] < burstTime[minIndex]) {
                    minIndex = j;
                }
            }

            // Process the selected job
            time += burstTime[minIndex];
            swap(arrivalTime, i, minIndex);
            swap(burstTime, i, minIndex);
            swap(process, i, minIndex);
        }
    }

    // Calculate completion, turnaround, and waiting times
    private static void calculateTimes(int[] arrivalTime, int[] burstTime, int[] completionTime, int[] TAT, int[] waitingTime, int n) {
        int totalWT = 0, totalTAT = 0;

        for (int i = 0; i < n; i++) {
            if (i == 0) {
                completionTime[i] = arrivalTime[i] + burstTime[i];
            } else {
                completionTime[i] = Math.max(completionTime[i - 1], arrivalTime[i]) + burstTime[i];
            }
            TAT[i] = completionTime[i] - arrivalTime[i];
            waitingTime[i] = TAT[i] - burstTime[i];
            totalTAT += TAT[i];
            totalWT += waitingTime[i];
        }

        System.out.printf("\nAverage Turnaround Time: %.2f ms\n", (float) totalTAT / n);
        System.out.printf("Average Waiting Time: %.2f ms\n", (float) totalWT / n);
    }

    // Display results in a tabular format
    private static void displayResults(int[] process, int[] arrivalTime, int[] burstTime, int[] completionTime, int[] TAT, int[] waitingTime, int n) {
        System.out.println("Process\tArrival Time\tBurst Time\tCompletion Time\tTurnaround Time\tWaiting Time");
        System.out.println("-------------------------------------------------------------------------");
        for (int i = 0; i < n; i++) {
            System.out.printf("P%d\t\t%d\t\t%d\t\t%d\t\t%d\t\t%d\n", process[i], arrivalTime[i], burstTime[i], completionTime[i], TAT[i], waitingTime[i]);
        }
    }

    // Swap elements in an array
    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
