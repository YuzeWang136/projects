
public class BinaryTree {
	public int cost;
	private Node root;
	public Node getRoot() {
		return root;
	}
	public void setRoot(Node root) {
		this.root = root;
	}
	public BinaryTree() 
	{
		root = null;
	}
	public void addKey(String keyValue, int weight) {
		if(root == null) {
			root = new Node();
			root.setValue(keyValue);
			return;

		}
		addKey(keyValue, weight, root);
	}
	public void addKey(String keyValue, int weight, Node curNode) {
		if(keyValue.compareTo(curNode.getValue()) > 0) {
			if (curNode.getRightNode() == null){
				curNode.setRightNode(new Node());
				curNode.getRightNode().setValue(keyValue);
				curNode.getRightNode().setWeight(weight);
			}
			else {
				addKey(keyValue, weight, curNode.getRightNode());
			}
		}
		else {
			if (curNode.getLeftNode() == null){
				curNode.setLeftNode(new Node());
				curNode.getLeftNode().setValue(keyValue);
				curNode.getLeftNode().setWeight(weight);
			}
			else {
				addKey(keyValue, weight, curNode.getLeftNode());
			}
		}
	}
	public String toString() 
	{
		String forPrint = "";
		forPrint = toString(forPrint, root);
		return forPrint;
	}
	public String toString(String forPrint, Node root) {
		if(root == null) {
			forPrint += "null";
			return forPrint;
		}
		else {
			forPrint += "(";
		}
		forPrint += root.getValue();
		forPrint += " ";
		forPrint = toString(forPrint, root.getLeftNode());
		forPrint += " ";
		forPrint = toString(forPrint, root.getRightNode());
		forPrint += ")";
		return forPrint;
	}
	
}

class Node{
	private String value;
	private int weight;
	private Node leftNode;
	private Node rightNode;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public Node getLeftNode() {
		if(leftNode == null) return null;
		return leftNode;
	}
	public void setLeftNode(Node leftNode) {
		this.leftNode = leftNode;
	}
	public Node getRightNode() {
		if(rightNode == null) return null;
		return rightNode;
	}
	public void setRightNode(Node rightNode) {
		this.rightNode = rightNode;
	}
}
