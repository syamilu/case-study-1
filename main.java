/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package groupassignment;


import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

/**
 *
 * @author wanmu
 */
public class EOPGroupAssignment {
    final static int COLS = 5;
    
    public static void main(String[] args) throws InterruptedException, IOException{
        Scanner input = new Scanner(System.in);
        boolean endsystem = false;//to determine user ending system
        int choice=-1, currentrow = 0;//currentrow to show current empty row
        String[][] patientRecord = new String[99][COLS];//0 col for name, 1 for age, 2 for phoneNo, 3 for appointment daychoice, 4 for appointment session
        char[] schedule = {'0', 'a', 'a', 'a', 'a', 'a'};// a=both session availble, b morning session available, c evening session available, d both not available
        File file = new File("systemRecord.txt");//use to read if there available systemRecord file
        File file2 = new File("appointment.txt");//to record appointment 
        
        
        /*
        read if there current patient record. This actually can be make to function but we already done the 
        flowchart.
        */
        if(file.exists()){
            try(Scanner fileinput = new Scanner(file)){
                boolean endread = true;
                String data = fileinput.nextLine()+"/";
                int charat = 0;
                int column = 0;
                do{
                    char ch = data.charAt(charat);
                    if(ch==','){
                        column++;
                    }
                    else if(ch=='.'){
                        column = 0;
                        currentrow++;
                    }
                    else if(ch=='/'){
                        endread = false;
                    }
                    else if(patientRecord[currentrow][column]==null){
                        patientRecord[currentrow][column] = "" + ch;
                    }
                    else{
                        patientRecord[currentrow][column] = patientRecord[currentrow][column]+ch;
                    } 
                    charat++;
                }while(endread);
            }
            catch(FileNotFoundException ex){
                System.out.println("Record read unsuccesfull. No Patient data retrieved");
            }
        }
        
        
        if(file2.exists()){
            try(Scanner fileinput = new Scanner(file2)){
            String data = fileinput.nextLine();
            for(int i = 0; i<5; i++){
                schedule[i+1] = data.charAt(i);
            }
            }
            catch(FileNotFoundException ex){
                System.out.println("Latest appointment record cannot be found");
            }
        }
        
        
        /*The actual start of the system*/
        System.out.println("\tWELCOME TO PATIENT MANAGEMENT SYSTEM VERSION 4.0");
        System.out.print("\t************************************************");
        
        do{
            System.out.println("\n===================");
            System.out.println("Select Operation :- ");
            System.out.println("===================");
            System.out.println("1. Display Patient Info");
            System.out.println("2. Add Patient");
            System.out.println("3. Delete Patient");
            System.out.println("4. Search Patient");//casesensitive, need to be fixed
            System.out.println("5. Edit Patient");
            System.out.println("6. Make an Appointment");
            System.out.println("7. End System");
            System.out.println("============================");        
            System.out.print("Please select one(1-7) : ");
            
            boolean continueInput = true;
            do{
                try{
                    choice = input.nextInt();
                    if(choice<=0||choice>7){
                        System.out.print("Invalid choice, please enter again(1-7) : ");
                    }
                    else
                        continueInput = false;
                }catch(Exception ex){
                        System.out.println("Invalid input. Input must be integer.");
                        input.nextLine();
                        System.out.print("Please enter again : ");
                    }
            }while(continueInput);
            
            switch(choice){
                case 1 : displayPatient(currentrow,patientRecord);break;
                case 2 : currentrow = addPatient(currentrow, patientRecord);break;
                case 3 : currentrow = deletePatient(currentrow, patientRecord);break;
                case 4 : currentrow = searchPatient(currentrow, patientRecord);break;
                case 5 : editPatient(currentrow, patientRecord);break;
                case 6 : makingAppointment(schedule, currentrow, patientRecord);break;
                case 7 : endsystem = true;break;
                default : System.out.println("Bug bro!");
            }
            
            if(choice>0&&choice<7)
            createFile(currentrow, patientRecord, schedule);//to update and create the file
            
        }while(endsystem==false);
        
        System.out.println("\nThank you for using the system!");
    }
    
    /**This function is for display patient, which we first validate is there any patient record.
    *if no patient record, then we will return back to main with notes that there is no patient
    * record found.
    */
    public static void displayPatient(int currentrow, String[][] patientRecord){
        if(patientRecord[0][0]==null){
            System.out.println("No patient record found.");
            return;
        }
        System.out.println();
        System.out.println("Patient List :-");
        System.out.println("**********************************");
        for(int i = 0; i<currentrow;i++){
            System.out.printf("Patient %d\t: %s\n", i+1, patientRecord[i][0]);
            System.out.printf("Age \t\t: %s\n", patientRecord[i][1]);
            System.out.printf("Phone number \t: %s\n", patientRecord[i][2]);
            if(patientRecord[i][3]!=null){
                System.out.printf("Appointment Day and Session : %s, %s\n", patientRecord[i][3], patientRecord[i][4]);
            }
            System.out.println("**********************************");
        }
    }
    
    /**This function is for add patient record to the string array
     */
    public static int addPatient(int currentrow, String[][] patientRecord) throws InterruptedException, IOException{
        Scanner input = new Scanner(System.in);
        
        System.out.print("Enter patient name : ");
        patientRecord[currentrow][0] = input.nextLine();
        System.out.print("Age : ");
        patientRecord[currentrow][1] = input.nextLine();
        System.out.print("Phone Number : ");
        patientRecord[currentrow][2] = input.nextLine();
        
        System.out.print("Please wait, adding patient info");
        for(int i = 0;i<3;i++){
            TimeUnit.SECONDS.sleep(1);
            System.out.print(".");
        }
        TimeUnit.SECONDS.sleep(1);
        System.out.printf("\nPatient %s succesfully added.\n", patientRecord[currentrow][0]);
        currentrow++;
        return currentrow;
    }
    
    /**This function is for delete Patient, if no patient record we return to main with notes no patiennt record
     * We ask forr confirmation for the deletion process
     */
    public static int deletePatient(int currentrow, String[][] patientRecord) throws InterruptedException{
        Scanner input = new Scanner(System.in);
        int delete = 0;
        if(patientRecord[0][0]==null){
            System.out.println("No patient record found.");
            return currentrow;
        }
        displayPatient(currentrow, patientRecord);
        if(currentrow == 1){
            System.out.printf("Delete %s?(y/n) : ", patientRecord[0][0]);
            int choice = choose();
            if(choice=='N'||choice=='n'){
                return currentrow;
            }
            else{
                delete = 1;
            }
        }
        else{
            System.out.printf("Select patient you want to delete(1 - %d) : ", currentrow);
            boolean continueInput = true;
            do{
                try{
                    delete = input.nextInt();
                    if(delete<1||delete>currentrow){
                        System.out.print("Invalid input, please enter again : ");
                    }
                    else{
                        continueInput = false;
                    }
                }catch(Exception ex){
                        System.out.println("Invalid input. Input must be integer.");
                        input.nextLine();
                        System.out.print("Please enter again : ");
                    }
            }while(continueInput);
        }
        System.out.printf("Deleting Patient %s, please wait", patientRecord[delete-1]);
        patientRecord[delete-1] = patientRecord[currentrow-1];
        currentrow--;
        patientRecord[currentrow] = patientRecord[currentrow+2];
        
        for(int i = 0;i<3;i++){
            TimeUnit.SECONDS.sleep(1);
            System.out.print(".");
        }
        
        TimeUnit.SECONDS.sleep(1);
        System.out.println("\nDelete Succesfull.");
        return currentrow;
    }
    
    /**Function for searching patient, ONLY USING THE NAME OF THE PATIENT
     * 
     */
    public static int searchPatient(int currentrow, String[][] patientRecord) throws InterruptedException, IOException{
        Scanner input = new Scanner(System.in);
        String search;
        char choice;
        boolean match = false;
        
        if(patientRecord[0][0]==null){
            System.out.println("No patient record found.");
            return currentrow;
        }
        
        System.out.print("Search for : ");
        search = input.nextLine();
        
        for(int i = 0; i < currentrow;i++){
            if(search.equalsIgnoreCase(patientRecord[i][0])){
                System.out.println("Patient found.");
                System.out.println("**********************************");
                System.out.printf("Patient %d\t: %s\n", i+1, patientRecord[i][0]);
                System.out.printf("Age \t\t: %s\n", patientRecord[i][1]);
                System.out.printf("Phone number \t: %s\n", patientRecord[i][2]);
                System.out.println("**********************************");
                match = true;
            }
        }
        
        if(match==false){
                System.out.println("Patient record not found.");
                System.out.println("Proceed to add Patient?(y/n)");   
                choice = choose();
                
                if(choice=='Y'||choice=='y'){
                    currentrow = addPatient(currentrow, patientRecord);
                }
        }
        return currentrow;
    } 
    
    /*basic function to validate input y or n*/
    public static char choose(){
        Scanner input = new Scanner(System.in);
        char choose;
        choose = input.next().charAt(0);
        while(choose!='Y' && choose!='N' && choose !='y' && choose!='n'){
            System.out.print("Invalid choice, please enter again(y/n) : ");
            choose = input.next().charAt(0);
        }
        return choose;
    }
    
    /** Function to edit patient data, user can choose which patient they want to edit
     *  and what data they want to edit
     */
    public static void editPatient(int currentrow, String[][] patientRecord) throws InterruptedException{//what happen kalau tekan awal2
        Scanner input = new Scanner(System.in);
        int edit=0, choice=0;
        
        if(patientRecord[0][0]==null){
            System.out.println("No patient record found.");
            return;
        }
        
        displayPatient(currentrow, patientRecord);
        System.out.print("Which patient you want to edit : ");
        boolean continueInput = true;
            do{
                try{
                    edit = input.nextInt();
                    if(!(edit>0&&edit<=currentrow)){
                        System.out.print("Invalid input, please enter again : ");
                    }
                    else{
                        continueInput = false;
                    }
                }catch(Exception ex){
                        System.out.println("Invalid input. Input must be integer.");
                        input.nextLine();
                        System.out.print("Please enter again : ");
                    }
            }while(continueInput);
       
        System.out.println("Choose what you want to edit :-");
        System.out.println("\t1. Name");
        System.out.println("\t2. Age");
        System.out.println("\t3. Phone Number");
        if(patientRecord[edit][3]!=null){
            System.out.println("Appointment cannot be edited.");
        }
        System.out.print("Edit choice(1-3) : ");
        
        
        continueInput = true;
            do{
                try{
                    choice = input.nextInt();
                    if(!(choice>0&&choice<=3)){
                        System.out.print("Invalid input, please enter again : ");
                    }
                    else{
                        continueInput = false;
                    }
                }catch(Exception ex){
                        System.out.println("Invalid input. Input must be integer.");
                        input.nextLine();
                        System.out.print("Please enter again : ");
                    }
            }while(continueInput);
            
        input.nextLine();
        edit = edit - 1;
        
        switch(choice){
            case 1 :
                System.out.print("Enter new name : ");
                patientRecord[edit][0] = input.nextLine();
                break;
            case 2 :
                System.out.print("Enter new age : ");
                patientRecord[edit][1] = input.nextLine();
                break;
            case 3 :
                System.out.print("Enter new phone number : ");
                patientRecord[edit][2] = input.nextLine();
                break;
            default :
                System.out.println("Bug detected");
        }
        System.out.print("Updating patient record");
        for(int i = 0;i<3;i++){
            TimeUnit.SECONDS.sleep(1);
            System.out.print(".");
        }
        TimeUnit.SECONDS.sleep(1);
        System.out.println();
        System.out.println("Record updated succesfully.");
    }
    
    /* Function to making appointment for patient*/
    public static void makingAppointment(char[] schedule, int currentrow, String[][] patientRecord){
        Scanner input = new Scanner(System.in);
        int rows = 6, daychoice=0, patient=1;
        String session = null, day = null;
        if(patientRecord[0][0]==null){
            System.out.println("No patient record found. Appointment cannot be made.");
            return;
        }
        System.out.println("Book Appointment :-");
        displayPatient(currentrow, patientRecord);
        if(currentrow == 1){
            System.out.print("Make appointment for patient 1?(y/n) : ");
            int choice = choose();
            if(choice=='n'||choice=='N'){
                return;
            }
        }
        else{
            System.out.printf("Please choose patient(1-%d) : ", currentrow);
            boolean continueInput = true;
            do{
                try{
                    patient = input.nextInt();
                    if(patient<1||patient>currentrow){
                        System.out.print("Invalid patient record, please enter again : ");
                    }
                    else
                        continueInput = false;
                }catch(Exception ex){
                        System.out.println("Invalid input. Input must be integer.");
                        input.nextLine();
                        System.out.print("Please enter again : ");
                    }
            }while(continueInput);
        }
        
        patient = patient-1;
        
        if(patientRecord[patient][3]!=null){
            System.out.println("Patient already have an appointment. No change can be made.");
            return;
        }
        System.out.println();
        
        System.out.println("|***************|***************************************|");
        System.out.println("|\tDAY\t|\tAVAILABLE SESSION\t\t|");
        System.out.println("|***************|***************************************|");
        
        //this for is to check what day and what session is avail
        for(int i = 1; i < rows; i++){
                switch(schedule[i]){
                    case 'a' : session = "Morning and Evening";break;
                    case 'b' : session = "Morning\t\t";break;
                    case 'c' : session = "Evening\t\t";break;
                    case 'd' : session = "No session available"; break;
                    default : System.out.println("Bug detected");
                }
                switch(i){
                    case 1 : System.out.printf("| %d.Monday\t|\t%s\t\t|\n", i, session);break;
                    case 2 : System.out.printf("| %d.Tuesday\t|\t%s\t\t|\n", i, session);break;
                    case 3 : System.out.printf("| %d.Wednesday\t|\t%s\t\t|\n", i, session);break;
                    case 4 : System.out.printf("| %d.Thursday\t|\t%s\t\t|\n", i, session);break;
                    case 5 : System.out.printf("| %d.Friday\t|\t%s\t\t|\n", i, session);break;
                    default : System.out.println("Bug detected");
                }
        }
        System.out.println("|***************|***************************************|");
        System.out.print("\nPlease choose your daychoice(1-5): ");
        
        boolean continueInput = true;
            do{
                try{
                    daychoice = input.nextInt();
                    if(daychoice<1||daychoice>5){
                        System.out.print("Invalid choice, please enter again : ");
                    }
                    else
                        continueInput = false;
                }catch(Exception ex){
                        System.out.println("Invalid input. Input must be integer.");
                        input.nextLine();
                        System.out.print("Please enter again : ");
                    }
            }while(continueInput);
            
        switch(daychoice){
            case 1 : day = "Monday";break;
            case 2 : day = "Tuesday";break;
            case 3 : day = "Wednesday";break;
            case 4 : day = "Thursday";break;
            case 5 : day = "Friday";break;
            default : System.out.println("Bug detected");
        }
        
        System.out.print("1. Morning Session\n2. Evening Session\nWhich session do you want? : ");
        int choice=0;
        continueInput = true;
        do{
            try{
                choice = input.nextInt();
                if(choice<1||choice>2){
                    System.out.print("Invalid choice, please enter again : ");
                }
                else
                    continueInput = false;
                }
            catch(Exception ex){
                System.out.println("Invalid input. Input must be integer.");
                input.nextLine();
                System.out.print("Please enter again : ");
            }
            }while(continueInput);
            
        switch(choice){
            case 1 : 
            switch (schedule[daychoice]) {
                case 'a':
                    System.out.printf("Appointment has been set, patient %s appointment will be on Morning, %s.\n", patientRecord[patient][0], day);
                    schedule[daychoice] = 'c';
                    patientRecord[patient][3] = day;
                    patientRecord[patient][4] = "Morning";
                    break;
                case 'b':
                    System.out.printf("Appointment has been set, patient %s appointment will be on Morning, %s.\n", patientRecord[patient][0], day);
                    schedule[daychoice] = 'd';
                    patientRecord[patient][3] = day;
                    patientRecord[patient][4] = "Morning";
                    break;
                case 'c':
                    System.out.printf("Sorry Morning session is not available on %s.\n", day);
                    break;
                case 'd':
                    System.out.printf("Sorry no session is available on %s.\n",  day);
                    break;
                default:
                    break;
            }
                break;

            case 2 : 
            switch (schedule[daychoice]) {
                case 'a':
                    System.out.printf("Appointment has been set, patient %s appointment will be on Evening, %s.\n", patientRecord[patient][0], day);
                    schedule[daychoice] = 'b';
                    patientRecord[patient][3] = day;
                    patientRecord[patient][4] = "Evening";
                    break;
                case 'b':
                    System.out.printf("Sorry Evening session is not available on %s.\n", day);
                    break;
                case 'c':
                    System.out.printf("Appointment has been set, patient %s appointment will be on Evening, %s.\n", patientRecord[patient][0], day);
                    schedule[daychoice] = 'd';
                    patientRecord[patient][3] = day;
                    patientRecord[patient][4] = "Evening";
                    break;
                case 'd':
                    System.out.printf("Sorry no session is available on %s.\n", day);
                    break;
                default:
                    break;
            }
                break;

            default : System.out.println("Bug detected.");
        }
    }
    
    
    /** Createfile function is where we create readable patient Record
     */
    public static void createFile(int currentrow, String[][] patientRecord, char[] schedule){
        File file = new File("PatientRecord.txt");
        
        try(PrintWriter output = new PrintWriter(file)){
            output.println("Patient List :-");
            output.println("**********************************");
            for(int i = 0; i<currentrow;i++){
                output.printf("Patient %d\t: %s\n", i+1, patientRecord[i][0]);
                output.printf("Age \t\t: %s\n", patientRecord[i][1]);
                output.printf("Phone number \t: %s\n", patientRecord[i][2]);
                if(patientRecord[i][3]!=null){
                    output.printf("Appointment Day and Session : %s, %s\n", patientRecord[i][3], patientRecord[i][4]);
                }
                output.println("**********************************");
            }
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            output.printf("Record Updated on : %s\n",dtf.format(now));
        }
        catch(FileNotFoundException ex){
            System.out.println("File was not created succesfully..");
        }
        
        updateFile(currentrow, patientRecord, schedule);
    }
    
    /*
    There are 2 feature for this function
    1. Create file for patientrecord. The file is for system read when we restart the system.
    2. read file feature, to read file and to update the file
    Basically its the same with createFile function but create File is for better ux design 
    and updateFile is for system. 
    */
    public static void updateFile(int currentrow, String[][] patientRecord, char[] schedule){
        File file = new File("systemRecord.txt");
        File file2 = new File("appointment.txt");
        
        try(PrintWriter output = new PrintWriter(file)){
            for(int i = 0; i<currentrow;i++){
                    output.print(patientRecord[i][0]+","+patientRecord[i][1]+","+patientRecord[i][2]);
                    if(patientRecord[i][4]!=null){
                        output.print(","+patientRecord[i][3]);
                        output.print(","+patientRecord[i][4]);
                    }
                    output.print(".");
            }
        }
        catch(FileNotFoundException ex){
            System.out.println("Error occured, record cannot be updated.");
        }
        
        try(PrintWriter output = new PrintWriter(file2)){
            for(int i = 1; i<=5;i++){
                output.print(schedule[i]);
            }
        }
        catch(FileNotFoundException ex){
            System.out.println("Schedule cannot being updated");
        }
        
        if(currentrow == 0){
            file.delete();
            file2.delete();
        }
    }
}
