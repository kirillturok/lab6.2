package com.company;

import java.util.Scanner;
import java.util.Stack;

public class Tree {
    private class Node {
        int data;
        Node left;
        Node right;
        boolean hasntLeft;

        //конструктор
        public Node(int data, Node left, Node right) {
            this.data = data;
            this.left = left;
            this.right = right;
        }
    }

    Node root = null;

    //метод добавления элемента в непрошитое дерево
    public void add(int data) {
        //создаем новый узел
        Node node = new Node(data, null, null);
        //если дерево пустое, то запишем новый узел в качестве корня
        if (root == null) {
            root = node;
            return;
        }
        Node current = root;
        //итеративно найдем место для нового элемента
        while (true) {
            //если новый элемент меньше текущего и у текущего есть потомок, то перейдем к этому потомку
            if (data < current.data && current.left != null) {
                current = current.left;
                continue;
            }
            //аналогично предыдущему
            if (data >= current.data && current.right != null) {
                current = current.right;
                continue;
            }
            //если новый элемент меньше текущего и у текущего нет левого потомка
            //(если б он был, мы бы не дошли до этого, итерация завершилась бы выше),
            //запишем новый элемент в качестве левого потомка для текущего
            if (data < current.data) {
                current.left = node;
                //аналогично, если больше либо равен
            } else current.right = node;
            return;
        }
    }

    //метод добавления в прошитое
    public void addInThreaded(){
        System.out.println("Введите элемент.");
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextInt()) {
            System.out.println("Необходимо ввести целое число. Повторите ввод.");
            scanner.next();
        }
        int data = scanner.nextInt();
        //удалим нити
        deleteThreads(root);
        //вставим элемент в непрошитое дерево
        add(data);
        //прошьем дерево
        thread();
    }

    //метод прямого прохода прошитого дерева
    public void strPrint(){
        Node curr = root;

        while (curr != null){
            System.out.print(curr.data + " ");
            if (curr.left != null && !curr.hasntLeft) curr = curr.left;
            else if (curr.left == null) curr = curr.right;
            else if (curr.hasntLeft) curr = curr.left;
        }

        System.out.println();
    }

    //метод прямой прошивки дерева
    public void thread(){
        //аналогично рекурсивному прямому проходу будем работать со стеком, чтоб добавить нити в дерево
        Stack<Node> stack = new Stack<>();
        stack.push(root);

        while (!stack.empty()){
            Node extracted = stack.pop();

            if (extracted.right != null) stack.push(extracted.right);
            if (extracted.left  != null && !extracted.hasntLeft) stack.push(extracted.left);

            if (stack.empty()) return;

            if (extracted.right == null && (extracted.hasntLeft || extracted.left == null)){

                if (stack.peek() == root){
                    extracted.left = null;
                    extracted.hasntLeft = false;
                } else {
                    extracted.left = stack.peek();
                    extracted.hasntLeft = true;
                }
            }
        }
    }

    //метод удаления нитей
    private void deleteThreads(Node n){
        if (n != null){
            if (n.hasntLeft){
                n.hasntLeft = false;
                n.left = null;
            }

            deleteThreads(n.left);
            deleteThreads(n.right);
        }
    }


    //метод удаления
    public boolean delete(int key) {
        Node current = root;
        Node parent = root;
        boolean isLeftChild = true;
        //найдем удаляемый элемент
        //также запомним родителя
        while (current.data != key) {
            parent = current;
            if (key < current.data) {
                isLeftChild = true;
                current = current.left;
            } else {
                isLeftChild = false;
                current = current.right;
            }

            if (current == null)
                return false;
        }

        //удаление листа
        if (current.left == null &&
                current.right == null) {
            if (current == root)
                root = null;
            else if (isLeftChild)
                parent.left = null;
            else
                parent.right = null;
        }
        else
            //удаление узла с левым потомком
            if (current.right == null) {
                if (current == root)
                    root = current.left;
                else
                if (isLeftChild)
                    parent.left = current.left;
                else
                    parent.right = current.left;
            }
            else
                //удаление узла с правым потомком
                if (current.left == null) {
                    if (current == root)
                        root = current.right;
                    else
                    if (isLeftChild)
                        parent.left = current.right;
                    else
                        parent.right = current.right;
                }
                else
                //удаление узла с двумя потомками
                {
                    //находим элемент на место удаляемого
                    Node successor = findNew(current);
                    if (current == root)
                        root = successor;
                    else if (isLeftChild)
                        parent.left = successor;
                    else
                        parent.right = successor;

                    successor.left = current.left;
                }

        return true;
    }



   //найдет либо левый лист правого поддерева
    //либо правый лист левого поддерева
    private Node findNew(Node delNode) {
        Node successorParent = delNode;
        Node successor = delNode;
        Node current = delNode.right;
        while(current != null) {
            successorParent = successor;
            successor = current;
            current = current.left;
        }
        if(successor != delNode.right) {
            successorParent.left = successor.right;
            successor.right = delNode.right;
        }
        return successor;
    }

    public void deleteFromThreaded(){
        System.out.println("Введите элемент.");
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextInt()) {
            System.out.println("Необходимо ввести целое число. Повторите ввод.");
            scanner.next();
        }
        int key = scanner.nextInt();
        //удалим нити
        deleteThreads(root);
        if(!delete(key)) System.out.println("Такого элемента нет.");
        //заново прошьем
        thread();
    }

}
