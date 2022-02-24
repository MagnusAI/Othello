public class OthelloAI38 implements IOthelloAI {
    private Position aiMove;

    @Override
    public Position decideMove(GameState s) {
        boolean isMaximizingPlayer = s.getPlayerInTurn() == 2;
        aiMove = new Position(-1, -1);
        minimaxAlphaBeta(s, 6, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaximizingPlayer);
        return aiMove;
    }

    private int minimaxAlphaBeta(GameState s, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0 || s.isFinished()) {
            return maximizingPlayer ? s.countTokens()[1] - s.countTokens()[0] : s.countTokens()[0] - s.countTokens()[1];
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Position p : s.legalMoves()) {
                GameState newState = new GameState(s.getBoard(), s.getPlayerInTurn());
                newState.insertToken(p);
                int eval = minimaxAlphaBeta(newState, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                aiMove = p;
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Position p : s.legalMoves()) {
                GameState newState = new GameState(s.getBoard(), s.getPlayerInTurn());
                newState.insertToken(p);
                int eval = minimaxAlphaBeta(newState, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                aiMove = p;
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }
}
