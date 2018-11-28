
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package myClasses;
import aima.search.framework.HeuristicFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CentralHeuristicFunction4 implements HeuristicFunction {

    /**
     *
     * @param n
     * @return
     */
    @Override
    public double getHeuristicValue(Object n){

        try {
            return ((CentralBoard) n).centralHeuristic4();
        } catch (Exception ex) {
            Logger.getLogger(CentralHeuristicFunction0.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
}