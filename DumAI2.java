import java.util.ArrayList;

/**
 * A simple OthelloAI-implementation. The method to decide the next move just
 * returns the first legal move that it finds.
 * @author Mai Ajspur
 * @version 9.2.2018
 */
public class DumAI2 implements IOthelloAI{

    /**
     * Returns first legal move
     */
    public Position decideMove(GameState s){
        ArrayList<Position> moves = s.legalMoves();
        if ( !moves.isEmpty() ) {
            System.out.println("DumAI2: " + moves.get(moves.size() - 1).col + ", " + moves.get(moves.size() - 1).row);
            return moves.get(moves.size() - 1);
        }
        else
            return new Position(-1,-1);
    }

}
