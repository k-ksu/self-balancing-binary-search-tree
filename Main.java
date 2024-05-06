import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        LineSegment[] segments = new LineSegment[n];
        for (int i = 0; i < n; i++) {
            String[] line = scanner.nextLine().split(" ");
            int x1 = Integer.parseInt(line[0]);
            int y1 = Integer.parseInt(line[1]);
            int x2 = Integer.parseInt(line[2]);
            int y2 = Integer.parseInt(line[3]);
            segments[i] = new LineSegment(x1, y1, x2, y2);
        }

        // Sorting using Merge-Sort
        mergeSort(segments, 0, segments.length - 1);

        AVLTree<LineSegment> tree = new AVLTree<>();
        for (LineSegment segment : segments) {
            tree.insert(segment);
        }

        boolean hasIntersections = false;
        LineSegment firstIntersectingSegment = null;
        LineSegment secondIntersectingSegment = null;

        List<LineSegment> sortedSegments = tree.inOrder();
        for (int i = 0; i < sortedSegments.size() - 1; i++) {
            if (sortedSegments.get(i).intersects(sortedSegments.get(i + 1))) {
                hasIntersections = true;
                firstIntersectingSegment = sortedSegments.get(i);
                secondIntersectingSegment = sortedSegments.get(i + 1);
                break;
            }
        }

        if (hasIntersections) {
            System.out.println("INTERSECTION");
            System.out.println(firstIntersectingSegment.getX1() + " " + firstIntersectingSegment.getY1() + " " +
                    firstIntersectingSegment.getX2() + " " + firstIntersectingSegment.getY2());
            System.out.println(secondIntersectingSegment.getX1() + " " + secondIntersectingSegment.getY1() + " " +
                    secondIntersectingSegment.getX2() + " " + secondIntersectingSegment.getY2());
        } else {
            System.out.println("NO INTERSECTIONS");
        }
    }

    // Merge-Sort implementation
    public static <T extends Comparable<T>> void mergeSort(T[] arr, int l, int r) {
        if (l < r) {
            int m = (l + r) / 2;

            mergeSort(arr, l, m);
            mergeSort(arr, m + 1, r);

            merge(arr, l, m, r);
        }
    }

    public static <T extends Comparable<T>> void merge(T[] arr, int l, int m, int r) {
        int n1 = m - l + 1;
        int n2 = r - m;

        T[] L = Arrays.copyOfRange(arr, l, m + 1);
        T[] R = Arrays.copyOfRange(arr, m + 1, r + 1);

        int i = 0, j = 0;
        int k = l;
        while (i < n1 && j < n2) {
            if (L[i].compareTo(R[j]) <= 0) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }

        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
        }
    }
}

class LineSegment implements Comparable<LineSegment> {
    private int x1, y1, x2, y2;

    public LineSegment(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public int compareTo(LineSegment other) {
        return Integer.compare(this.x1, other.x1);
    }

    public boolean intersects(LineSegment other) {
        int o1 = orientation(this, other.x1, other.y1);
        int o2 = orientation(this, other.x2, other.y2);
        int o3 = orientation(other, this.x1, this.y1);
        int o4 = orientation(other, this.x2, this.y2);

        if (o1 != o2 && o3 != o4) {
            return true;
        }

        return false;
    }

    private int orientation(LineSegment segment, int x, int y) {
        int val = (segment.y2 - segment.y1) * (x - segment.x2) -
                (segment.x2 - segment.x1) * (y - segment.y2);

        if (val == 0) {
            return 0;
        }

        return (val > 0) ? 1 : 2;
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }
}

class AVLTree<T extends Comparable<T>> {
    private Node<T> root;

    private static class Node<T> {
        T data;
        Node<T> left, right;
        int height;

        Node(T data) {
            this.data = data;
            this.height = 1;
        }
    }

    public void insert(T data) {
        root = insert(root, data);
    }

    private Node<T> insert(Node<T> node, T data) {
        if (node == null) {
            return new Node<>(data);
        }

        int cmp = data.compareTo(node.data);
        if (cmp < 0) {
            node.left = insert(node.left, data);
        } else if (cmp > 0) {
            node.right = insert(node.right, data);
        } else {
            return node; // Duplicate keys are not allowed
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        if (balance > 1 && data.compareTo(node.left.data) < 0) {
            return rotateRight(node);
        }
        if (balance < -1 && data.compareTo(node.right.data) > 0) {
            return rotateLeft(node);
        }
        if (balance > 1 && data.compareTo(node.left.data) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (balance < -1 && data.compareTo(node.right.data) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        return node;
    }

    private int height(Node<T> node) {
        return (node == null) ? 0 : node.height;
    }

    private int getBalance(Node<T> node) {
        return (node == null) ? 0 : height(node.left) - height(node.right);
    }

    private Node<T> rotateRight(Node<T> y) {
        Node<T> x = y.left;
        Node<T> T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private Node<T> rotateLeft(Node<T> x) {
        Node<T> y = x.right;
        Node<T> T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    public List<T> inOrder() {
        List<T> resultList = new ArrayList<>();
        inOrder(root, resultList);
        return resultList;
    }

    private void inOrder(Node<T> node, List<T> resultList) {
        if (node != null) {
            inOrder(node.left, resultList);
            resultList.add(node.data);
            inOrder(node.right, resultList);
        }
    }
}
