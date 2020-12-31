import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SokobanBFS {

    static int n, m;



    public static void main(String[] args) {
        //Scanner in = new Scanner(System.in);
        Scanner in = null;
        try {
            in = new Scanner(new File("src/main/resources/labyrinth.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String input = "";
        while(in.hasNextLine()) {
            input += in.nextLine() + "\n";
        }
        n = input.trim().split("\n").length;
        m = input.trim().split("\n")[0].length();

        String initialState = input.replace("\n", "");

        if (!initialState.contains(".")) {
            System.out.println();
            return;
        }


        Set<String> visitedStates = new HashSet<>();
        List<String> queue = new ArrayList<>();
        queue.add(initialState);


        do {
            List<String> nextStates = new ArrayList<>();
            for(String state : queue) {
                String moveLeftState = moveLeft(state);
                String moveRightState = moveRight(state);
                String moveUpState = moveUp(state);
                String moveDownState = moveDown(state);
                if(moveLeftState != null && !visitedStates.contains(moveLeftState.substring(0, n*m))) {
                    nextStates.add(moveLeftState + "L");
                }
                if(moveRightState != null && !visitedStates.contains(moveRightState.substring(0, n*m))) {
                    nextStates.add(moveRightState + "R");
                }
                if(moveUpState != null && !visitedStates.contains(moveUpState.substring(0, n*m))) {
                    nextStates.add(moveUpState + "U");
                }
                if(moveDownState != null && !visitedStates.contains(moveDownState.substring(0, n*m))) {
                    nextStates.add(moveDownState + "D");
                }
                if(!visitedStates.contains(state.substring(0, n*m))) {
                    visitedStates.add(state.substring(0, n*m));
                }
            }

            for (String state : nextStates) {

                if (isSolved(state, initialState)) {
                    System.out.println(state.substring(n*m));
                    return;
                }
            }

            queue = nextStates;
        }while(!queue.isEmpty());
    }

    static boolean isSolved(String state, String initialState) {
        for (int i = 0; i < initialState.length(); i++) {
            if (initialState.charAt(i) == '.') {
                if (state.charAt(i) != '$') {
                    return false;
                }
            }
        }
        return true;
    }

    public static String moveLeft(String currentState) {
        return move(currentState, -1);
    }
    public static String moveRight(String currentState) {
        return move(currentState, 1);
    }

    public static String moveUp(String currentState) {
        return move(currentState, -m);
    }

    public static String moveDown(String currentState) {
        return move(currentState, m);
    }

    public static String move(String state, int direction) {
        int index = state.indexOf('@');
        char target = state.charAt(index + direction);
        char target2 = target != '#' ? state.charAt(index + direction * 2) : '#';
        if(target == ' ' || target == '.') {
            String helpState = state.replace('@', ' ');
            return helpState.substring(0, index + direction) + '@' + helpState.substring(index + direction + 1);
        } else if (target == '$' && (target2 == ' ' || target2 == '.')) {
            state = state.substring(0, index) + ' ' + state.substring(index + 1);
            state = state.substring(0, index + direction) + '@' + state.substring(index + direction + 1);
            state = state.substring(0, index + direction * 2) + '$' + state.substring(index + direction * 2 + 1);
            return state;
        }
        return null;
    }

    public static void printLabyrinth(String labyrinth) {
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < m; j++) {
                System.out.print(labyrinth.charAt(m*i + j));
            }
            System.out.println();
        }
    }
}
