import java.util.ArrayList;
import java.util.List;

public class OthelloAI38 implements IOthelloAI {
    private class Move {
        Position position;
        int value;

        Move(Position position, int value) {
            this.position = position;
            this.value = value;
        }
    }

    @Override
    public Position decideMove(GameState s) {
        boolean isMaximizingPlayer = s.getPlayerInTurn() == 2;

        System.out.println("Possible moves:");
        for (Position p : s.legalMoves()) {
            System.out.println(p);
        }
        System.out.println("--------------------");

        Move move = minimax(s, 6, isMaximizingPlayer, new Position(-1, -1));
        System.out.println("OthelloAI38: " + move.position + " Value: " + move.value);
        return move.position;
    }

    private Move minimax(GameState s, int depth, boolean maximizingPlayer, Position position) {
        if (depth == 0 || s.isFinished()) {
            int value = getMaximizingPlayerScore(s);

            if (isCorner(s, position)) {
                value += maximizingPlayer ? -5 : 5;
            }

            System.out.println(position + " | value: " + value);
            return new Move(position, value);
        }

        if (maximizingPlayer) {
            Move maxMove = new Move(new Position(-1, -1), Integer.MIN_VALUE);

            if (s.legalMoves().isEmpty()) {
                GameState evalState = copyState(s);
                evalState.changePlayer();
                Move eval = minimax(evalState, depth - 1, false, position);

                if (eval.value > maxMove.value) {
                    maxMove = new Move(position, eval.value);
                }

            } else {
                for (Position p : s.legalMoves()) {
                    GameState evalState = copyState(s);
                    evalState.insertToken(p);
                    Move eval = minimax(evalState, depth - 1, false, p);
                    int a = evalState.countTokens()[0];
                    int b = evalState.countTokens()[1];
                    if (eval.value > maxMove.value) {
                        maxMove = new Move(p, eval.value);
                    }
                }
            }
            return maxMove;
        } else {
            Move minMove = new Move(new Position(-1, -1), Integer.MAX_VALUE);

            if (s.legalMoves().isEmpty()) {
                GameState evalState = copyState(s);
                evalState.changePlayer();
                Move eval = minimax(evalState, depth - 1, false, position);

                if (eval.value > minMove.value) {
                    minMove = new Move(position, eval.value);
                }
            }
            for (Position p : s.legalMoves()) {
                GameState evalState = copyState(s);
                evalState.insertToken(p);
                Move eval = minimax(evalState, depth - 1, true, p);

                if (eval.value < minMove.value) {
                    minMove = new Move(p, eval.value);
                }
            }
            return minMove;
        }
    }

    private GameState copyState(GameState s) {
        return new GameState(s.getBoard(), s.getPlayerInTurn());
    }

    private boolean isCorner(GameState s, Position p) {
        int maxIndex = s.getBoard().length-1;
        List<Position> corners = new ArrayList<>(
                List.of(
                        new Position(0,0),
                        new Position(0,maxIndex),
                        new Position(maxIndex, 0),
                        new Position(maxIndex, maxIndex)
                ));
        return corners.contains(p);
    }

    private int getMaximizingPlayerScore(GameState s) {
        return s.countTokens()[1] - s.countTokens()[0];
    }
}
