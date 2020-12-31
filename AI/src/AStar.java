import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class AStar {


    public static void main(String[] args) {
            Scanner in = null;
            /*try {
                in = new Scanner(new File("src/resources/graph-"+5+".txt"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/
            in = new Scanner(System.in);
            // number of nodes
            int n = in.nextInt();
            //number of edges
            int m = in.nextInt();
            Node[] nodes = new Node[n];
            for (int i = 0; i < n; i++) {
                int h = in.nextInt();
                nodes[i] = new Node(h);
                nodes[i].index = i;
                if (i == n - 1) {
                    nodes[i].markAsGoalState();
                }
            }

            //save neighbor data in Node objects
            for (int j = 0; j < m; j++) {
                int startNode = in.nextInt();
                int endNode = in.nextInt();
                int cost = in.nextInt();
                nodes[startNode].addNeighbor(nodes[endNode]);
                nodes[startNode].addCost(cost);
            }

            Node init = nodes[0];
            // init search with first node
            ArrayList<Node> expandedNodes = search(init);
            for(Node node : expandedNodes) {
                System.out.println(node.index);
            }
    }

    private static ArrayList<Node> search(Node init) {
        // initialize list of already visited nodes
        ArrayList<Node> expNodes = new ArrayList<>();
        expNodes.add(init);
        while(true) {
            Node bestNode = select(expNodes);
            if(bestNode == null) {
                return expNodes;
            }
            expNodes.add(bestNode);
            if(bestNode.isGoalState) {
                return expNodes;
            }
        }
    }

    private static Node select(ArrayList<Node> expNodes) {
        Node selection = null;
        int selectionCost = 0;
        int minF = Integer.MAX_VALUE;
        for(Node node: expNodes) {
            for(int i = 0; i < node.neighbors.size(); i++) {
                if(expNodes.contains(node.neighbors.get(i))) continue;

                int f = node.neighbors.get(i).h + node.costs.get(i);
                if(minF >= f) {
                    minF = f;
                    selection = node.neighbors.get(i);
                    selectionCost = node.costs.get(i);
                }
            }
        }
        if (selection == null) return null;
        for(int i = 0; i < selection.neighbors.size(); i++) {
            selection.costs.set(i, selection.costs.get(i) + selectionCost);
        }
        return selection;
    }


    static class Node {
        boolean isGoalState = false;
        int index;
        int h;
        // this value is accumulated
        int accumulatedCost = 0;
        ArrayList<Node> neighbors = new ArrayList<>();
        ArrayList<Integer> costs = new ArrayList<>();

        Node(int h) {
            this.h = h;
        }

        void markAsGoalState () {
            this.isGoalState = true;
        }

        void addNeighbor(Node neighbor) {
            this.neighbors.add(neighbor);
        }

        void addCost(int cost) {
            this.costs.add(cost);
        }
    }

}
