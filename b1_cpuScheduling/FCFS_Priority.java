import java.util.*;

class FCFS_Priority{
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the choice: ");
        System.out.println("\n 1.FCFS \t 2.Priority(Non-preemptive) \t 3.both");
        int choice = sc.nextInt();

        System.out.println("Enter the no. of process:");
        int n=sc.nextInt();

        int process[] = new int[n];
        int arrivalTime[] = new int[n+1];
        int burstTime[] = new int[n+1];
        int completionTime[] = new int[n];
        int priority[] = new int[n];
        int TAT[] = new int[n];
        int wat[] = new int[n];
        int temp;

        for(int i=0; i<n; i++)
        {
            process[i]=(i+1);
            System.out.print("Enter the arrival time of p"+(i+1)+":");
            arrivalTime[i]= sc.nextInt();
            System.out.print("Enter the brust time of p"+(i+1)+":");
            burstTime[i]=sc.nextInt();
            System.out.print("Enter the priority of p"+(i+1)+":");
            priority[i]=sc.nextInt();
        }

         if(choice == 1 || choice == 3){
            System.out.println("\n*** First Come First Serve Scheduling ***");
            sortFcfs(n,arrivalTime,process,burstTime);
            calculateTimes(arrivalTime,burstTime,n,TAT,wat,completionTime);
            displayResults(process, arrivalTime,burstTime,TAT,wat,n,completionTime,priority);
        }
        if(choice == 2 || choice == 3){
            System.out.println("\n*** Priority (Non Preemptive) ***");
            sortPriority(n, arrivalTime,process,burstTime,priority);
             calculateTimes(arrivalTime,burstTime,n,TAT,wat,completionTime);
             displayResults(process, arrivalTime,burstTime,TAT,wat,n,completionTime,priority);    
        }
    }
     private static void sortFcfs(int n, int[] arrivalTime, int[] process, int[] burstTime){

            for(int i=0;i<n-1;i++){
                for(int j=i+1;j<n;j++){
                    if(arrivalTime[i] > arrivalTime[j]){
                    swap(arrivalTime,i,j);
                    swap(burstTime,i,j);
                    swap(process,i,j);
                    }
                }
            }
        }
    private static void sortPriority(int n,int[] arrivalTime, int[] process, int[] burstTime, int[] priority){
         sortFcfs(n,arrivalTime,process,burstTime);

    int time = 0;
    for (int i = 0; i < n; i++) {
        int minIndex = i;
        for (int j = i; j < n; j++) {
            // Select the process with highest priority (highest priority value) that has arrived
            if (arrivalTime[j] <= time && priority[j] > priority[minIndex]) {
                minIndex = j;
            } 
            // If priorities are equal, select the process with the earlier arrival time
            else if (arrivalTime[j] <= time && priority[j] == priority[minIndex] && arrivalTime[j] < arrivalTime[minIndex]) {
                minIndex = j;
            }
        }
        
        // Move time forward by the burst time of the selected process
        time += burstTime[minIndex];

        // Swap elements to bring the selected process to the current position
        swap(arrivalTime, i, minIndex);
        swap(burstTime, i, minIndex);
        swap(priority, i, minIndex);
        swap(process, i, minIndex);

    }
 }
    private static void calculateTimes(int[] arrivalTime, int[] burstTime, int n, int[] TAT, int[] wat, int[] completionTime){
        int totalWT=0, totalTAT=0;
        for(int i=0;i<n;i++){
            if(i==0){
                completionTime[i]=arrivalTime[i]+burstTime[i];
            }
            else{
                completionTime[i]=Math.max(completionTime[i-1],arrivalTime[i])+burstTime[i];
            }
            TAT[i]= completionTime[i]-arrivalTime[i];
            wat[i] = TAT[i]-burstTime[i];
            totalTAT += TAT[i];
            totalWT += wat[i];
        }
        System.out.printf("\n average Turnaround Time: %.2f ms\n",(float) totalTAT/n);
        System.out.printf("average waiting Time: %.2f ms\n",(float) totalWT/n);

    }
    private static void displayResults(int[] process, int[] arrivalTime, int[] burstTime, int[] TAT, int[] wat, int n,int[] completionTime,int[] priority){
         System.out.println("Process\tArrival Time\tBurst Time\tpriority\tcompletion time\t\tTurnaround Time\tWaiting Time");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < n; i++) {
            System.out.printf("P%d\t\t%d\t\t%d\t\t%d\t\t%d\t\t%d\t\t%d\n", process[i], arrivalTime[i], burstTime[i], priority[i],completionTime[i], TAT[i], wat[i]);
        }
    }
    private static void swap(int [] arr, int i, int j){
            int temp=arr[i];
            arr[i]=arr[j];
            arr[j]=temp;
        }
}