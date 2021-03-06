package org.academiadecodigo.hexallents.menu;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.menu.MenuInputScanner;
import static org.academiadecodigo.hexallents.helper_classes.Messages.*;

/**
 * Created by GuessWho on 13/03/2018.
 */
public class Menu {

    private Prompt prompt;

    public Menu() {
        prompt = new Prompt(System.in, System.out);
        showMenu();
    }

    private void showMenu() {

        String[] menuOptions = {
            GAME_OPTIONS_START,
            GAME_OPTIONS_QUIT
        };

        MenuInputScanner scanner = new MenuInputScanner(menuOptions);
        scanner.setMessage(GAME_OPTIONS);

        int answerIndex = prompt.getUserInput(scanner);

        if (answerIndex == 2) {
            System.out.println(CONNECTION_CLOSED);
            System.exit(0);
        }
    }
}
