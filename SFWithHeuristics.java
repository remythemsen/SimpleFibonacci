import java.util.ArrayList;
import java.lang.StringBuilder;

/*
 * @author Chris Matrakou, Remy Themsen.
 * Both Students of ITU Copenhagen Spring 2016.
 * 
 * This is a priority queue implementation of the simple fibonacci heap as
 * described in the paper "Fibonacci Heaps Revisited" By Haim Kaplan, Robert E. 
 * Tarjan and Uri Zwick, 2014.
 *
 *
 * This Variation has the following heuristics applied (as proposed in the paper).
 *
 * 1. The Heap Order heuristic
 * 2. The increasing rank heuristic
 *
 */

public class SFWithHeuristics {

	// Map used for decrease specific Key and contains
	private Node[] nodeMap;
	// Root Node to access the heap
	private Heap heap;
	//Array to keep track of ranks
	private Node[] A;
	//golden ratio
	private final double gratio = 1.618033;
	// potential size of heap
	private final int n;

	public SFWithHeuristics(int n) {

		this.n = n;
		// Initializing map with ref's to where in the heap stuff is.
		// Index is 'ID' and Val i Ref to Node in heap
		this.nodeMap = new Node[n];
		//initialize arraylist with size logarithmic to n
		int sizeOfA = (int) Math.round(Math.ceil((Math.log(n) / Math.log(gratio))));
		this.A = new Node[sizeOfA];

		// Initializing the main Heap
		this.heap = new Heap();
	}
	public SFWithHeuristics NewHeap(int max){
		return new SFWithHeuristics(max);
	}
	// Inserts a new Entry into the Heap
	public void Insert(int id, double key) {

		// inserting item into heap
		Node node = this.makeItem(id, key);

		this.heap.root = this.insert(node, this.heap);

		// Keeping track with nodemap
		this.nodeMap[id] = node;

	}

	// Returns minimum Key
	public double FindMin() {
		return this.heap.Root().Key();
	}

	// Removes minimum Key, and returns the reference to it
	public int DeleteMin() {
		// Getting back reference
		int oldMin = this.heap.Root().Id();
		// removing nodemap entry
		this.nodeMap[this.heap.Root().Id()] = null;

		// Update root
		this.heap.Root(this.deleteMin(this.heap.Root()));

		return oldMin;

	}

	// Checks is Heap is empty
	public boolean IsEmpty() {
		return this.heap.Root() == null;
	}

	// Checks if a given id(could be vertex id) is in the Heap returns boolean
	public boolean Contains(int id) {
		if (this.nodeMap[id] == null) {
			return false;
		} else {
			return true;
		}
	}

	// Decreases the Key of a given id
	public void DecreaseKey(int id, double key) {
		if (this.Contains(id)) {
			this.heap.Root(this.decreaseKey(this.nodeMap[id], key, this.heap.Root()));
		}
	}

	//Internal Heap Class
	private class Heap {

		// A heap is considered empty if the root is null

		private Node root;

		public Node Root() {
			return this.root;
		}
		private void Root(Node r) {
			this.root = r;
		}
	}

	//Internal Node class 
	private class Node {

		//Key of the Node

		private double key;
		//id (data related to the Node)
		private int id;
		//rank and state
		private int rank;
		private boolean state;
		//pointers to siblings
		private Node next;
		private Node prev;
		//parent
		private Node parent;
		//pointer to first child
		private Node firstChild;

		// Setters
		public void Id(int Id) {
			this.id = Id;
		}

		public void Key(double Key) {
			this.key = Key;
		}

		public void Rank(int Rank) {
			this.rank = Rank;
		}

		public void State(boolean State) {
			this.state = State;
		}

		public void FirstChild(Node Child) {
			this.firstChild = Child;
		}

		public void Parent(Node Parent) {
			this.parent = Parent;
		}

		public void Prev(Node Prev) {
			this.prev = Prev;
		}

		public void Next(Node Next) {
			this.next = Next;
		}

		// Getters
		public double Key() {
			return this.key;
		}

		public int Id() {
			return this.id;
		}

		public Node FirstChild() {
			return this.firstChild;
		}

		public boolean State() {
			return this.state;
		}

		public Node Parent() {
			return this.parent;
		}

		public int Rank() {
			return this.rank;
		}

		public Node Prev() {
			return this.prev;
		}

		public Node Next() {
			return this.next;
		}

		@Override
		public String toString() {
			return String.valueOf(this.Key());
		}

	}//end of private class Node

	private Node makeItem(int id, double key) {
		Node x = new Node();
		x.Id(id);
		x.Key(key);
		x.Rank(0);
		x.State(false);
		x.FirstChild(null);
		return x;
	}

	private Node meld(Node g, Node h) {
		if (g == null) {
			return h;
		}
		if (h == null) {
			return g;
		}
		return link(g, h);
	}

	private Node delete(Node x, Node h) {
		Double k = new Double(Double.NEGATIVE_INFINITY);
		decreaseKey(x, k, h);
		return deleteMin(h);
	}

	private Node link(Node x, Node y) {

		if (x.Key() > y.Key()) {
			addChild(x, y);
			return y;
		} else {
			addChild(y, x);
			return x;
		}
	}

	private void addChild(Node x, Node y) {
		x.Parent(y);
		Node z = y.FirstChild();
		x.Prev(null);
		x.Next(z);
		if (z != null) {
			z.Prev(x);
		}
		y.FirstChild(x);

	}

	private Node insert(Node x, Heap h) {
		return meld(x, h.Root());
	}

	private void Cut(Node x) {
		Node y = new Node();
		y = x.Parent();
		if (y.FirstChild().equals(x)) {
			y.FirstChild(x.Next());
		}
		if (x.Prev() != null) {
			x.Prev().Next(x.Next());
		}
		if (x.Next() != null) {
			x.Next().Prev(x.Prev());
		}
	}

	private void decreaseRank(Node y) {
		int k;

		do {
			y = y.Parent();
			if(y == this.heap.Root()) {
				break;
			}
			if (y.Rank() > 0) {
				y.Rank(y.Rank() - 1);
			}
			y.State(!y.State());
			k = y.Rank();
		} while (!(y.State() == true) || !(k >= y.Parent().Rank()));
	}

	private Node decreaseKey(Node x, double k, Node h) {
		x.Key(k);

		if (x == h) {
			return h;
		}

		// Increasing Rank Heuristic
		if(x.Rank() < x.Parent().Rank()) {
			h.State(false);
			decreaseRank(x);
		}

		// Heap order heuristic
		if(k < x.Parent().Key()) {
			Cut(x);
			return link(x, h);
		} else {
			return h;
		}
	}

	private Node deleteMin(Node h) {
		Node y = new Node();
		Node x = new Node();
		x = h.FirstChild();
		int maxRank = 0;

		while (x != null) {
			y = x;
			x = x.Next();

			while (A[y.Rank()] != null) {
				y = link(y, A[y.Rank()]);
				A[y.Rank()] = null;
				y.Rank(y.Rank() + 1);
			}

			A[y.Rank()] = y;

			if (y.Rank() > maxRank) {
				maxRank = y.Rank();
			}
		}

		for (int i = 0; i <= maxRank; i++) {
			if (A[i] != null) {
				if (x == null) {
					x = A[i];
				} else {
					x = link(x, A[i]);
				}

				A[i] = null;
			}
		}
		return x;
	}

	public String PrettyPrint(){
		return this.traverseTree(this.heap.Root());
	}
	private String traverseTree(Node node) {
		if(node == null) return "";
		StringBuilder children = new StringBuilder();
		if(node.FirstChild() != null) {
			Node c = node.FirstChild();
			children.append(traverseTree(c));
			while(c.Next() != null) {
				c = c.Next();
				children.append(traverseTree(c));
			}
		}
		return node.toString() + "("+children.toString()+")";
	}
}
