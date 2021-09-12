import java.util.ArrayList;

public class BSTOptimizer {
	public boolean MEMOIZE;
	public int CALLS;
	private BinaryTree tree;
	private ArrayList<Node> Nodes;
	private int[][] memo;
	private Node[][] memoNode;
	public BSTOptimizer() {
		Nodes = new ArrayList<Node>();
		tree = new BinaryTree();
	}
	public void addKey(String keyValue, int weight) {
		Node newNode = new Node();
		newNode.setValue(keyValue);
		newNode.setWeight(weight);
		Nodes.add(newNode);
		memo = new int[Nodes.size()+2][Nodes.size()+2];
		memoNode = new Node[Nodes.size()+2][Nodes.size()+2];
	}
	public void addKey(String keyValue, Node curNode) {
		if(keyValue.compareTo(curNode.getValue()) > 0) {
			if (curNode.getRightNode() == null){
				curNode.setRightNode(new Node());
				curNode.getRightNode().setValue(keyValue);
			}
			else {
				addKey(keyValue, curNode.getRightNode());
			}
		}
		else {
			if (curNode.getLeftNode() == null){
				curNode.setLeftNode(new Node());
				curNode.getLeftNode().setValue(keyValue);
			}
			else {
				addKey(keyValue, curNode.getLeftNode());
			}
		}
	}
	public BinaryTree optimize() {
		tree = new BinaryTree();
		for(int i = 1; i <= Nodes.size(); i++) {
			memo[i][i-1] = Nodes.get(i-1).getWeight();
			memo[i][i] = Nodes.get(i-1).getWeight();
		}
		optimize(1, 1);
		/*for(int i = 0; i <= Nodes.size(); i++) {
			for(int j = 0; j <= Nodes.size(); j++) {
				if(memoNode[i][j] != null) System.out.println(i + " " + j + " " +memoNode[i][j].getValue());
			}
		}*/
		createTree(1,Nodes.size());
		return tree;
	}
	private int optimize(int length, int start) {
		
		CALLS ++;
		if(length == Nodes.size()) return Integer.MAX_VALUE;
		if(start + length - 1 > Nodes.size()) return Integer.MAX_VALUE;
		int end = start + length;
		int minCost = Integer.MAX_VALUE;
		for(int i = start; i < end; i++) {
			int curCost = Integer.MAX_VALUE;
			if(MEMOIZE) {
				curCost = memo[start][i-1] + memo[i+1][end];
			}
			else {
				
			}
			if(curCost < minCost) {
				minCost = curCost;
				memoNode[start][end] = Nodes.get(i-1);
			}
		}
		memo[start][end] = minCost;
		if(end < Nodes.size()) {
			optimize(length, start + 1);
		}
		else {
			optimize(length + 1, 1);
		}
		return Integer.MAX_VALUE;
	}
	private void createTree(int start, int end) {
		//System.out.println(start+ " "+end);
		if(start >= end) return;
		tree.addKey(memoNode[start][end].getValue(),memoNode[start][end].getWeight());
		if(start + 1 == end) return;
		createTree((start+end)/2, end);
		createTree(start,(start+end)/2 - 1);
	}
	private int calCost(Node curNode, int deepth, int curCost) {
		if(curNode == null) return curCost;
		curCost += curNode.getWeight() * deepth;
		curCost += calCost(curNode.getLeftNode(), deepth + 1, curCost);
		curCost += calCost(curNode.getRightNode(), deepth + 1, curCost);
		return curCost;
	}
}

