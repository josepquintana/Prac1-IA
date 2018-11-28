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
import java.util.logging.Level;
import java.util.logging.Logger;

public class CentralSuccessorFunctionHC implements SuccessorFunction{
    
    @Override
    public List getSuccessors(Object state){
        ArrayList retList = new ArrayList();
        CentralBoard board = (CentralBoard) state;
        for(int i = 0; i < CentralBoard.getnClients(); ++i){
            if(board.canRemove(i)){
                CentralBoard aux = new CentralBoard(board);
                try {
                    aux.doRemove(i);
                } catch (Exception ex) {
                    Logger.getLogger(CentralSuccessorFunctionHC.class.getName()).log(Level.SEVERE, null, ex);
                }
                Successor S = new Successor("Remove client " + i + ".", aux);
                retList.add(S);
            }
            for(int j = 0; j < CentralBoard.getnCentrals(); ++j){
                if(board.canAdd(i, j)){
                    CentralBoard aux = new CentralBoard(board);
                    try {
                        aux.doAdd(i, j);
                    } catch (Exception ex) {
                        Logger.getLogger(CentralSuccessorFunctionHC.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Successor S = new Successor("Moved client " + i + " to central " + j + ".", aux);
                    retList.add(S);
                }
            }
        }
        for(int i = 0; i < CentralBoard.getnClients()/2; ++i){
            for(int j = 0; j < CentralBoard.getnClients(); ++j){
                if(board.canSwap(i,j)){
                    CentralBoard aux = new CentralBoard(board);
                    try {
                        aux.doSwap(i, j);
                    } catch (Exception ex) {
                        Logger.getLogger(CentralSuccessorFunctionHC.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Successor S = new Successor("Swapped clients " + i + " and " + j + ".", aux);
                    retList.add(S);
                }
            }
        }
        
        return retList;
    }
}