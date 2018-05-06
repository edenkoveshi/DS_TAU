/**
 *
 * WAVLTree
 *
 * An implementation of a WAVL Tree with
 * distinct integer keys and info
 * 
 * Implemented by:
 * Eden Koveshi
 * Amir Hertz
 *
 */
public class WAVLTree {
	
	private WAVLNode root;
	private int size; //in order to return size in O(1)
	private int balanceops; //global variable,counting balance operations
	private int index; //global variable for info/key to array functions.
	private WAVLNode minFP; //finger pointer to minimal key node.in order to return min in O(1)
	private WAVLNode maxFP; //finger pointer to maximal key node. to return max in O(1)
	
   /**
	* create an empty WAVL
	* with external leaf
	*/
	public WAVLTree(){
		this.root = new WAVLNode(null);
		this.size=0;
		minFP = root;
		maxFP = root;
	}
	
	public WAVLNode getRoot() {
		return this.root;
	}
	
	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 * time complexity: O(1)
	 */
	public boolean empty() {
		return root.isExternal();
	}
	
	/**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree
     * otherwise, returns null
     * time complexity: O(log n), n=number of tree nodes
     */
	public String search(int k){
		if (this.empty())return null;
		WAVLNode parent = findParent(this.root, k);
		if (this.root.getKey() == k) return this.root.getInfo();
		if (!parent.getRight().isExternal() && parent.getRight().getKey() == k) return parent.getRight().getInfo();
		if (!parent.getLeft().isExternal() && parent.getLeft().getKey() == k) return parent.getLeft().getInfo();	
		return null;
	}
	
	/** 
	 * returns the parent of an item with key k if it exists in the tree
     * otherwise, returns null
     * time complexity: O(log n), n=number of tree nodes
     */
	private WAVLNode findParent(WAVLNode node,int k){
		if(node.isExternal())
		   {
			   return node.getParent();
		   }
		   if(k==node.getKey())
			   return node.getParent();
		   if(k>node.getKey())
			   return findParent(node.getRight(),k);
		   return findParent(node.getLeft(),k);
	}
	
   /**
	* public String min()
	*
	* Returns the info of the item with the smallest key in the tree,
	* or null if the tree is empty
	* time complexity: O(1)
	*/
	public String min(){
		if(empty()) return null;
		return minFP.getInfo();
	}
	
   /**
	* public String max()
	*
	* Returns the info of the item with the largest key in the tree,
	* or null if the tree is empty
	* time complexity: O(1)
	*/
	public String max(){
		if(empty()) return null;
		return maxFP.getInfo();
	}
	
	// update finger pointers after insertion
	private void insertUpdateFP(WAVLNode newNode){
		if (minFP.isExternal() || newNode.getKey() < minFP.getKey()) minFP = newNode;
		if (maxFP.isExternal() || newNode.getKey() > maxFP.getKey()) maxFP = newNode;
	}
	
	// update finger pointers after deletion
	private void deleteUpdateFP(WAVLNode oldNode){
		if (size == 0) {
			maxFP = minFP = new WAVLNode(null);
		}
		else{
			if (oldNode.getKey() == minFP.getKey()){
				if (minFP.getRight().isExternal()) minFP = minFP.getParent();
				else minFP = minFP.getRight();
			}
			if (oldNode.getKey() == maxFP.getKey()){
				if (maxFP.getLeft().isExternal()) maxFP = maxFP.getParent();
				else maxFP = maxFP.getLeft();
			}
		}
	}
			
   /**
	* public int[] keysToArray()
	*
	* Returns a sorted array which contains all keys in the tree,
	* or an empty array if the tree is empty.
	* time complexity: O(n)
	*/
	public int[] keysToArray(){
		int[] arr = new int[this.size()];
		this.index=0;
		if(!root.isExternal()) keysToArray(arr,root);
		return arr;	
	}
	
	/**
	 * private void keysToArray(int[] arr,WAVLNode node)
	 * 
	 * inner function for keysToArray()
	 * build recursively the keys array
	 * go over the tree in-order
	 * time complexity: O(n)
	 */
	private void keysToArray(int[] arr,WAVLNode node){
		if(!(node.isExternal())){
			keysToArray(arr,node.getLeft());// in-order
			arr[index]=node.getKey();
			index++;
			keysToArray(arr,node.getRight());
		}
	}
	
   /**
	* public String[] infoToArray()
	*
	* Returns an array which contains all info in the tree,
	* sorted by their respective keys,
	* or an empty array if the tree is empty.
	* time complexity: O(n)
	*/
	public String[] infoToArray(){
		String[] arr = new String[this.size()];
		this.index=0;
		if(!root.isExternal()) infoToArray(arr,root);
		return arr;	
	}
	
	/**
	 * private void infoToArray(String[] arr,WAVLNode node)
	 * 
	 * inner function for infoToArray()
	 * build recursively the info array
	 * go over the tree in-order
	 * time complexity: O(n)
	 */
	private void infoToArray(String[] arr,WAVLNode node){
		if(!(node.isExternal())){
			infoToArray(arr,node.getLeft());// in-order
			arr[index]=node.getInfo();
			index++;
			infoToArray(arr,node.getRight());
		}
	}
	
	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 * time complexity: O(1)
	 */
	public int size(){
		return this.size;
	}
	
   /**
	* public int insert(int k, String i)
	* inserts an item with key k and info i to the WAVL tree.
	* the tree must remain valid (keep its invariants).
	* returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
	* returns -1 if an item with key k already exists in the tree.
	* time complexity: O(log n), n=number of tree nodes
	*/
	public int insert(int k, String i) {
		this.balanceops = 0;
		if (this.empty()){
			//new node is the root
			this.root = new WAVLNode(k,i);
			this.insertUpdateFP(root); //update minFP & maxFP
			size++;
			return balanceops;
		}
		WAVLNode parent=findParent(root,k);
		if(root.getKey() == k || ((!parent.getRight().isExternal() && parent.getRight().getKey()==k)||(!parent.getLeft().isExternal() && parent.getLeft().getKey()==k)))
			return -1; //node already exists in the tree
		
		WAVLNode NodeToInsert=new WAVLNode(k,i);
		this.insertUpdateFP(NodeToInsert); //update minFP & maxFP
		NodeToInsert.setParent(parent);
		//new node is right child
		if(k > parent.getKey()) parent.setRight(NodeToInsert);
		//new node is left child
		if(k < parent.getKey()) parent.setLeft(NodeToInsert);
		BalanceAfterInsertion(NodeToInsert);
		size++;
		
		return balanceops;
	}
	
   /**
	* Balances the tree after insertion
	* either rotates around node or promotes the node's rank, according to the situation
	* time complexity: O(log n)-worst case, O(1)- amortized
	*/
	private void BalanceAfterInsertion(WAVLNode node){
		
		WAVLNode parent=node.getParent();
		if(node == root || parent.getRank()-node.getRank() > 0)
			   return;//the tree is valid WAVL
		
		WAVLNode rightchild=node.getRight();
		WAVLNode leftchild=node.getLeft();
		
		if(node == parent.getLeft()){
			//case 1 - promote
			if(parent.getRank()-parent.getRight().getRank()==1){
				promote(parent);
				BalanceAfterInsertion(parent);
			}
			//case 2 - rotate right
			else if(rightchild.getRank() < leftchild.getRank()){
				demote(parent);
				RotateRight(node);	
			}
			//case 3 - double rotate
			else{
				demote(parent);
				demote(node);
				promote(rightchild);
				RotateLeft(rightchild);
				RotateRight(rightchild);//no longer a right child of node,but it still points to the same node	
			}
		}
		// up to symmetry
		else{
			//case 1 - promote
			if(parent.getRank()-parent.getLeft().getRank()==1){
				promote(parent);
				BalanceAfterInsertion(parent);	
			}
			//case 2 - rotate left
			else if(leftchild.getRank() < rightchild.getRank()){
				demote(parent);
				RotateLeft(node);
			}
			//case 3 - double rotate
			else{
				demote(parent);
				demote(node);
				promote(leftchild);
				RotateRight(leftchild);
				RotateLeft(leftchild);//no longer a right child of node,but it still points to the same node	
			}
		}
	}
	
   /**
	* public int delete(int k)
	*
	* deletes an item with key k from the binary tree, if it is there;
	* the tree must remain valid (keep its invariants).
	* returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
	* returns -1 if an item with key k was not found in the tree.
	* time complexity: O(log n), n=number of tree nodes
	*/
	public int delete(int k){
		
		balanceops = 0;
		if (size == 0) return -1;
		WAVLNode parent=findParent(root,k);
		WAVLNode node;
		boolean isRight = false;
		
		if (root.getKey() == k) node = root;
		else if(!parent.getLeft().isExternal() && parent.getLeft().getKey() == k){
			node=parent.getLeft();
			isRight=false;
		}
		else if(!parent.getRight().isExternal() && parent.getRight().getKey() == k){
			node=parent.getRight();
			isRight=true;
		}
		else return -1;
		size--;
		this.deleteUpdateFP(node); //update minFP & maxFP
		node = deleteInitial(node, isRight); // move deletion to leaf
		balanceAfterDeletion(node);  

		return balanceops;
	}
   
	private WAVLNode deleteInitial(WAVLNode node, boolean isRight ){
		WAVLNode parent=node.getParent();
		//node has two children
		if (!node.getLeft().isExternal() && !node.getRight().isExternal()){
			WAVLNode successor = this.successor(node);
			this.swap(node,successor);
			//node = successor;
			parent=node.getParent();
			if(node==parent.getRight()) isRight=true;
			else isRight=false;
		}
		// node has one child
		if (!node.isALeaf()){
			
			if (!node.getLeft().isExternal()){
				swap(node,node.getLeft());
				//node = node.getLeft();
			}
			else{
				swap(node,node.getRight());
				//node = node.getRight();
			}
			parent=node.getParent();
			if(node==parent.getRight()) isRight=true;
			else isRight=false;
		}
		//node is a leaf
		if (node == this.root){
			root = new WAVLNode(null);
			return root;
		}
		node = new WAVLNode(parent);
		if(isRight) parent.setRight(node);
		else parent.setLeft(node);
		if(parent.getRank()-parent.getRight().getRank() == 2 && parent.getRank()-parent.getLeft().getRank() == 2)
		   {	//not a legal leaf
			   demote(parent);
			   return parent;
		   }
		return node;
	}
   /**
	* Balances the tree after deletion op.
	* either rotates around node or demotes node.
	* time complexity: O(log n)-worst case, O(1)- amortized
	*/
	private void balanceAfterDeletion(WAVLNode node)
	{
		WAVLNode parent =  node.getParent();
		//finish rebalance
		if (parent==null || parent.getRank()-node.getRank()<3){
			return;
		}
		// node is right child
		if (node == parent.getRight()){
			WAVLNode brother = parent.getLeft();
			//case 1
			if (parent.getRank()-brother.getRank() == 2){
				demote(parent);
				balanceAfterDeletion(parent); 
			}
			//case 2
			else if (brother.getRank()-brother.getLeft().getRank()==2 && brother.getRank()-brother.getRight().getRank()==2){
				demote(parent);
				demote(brother);
				balanceAfterDeletion(parent);	
			}
   
			//case 3
			else if (brother.getRank()-brother.getLeft().getRank()==1){
				RotateRight(brother);
				promote(brother);
				demote(parent);
				//not a legal leaf
				if (parent.isALeaf() && parent.getRank()-parent.getLeft().getRank()==2 && parent.getRank()-parent.getRight().getRank()==2){
					demote(parent);
				}
			}
			//case 4
			else{
				WAVLNode axis = brother.getRight();
				demote(parent);
				demote(parent);
				demote(brother);
				promote(axis);
				promote(axis);
				RotateLeft(axis);
				RotateRight(axis);	
			}
		}
		// up to symmetry
		else{
			WAVLNode brother = parent.getRight();
			//case 1
			
			if (parent.getRank()-brother.getRank() == 2){
				demote(parent);
				balanceAfterDeletion(parent); 
			}
			//case 2
			else if (brother.getRank()-brother.getLeft().getRank()==2 && brother.getRank()-brother.getRight().getRank()==2){
				demote(parent);
				demote(brother);
				balanceAfterDeletion(parent);	
			}  
			//case 3
			else if (brother.getRank()-brother.getRight().getRank()==1){
				RotateLeft(brother);
				promote(brother);
				demote(parent);
				//not a legal leaf
				if (parent.isALeaf() && parent.getRank()-parent.getRight().getRank()==2 && parent.getRank()-parent.getLeft().getRank()==2){
					demote(parent);
				} 
			}
			//case 4
			else{
				WAVLNode axis = brother.getLeft();
				demote(parent);
				demote(parent);
				demote(brother);
				promote(axis);
				promote(axis);
				RotateRight(axis);
				RotateLeft(axis);
		   } 
	   }
   }  
	
   /**
	* private void swap(WAVLNode nodeA, WAVLNode nodeB)
	* nodA must be higher in the tree than nodeB
	* time complexity: O(1)
	*/
	private void swap(WAVLNode nodeA, WAVLNode nodeB){
		
		WAVLNode tmp = new WAVLNode(nodeA.getKey(),nodeA.getInfo());
		tmp.setParent(nodeA.getParent());
		tmp.setRight(nodeA.getRight());
		tmp.setLeft(nodeA.getLeft());
		tmp.setRank(nodeA.getRank());
		
		nodeA.setRight(nodeB.getRight());
		nodeA.setLeft(nodeB.getLeft());
		nodeA.setRank(nodeB.getRank());
		nodeA.getLeft().setParent(nodeA);
		nodeA.getRight().setParent(nodeA);
		if (nodeB.getParent() == nodeA) nodeA.setParent(nodeB);
		else{
			nodeA.setParent(nodeB.getParent());
			if (nodeB == nodeB.getParent().getLeft()) nodeB.getParent().setLeft(nodeA);
			else nodeB.getParent().setRight(nodeA);
		}
		
		nodeB.setParent(tmp.getParent());
		if (tmp.getRight() == nodeB) nodeB.setRight(nodeA);
		else nodeB.setRight(tmp.getRight());
		if (tmp.getLeft() == nodeB) nodeB.setLeft(nodeA);
		else nodeB.setLeft(tmp.getLeft());
		nodeB.setRank(tmp.getRank());
		nodeB.getLeft().setParent(nodeB);
		nodeB.getRight().setParent(nodeB);
		if (nodeB.getParent() != null){
			if (nodeA == nodeB.getParent().getLeft()) nodeB.getParent().setLeft(nodeB);
			else nodeB.getParent().setRight(nodeB);
		}
		else root = nodeB;
	}
	
   /**
	* returns the successor of node
	* when node has right child
	* @param node
	* @return successor
	*/
	private WAVLNode successor(WAVLNode node){
		WAVLNode successor=node.getRight();
		while(successor.getLeft().getRank()!=-1)successor=successor.getLeft();	
		return successor;
	}
	
   /**
	* Performs right rotation around node.
	* @param node
	*/
	private void RotateRight(WAVLNode node){
		WAVLNode grandparent=node.getParent().getParent();
		if(grandparent != null){
			if(grandparent.getRight() == node.getParent()) grandparent.setRight(node);
			else grandparent.setLeft(node);	
		}
		else root = node;
		node.getParent().setParent(node);
		node.getParent().setLeft(node.getRight());
		node.getRight().setParent(node.getParent());
		node.setRight(node.getParent());
		node.setParent(grandparent);
		balanceops++;
	}
	
	/**
	* Performs left rotation around node.
	* @param node
	*/
	private void RotateLeft(WAVLNode node){
		WAVLNode grandparent=node.getParent().getParent();
		if(grandparent != null){
			if(grandparent.getRight() == node.getParent()) grandparent.setRight(node);
			else grandparent.setLeft(node);
		}
		else root = node;
		node.getParent().setParent(node);
		node.getParent().setRight(node.getLeft());
		node.getLeft().setParent(node.getParent());
		node.setLeft(node.getParent());
		node.setParent(grandparent);
		balanceops++;
	}
 
   /**
	* Increases node's rank by 1.
	* @param node
    */
	private void promote(WAVLNode node){
		   node.setRank(node.getRank()+1);
		   balanceops++;
	}
	
   /**
	* Decreases node's rank by 1.
	* @param node
	*/
	public void demote(WAVLNode node){
		node.setRank(node.getRank()-1);
		balanceops++; 
	}
	

/**
 * public class WAVLNode
 *
 * If you wish to implement classes other than WAVLTree
 * (for example WAVLNode), do it in this file, not in 
 * another file.
 * This is an example which can be deleted if no such classes are necessary.
 */
public class WAVLNode{
	  
	private WAVLNode left;
	private WAVLNode right;
	private WAVLNode parent;
	private int rank;
	private int key;
	private String info;
	
   /**
	* Leaf Constructor
	* Create leaf with external nodes as children.
	* Rank is 0.
	* @param key
	* @param info
	*/
	public WAVLNode (int key,String info)
	  {
		  this.key=key;
		  this.info=info;
		  this.rank=0;
		  this.right=new WAVLNode(this);
		  this.left=new WAVLNode(this);
		  this.parent=null;
	  }
   /**
	* External node Constructor
	* Rank is -1.
	* @param parent
	*/
	public WAVLNode (WAVLNode parent)
	  {
		  this.parent=parent;
		  this.rank=-1;
		  this.key=0;
	  }

	public WAVLNode getLeft() {
		return left;
	}
	public void setLeft(WAVLNode left) {
		this.left = left;
	}
	public WAVLNode getRight() {
		return right;
	}
	public void setRight(WAVLNode right) {
		this.right = right;
	}
	public WAVLNode getParent() {
		return parent;
	}
	public void setParent(WAVLNode parent) {
		this.parent = parent;
	}
	public boolean isALeaf() {
		if (this.rank == -1) return false;
		return (this.left.isExternal() && this.right.isExternal());
	}
	public boolean isExternal() {
		return this.rank==-1;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
}

}
