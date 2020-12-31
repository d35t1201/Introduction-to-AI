import java.util.*;

public class LabyrinthSolver {

    public static void main(String[] args) {
       Scanner input = new Scanner(System.in);
       String labString = "";
       while(input.hasNextLine()) {
           labString += input.nextLine() + "\n";
       }

       int n = labString.trim().split("\n").length;
       int m = labString.trim().split("\n")[0].length();

       labString = labString.replace("\n", "");
       String goalState = labString.replace("@", " ").replace(".", "@");
       List<String> visitedStates = new ArrayList<>();
       List<String> nextStates = new ArrayList<>();
       nextStates.add(labString);
       int stepsNeeded = 0;

        do {
            String currentState = nextStates.get(0);
            nextStates.remove(0);
            if(currentState.equals(goalState)) {

                break;
            }
            visitedStates.add(currentState);

            String moveLeftState = moveLeft(currentState, n, m);
            String moveRightState = moveRight(currentState, n, m);
            String moveUpState = moveUp(currentState, n ,m);
            String moveDownState = moveDown(currentState, n ,m);

            if(moveLeftState != null && !contains(visitedStates,moveLeftState)) {
                nextStates.add(moveLeftState);
            }
            if(moveRightState != null && !contains(visitedStates, moveRightState)) {
                nextStates.add(moveRightState);
            }
            if(moveUpState != null && !contains(visitedStates, moveUpState)) {
                nextStates.add(moveUpState);
            }
            if(moveDownState != null && !contains(visitedStates, moveDownState)) {
                nextStates.add(moveDownState);
            }
        }while(!nextStates.isEmpty());
    }

    public static String moveLeft(String currentState, int n, int m) {
        int index = currentState.indexOf('@');
        if(index % m > 1 && currentState.charAt(index-1) != '#') {
            String helpState = currentState.replace('@', ' ');
            return helpState.substring(0, index-1 ) + '@' + helpState.substring(index);
        }
        return null;
    }
    public static String moveRight(String currentState, int n, int m) {
        int index = currentState.indexOf('@');
        if(index % m < m-2  && currentState.charAt(index+1) != '#') {
            String helpState = currentState.replace('@', ' ');
            return helpState.substring(0, index+1 ) + '@' + helpState.substring(index+2);
        }
        return null;
    }

    public static String moveUp(String currentState, int n, int m) {
        int index = currentState.indexOf('@');
        if(index / m > 1 && currentState.charAt(index-m) != '#') {
            String helpState = currentState.replace('@', ' ');
            return helpState.substring(0, index-m ) + '@' + helpState.substring(index-m+1);
        }
        return null;
    }

    public static String moveDown(String currentState, int n, int m) {
        int index = currentState.indexOf('@');
        if(index / m < n-2 && currentState.charAt(index+m) != '#') {
            String helpState = currentState.replace('@', ' ');
            return helpState.substring(0, index+m ) + '@' + helpState.substring(index+m+1);
        }
        return null;
    }

    public static boolean contains(List<String> visitedStates, String state) {
        for(int i = 0; i < visitedStates.size(); i++) {
            if(state.equals(visitedStates.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static void printLabyrinth(String[] labyrinth) {
        for(int i = 0; i < labyrinth.length; i++) {
            for(int j = 0; j < labyrinth[i].length(); j++) {
                System.out.print(labyrinth[i].charAt(j));
            }
            System.out.println();
        }
    }
}