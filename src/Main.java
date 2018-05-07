/* Samuel E. Carrillo 9/20/17 - 9/xx/17
 * CSC 406 Problem 2
 */

import java.io.*;
import java.io.IOException;
import javax.swing.*;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.lang.*;//allows threads to be crated as objects from Thread class
import java.util.concurrent.*;//allows the creation of a threadpool
import java.util.concurrent.locks.*;//allows us to lock a function


public class Main {

    public static void main(String[] args) throws Exception {
        // the printwriter for the tasks
        PrintWriter outf1;
        outf1 = new PrintWriter(new File("CSC406ProblemTwo1.txt"));
        // create the thread pool
        ExecutorService executor = Executors.newFixedThreadPool(9);
        Router centralRouter = new Router(outf1);

        //has to be passed router to access 2d array
        executor.execute(new Job("PB", 1, 'D', 60000,centralRouter));// 60K
        executor.execute(new Job("PB", 3, 'P', 100000,centralRouter));// 100K
        executor.execute(new Job("PB", 2, 'D', 75000,centralRouter));// 75K
        executor.execute(new Job("FB", 1, 'P', 30000,centralRouter));// 30K
        executor.execute(new Job("FB", 2, 'D', 150000,centralRouter));// 150K
        executor.execute(new Job("FB", 3, 'P', 89000,centralRouter));// 89K
        executor.execute(new Job("MB", 1, 'P', 200000,centralRouter));// 200K
        executor.execute(new Job("MB", 2, 'D', 140000,centralRouter));// 140K
        executor.execute(new Job("MB", 3, 'P', 135000,centralRouter));// 135K

        // now shut down the executor
        executor.shutdown();

        // now let all threads shut down and wait till all tasks are finished
        // prints out the final counts
        while (!executor.isTerminated());

        //PB PRINT
        System.out.println("PB Print service charge: " + (centralRouter.getServiceChargeP("PB")*.007)
                + ", PB Data service charge: " + (centralRouter.getServiceChargeD("PB")*.008) + ", Total cost of service charges for branch: "
                + ((centralRouter.getServiceChargeP("PB")*.007) + centralRouter.getServiceChargeD("PB")*.008));
        System.out.flush();
        System.out.println("Total number of characters processed for Data for Production Branch: "+centralRouter.getServiceChargeD("PB"));
        System.out.println("Total number of characters processed for Print for Production Branch: "+centralRouter.getServiceChargeP("PB"));
        System.out.println();
        //FB PRINT
        System.out.println("FB Print service charge: " + (centralRouter.getServiceChargeP("FB")*.009)
                + ", FB Data service charge: " + (centralRouter.getServiceChargeD("FB")*.007) + ", Total cost of service charges for branch: "
                + ((centralRouter.getServiceChargeP("FB")*.009) + (centralRouter.getServiceChargeD("FB")*.007)));
        System.out.flush();
        System.out.println("Total number of characters processed for Data for Production Branch: "+centralRouter.getServiceChargeD("FB"));
        System.out.println("Total number of characters processed for Print for Production Branch: "+centralRouter.getServiceChargeP("FB"));
        System.out.println();
        //MB PRINT
        System.out.println("MB Print service charge: " + (centralRouter.getServiceChargeP("MB")*.0095)
                + ", MB Data service charge: " + (centralRouter.getServiceChargeD("MB")*.0082) + ", Total cost of service charges for branch: "
                + ((centralRouter.getServiceChargeP("MB")*.0095) + (centralRouter.getServiceChargeD("MB")*.0082)));
        System.out.flush();
        System.out.println("Total number of characters processed for Data for Production Branch: "+centralRouter.getServiceChargeD("MB"));
        System.out.println("Total number of characters processed for Print for Production Branch: "+centralRouter.getServiceChargeP("MB"));
        System.out.println();


        //PB PRINT OUTF1
        outf1.println("PB Print service charge: " + (centralRouter.getServiceChargeP("PB")*.007)
                + ", PB Data service charge: " + (centralRouter.getServiceChargeD("PB")*.008) + ", Total cost of service charges for branch: "
                + ((centralRouter.getServiceChargeP("PB")*.007) + centralRouter.getServiceChargeD("PB")*.008));
        outf1.flush();
        outf1.println("Total number of characters processed for Data for Production Branch: "+centralRouter.getServiceChargeD("PB"));
        outf1.flush();
        outf1.println("Total number of characters processed for Print for Production Branch: "+centralRouter.getServiceChargeP("PB"));
        outf1.flush();
        outf1.println();
        //FB PRINT OUTF1
        outf1.println("FB Print service charge: " + (centralRouter.getServiceChargeP("FB")*.009)
                + ", FB Data service charge: " + (centralRouter.getServiceChargeD("FB")*.007) + ", Total cost of service charges for branch: "
                + ((centralRouter.getServiceChargeP("FB")*.009) + (centralRouter.getServiceChargeD("FB")*.007)));
        outf1.flush();
        outf1.println("Total number of characters processed for Data for Production Branch: "+centralRouter.getServiceChargeD("FB"));
        outf1.flush();
        outf1.println("Total number of characters processed for Print for Production Branch: "+centralRouter.getServiceChargeP("FB"));
        outf1.flush();
        outf1.println();
        //MB PRINT OUTF1
        outf1.println("MB Print service charge: " + (centralRouter.getServiceChargeP("MB")*.0095)
                + ", MB Data service charge: " + (centralRouter.getServiceChargeD("MB")*.0082) + ", Total cost of service charges for branch: "
                + ((centralRouter.getServiceChargeP("MB")*.0095) + (centralRouter.getServiceChargeD("MB")*.0082)));
        outf1.flush();
        outf1.println("Total number of characters processed for Data for Production Branch: "+centralRouter.getServiceChargeD("MB"));
        outf1.flush();
        outf1.println("Total number of characters processed for Print for Production Branch: "+centralRouter.getServiceChargeP("MB"));
        outf1.flush();
        outf1.println();
        //close printwriter
        outf1.close();

    }// end of main

    public static class Router {
        PrintWriter outf;//print writer with scope set to class router
        static double serviceChargeP, serviceChargeD;
        String branch;
        int port;
        char dataOrPrint; // data = d, print = p
        int amtOfChars;
        static int [][] routerCost = new int [3][2];//needs to be static
        private static Lock lock = new ReentrantLock(); // the thread lock

        // Router constructor, initializes all to zero
        public Router(PrintWriter inPW) {
            outf = inPW;
            port = 0;
            serviceChargeP=0;
            serviceChargeD=0;
            dataOrPrint=' ';
            amtOfChars = 0;
        }

        public double getServiceChargeP(String inBranch) {
            if (inBranch == "PB"){
                serviceChargeP = routerCost[0][1];
                return serviceChargeP;
            }else if(inBranch =="FB"){
                serviceChargeP = routerCost[1][1];
                return serviceChargeP;
            }else if(inBranch == "MB"){
                serviceChargeP = routerCost[2][1];
            }
            return serviceChargeP;
        }

        public double getServiceChargeD(String inBranch) {
            if (inBranch == "PB"){
                serviceChargeP = routerCost[0][0];
                return serviceChargeP;
            }else if(inBranch =="FB"){
                serviceChargeP = routerCost[1][0];
                return serviceChargeP;
            }else if(inBranch == "MB"){
                serviceChargeP = routerCost[2][0];
            }
            return serviceChargeP;
        }

        // calcservicecharge>jobtask run()>main
        public void aggregateCharsIntoArray(int amtOfChars, String inBranch, char dataOrPrint) {
            lock.lock();
            switch (inBranch) {
                // production branch case
                case "PB":
                    if (dataOrPrint == 'D') {
                        //serviceChargeD = .008 * amtOfChars;
                        routerCost[0][0] +=amtOfChars;
                    }
                    if (dataOrPrint == 'P') {
                        //serviceChargeP = .007 * amtOfChars;
                        routerCost[0][1] += amtOfChars;
                    }

                    break;
                // financial branch case
                case "FB":
                    if (dataOrPrint == 'D') {
                        //serviceChargeD = .007 * amtOfChars;
                        routerCost[1][0]+= amtOfChars;

                    }
                    if (dataOrPrint == 'P') {
                        //serviceChargeP = .009 * amtOfChars;
                        routerCost[1][1]+=amtOfChars;
                    }
                    break;
                // marketing branch case
                case "MB":
                    if (dataOrPrint == 'D') {
                        //serviceChargeD = .0082 * amtOfChars;
                        routerCost[2][0] += amtOfChars;
                    }
                    if (dataOrPrint == 'P') {
                        //serviceChargeP = .0095 * amtOfChars;
                        routerCost[2][1] +=amtOfChars;
                    }

                    break;
            }// end of case switch

//			for(int i =0;i<=3;i++){
//				for(int j=0;j<=i;j++){
//					System.out.println("this is whats in the array "+routerCost[i][j]);
//				}
//			}

            lock.unlock(); // releases the lock
        }// end of calculateServiceCharge
    }// end of class router

    // job task object
    public static class Job implements Runnable {
        // the parameters for job task
        Router myRouter;
        String branch;
        int port;
        char dataOrPrint; // data = d, print = p
        int amtOfChars;
        double serviceChargeD, serviceChargeP;

        // job constructor
        public Job(String inBranch, int inPort, char inDataOrPrint, int chars, Router inRouter) {
            branch = inBranch;
            port = inPort;
            dataOrPrint = inDataOrPrint;
            amtOfChars = chars;
            myRouter = inRouter;

        }

        // the run
        public void run() {
            try {
                myRouter.aggregateCharsIntoArray(amtOfChars, branch, dataOrPrint);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }// end of run
    }// end of class jobTask

}// end of file