import myClasses.CentralHeuristicFunction0;
import myClasses.CentralSuccessorFunctionSA;
import myClasses.CentralHeuristicFunction3;
import myClasses.CentralHeuristicFunction2;
import myClasses.CentralHeuristicFunction1;
import myClasses.CentralBoard;
import myClasses.CentralGoalTest;
import myClasses.CentralSuccessorFunctionHC;
import IA.Energia.Clientes;
import IA.Energia.Centrales;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;
import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import myClasses.CentralHeuristicFunction4;


public class Main {

    static int nReplica        = 10;
    static long execTime       = 0;
    
    static int seed            = 0;
    
    static int[] centrType     = {5, 10, 25};
    static int numCentr        = 20;
    static int nClient         = 1000;
    static double[] clientType = {0.25, 0.3, 0.45};
    static double clientGarant = 0.75;
    
    static int solIni          = 3;
    static int heuristic       = 3;
    
    static int nSolIni         = 6;
    static int nHeuristic      = 4;
    
    static File out;
    static PrintWriter writer;

    public static void main(String[] args) throws Exception 
    {    
        String pathToFile = new String();
        //pathToFile = createNewFile();
        pathToFile = "output.csv";
        
        out = new File(pathToFile);
        writer = new PrintWriter(out);
        writer.println("PenalConst,SearchAlg,NodesExpd,ExecTime,Profits,Valid");
        
        System.out.println("Printing results to: " + pathToFile);
        
        Random random = new Random();
        for(int replica = 1; replica <= Main.nReplica; ++replica)
        {
            seed = random.nextInt(1500);    
            System.out.println("> Rèplica #" + replica + " amb seed: " + seed);
            Clientes clientes = new Clientes(nClient, clientType, clientGarant, seed);
            Centrales centrales = new Centrales(centrType, seed);
            CentralBoard board = new CentralBoard(centrales, clientes, Main.solIni);
            writer.print("10000,");
            
            writer.print("HC,");
            SearchHC(board, Main.heuristic);
            
            writer.print("SA,");
            SearchSA(board, Main.heuristic);
            
        }
        writer.close();   
    }

    private static void SearchHC(CentralBoard board, int heuristic) throws Exception {
            System.out.println("Hill Climbing");
            long timeInit = System.currentTimeMillis();
            Problem p;
            switch(heuristic){   
                case 0:
                    p = new Problem(board, new CentralSuccessorFunctionHC(), new CentralGoalTest(), new CentralHeuristicFunction0());
                    break;
                case 1:
                    p = new Problem(board, new CentralSuccessorFunctionHC(), new CentralGoalTest(), new CentralHeuristicFunction1());
                    break;
                case 2:
                    p = new Problem(board, new CentralSuccessorFunctionHC(), new CentralGoalTest(), new CentralHeuristicFunction2());
                    break;
                case 3:
                    p = new Problem(board, new CentralSuccessorFunctionHC(), new CentralGoalTest(), new CentralHeuristicFunction3());
                    break;
                case 4:
                    p = new Problem(board, new CentralSuccessorFunctionHC(), new CentralGoalTest(), new CentralHeuristicFunction4());
                    break;
                default:
                    p = new Problem(board, new CentralSuccessorFunctionHC(), new CentralGoalTest(), new CentralHeuristicFunction0());
            }
            Search s = new HillClimbingSearch();
            SearchAgent a = new SearchAgent(p, s);
            long timeFin = System.currentTimeMillis();
            
            printActions(a.getActions());
            printInstrumentation(a.getInstrumentation());
            CentralBoard estatFinal = (CentralBoard)s.getGoalState();
            
            double benefici = (-1.0)*estatFinal.centralHeuristic0();
            execTime = timeFin-timeInit;
            
            writer.print(execTime + "," + ((int) benefici) + "," + estatFinal.isValid() +"\n");
            
            System.out.println("Execution Time: " + execTime + "ms");
            System.out.println("Benefici: " + ((int) benefici) + "\n");
            System.out.println("Valid: " + estatFinal.isValid());
            System.out.println("_______________________________\n");
    }
    
    private static void SearchSA(CentralBoard board, int heuristic) throws Exception {
        System.out.println("Simulated Annealing");
        long timeInit = System.currentTimeMillis();
        Problem p;
        switch(heuristic){   
            case 0:
                p = new Problem(board, new CentralSuccessorFunctionSA(), new CentralGoalTest(), new CentralHeuristicFunction0());
                break;
            case 1:
                p = new Problem(board, new CentralSuccessorFunctionSA(), new CentralGoalTest(), new CentralHeuristicFunction1());
                break;
            case 2:
                p = new Problem(board, new CentralSuccessorFunctionSA(), new CentralGoalTest(), new CentralHeuristicFunction2());
                break;
            case 3:
                p = new Problem(board, new CentralSuccessorFunctionSA(), new CentralGoalTest(), new CentralHeuristicFunction3());
                break;
            case 4:
                    p = new Problem(board, new CentralSuccessorFunctionHC(), new CentralGoalTest(), new CentralHeuristicFunction4());
                    break;
            default:
                p = new Problem(board, new CentralSuccessorFunctionSA(), new CentralGoalTest(), new CentralHeuristicFunction0());
        }
        Search s = new SimulatedAnnealingSearch(40000,60,64,0.1);
        SearchAgent agent = new SearchAgent(p, s);
        long timeFin = System.currentTimeMillis();
        
        printActions(agent.getActions());
        printInstrumentation(agent.getInstrumentation());
        
        CentralBoard estatFinal = (CentralBoard)s.getGoalState();
        double benefici = (-1.0)*estatFinal.centralHeuristic0();
        execTime = timeFin-timeInit;

        writer.print(execTime + "," + ((int) benefici) + "," + estatFinal.isValid() +"\n");

        System.out.println("Tiempo de ejecución: " + execTime + "ms");
        System.out.println("Benefici: " + ((int) benefici));
        System.out.println("Valid: " + estatFinal.isValid());
        System.out.println("_______________________________\n");
    }
   
    private static void printInstrumentation(Properties properties) {
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
            if (key.equals("nodesExpanded")) {
                writer.print(property + ",");
            }
        }
    }
    
    private static void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i).toString();
            //  System.out.println(action);
        }
    }
    
    private static String readFilename(){
        String s = new String();
        try (Scanner reader = new Scanner(System.in)) { 
            System.out.println("Enter the output filename: ");
            s = reader.next();
            reader.close();  
        }
        return s;
    }
    
    private static String createNewFile() {
        String s = new String();
        s = readFilename();
        String path = new String();
        path = s + ".csv";
        return path;
    }
}