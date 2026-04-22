Prommpt 1:
I'm building a Snake game in Java using Swing. Create a single file called SnakeGame.java. It should have a main method that opens a JFrame window that is 600 by 600 pixels and titled Snake. Inside the frame, add a JPanel subclass called GamePanel. Do not add any game logic yet. Just get the window to open correctly.
Prompt 2: 
Now extend SnakeGame.java. Keep it as one file. Add a dark background grid and draw a starting snake that is three segments long near the center of the board, facing right. Each cell should be a 30x30 pixel square. Draw the snake in green and the background in dark gray. Do not add movement yet.
Prompt 3:
Make the snake move automatically using a Swing timer that ticks every 150 milliseconds. Add arrow key controls so the player can steer, but don't allow the snake to reverse direction. For now, have the snake wrap around the edges instead of dying. Make sure the panel can receive keyboard input.
Prompt 4:
Add a food pellet that spawns at a random empty cell. When the snake eats it, grow by one segment and spawn new food. Add collision detection: hitting a wall or the snake's own body should end the game, stop movement, and show a "Game Over" message with the final score. Display the current score in the top-left corner during play. When the game is over, let the player press R to reset everything and play again.

5 Additonal Prompts:

Prompt 5: High score 
 In SnakeGame.java, add an high score that saves across sessions using Preferences

 High score displays top-left + Game Over, persists across restarts.

Fixed
Added missing Preferences import
Used GamePanel.class for prefs node key
"snakeHighScore" unique key prevents conflicts

Prompt 6: Speed progression
6 Levels each level will increase speed starting at 250 ms and increasing by 50 ms the first few level than by 25 mms unti hitting a threshold of 75ms.
The speed is displayed at the top left as well as the currentt level the player is currently on from 1-6.

Speed increases automatically after eating food
Level and speed display on screen
Resets to Level 1 on 'R' restart
High score is still saved after losing.

Prompt 7:Added a start screen main menu to the Snake game which changged ffrom pressing R to Space. Also you can see your high score at the main menu and after losing pressing space instantly resest the game again. 
Had trouble with space not working for quite a long time but solved it finally by fixing space being blocked and undetected by another code.

Prompt8: Added the ability to press P to pause the game mid playthorugh which freezez the snake into place and you can press P again to resume. Pressing space will instanlty reset the snake game and allow to spawn in a different location as well as where the food is.

Prompt 9: Added the menu options for describing what P and Space will do when pressed and clearly lbeled on the top left corner at all times when playing during gameplay. 