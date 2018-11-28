/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myClasses;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CentralSuccessorFunctionSA implements SuccessorFunction{

    @Override
    public List getSuccessors(Object state){
        ArrayList retList = new ArrayList();
        CentralBoard board = (CentralBoard) state;
        Random random = new Random();
        boolean notRemove = false;
        boolean notAdd = false;
        boolean notSwap = false;
        boolean notAny = false;
        while(retList.isEmpty() && !notAny){
            notAny = notSwap && notAdd && notRemove;
            int chooseOp = random.nextInt(3);
            switch(chooseOp){
                case 0:
                    if (!notRemove && board.canAnyRemove()){
                        while(retList.isEmpty()){
                            int chooseClient = random.nextInt(CentralBoard.getnClients());
                            if(board.canRemove(chooseClient)){
                                try {
                                    CentralBoard aux = new CentralBoard(board);
                                    aux.doRemove(chooseClient);
                                    Successor S = new Successor("Removed client " + chooseClient, aux);
                                    retList.add(S);
                                } catch (Exception ex) {
                                    Logger.getLogger(CentralSuccessorFunctionSA.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                    else notRemove = true;
                    break;
                case 1:
                    if (!notAdd && board.canAnyAdd()){
                        while(retList.isEmpty()){
                            int chooseClient = random.nextInt(CentralBoard.getnClients());
                            int chooseCentral = random.nextInt(CentralBoard.getnCentrals());
                            if(board.canAdd(chooseClient, chooseCentral)){
                                try {
                                    CentralBoard aux = new CentralBoard(board);
                                    aux.doAdd(chooseClient,chooseCentral);
                                    Successor S = new Successor("Moved client " + chooseClient + " to central " + chooseCentral, aux);
                                    retList.add(S);
                                } catch (Exception ex) {
                                    Logger.getLogger(CentralSuccessorFunctionSA.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                    else notAdd = true;
                    break;
                case 2:
                    if (!notSwap && board.canAnySwap()){
                        while(retList.isEmpty()){
                            int chooseClient1 = random.nextInt(CentralBoard.getnClients());
                            int chooseClient2 = random.nextInt(CentralBoard.getnClients());
                            if(board.canSwap(chooseClient1, chooseClient2)){
                                try {
                                    CentralBoard aux = new CentralBoard(board);
                                    aux.doSwap(chooseClient1, chooseClient2);
                                    Successor S = new Successor("Swapped clients " + chooseClient1 + " and " + chooseClient2 + ".", aux);
                                    retList.add(S);
                                } catch (Exception ex) {
                                    Logger.getLogger(CentralSuccessorFunctionSA.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                    else notSwap = true;
                    break;
            }
        }
        return retList;
    }
}