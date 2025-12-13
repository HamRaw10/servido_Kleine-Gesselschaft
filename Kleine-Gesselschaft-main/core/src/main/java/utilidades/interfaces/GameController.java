package utilidades.interfaces;

public interface GameController {
    void isGoal(int direction);

    void connect(int numPlayer);

    void start();

    void startGame();

    

    void updatePadPosition(int numPlayer, int y);

    void updateBallPosition(int x, int y);

    void updateScore(String score);

    void endGame(int winner);

    void backToMenu();

    void move(int playerId, float x, float y);

    void updateChatMessage(int playerId, String message);
}
