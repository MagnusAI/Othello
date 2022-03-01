import java.util.ArrayList;
import java.util.List;

public class OthelloAI38 implements IOthelloAI {

    private volatile int maxCount = 0;
    private volatile int minCount = 0;

    private class Move {
        int utility;
        Position position;

        Move(int utility, Position position) {
            this.utility = utility;
            this.position = position;
        }
    }

    @Override
    public Position decideMove(GameState s) {
        this.maxCount = 0;
        this.minCount = 0;
        System.out.println("Possible moves:");
        for (Position p : s.legalMoves()) {
            System.out.println(p);
        }
        System.out.println("--------------------");
        return minimaxSearch(s);
    }

    private Position minimaxSearch(GameState state) {
        int depth = 5;
        Move move = state.getPlayerInTurn() == 2 ?
                maxValue(state, new Position(-1, -1), depth) :
                minValue(state, new Position(-1, -1), depth);
        System.out.println("decision: " + move.position + " | " + move.utility);
        System.out.println("max: " + maxCount + " min: " + minCount);
        return move.position;
    }

    private Move maxValue(GameState s, Position position, int depth) {
        this.maxCount++;
        if (isTerminal(s) || depth == 0) {
            return new Move(utility(s, position), position);
        }
        Move v1 = new Move(Integer.MIN_VALUE, position);
        if (s.legalMoves().isEmpty()) {
            Move v2 = passTurn(s, position, depth - 1);
            if (v2.utility > v1.utility) {
                v1 = new Move(v2.utility, position);
            }
        } else {
            for (Position p : s.legalMoves()) {
                Move v2 = minValue(placeToken(s, p), p, depth - 1);
                if (v2.utility > v1.utility) {
                    v1 = new Move(v2.utility, p);
                }
            }
        }
        return v1;
    }

    private Move minValue(GameState s, Position position, int depth) {
        this.minCount++;
        if (isTerminal(s) || depth == 0) {
            return new Move(utility(s, position), null);
        }
        Move v1 = new Move(Integer.MAX_VALUE, position);
        if (s.legalMoves().isEmpty()) {
            Move v2 = passTurn(s, position, depth - 1);
            if (v2.utility < v1.utility) {
                v1 = new Move(v2.utility, position);
            }
        } else {
            for (Position p : s.legalMoves()) {
                Move v2 = maxValue(placeToken(s, p), p, depth - 1);
                if (v2.utility < v1.utility) {
                    v1 = new Move(v2.utility, p);
                }
            }
        }
        return v1;
    }

    private Move passTurn(GameState state, Position position, int depth) {
        GameState stateCopy = copyState(state);
        stateCopy.changePlayer();
        return stateCopy.getPlayerInTurn() == 2 ?
                maxValue(stateCopy, position, depth) :
                minValue(stateCopy, position, depth);
    }

    private boolean isTerminal(GameState s) {
        return s.isFinished();
    }

    private int utility(GameState s, Position p) {
        int modifier = (int) Math.ceil((double) s.getBoard().length / 2);
        int utility = s.countTokens()[1] - s.countTokens()[0]; //tokens difference
        if (isCorner(s, p)) {
            utility += s.getPlayerInTurn() == 2 ? -modifier : modifier;
        } else if (isAdjoiningCorner(s, p)) {
            utility += s.getPlayerInTurn() == 2 ? modifier / 2 : -(modifier / 2);
        }

        return utility;
    }

    private GameState placeToken(GameState s, Position p) {
        GameState state = copyState(s);
        state.insertToken(p);
        return state;
    }

    private GameState copyState(GameState s) {
        return new GameState(s.getBoard(), s.getPlayerInTurn());
    }

    private boolean isCorner(GameState s, Position p) {
        int maxIndex = s.getBoard().length - 1;
        List<Position> corners = new ArrayList<>(
                List.of(
                        new Position(0, 0),
                        new Position(0, maxIndex),
                        new Position(maxIndex, 0),
                        new Position(maxIndex, maxIndex)
                ));
        return corners.contains(p);
    }

    private boolean isAdjoiningCorner(GameState s, Position p) {
        int maxIndex = s.getBoard().length -1;
        List<Position> adjoiningCornerPositions = new ArrayList<>(
                List.of(
                        new Position(0, 1),
                        new Position(1, 0),
                        new Position(1, 1),
                        new Position(maxIndex-1, 0),
                        new Position(maxIndex-1, 1),
                        new Position(maxIndex, 1),
                        new Position(0, maxIndex-1),
                        new Position(1, maxIndex-1),
                        new Position(1, maxIndex),
                        new Position(maxIndex, maxIndex-1),
                        new Position(maxIndex-1, maxIndex),
                        new Position(maxIndex-1, maxIndex-1)
                ));
        return adjoiningCornerPositions.contains(p);
    }
}
