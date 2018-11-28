package myClasses;

import IA.Energia.Central;
import IA.Energia.Centrales;
import IA.Energia.Cliente;
import IA.Energia.Clientes;
import IA.Energia.VEnergia;
import aima.util.Pair;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author BernatF
 */
public class CentralBoard {

    public static int getnCentrals() {
        return nCentrals;
    }

    public static int getnClients() {
        return nClients;
    }
    
    static Centrales centrals;
    static Clientes clients;
    static int nCentrals;
    static int nClients;
    static double[][] dist;
    
    private double costCentrals;
    private double costIndemns;
    private double benefitClients;
    private double distanceLoss;
    
    private int[] asigs;
    private double[] consum;
    
    public CentralBoard(CentralBoard prev){
        this.asigs = new int[prev.asigs.length];
        this.consum = new double[prev.consum.length];
        for(int i = 0; i < CentralBoard.nClients; ++i){
            this.asigs[i] = prev.asigs[i];
        }
         for(int i = 0; i < CentralBoard.nCentrals; ++i){
            this.consum[i] = prev.consum[i];
        }
        this.costCentrals = prev.costCentrals;
        this.benefitClients = prev.benefitClients;
        this.costIndemns = prev.costIndemns;
        this.distanceLoss = prev.distanceLoss;
    }
    
    public CentralBoard(Centrales ce, Clientes cl, int solIni ) throws Exception{
        CentralBoard.centrals = ce;
        CentralBoard.clients = cl;
        CentralBoard.nCentrals = ce.size();
        CentralBoard.nClients = cl.size();
        
        CentralBoard.dist = new double[CentralBoard.nClients][CentralBoard.nCentrals];
        
        for(int i = 0; i < CentralBoard.nClients; ++i){
            for(int j = 0; j < CentralBoard.nCentrals; ++j){
                dist[i][j] = CentralBoard.getDist(i, j);
            }
        }
        
        this.asigs = new int[CentralBoard.nClients];
        this.benefitClients = 0;
        this.costIndemns = 0;
        for(int i = 0; i < CentralBoard.nClients; ++i){
            this.asigs[i] = -1;
            if(CentralBoard.clients.get(i).getContrato() == Cliente.NOGARANTIZADO) this.costIndemns += this.getIndemnClient(i);
        }
        
        this.consum = new double[CentralBoard.nCentrals];
        this.costCentrals = 0;
        for(int i = 0; i < CentralBoard.nCentrals; ++i){
            this.consum[i] = 0.0;
            this.costCentrals += VEnergia.getCosteParada(CentralBoard.centrals.get(i).getTipo());
        }
        
        this.distanceLoss = 0;
        
        switch(solIni){
            case 0:
                solIni0(); //Iterate Guaranteed
                break;
            case 1:
                solIni1(); //Iterate All
                break;
            case 2:
                solIni2(); //Proximity Guaranteed
                break;
            case 3:
                solIni3(); //Proximity All
                break;
            case 4:
                solIni4(); //Random Guaranteed
                break;
            case 5:
                solIni5(); //Random All
                break;
            case 6:        //Empty
                break;
        }
    }
    
    private void solIni0() throws Exception{
        for(int i = 0; i < CentralBoard.nClients; ++i){
            if(CentralBoard.clients.get(i).getContrato() == Cliente.GARANTIZADO){
                int j = this.getAvailableCentralIndex0(i);
                if(j == -1) throw new Exception("Couldn't do Initial Soluction");
                this.doAdd(i,j);
            }
        }
    }
    
    private void solIni1() throws Exception{
        for(int i = 0; i < CentralBoard.nClients; ++i){
            if(CentralBoard.clients.get(i).getContrato() == Cliente.GARANTIZADO){
                int j = this.getAvailableCentralIndex0(i);
                if(j == -1) throw new Exception("Couldn't do Initial Soluction");
                this.doAdd(i,j);
            }
        }
        for(int i = 0; i < CentralBoard.nClients; ++i){
            if(CentralBoard.clients.get(i).getContrato() == Cliente.NOGARANTIZADO){
                int j = this.getAvailableCentralIndex0(i);
                if(j != -1) this.doAdd(i,j);
            }
        }
    }
    
    private void solIni2() throws Exception{
        for(int i = 0; i < CentralBoard.nClients; ++i){
            if(CentralBoard.clients.get(i).getContrato() == Cliente.GARANTIZADO){
                int j = this.getAvailableCentralIndex1(i);
                if(j == -1) throw new Exception("Couldn't do Initial Soluction");
                this.doAdd(i,j);
            }
        }
    }
    
    private void solIni3() throws Exception{
        for(int i = 0; i < CentralBoard.nClients; ++i){
            if(CentralBoard.clients.get(i).getContrato() == Cliente.GARANTIZADO){
                int j = this.getAvailableCentralIndex1(i);
                if(j == -1) throw new Exception("Couldn't do Initial Soluction");
                this.doAdd(i,j);
            }
        }
        for(int i = 0; i < CentralBoard.nClients; ++i){
            if(CentralBoard.clients.get(i).getContrato() == Cliente.NOGARANTIZADO){
                int j = this.getAvailableCentralIndex1(i);
                if(j != -1) this.doAdd(i,j);
            }
        }
    }
    
    private void solIni4() throws Exception{
        Collections.shuffle(CentralBoard.centrals);
        Collections.shuffle(CentralBoard.clients);
        ArrayList<Integer> it = new ArrayList();
        for(int i = 0; i < CentralBoard.nClients; ++i){
            it.add(i);
        }
        Collections.shuffle(it);
        for(int i = 0; i < it.size(); ++i){
            if(CentralBoard.clients.get(it.get(i)).getContrato() == Cliente.GARANTIZADO){
                int j = this.getAvailableCentralIndex2(it.get(i));
                if(j == -1) throw new Exception("Couldn't do Initial Soluction");
                this.doAdd(it.get(i),j);
            }
        }
    }
    
    private void solIni5() throws Exception{
        Collections.shuffle(CentralBoard.centrals);
        Collections.shuffle(CentralBoard.clients);
        ArrayList<Integer> it = new ArrayList();
        for(int i = 0; i < CentralBoard.nClients; ++i){
            it.add(i);
        }
        Collections.shuffle(it);
        for(int i = 0; i < it.size(); ++i){
            if(CentralBoard.clients.get(it.get(i)).getContrato() == Cliente.GARANTIZADO){
                int j = this.getAvailableCentralIndex2(it.get(i));
                if(j == -1) throw new Exception("Couldn't do Initial Soluction");
                this.doAdd(it.get(i),j);
            }
        }
        for(int i = 0; i < it.size(); ++i){
            if(CentralBoard.clients.get(it.get(i)).getContrato() == Cliente.NOGARANTIZADO){
                int j = this.getAvailableCentralIndex2(it.get(i));
                if(j == -1) this.doAdd(it.get(i),j);
            }
        }
    }
    
    private int getAvailableCentralIndex0(int iClient){
        for(int i = 0; i < CentralBoard.nCentrals; ++i){
            if(this.canAdd(iClient, i)) return i;
        }
        return -1;
    }
    
    private int getAvailableCentralIndex1(int iClient){
        Pair dists[] = new Pair[CentralBoard.nCentrals];
        for(int i = 0; i < CentralBoard.nCentrals; ++i){
            dists[i] = new Pair(CentralBoard.dist[iClient][i], i);
        }
        Arrays.sort(dists, new Comparator<Pair>(){
            @Override
            public int compare(Pair p1, Pair p2) {
                return ((Double) p1.getFirst()).compareTo(((Double) p2.getFirst()));
            }
        });
        for(int i = 0; i < CentralBoard.nCentrals; ++i){
            if(this.canAdd(iClient, (Integer) dists[i].getSecond())) return (Integer) dists[i].getSecond();
        }
        return -1;
    }
    
    private int getAvailableCentralIndex2(int iClient){
        ArrayList<Integer> it = new ArrayList();
        for(int i = 0; i < CentralBoard.nCentrals; ++i){
            it.add(i);
        }
        Collections.shuffle(it);
        for(int i = 0; i < it.size(); ++i){
            if(this.canAdd(iClient, it.get(i))) return it.get(i);
        }
        return -1;
    }
     
    public boolean canRemove(int iClient){
        if(CentralBoard.clients.get(iClient).getContrato() == Cliente.GARANTIZADO) return false;
        return this.asigs[iClient] != -1;
    }
    
    public void doRemove(int iClient) throws Exception{
        this.consum[this.asigs[iClient]] -= CentralBoard.clients.get(iClient).getConsumo()*(1 + CentralBoard.getLossDist(iClient,this.asigs[iClient]));
        if((int) this.consum[this.asigs[iClient]] == 0){
                this.costCentrals -= VEnergia.getCosteMarcha(CentralBoard.centrals.get(this.asigs[iClient]).getTipo()) + 
                                     VEnergia.getCosteProduccionMW(CentralBoard.centrals.get(this.asigs[iClient]).getTipo())*CentralBoard.centrals.get(this.asigs[iClient]).getProduccion();
                this.costCentrals += VEnergia.getCosteParada(CentralBoard.centrals.get(this.asigs[iClient]).getTipo());
        }
        this.distanceLoss -= CentralBoard.clients.get(iClient).getConsumo() *
                             CentralBoard.getLossDist(iClient, this.asigs[iClient]) *
                             VEnergia.getCosteProduccionMW(CentralBoard.centrals.get(this.asigs[iClient]).getTipo());
        this.asigs[iClient] = -1;
        this.benefitClients -= this.getBenefitClient(iClient);
        this.costIndemns += this.getIndemnClient(iClient);
    }
    
    public boolean canAdd(int iClient, int iCentral){
        if(iCentral == this.asigs[iClient]) return false;
        return (CentralBoard.centrals.get(iCentral).getProduccion() - this.consum[iCentral] >= CentralBoard.clients.get(iClient).getConsumo()*(1 + CentralBoard.getLossDist(iClient,iCentral)));
    }
    
    public void doAdd(int iClient, int iCentral) throws Exception{
        int prevAssig = this.asigs[iClient];
        this.asigs[iClient] = iCentral;
        if((int) this.consum[iCentral] == 0){
            this.costCentrals -= VEnergia.getCosteParada(CentralBoard.centrals.get(iCentral).getTipo());
            this.costCentrals += VEnergia.getCosteMarcha(CentralBoard.centrals.get(iCentral).getTipo()) + 
                                 VEnergia.getCosteProduccionMW(CentralBoard.centrals.get(iCentral).getTipo())*CentralBoard.centrals.get(iCentral).getProduccion();
        }
        this.consum[iCentral] += CentralBoard.clients.get(iClient).getConsumo()*(1 + CentralBoard.getLossDist(iClient,iCentral));
        if(prevAssig != -1){
            this.consum[prevAssig] -= CentralBoard.clients.get(iClient).getConsumo()*(1 + CentralBoard.getLossDist(iClient,prevAssig));
            if((int) this.consum[prevAssig] == 0){
                this.costCentrals -= VEnergia.getCosteMarcha(CentralBoard.centrals.get(prevAssig).getTipo()) + 
                                     VEnergia.getCosteProduccionMW(CentralBoard.centrals.get(prevAssig).getTipo())*CentralBoard.centrals.get(prevAssig).getProduccion();
                this.costCentrals += VEnergia.getCosteParada(CentralBoard.centrals.get(prevAssig).getTipo());
            }
            this.distanceLoss -= CentralBoard.clients.get(iClient).getConsumo() *
                                 CentralBoard.getLossDist(iClient, prevAssig) *
                                 VEnergia.getCosteProduccionMW(CentralBoard.centrals.get(prevAssig).getTipo());
        }
        else{
            this.benefitClients += this.getBenefitClient(iClient);
            this.costIndemns -= this.getIndemnClient(iClient);
        }
        this.distanceLoss += CentralBoard.clients.get(iClient).getConsumo() *
                             CentralBoard.getLossDist(iClient, this.asigs[iClient]) *
                             VEnergia.getCosteProduccionMW(CentralBoard.centrals.get(this.asigs[iClient]).getTipo());
    }
    
    public boolean canAnyRemove(){
        for(int i = 0; i < CentralBoard.nClients; ++i){
            if(this.canRemove(i)) return true;
        }
        return false;
    }
    
    public boolean canAnyAdd(){
        for(int i = 0; i < CentralBoard.nClients; ++i){
            for(int j = 0; j < CentralBoard.nCentrals; ++j){
                if(this.canAdd(i, j)) return true;
            }
        }
        return false;
    }
    
    public boolean canAnySwap(){
        for(int i = 0; i < CentralBoard.nClients; ++i){
            for(int j = 0; j < CentralBoard.nClients; ++j){
                if(this.canSwap(i, j)) return true;
            }
        }
        return false;
    }
    
    public boolean canSwap(int iClient1, int iClient2){
        if (iClient1 == iClient2) return false;
        if(this.asigs[iClient1] == this.asigs[iClient2]) return false;
        if(this.asigs[iClient1] == -1){
            if(CentralBoard.clients.get(iClient2).getContrato() == Cliente.GARANTIZADO) return false;
            else{
                double consumo1 = CentralBoard.clients.get(iClient1).getConsumo();
                double consumo2 = CentralBoard.clients.get(iClient2).getConsumo();
                double perdida12 = CentralBoard.getLossDist(iClient1, this.asigs[iClient2]);
                double perdida22 = CentralBoard.getLossDist(iClient2, this.asigs[iClient2]);
                double prod2 = CentralBoard.centrals.get(this.asigs[iClient2]).getProduccion();
                return prod2 - this.consum[this.asigs[iClient2]] >= consumo1*(1+perdida12) - consumo2*(1+perdida22);
            }
        }
        else if(this.asigs[iClient2] == -1){
            if(CentralBoard.clients.get(iClient1).getContrato() == Cliente.GARANTIZADO) return false;
            else{
                double consumo1 = CentralBoard.clients.get(iClient1).getConsumo();
                double consumo2 = CentralBoard.clients.get(iClient2).getConsumo();
                double perdida21 = CentralBoard.getLossDist(iClient2, this.asigs[iClient1]);
                double perdida11 = CentralBoard.getLossDist(iClient1, this.asigs[iClient1]);
                double prod1 = CentralBoard.centrals.get(this.asigs[iClient1]).getProduccion();
                return prod1 - this.consum[this.asigs[iClient1]] >= consumo2*(1+perdida21) - consumo1*(1+perdida11);
            }
        }
        else{
            double consumo1 = CentralBoard.clients.get(iClient1).getConsumo();
            double consumo2 = CentralBoard.clients.get(iClient2).getConsumo();
            double perdida21 = CentralBoard.getLossDist(iClient2, this.asigs[iClient1]);
            double perdida11 = CentralBoard.getLossDist(iClient1, this.asigs[iClient1]);
            double perdida12 = CentralBoard.getLossDist(iClient1, this.asigs[iClient2]);
            double perdida22 = CentralBoard.getLossDist(iClient2, this.asigs[iClient2]);
            double prod1 = CentralBoard.centrals.get(this.asigs[iClient1]).getProduccion();
            double prod2 = CentralBoard.centrals.get(this.asigs[iClient2]).getProduccion();
            return prod1 - this.consum[this.asigs[iClient1]] >= consumo2*(1+perdida21) - consumo1*(1+perdida11) &&
                   prod2 - this.consum[this.asigs[iClient2]] >= consumo1*(1+perdida12) - consumo2*(1+perdida22);
        }
    }
    
    public void doSwap(int iClient1, int iClient2) throws Exception{
        if(this.asigs[iClient1] == -1){
            double consumo1 = CentralBoard.clients.get(iClient1).getConsumo();
            double consumo2 = CentralBoard.clients.get(iClient2).getConsumo();
            double perdida12 = CentralBoard.getLossDist(iClient1, this.asigs[iClient2]);
            double perdida22 = CentralBoard.getLossDist(iClient2, this.asigs[iClient2]);
            this.consum[this.asigs[iClient2]] -= consumo2*(1 + perdida22);
            this.consum[this.asigs[iClient2]] += consumo1*(1 + perdida12);
            this.asigs[iClient1] = this.asigs[iClient2];
            this.asigs[iClient2] = -1;
            this.benefitClients += this.getBenefitClient(iClient1);
            this.benefitClients -= this.getBenefitClient(iClient2);
            this.costIndemns -= this.getIndemnClient(iClient1);
            this.costIndemns += this.getIndemnClient(iClient2);
            this.distanceLoss += CentralBoard.clients.get(iClient1).getConsumo() *
                                 CentralBoard.getLossDist(iClient1, this.asigs[iClient1]) *
                                 VEnergia.getCosteProduccionMW(CentralBoard.centrals.get(this.asigs[iClient1]).getTipo());
            this.distanceLoss -= CentralBoard.clients.get(iClient2).getConsumo() *
                                 CentralBoard.getLossDist(iClient2, this.asigs[iClient1]) *
                                 VEnergia.getCosteProduccionMW(CentralBoard.centrals.get(this.asigs[iClient1]).getTipo());
        }
        else if(this.asigs[iClient2] == -1){
            double consumo1 = CentralBoard.clients.get(iClient1).getConsumo();
            double consumo2 = CentralBoard.clients.get(iClient2).getConsumo();
            double perdida21 = CentralBoard.getLossDist(iClient2, this.asigs[iClient1]);
            double perdida11 = CentralBoard.getLossDist(iClient1, this.asigs[iClient1]);
            this.consum[this.asigs[iClient1]] -= consumo1*(1 + perdida11);
            this.consum[this.asigs[iClient1]] += consumo2*(1 + perdida21);
            this.asigs[iClient2] = this.asigs[iClient1];
            this.asigs[iClient1] = -1;
            this.benefitClients -= this.getBenefitClient(iClient1);
            this.benefitClients += this.getBenefitClient(iClient2);
            this.costIndemns += this.getIndemnClient(iClient1);
            this.costIndemns -= this.getIndemnClient(iClient2);
            this.distanceLoss += CentralBoard.clients.get(iClient2).getConsumo() *
                                 CentralBoard.getLossDist(iClient2, this.asigs[iClient2]) *
                                 VEnergia.getCosteProduccionMW(CentralBoard.centrals.get(this.asigs[iClient2]).getTipo());
            this.distanceLoss -= CentralBoard.clients.get(iClient1).getConsumo() *
                                 CentralBoard.getLossDist(iClient1, this.asigs[iClient2]) *
                                 VEnergia.getCosteProduccionMW(CentralBoard.centrals.get(this.asigs[iClient2]).getTipo());
        }
        else{
            double consumo1 = CentralBoard.clients.get(iClient1).getConsumo();
            double consumo2 = CentralBoard.clients.get(iClient2).getConsumo();
            double perdida21 = CentralBoard.getLossDist(iClient2, this.asigs[iClient1]);
            double perdida11 = CentralBoard.getLossDist(iClient1, this.asigs[iClient1]);
            double perdida12 = CentralBoard.getLossDist(iClient1, this.asigs[iClient2]);
            double perdida22 = CentralBoard.getLossDist(iClient2, this.asigs[iClient2]);
            this.consum[this.asigs[iClient1]] -= consumo1*(1 + perdida11);
            this.consum[this.asigs[iClient1]] += consumo2*(1 + perdida21);
            this.consum[this.asigs[iClient2]] -= consumo2*(1 + perdida22);
            this.consum[this.asigs[iClient2]] += consumo1*(1 + perdida12);
            int aux = this.asigs[iClient2];
            this.asigs[iClient2] = this.asigs[iClient1];
            this.asigs[iClient1] = aux;
            this.distanceLoss += CentralBoard.clients.get(iClient2).getConsumo() *
                                 CentralBoard.getLossDist(iClient2, this.asigs[iClient2]) *
                                 VEnergia.getCosteProduccionMW(CentralBoard.centrals.get(this.asigs[iClient2]).getTipo());
            this.distanceLoss -= CentralBoard.clients.get(iClient1).getConsumo() *
                                 CentralBoard.getLossDist(iClient1, this.asigs[iClient2]) *
                                 VEnergia.getCosteProduccionMW(CentralBoard.centrals.get(this.asigs[iClient2]).getTipo());
            this.distanceLoss += CentralBoard.clients.get(iClient1).getConsumo() *
                                 CentralBoard.getLossDist(iClient1, this.asigs[iClient1]) *
                                 VEnergia.getCosteProduccionMW(CentralBoard.centrals.get(this.asigs[iClient1]).getTipo());
            this.distanceLoss -= CentralBoard.clients.get(iClient2).getConsumo() *
                                 CentralBoard.getLossDist(iClient2, this.asigs[iClient1]) *
                                 VEnergia.getCosteProduccionMW(CentralBoard.centrals.get(this.asigs[iClient1]).getTipo());
        }
    }
    
    private static double getDist(int iClient, int iCentral){
        int xClient = CentralBoard.clients.get(iClient).getCoordX();
        int yClient = CentralBoard.clients.get(iClient).getCoordY();
        int xCentral = CentralBoard.centrals.get(iCentral).getCoordX();
        int yCentral = CentralBoard.centrals.get(iCentral).getCoordY();
        return sqrt(pow(xClient-xCentral, 2) + pow(yClient-yCentral,2));
    }
    
    private static double getLossDist(int iClient, int iCentral){
        return VEnergia.getPerdida(dist[iClient][iCentral]);
    }
    
    public boolean isGoal(){
        return false;
    }
    
    public double centralHeuristic0() throws Exception{ //BENEFIT
        double benefit = this.benefitClients - this.costIndemns - this.costCentrals;
        return benefit*(-1);
    }
    
    public double centralHeuristic1() throws Exception{ //BENEFIT & DISTANCE LOSS
        double benefit = this.benefitClients - this.costIndemns - this.costCentrals - this.distanceLoss;
        return benefit*(-1);
    }
    
    public double centralHeuristic2() throws Exception{ //BENEFIT & NON_PROVIDED LOSS
        double nonProvidedLoss = this.getNonProviderLoss();
        double benefit = this.benefitClients - this.costIndemns - this.costCentrals - nonProvidedLoss;
        return benefit*(-1);
    }
    
    public double centralHeuristic3() throws Exception{ //BENEFIT & NON_PROVIDED LOSS & DISTANCE LOSS
        double nonProvidedLoss = this.getNonProviderLoss();
        double benefit = this.benefitClients - this.costIndemns - this.costCentrals - nonProvidedLoss - this.distanceLoss;
        return benefit*(-1);
    }
    
    public double centralHeuristic4() throws Exception{ //BENEFIT & NON_PROVIDED LOSS & DISTANCE LOSS && NON GUARANTEED
        double nonProvidedLoss = this.getNonProviderLoss();
        double nonGuaranteedPenalty = this.getNonGuaranteedNotAssigned();
        double benefit = this.benefitClients - this.costIndemns - this.costCentrals - nonProvidedLoss - this.distanceLoss - nonGuaranteedPenalty;
        return benefit*(-1);
    }
    
    private double getNonGuaranteedNotAssigned(){
        int count = 0;
        for(int i = 0; i < CentralBoard.nClients; ++i){
            if(CentralBoard.clients.get(i).getContrato() == Cliente.GARANTIZADO && this.asigs[i] == -1) ++count;
        }
        return count;
    }
    
    private double getCostAllCentrals() throws Exception {
        double cost = 0;
        for(int i = 0; i < CentralBoard.nCentrals; ++i){
            cost += this.getCostCentral(i);
        }
        return cost;
    }
    
    private double getCostCentral(int iCentral) throws Exception{
        if((int) this.consum[iCentral] == 0){
            return VEnergia.getCosteParada(CentralBoard.centrals.get(iCentral).getTipo());
        }
        else{
            return VEnergia.getCosteMarcha(CentralBoard.centrals.get(iCentral).getTipo()) + 
                   VEnergia.getCosteProduccionMW(CentralBoard.centrals.get(iCentral).getTipo())*CentralBoard.centrals.get(iCentral).getProduccion();
        }
    }

    private double getCostAllIndemns() throws Exception {
        double cost = 0;
        for(int i = 0; i < CentralBoard.nClients; ++i){
            if(this.asigs[i] == -1)
                cost += this.getIndemnClient(i);
        }
        return cost;
    }
    
    private double getIndemnClient(int iClient) throws Exception{
        return VEnergia.getTarifaClientePenalizacion(CentralBoard.clients.get(iClient).getTipo())*CentralBoard.clients.get(iClient).getConsumo();
    }

    private double getBenefitAllClients() throws Exception {
        double benefit = 0;
        for(int i = 0; i < CentralBoard.nClients; ++i){
            if (this.asigs[i] != -1){
                benefit += this.getBenefitClient(i);
            }
        }
        return benefit;
    }
    
    private double getBenefitClient(int iClient) throws Exception{
        if (CentralBoard.clients.get(iClient).getContrato() == Cliente.GARANTIZADO){
            return CentralBoard.clients.get(iClient).getConsumo()*VEnergia.getTarifaClienteGarantizada(CentralBoard.clients.get(iClient).getTipo());
        }
        else{
            return CentralBoard.clients.get(iClient).getConsumo()*VEnergia.getTarifaClienteNoGarantizada(CentralBoard.clients.get(iClient).getTipo());
        }
    }
    
    private double getNonProviderLoss() throws Exception {
        double nonProvided = 0;
        for(int i = 0; i < CentralBoard.nCentrals; ++i){
            if((int) this.consum[i] != 0){
                nonProvided += (CentralBoard.centrals.get(i).getProduccion() - this.consum[i])*
                                VEnergia.getCosteProduccionMW(CentralBoard.centrals.get(i).getTipo());
            }
        }
        return nonProvided;
    }
    
    public void printBoard() throws Exception{
        boolean fullLogs = true;
        int assigned = 0;
        int unassigned = 0;
        int empty = 0;
        System.out.println("STATE DEFINITION:\n\n");
        for(int i = 0; i < CentralBoard.nClients;++i){
            if(fullLogs){
                System.out.println("    CLIENT NUMBER: " + i);
                System.out.println("        - Guaranteed Contract : " + (CentralBoard.clients.get(i).getContrato() == Cliente.GARANTIZADO));
                switch(CentralBoard.clients.get(i).getTipo()){
                    case Cliente.CLIENTEG:
                        System.out.println("        - Client Type : GRANDE");
                        break;
                    case Cliente.CLIENTEMG:
                        System.out.println("        - Client Type : MUY GRANDE");
                        break;
                    case Cliente.CLIENTEXG:
                        System.out.println("        - Client Type : EXTRA GRANDE");
                        break;
                }
                System.out.println("        - Consume : " + CentralBoard.clients.get(i).getConsumo());
                System.out.println("        - Tarifa : " + ((CentralBoard.clients.get(i).getContrato() == Cliente.GARANTIZADO) ? VEnergia.getTarifaClienteGarantizada(CentralBoard.clients.get(i).getTipo()) : VEnergia.getTarifaClienteNoGarantizada(CentralBoard.clients.get(i).getTipo()) ));
            }
            if (this.asigs[i] != -1){
                ++assigned;
                if(fullLogs){
                    System.out.println("        - Assigned to central number: " + this.asigs[i]);
                    System.out.println("             · With distance losses : " + CentralBoard.getLossDist(i, this.asigs[i]));
                    System.out.println("               (Distance) : " + CentralBoard.getDist(i, this.asigs[i]));
                    System.out.println("             · Total Consume : " + (1 + CentralBoard.getLossDist(i, this.asigs[i]))*CentralBoard.clients.get(i).getConsumo());
                }
            }
            else{
                ++unassigned;
                if(fullLogs) System.out.println("        - Unassigned with indemnization: " + VEnergia.getTarifaClientePenalizacion(CentralBoard.clients.get(i).getTipo()));
            }
            if(fullLogs) System.out.println();
        }
        System.out.println("    - Total unassigned: " +  unassigned);
        System.out.println("    - Total assigned: " +  assigned);
        System.out.println("----------------------");
        System.out.println();
        for(int i = 0; i < CentralBoard.nCentrals; ++i){
            if(fullLogs){
                System.out.println("    CENTRAL NUMBER: " + i);
                switch(CentralBoard.centrals.get(i).getTipo()){
                    case Central.CENTRALA:
                        System.out.println("        - Central Type : A");
                        break;
                    case Central.CENTRALB:
                        System.out.println("        - Central Type : B");
                        break;
                    case Central.CENTRALC:
                        System.out.println("        - Central Type : C");
                        break;
                }
                System.out.println("        - Production : " + CentralBoard.centrals.get(i).getProduccion());
                System.out.println("        - Coste Marcha : " + VEnergia.getCosteMarcha(CentralBoard.centrals.get(i).getTipo()));
                System.out.println("        - Coste Parada : " + VEnergia.getCosteParada(CentralBoard.centrals.get(i).getTipo()));
                System.out.println("        - Coste Produccion : " + VEnergia.getCosteProduccionMW(CentralBoard.centrals.get(i).getTipo()));
                System.out.println("        - Consumed : " + ((int) this.consum[i]));
            }
            if ((int) this.consum[i] == 0) ++ empty;
            if(fullLogs) System.out.println();
        }
        System.out.println("    - Total empty: " +  empty);
        System.out.println("----------------------");
        System.out.println();
        System.out.println("   PROFIT: " + this.centralHeuristic0()*(-1));
        System.out.println("       - Client Profit: " + this.getBenefitAllClients());
        System.out.println("       - Client Indemnization: " + this.getCostAllIndemns());
        System.out.println("       - Central Cost: " + this.getCostAllCentrals());
        System.out.println();
        System.out.println("**********************");
        System.out.println();
        System.out.println();
    }
    
    public boolean isValid(){
        for(int i = 0; i < CentralBoard.nClients; ++i){
            if(this.asigs[i] == -1 && CentralBoard.clients.get(i).getContrato() == Cliente.GARANTIZADO) return false;
        }
        return true;
    }
}
