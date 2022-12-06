package com.example.pathfinder;

import java.util.*;


public class ListGraph<T> implements Graph<T> {
    private Map<T, Set<Edge>> nodes = new HashMap<>();
    // en datasamling som har vår node T som nyckel och en samling
    // av förbindelser som values (edge)

    int counterOfLoop = 0; // räknar antalet varv en algoritm går



    public ListGraph() {

    }

    @Override
    public void add(T node) {
        nodes.putIfAbsent(node, new HashSet<>());
        // stoppar in en ny hashset om den noden man skickar in inte redan finns
        //add – tar emot en nod och stoppar in den i grafen. Om noden redan finns i grafen blir det ingen förändring.
    }

    @Override
    public void connect(T node1, T node2, String name, int weight) {
        //metod för att förbinda ihop två noder med varandra
        if (!nodes.containsKey(node1) || !nodes.containsKey(node2)) {
            throw new NoSuchElementException();
        }
        if (weight < 0) {
            throw new IllegalArgumentException();
        }
        /*if (getEdgeBetween(node1, node2) != null) {
            throw new IllegalStateException();
        }*/
        Set<Edge> se1 = nodes.get(node1); // tar fram de egdes som finns för node1
        Edge e1 = new Edge(node2, name, weight); // denna edge ska vi stoppa in i node1, och den ska peka ut node2
        se1.add(e1); // genom att stoppa in den i se1 har vi förbundit noderna node1 & node2 med varandra
        Set<Edge> se2 = nodes.get(node2); // eftersom det är en oriktad graf måste vi förbinda även åt andra hållet, dvs node2 till node1
        Edge e2 = new Edge(node1, name, weight); //
        se2.add(e2); // lägger in kopplingen åt andra hållet
    }

    @Override
    public void setConnectionWeight(T node1, T node2, int weight) {
        if (!nodes.containsKey(node1) || !nodes.containsKey(node2)) {
            throw new NoSuchElementException();
        }
        if (weight < 0) {
            throw new IllegalArgumentException();
        }
        if (getEdgeBetween(node1, node2) == null) {
            throw new NoSuchElementException();
        }
        Edge<T> edge1 = getEdgeBetween(node1, node2);
        Edge<T> edge2 = getEdgeBetween(node2, node1);
        edge1.setWeight(weight);
        edge2.setWeight(weight);
    }

    @Override
    public Set getNodes() {
        return Collections.unmodifiableSet(nodes.keySet());
    }

    @Override
    public Collection<Edge<T>> getEdgesFrom(T node) {
        if (!nodes.containsKey(node)) {
            throw new NoSuchElementException();
        }
        Collection<Edge<T>> edges = new ArrayList<>();
        for (Edge<T> e : nodes.get(node)) {
            edges.add(e);
        }
        return edges;
    }


    @Override
    public Edge getEdgeBetween(T node1, T node2) {
        if (!nodes.containsKey(node1) || !nodes.containsKey(node2)) {
            throw new NoSuchElementException();
        }
        for (Edge e : nodes.get(node1)) {
            if (e.getDestination().equals(node2)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public void disconnect(T node1, T node2) {
        if (!nodes.containsKey(node1) || !nodes.containsKey(node2)) {
            throw new NoSuchElementException();
        }
        if (getEdgeBetween(node1, node2) == null) {
            throw new IllegalStateException();
        }
        Edge<T> edge1 = getEdgeBetween(node1, node2);
        Edge<T> edge2 = getEdgeBetween(node2, node1);

        Set<Edge> edgesFromNode1 = nodes.get(node1); // alla edges kopplade från/till node1
        edgesFromNode1.remove(edge1); //boolean hasBeenRemoved = edgesFromNode1.remove(edge1);
        //System.out.println(hasBeenRemoved);

        Set<Edge> edgesFromNode2 = nodes.get(node2);
        edgesFromNode2.remove(edge2);
    }

    @Override
    public void remove(T node) {
        if (!nodes.containsKey(node)) {
            throw new NoSuchElementException();
        }
        Collection<Edge<T>> edges = new ArrayList<>(getEdgesFrom(node));
        for (Edge<T> e : edges) {
            disconnect(node, e.getDestination());
        }
        nodes.remove(node);
    }

    private void depthFirstSearch(T where, T whereFrom, Set<T> visited, Map<T, T> via) {
        long start = System.nanoTime();
        visited.add(where);
        via.put(where, whereFrom);
        for (Edge<T> e : nodes.get(where)) {
            if (!visited.contains(e.getDestination())) {
                depthFirstSearch(e.getDestination(), where, visited, via);
                counterOfLoop++;
            }
        }
        System.out.println(counterOfLoop);
        long elapsed = System.nanoTime() - start;
        System.out.println(elapsed);
    }

    private void depthFirstSearch(T where, Set<T> visited) { // where är den noden vi besöker just nu
        visited.add(where); //nu är vi klara med den aktuella noden, och går därefter igenom alla grannar till den
        for (Edge<T> e : nodes.get(where)) {
            if (!visited.contains(e.getDestination())) { // går igenom alla noder som går från startnoden
                depthFirstSearch(e.getDestination(), visited);
            }
        }
    }

    public List<Edge<T>> getShortestPathBFS(T from, T to, Set<T> visited, Map<T, T> via) { //Kopierad från Josefs föreläsning, inte implementerad i koden!
        long start = System.nanoTime();
        LinkedList<T> queue = new LinkedList<>();
        visited.add(from);
        queue.add(from);
        while (!queue.isEmpty()) {
            T node = queue.pollFirst();
            for (Edge<T> e : nodes.get(node)) {
                T toNode = e.getDestination();
                if (!visited.contains(toNode)) {
                    queue.add(toNode);
                    visited.add(toNode);
                    via.put(toNode, node);
                    counterOfLoop++;
                }
            }
        }
        System.out.println(counterOfLoop);
        long elapsed = System.nanoTime() - start;
        System.out.println(elapsed);
        return gatherPath(from, to, via);
    }















    @Override
    public boolean pathExists(T from, T to) {
        // ska returnera true om det finns en väg mellan nod1 och nod 2 (som skickas in som parametrar
        if (!nodes.containsKey(from) || !nodes.containsKey(to)) {
            return false;
        }
        Set<T> visited = new HashSet<>(); // här ska vi stoppa in de städer vi besöker när vi går igenom alla noder, så att vi lär oss grannarna
        depthFirstSearch(from, visited); // denna metod ska stoppa in alla noder den har besökt från node from, i setet visited, så att vi sedan kan kolla om den vi söker finns med där
        return visited.contains(to); //om den vi sökte finns med så vet vi att det finns en förbindelse mellan noderna
    }

    /* DFS algoritm call
    @Override
    public List<Edge<T>> getPath(T from, T to) {
        Set<T> visited = new HashSet<>();
        Map<T, T> via = new HashMap<>();
        depthFirstSearch(from, null, visited, via);
        List<Edge> path = new ArrayList<>();
        if (!visited.contains(to)) {
            return null;
        }
        return gatherPath(from, to, via);
    } */

   //BFS Algoritm call
    @Override
    public List<Edge<T>> getPath(T from, T to) {
        Set<T> visited = new HashSet<>();
        Map<T, T> via = new HashMap<>();
        getShortestPathBFS(from, to, visited, via);
        List<Edge> path = new ArrayList<>();
        if (!visited.contains(to)) {
            return null;
        }
        return gatherPath(from, to, via);
    }


      /* Dijkstras Algoritm call
    @Override
    public List<Edge<T>> getPath(T from, T to) {
        Set<T> visited = new HashSet<>();
        Map<T, T> via = new HashMap<>();
        execute(from);
        List<Edge> path = new ArrayList<>();
        if (!visited.contains(to)) {
            return null;
        }
        return gatherPath(from, to, via);
    } */



    private List<Edge<T>> gatherPath(T from, T to, Map<T, T> via) {
        List<Edge<T>> path = new ArrayList<>();
        T where = to;
        while (!where.equals(from)) {
            T node = via.get(where);
            Edge e = getEdgeBetween(node, where);
            path.add(e);
            where = node;
        }
        Collections.reverse(path);
        return path;
    }

    @Override
    public String toString() {
        String string = ""; // för varje nod ska det stå till vilka andra noder den har kopplingar
        for (T node : nodes.keySet()) {
            string = string + node + ": ";
            for (Edge e : nodes.get(node)) {
                string = string + e.toString();
                string = string + "\n";
            }
        }
        return string;
    }
}