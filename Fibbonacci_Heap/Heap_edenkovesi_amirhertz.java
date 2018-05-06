import java.util.Arrays;
/**
 *
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over non-negative integers.
 * 
 * Implemented by:
 * Eden Koveshi
 * Amir Hertz
 *
 */
public class FibonacciHeap
{
	private HeapNode min;
	private int size;
	private int marked;
	private int roots;//== #trees
	public static int links = 0;
	public static int cuts = 0;
	/**
	 * Constructor 1
	 * starts with empty heap
	 */
	public FibonacciHeap(){
		min = null;
		size = 0;
		marked = 0;
		roots = 0;
	}
	/**
	 * Constructor 1
	 * starts with one element
	 */
	public FibonacciHeap(int key){
		this();
		insert(key);
	}
	
	/**
    * public boolean empty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    * 
    * Time Complexity: O(1)
    *   
    */
    public boolean empty()
    {
    	return min == null;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * 
    * Time Complexity: O(1) 
    */
    public HeapNode insert(int key)
    {  
    	HeapNode node = new HeapNode(key);
    	size++;
    	return insert(node); //inner method for more methods
    	
    }
    
    /**
     * private HeapNode insert(HeapNode x)
     *
     * internal insertion of an existing HeapNode.
     * this method is called when making new insertion, cutting node
     * and at the end of Consolidate process. 
     * 
     * Time Complexity: O(1) 
     */
    private HeapNode insert (HeapNode x)
    {
    	if (min == null){
    		min = x;
    		min.setNext(min);
    		min.setPrev(min);
    	}
    	else {
    		//chains x to the main roots
    		HeapNode tmpNode = min.getNext();
    		min.setNext(x);
    		x.setPrev(min);
    		x.setNext(tmpNode);
    		x.getNext().setPrev(x);
    		//update min
    		if (x.getKey() < min.getKey()){
    			min = x;
    		}
    	}
    	roots++;
    	return x;
    }

   /**
    * public void deleteMin()
    * Delete the node containing the minimum key.
    * Time Complexity: O(log n) amortized / O(n) worst case
    */
    public void deleteMin()
    {
    	if(size==0)
    		return;
    	HeapNode child=min.getChild();
    	roots += min.getRank()-1;
    	
    	if(child!=null) 
    	{
    		HeapNode first=child;
    		do //update children of min
    		{
    			
    			unmark(child);
    			child.setParent(null);
    			child=child.getNext();
    		} while(child!=first);
    		
    		if(roots==min.getRank()) //the heap had only one root
    		{ 
    			min=child;
    		}
    		else //chains min's children to the main roots and skipping min
    		{	
		    	child.getPrev().setNext(min.getNext());;
		     	min.getNext().setPrev(child.getPrev());
		     	child.setPrev(min.getPrev());
		     	min.getPrev().setNext(child);
		     	min=child;
    		}
    	}
    	else //min doesn't have children
    	{
    		if(size==1) //heap is now empty
    		{
    			min=null;
    		}
    		else //skipping min
    		{
    			min.getPrev().setNext(min.getNext());
    			min.getNext().setPrev(min.getPrev());
    			min=min.getNext();
    		}
    	}
    	size--;
    	
    	if( size!=0 && roots > 1 )
        	Consolidate();//rebalancing+min update
    	
    	
    }
    
    /**
     * private void Consolidate()
     * unites all trees with the same rank and updates the minimal root
     * Time Complexity: O(log n) amortized / O(n) worst case
     */
    private void Consolidate()
    {
    	HeapNode[] arr = new HeapNode[(int) Math.ceil(1.4405*Math.log(size)*1.442695)+1]; //array size is the maximal rank.
    	HeapNode x =min;
    	while(roots>0) //go over roots
    	{
    		min=x.getNext();
    		x.setNext(x);
    		x.setPrev(x);
    		roots--;
    		while(arr[x.getRank()]!=null) //cell is occupied
    		{
    			HeapNode y=arr[x.getRank()];
    			arr[x.getRank()]=null;
    			if(x.getKey()>y.getKey())
    			{ 
    				x=Join(y,x);
    			}
    			else
    			{
      				x=Join(x,y);
    			}
    		
    		}
    		arr[x.getRank()]=x;
    		x=min;
    	}
    	min=null;
    	for(int i=0;i<arr.length;i++) //rebuilding the heap
    	{
    		if(arr[i]!=null)
    		
    			insert(arr[i]);
    	}
    }

    /**
     * public HeapNode Join(HeapNode x,HeapNode y)
     * 
     * @param x
     * @param y
     * assumes x.key<y.key
     * join y as x child and return x.
     * 
     * Time Complexity: O(1)
     */
    public HeapNode Join(HeapNode x,HeapNode y)
    {
    	
    	if(x.getChild()!=null) 
    	{
    		//chains y to x children
    		y.setPrev(x.getChild().getPrev());
    		y.setNext(x.getChild());
    		x.getChild().getPrev().setNext(y);
    		x.getChild().setPrev(y);
    	}
    	x.setChild(y);
    	y.setParent(x);
    	x.setRank(x.getRank()+1);
    	links++;
    	return x;
    }

   /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal.
    * 
    * Time Complexity: O(1)
    */
    public HeapNode findMin()
    {
    	return min; 
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    * Time Complexity: O(1)
    */
    public void meld (FibonacciHeap heap2)
    {
    	HeapNode min2=heap2.findMin();
    	if(heap2.empty()) return; 
    	if(this.empty()) min=min2;
    	else{
    		HeapNode temp=min.getNext();
        	min.setNext(min2);
        	min2.getPrev().setNext(temp);
        	temp.setPrev(min2.getPrev());
        	min2.setPrev(min);	
    	}
    	//combine private fields
    	roots+=heap2.getRoots();
    	size+=heap2.size();
    	marked+=heap2.getMarked();
    	
    	if(this.min.getKey()>min2.getKey())
    		  min=min2;
    	  
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    * 
    * Time Complexity: O(1)
    *   
    */
    public int size()
    {
    	return size;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    * Time Complexity: O(n) worst case
    * 
    */
    public int[] countersRep()
    {
    	
    	
    	int[] arr = new int[(int) Math.ceil(1.44*Math.log(size)*1.442695)+1];//array size is the maximal rank.
	
		if (size == 0) return new int[0];
		
		HeapNode currentRoot = min.getNext();
		arr[min.getRank()]++;
		int maxRank =min.getRank();
		while (currentRoot != min){ //go over roots
			if (maxRank<currentRoot.getRank()) maxRank = currentRoot.getRank();
			arr[currentRoot.getRank()]++;
			currentRoot = currentRoot.getNext();
		}
		return Arrays.copyOfRange(arr,0,maxRank+1);	//arr[last index] is at least 1
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
    * 
    * Time Complexity: O(log n) amortized / O(n) worst case
    *
    */
    public void delete(HeapNode x) 
    {    
    	decreaseKey(x,Integer.MAX_VALUE);
    	deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    * Time Complexity: O(1) amortized, O(n) worst case
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	x.setKey(x.getKey()-delta);
    	if (x.getParent() != null && x.getKey() < x.getParent().getKey()){
    		cascadingCut(x);
    	}
    	else if (x.getKey()<min.getKey())
    		min=x;
    }
    
    /**
     * private void cut(HeapNode x)
     * @param x
     * cuts x from the tree and insert it as a root.
     * Time Complexity: O(1).
     */
    private void cut(HeapNode x){
    	cuts++;
    	HeapNode parent=x.getParent();
    	x.setParent(null);
    	if(parent.getChild()==x)
    	{
    		if(x.getNext()!=x)
    			parent.setChild(x.getNext()); 
    		else
    			parent.setChild(null);
    	}
    	parent.setRank(parent.getRank()-1);
    	
    	HeapNode next=x.getNext();
    	next.setPrev(x.getPrev());
    	HeapNode prev=x.getPrev();
    	prev.setNext(x.getNext());
    	
    	insert(x);//inserts x as a root and updates #roots
    	unmark(x);
    	
    }

    /**
     *private void cascadingCut(HeapNode x) 
     * @param x
     * cuts the series of marked ancestors of x.
     * Time Complexity: O(1) amortized, O(n) worst case
     */
    private void cascadingCut(HeapNode x){
    	HeapNode parent=x.getParent();
    	cut (x);
    	if (!parent.isRoot()){ 
    		if (parent.isMarked()){ //continue cascade
    			cascadingCut(parent);
    		}
    		else{ //stop cascading by marking
    			mark(parent);
    		}
    	}
    }
    /**
     * void mark(HeapNode x)
     * marks x.
     */
    private void mark(HeapNode x){
    	if(!x.isMarked())
    	{
    		x.setMark(true);
    		marked++;
    	}
    }
    /**
     * void unmark(HeapNode x)
     * @param x
     * unmarks x.
     */
    private void unmark(HeapNode x){
    	if(x.isMarked())
    	{
    		x.setMark(false);
    		marked--;
    	}
    }
    
   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap.
    * 
    * Time Complexity: O(1)
    */
    public int potential() 
    {    
    	return roots + 2 * marked;
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    * 
    * Time Complexity: O(1)
    */
    public static int totalLinks()
    {    
    	return links;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods).
    * 
    * Time Complexity: O(1)
    */
    public static int totalCuts()
    {    
    	return cuts;
    }
    
    /**
	 *More Getters (are used when melding)
	 */
	public int getMarked() {
		return marked;
	}


	public int getRoots() {
		return roots;
	}
	
	/**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{
    	private int key;
    	private int rank;
    	private boolean mark;
    	private HeapNode child;
    	private HeapNode parent;
    	private HeapNode next;
    	private HeapNode prev;
    	/**
    	 * Constructor.
    	 * @param newKey
    	 */
    	public HeapNode(int newKey){
    		mark = false;
    		key = newKey;
    	}
    	/**
    	 * boolean isRoot()
    	 * @return true if the node is a root, false otherwise.
    	 */
		public boolean isRoot() {
			
			return parent == null;
		}
		/**
		 * Getters/Setters
		 */
		public int getKey() {
			return key;
		}

		public void setKey(int key) {
			this.key = key;
		}

		public int getRank() {
			return rank;
		}

		public void setRank(int rank) {
			this.rank = rank;
		}

		public boolean isMarked() {
			return mark;
		}

		public void setMark(boolean mark) {
			this.mark = mark;
		}

		public HeapNode getChild() {
			return child;
		}

		public void setChild(HeapNode child) {
			this.child = child;
		}

		public HeapNode getParent() {
			return parent;
		}

		public void setParent(HeapNode parent) {
			this.parent = parent;
		}
		public HeapNode getNext()
		{
		return next;
		}

		public void setNext(HeapNode next) {
			this.next = next;
		}

		public HeapNode getPrev() {
			return prev;
		}

		public void setPrev(HeapNode prev) {
			this.prev = prev;
		}
    	
    	
    	
    }
}
