/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package myClasses;
import aima.search.framework.GoalTest;

public class CentralGoalTest implements GoalTest {

    /**
     *
     * @param state
     * @return
     */
    @Override
    public boolean isGoalState(Object state){

        return((CentralBoard) state).isGoal();
    }
}
