import java.util.ArrayList;
import java.lang.StringBuilder;

/*
* @Authors Chris Matrakou, Remy Themsen.
* Both Students of ITU Copenhagen Spring 2016.
* 
* This is a priority queue implementation of the simple fibonacci heap as
* described in the paper "Fibonacci Heaps Revisited" By Haim Kaplan, Robert E. 
* Tarjan and Uri Zwick, 2014.
*
* This variant has a reduced number of pointers, more specifically its a 
* reduction of pointers from four to three on each node. 
*
*/

public class SFPointerReduced {

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

	public SFPointerReduced(int n) {
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
	public SFPointerReduced NewHeap(int max){
		return new SFPointerReduced(max);
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

	// Removes minimum Key, and returns it
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
		//		private Node next;
		//		private Node prev;
		private Node sibling;
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

		//		public void Prev(Node Prev) {
		//			this.prev = Prev;
		//		}
		//
		//		public void Next(Node Next) {
		//			this.next = Next;
		//		}
		public void Sibling(Node s) {
			this.sibling = s;
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

		//		public Node Prev() {
		//			return this.prev;
		//		}
		//
		//		public Node Next() {
		//			return this.next;
		//		}
		public Node Sibling() {
			return this.sibling;
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
		//only root has sibling == null [invariant]
		if(x.FirstChild() != null)
			x.FirstChild().Sibling(null);
		y.Parent(null);

		y.Sibling(null);
		if(x.FirstChild() != null && x.FirstChild().Parent() != x)
			x.FirstChild(null);
		if(x.FirstChild() != null && x.FirstChild().Sibling() == y) {
			x.FirstChild().Sibling(null);
		}
		x.Parent(y);
		if(x.FirstChild() != null)
			x.FirstChild().Sibling(null);

		// What if y already has children?
		if(y.FirstChild() != null && y.FirstChild().Parent() == y) {
			Node z = y.FirstChild();
			y.FirstChild(x);
			z.Sibling(x);
			//if x does not have a child, firstchild points to next node because 
			//else we can not access next node
			if (x.FirstChild() == null) {
				x.FirstChild(z);
			} //if it has a firstchild point to its parent sibling(next)
			else {
				x.FirstChild().Sibling(z);
			}

		} else {
			// Then Y already has a child
			y.FirstChild(x);
			x.Sibling(null);
		}

	}

	private Node insert(Node x, Heap h) {
		return meld(x, h.Root());
	}

	private void Cut(Node x) {
		// Does X have a parent?

		Node y = new Node();
		y = x.Parent();

		if (y.FirstChild() == x) {
			// Case X's first childs parent is in fact X (and not X's direct (next) sibling)
			Node z = x.FirstChild();
			if (z.Parent() != x) {
				y.FirstChild(z); // z was in fact x's direct sibling
			} else { // x's sibling is accessed through it's first child's sibling (like in paper)
				y.FirstChild(x.FirstChild().Sibling());
			}
		} else {
			// Case X is not the first child
			// Grab it's sibling (since X is not first child, then it's sibling will
			// - be the 'prev' sibling 
			Node z = x.Sibling(); // z is the prev of x
			Node w; // w is the next of x

			// get the correct W
			if (x.FirstChild().Parent() == x) {
				w = x.FirstChild().Sibling(); // z was in fact x's direct sibling
			} else { // x's sibling is accessed through it's first child's sibling (like in paper)
				w = x.FirstChild();
			}

			// Setting the 'prev' of W to be z
			w.Sibling(z);

			// Update z to point to x's 'next' sibling W
			if (z.Sibling() == x) { // Check if 'z' is point directly to X
				z.Sibling(w);
			} else {
				z.FirstChild().Sibling(w);
			}

		}

	}

	private void decreaseRank(Node y) {
		do {
			y = y.Parent();
			if (y.Rank() > 0) {
				y.Rank(y.Rank() - 1);
			}
			y.State(!y.State());
		} while (y.State() == false);
	}

	private Node decreaseKey(Node x, double k, Node h) {
		x.Key(k);
		if (x.equals(h)) {
			return h;
		}
		h.State(false);
		decreaseRank(x);
		Cut(x);
		return link(x, h);
	}

	private Node deleteMin(Node h) {

		Node y = new Node();
		Node x = new Node();
		x = h.FirstChild();
		int maxRank = 0;

		// Loops of 'h's' first childs siblings
		while (x != null) {
			y = x;

			// Getting X's Nex Sibling
			x = getNextSibling(x);

			// Linking Nodes together with same rank
			// Then increase the rank by 1 of the new 'winner'
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

	@Override
	public String PrettyPrint() {
		return this.traverseTree(this.heap.Root());
	}


	private String traverseTree(Node node) {
		StringBuilder s = new StringBuilder();

		//base case
		if (node.FirstChild() == null || node.FirstChild().Parent() != node) {
			return node.toString() + "()";
		} //merge step
		else {
			Node c = node.FirstChild();
			s.append(traverseTree(c));

			while (hasNextSibling(c)) {
				s.append(traverseTree(getNextSibling(c)));
				c = getNextSibling(c);
			}

		}
		return node.toString() + "("+s.toString()+")";
	}

	private boolean hasNextSibling(Node c) {
		if (c.FirstChild() != null && c.FirstChild().Sibling() != null) {
			return true;
		} else {
			return false;
		}
	}

	private Node getNextSibling(Node c) {
		if (c.FirstChild() != null && c.FirstChild().Parent() == c) {
			return c.FirstChild().Sibling();
		} else {
			return c.FirstChild();
		}
	}
}
