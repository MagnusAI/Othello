import java.util.ArrayList;
import java.util.List;

public class OthelloAI38 implements IOthelloAI {

    private int totalMoves = 0;
    private Double totalTimeToMove = 0.0; // nanoseconds

    @Override
    public Position decideMove(GameState s) {
        totalMoves++;
        return minimaxSearch(s);
    }

    private Position minimaxSearch(GameState state) {
        long startTime = System.nanoTime(); // Start timer to calculate avg. time per move.

        int depth = 6;
        Move move = state.getPlayerInTurn() == 2
                ? maxValue(state, null, Integer.MIN_VALUE, Integer.MAX_VALUE, depth)
                : minValue(state, null, Integer.MIN_VALUE, Integer.MAX_VALUE, depth);

        // Used to print avg. time and moves
        this.totalTimeToMove += (System.nanoTime() - startTime);
        move.print(startTime);
        if (placeToken(state, move.position).isFinished()) {
            this.printMovesAndTime();
        }

        return move.position;
    }

    private Move maxValue(GameState s, Position position, int alpha, int beta, int depth) {
        if (isTerminal(s) || depth == 0) {
            return new Move(utility(s, position), position);
        }

        Move v1 = new Move(Integer.MIN_VALUE, position);

        if (s.legalMoves().isEmpty()) {
            Move v2 = passTurn(s, position, alpha, beta, depth - 1);
            if (v2.utility > v1.utility) {
                v1 = new Move(v2.utility, position);
            }
        } else {
            for (Position p : s.legalMoves()) {
                Move v2 = minValue(placeToken(s, p), p, alpha, beta, depth - 1);
                if (v2.utility > v1.utility) {
                    v1 = new Move(v2.utility, p);
                    alpha = Math.max(alpha, v2.utility);
                }
                if (v1.utility >= beta) {
                    break;
                }
            }
        }
        return v1;
    }

    private Move minValue(GameState s, Position position, int alpha, int beta, int depth) {
        if (isTerminal(s) || depth == 0) {
            return new Move(utility(s, position), null);
        }
        Move v1 = new Move(Integer.MAX_VALUE, position);

        if (s.legalMoves().isEmpty()) {
            Move v2 = passTurn(s, position, alpha, beta, depth - 1);
            if (v2.utility < v1.utility) {
                v1 = new Move(v2.utility, position);
            }
        } else {
            for (Position p : s.legalMoves()) {
                Move v2 = maxValue(placeToken(s, p), p, alpha, beta, depth - 1);
                if (v2.utility < v1.utility) {
                    v1 = new Move(v2.utility, p);
                    beta = Math.min(beta, v1.utility);
                }
                if (v1.utility <= alpha) {
                    break;
                }
            }
        }
        return v1;
    }

    private Move passTurn(GameState state, Position position, int alpha, int beta, int depth) {
        GameState stateCopy = copyState(state);
        stateCopy.changePlayer();
        return stateCopy.getPlayerInTurn() == 2 ? maxValue(stateCopy, position, alpha, beta, depth)
                : minValue(stateCopy, position, alpha, beta, depth);
    }

    private boolean isTerminal(GameState s) {
        return s.isFinished();
    }

    private int utility(GameState s, Position p) {
        int modifier = (int) Math.ceil((double) s.getBoard().length / 2);

        int utility = s.countTokens()[1] - s.countTokens()[0]; // tokens difference

        /*
         * If it is currently the maximizing players turn that means the minimizing
         * placed the last token.
         * We either add or substract the modifier from the utility in order to
         * encourage or defer the player
         * from choosing the position.
         */

        if (isCorner(s, p)) {
            utility += s.getPlayerInTurn() == 2 ? -modifier : modifier; // encoruage the corner position.
        } else if (isAdjoiningCorner(s, p)) {
            utility += s.getPlayerInTurn() == 2 ? modifier / 2 : -(modifier / 2); // defer if position is adjoining a
                                                                                  // corner.
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
                        new Position(maxIndex, maxIndex)));
        return corners.contains(p);
    }

    private boolean isAdjoiningCorner(GameState s, Position p) {
        int maxIndex = s.getBoard().length - 1;
        List<Position> adjoiningCornerPositions = new ArrayList<>(
                List.of(
                        new Position(0, 1),
                        new Position(1, 0),
                        new Position(1, 1),
                        new Position(maxIndex - 1, 0),
                        new Position(maxIndex - 1, 1),
                        new Position(maxIndex, 1),
                        new Position(0, maxIndex - 1),
                        new Position(1, maxIndex - 1),
                        new Position(1, maxIndex),
                        new Position(maxIndex, maxIndex - 1),
                        new Position(maxIndex - 1, maxIndex),
                        new Position(maxIndex - 1, maxIndex - 1)));
        return adjoiningCornerPositions.contains(p);
    }

    public void printMovesAndTime() {
        String moves = String.format("Total moves: %3d%n", this.totalMoves);
        String avgTime = String.format("Avg. time pr. move (seconds): %3f%n",
                (this.totalTimeToMove / 1000000000.0) / this.totalMoves);
        System.out.println(moves + avgTime);
    }

    private class Move {
        int utility;
        Position position;

        Move(int utility, Position position) {
            this.utility = utility;
            this.position = position;
        }

        public void print(long startTime) {
            String utilityString = String.format(" | Utility: %3d", this.utility);
            String time = String.format(" | Time (seconds): %.2f", (System.nanoTime() - startTime) / 1000000000.0);
            System.out.printf("[OthelloAI38] Position: " + this.position + utilityString + time + "\n");
        }
    }
}
