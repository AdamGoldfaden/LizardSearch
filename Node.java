import java.util.ArrayList;
import java.util.Stack;

public class Node
{
   public int val;
   public int row;
   public int col;
   public ArrayList<Node> children;
   public ArrayList<Node> preds;
   
   public Node (int row, int col, int val)
   {
      this.val = val;
      this.row = row;
      this.col = col;
      this.children = new ArrayList<Node>();
      this.preds = new ArrayList<Node>();
   }
   
   public Node(Node other)
   {
      this.val = other.val;
      this.row = other.row;
      this.col = other.col;
      this.children = other.children;
      this.preds = new ArrayList<Node>(other.preds);
   }
   
   public void addPred (Node p) 
   {
      preds.add(new Node(p));
   }
   
   public void addChild (Node c)
   {
      children.add(new Node(c));
   }
   
   public boolean equals(Node o)
   {
      return this.row == o.row && this.col == o.col;
   }
}