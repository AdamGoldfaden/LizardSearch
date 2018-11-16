import java.io.*;
import java.util.*;

public class LizardSearch 
{
   private static String searchType;
   private static int n;
   private static int p;
   private static ArrayList<Node> trees;
   private static int maxTreeRow;
   private static Node [][] mat;
   private static PrintWriter out;
   
   public static void main(String [] args) throws FileNotFoundException
   {
      ReadText();
      if(CheckFail()) {
         out.println("FAIL");
         out.close();
         return;
      }
      if(p == 0) {
         out.println("OK");
         PrintMat();
         return;
      }
      
      if(searchType.equals("SA")) {
         if(RunSA()) {
            out.println("OK");
            PrintMat();
         }
         else
            out.println("FAIL");
      }
      
      else if(searchType.equals("BFS")) {
         CreateChildren();
         Node plz = RunBFS();
         if(plz.preds.size() != 0) {
            out.println("OK");
            PrintMat(plz);
         }
         else
            out.println("FAIL");
      }
      
      else if(searchType.equals("DFS")) {
         CreateChildren();
         Node plz = RunDFS();
         if(plz.preds.size() != 0) {
            out.println("OK");
            PrintMat(plz);
         }
         else
            out.println("FAIL");
      }

      
      out.close();
      
   }
   
   static boolean RunSA() {
      ArrayList<Node> lizards = new ArrayList<Node>();
      Random rand = new Random();
      
      for(int i = 0; i < p; i++) {
         int r = rand.nextInt(n);
         int c = rand.nextInt(n);
         while(mat[r][c].val != 0) {
            r = rand.nextInt(n);
            c = rand.nextInt(n);
         }
         mat[r][c].val = 1;
         lizards.add(new Node(mat[r][c]));
      }
      
      Date d = new Date();
      long t1 = d.getTime();
      long t2 = d.getTime();
      int k = 0;
      double T0 = 50;
      double T = T0;
      int curConflicts = CheckConflicts(lizards);
       

      while(t2 - t1 < 270000 && T > 0) {
         if(curConflicts == 0)
            return true;
         
         int liz = rand.nextInt(p);
         int r = rand.nextInt(n);
         int c = rand.nextInt(n);
         ArrayList<Node> neighborLizards = new ArrayList<Node>(lizards);
         
         while(mat[r][c].val != 0) {
            r = rand.nextInt(n);
            c = rand.nextInt(n);
         }
         
         mat[lizards.get(liz).row][lizards.get(liz).col].val = 0;
         mat[r][c].val = 1;
         neighborLizards.set(liz, new Node(mat[r][c]));
         int neighborConflicts = CheckConflicts(neighborLizards);
         
         double delta = (double)(neighborConflicts - curConflicts);
         if(delta < 0) {
            lizards = new ArrayList<Node>(neighborLizards);
            curConflicts = neighborConflicts;
         }
         else {
            double P = Math.exp(-delta/T);
            double R = rand.nextDouble();
            
            if(R < P) {
               lizards = new ArrayList<Node>(neighborLizards);
               curConflicts = neighborConflicts;
            }
            else {
               mat[lizards.get(liz).row][lizards.get(liz).col].val = 1;
               mat[r][c].val = 0;
            }
         }
         
         k++;
         
         T = T0/(1.0 + Math.log(1 + k));
         if(T == 0)
            T = T0;
         t2 = new Date().getTime();
      }
      return false;
   }
      
   static Node RunBFS() {
      Queue<Node> list = new LinkedList<Node>();
      
      for(int i = 0; i < n; i++) {
         list.add(new Node(mat[0][i]));
      }
      
      
      while(!list.isEmpty()) {
         Node cur = list.poll();
         
         if(CheckAll(cur) && cur.val == 0 && cur.preds.size() + 1 == p) {
            cur.addPred(cur);
            return cur;
         }
         
         else if(CheckAll(cur) && cur.val == 0) {
            for(int i = 0; i < cur.children.size(); i++) {
               Node nn = new Node(cur.children.get(i));
               nn.addPred(cur);
               for(int j = 0; j < cur.preds.size(); j++) {
                  nn.addPred(cur.preds.get(j));
               }
               list.add(nn);
            }
         }
         
         else if(CheckAll(cur) && cur.val == 2) {
            for(int i = 0; i < cur.children.size(); i++) {
               Node nn = new Node(cur.children.get(i));
               for(int j = 0; j < cur.preds.size(); j++) {
                  nn.addPred(cur.preds.get(j));
               }
               list.add(nn);
            }
         }
         
      }
      
      return new Node(-1,-1,-1);
   }
   
   
   static Node RunDFS() {
      Stack<Node> list = new Stack<Node>();
      
      for(int i = 0; i < n; i++) {
         list.push(new Node(mat[0][i]));
      }
      
      
      while(!list.isEmpty()) {
         Node cur = list.pop();
         
         if(CheckAll(cur) && cur.val == 0 && cur.preds.size() + 1 == p) {
            cur.addPred(cur);
            return cur;
         }
         
         else if(CheckAll(cur) && cur.val == 0) {
            for(int i = 0; i < cur.children.size(); i++) {
               Node nn = new Node(cur.children.get(i));
               nn.addPred(cur);
               for(int j = 0; j < cur.preds.size(); j++) {
                  nn.addPred(cur.preds.get(j));
               }
               list.push(nn);
            }
         }
         
         else if(CheckAll(cur) && cur.val == 2) {
            for(int i = 0; i < cur.children.size(); i++) {
               Node nn = new Node(cur.children.get(i));
               for(int j = 0; j < cur.preds.size(); j++) {
                  nn.addPred(cur.preds.get(j));
               }
               list.push(nn);
            }
         }
         
      }
      
      return new Node(-1,-1,-1);
   }

   
   static void ReadText() throws FileNotFoundException
   {
      File input = new File("input.txt");
      Scanner in = new Scanner(input);
      
      searchType = in.next();
      n = in.nextInt(); p = in.nextInt(); out = new PrintWriter("output.txt");
      mat = new Node[n][n]; trees = new ArrayList<Node>(); maxTreeRow = -1;
      in.nextLine();
      
      Scanner inRow; int r = 0;
      while(in.hasNextLine()) {
         String row = in.nextLine();
         inRow = new Scanner(row); inRow.useDelimiter("");
  
         for(int c = 0; c < n; c++) {
            if(inRow.hasNextInt()) {
               mat[r][c] = new Node(r,c,inRow.nextInt());
            }
               
            if(mat[r][c].val == 2) {
               trees.add(new Node(mat[r][c]));
               if(r > maxTreeRow)
                  maxTreeRow = r;
            }
         }
    
         r++;
         inRow.close();
      }
      
      in.close();
   }
   
   static void CreateChildren() {
      for(int r = 0; r < mat.length; r++) {
         for(int c = 0; c < mat[r].length; c++) {
            if(r != n-1) {
               
               //Add child nodes to the right at row+1
               for(int i = c+2; i <= n-1; i++) {
                  mat[r][c].addChild(mat[r+1][i]);
               }
                  
               //Add child nodes to the left at row+1
               for(int i = c-2; i >= 0; i--) {
                  mat[r][c].addChild(mat[r+1][i]);
               } 
            }
               
            Stack<Node> treesInRow = CheckTreesR(r);
            
            while(!treesInRow.empty()) {
               Node tree = treesInRow.pop();
               
               //Add child nodes in row to the right of the tree if the node is to the left of the tree
               if(c < tree.col) {
                  for(int i = tree.col+1; i < n; i++) {
                     if(mat[r][i].val == 0) {
                        boolean add = true;
                        for(int j = 0; j < mat[r][c].children.size(); j++) {
                           if(mat[r][c].children.get(j).row == mat[r][i].row  && mat[r][c].children.get(j).col == mat[r][i].col)
                              add = false;
                        }
                        
                        if(add) {mat[r][c].addChild(mat[r][i]);}
                     }
                  }
               }
                  
               //Add child nodes in row to the left of the tree if the node is to the right of the tree
               else if(c > tree.col) {
                  for(int i = tree.col-1; i >= 0; i--) {
                     if(mat[r][i].val == 0) {
                        boolean add = true;
                        for(int j = 0; j < mat[r][c].children.size(); j++) {
                           if(mat[r][c].children.get(j).row == mat[r][i].row  && mat[r][c].children.get(j).col == mat[r][i].col)
                              add = false;
                        }

                        if(add) {mat[r][c].addChild(mat[r][i]);}
                     }
                  }
               }
            }
         }
      }
   }
   
   static boolean CheckVertical(Node no) {
      for(int i = 0; i < no.preds.size(); i++) {
         if(no.col == no.preds.get(i).col) {
            boolean blocking = false;
            for(int j = 0; j < trees.size(); j++) {
               if(trees.get(j).col == no.col && trees.get(j).row > no.preds.get(i).row && trees.get(j).row < no.row) {
                  blocking = true;
                  break;
               }
            }
            if(!blocking) {return false;}
         }
      }
      return true;
   }
   
   static boolean CheckFwdDiagonal(Node no) {
      int r = no.row;
      int c = no.col;
      ArrayList<Integer> rowNums = new ArrayList<Integer>();
      ArrayList<Integer> colNums = new ArrayList<Integer>();
      
      while(r >= 0 && r < n && c >= 0 && c < n) {
         for(int i = 0; i < no.preds.size(); i++) {
            if(r == no.preds.get(i).row && c == no.preds.get(i).col) {
               boolean blocking = false;
               for(int j = 0; j < trees.size(); j++) {
                  for(int k = 0; k < rowNums.size(); k++) {
                     if(trees.get(j).row == rowNums.get(k) && trees.get(j).col == colNums.get(k)) {
                        blocking = true;
                        break;
                     }
                  }
                  if(blocking) {break;}
               }
               
               if(!blocking) {return false;}
            }
         }
         
         rowNums.add(r); colNums.add(c);
         r--; c++;
      }
      return true;
   }
   
   static boolean CheckBwdDiagonal(Node no) {
      int r = no.row;
      int c = no.col;
      ArrayList<Integer> rowNums = new ArrayList<Integer>();
      ArrayList<Integer> colNums = new ArrayList<Integer>();
      
      while(r >= 0 && r < n && c >= 0 && c < n) {
         for(int i = 0; i < no.preds.size(); i++) {
            if(r == no.preds.get(i).row && c == no.preds.get(i).col) {
               boolean blocking = false;
               for(int j = 0; j < trees.size(); j++) {
                  for(int k = 0; k < rowNums.size(); k++) {
                     if(trees.get(j).row == rowNums.get(k) && trees.get(j).col == colNums.get(k)) {
                        blocking = true;
                        break;
                     }
                  }
                  if(blocking) {break;}
               }
               
               if(!blocking) {return false;}
            }
         }
         
         rowNums.add(r); colNums.add(c);
         r--; c--;
      }
      return true;
   }
   
   private static boolean CheckAll(Node no) {
      return CheckVertical(no) && CheckFwdDiagonal(no) && CheckBwdDiagonal(no);
   }

   static boolean CheckFail() {
      if(trees.size() == 0 && p > n) {
         return true;
      }
      else if(p < 0 || n < 0) {
         return true;
      }
      else if(trees.size() == n*n || n*n - trees.size() < p) {
         return true;
      }
      return false;
   }
   
   static void PrintMat (Node no) {
      for(int i = 0; i < no.preds.size(); i++) {
         mat[no.preds.get(i).row][no.preds.get(i).col].val = 1;
      }
      
      for(int i = 0; i < mat.length; i++) {
         for(int j = 0; j < mat[i].length; j++) {
            //System.out.print(mat[i][j].val);
            out.print(mat[i][j].val);
         }
         //System.out.println();
         out.println();
      }
   }
   
   static void PrintMat() {
      for(int i = 0; i < mat.length; i++) {
         for(int j = 0; j < mat[i].length; j++) {
            //System.out.print(mat[i][j].val);
            out.print(mat[i][j].val);
         }
         //System.out.println();
         out.println();
      }
   }
   
   static void CheckChildren() {
      for(int i = 0; i < mat.length; i++) {
         for(int j = 0; j < mat[i].length; j++) {
            System.out.print("row = " + (i+1) + ", col = " + (j+1) + ": ");

            for(int k = 0; k < mat[i][j].children.size(); k++) {
               System.out.print("(" + (mat[i][j].children.get(k).row+1) + "," + (mat[i][j].children.get(k).col+1) + ") ");
            }
            
            System.out.println();
         }
      }
   }
   
   static Stack<Node> CheckTreesR(int r) {
      Stack<Node> ret = new Stack<Node>();
      for(int i = 0; i < trees.size(); i++)
         if(trees.get(i).row == r)
            ret.push(trees.get(i));
      return ret;
   }
   
   static Stack<Node> CheckTreesC(int c) {
      Stack<Node> ret = new Stack<Node>();
      for(int i = 0; i < trees.size(); i++)
         if(trees.get(i).col == c)
            ret.push(trees.get(i));
      return ret;
   }
   
   static int CheckConflicts(ArrayList<Node> lizards) {
      int conflicts = 0;
      
      for(int a = 0; a < lizards.size() - 1; a++) {
         Node no = lizards.get(a);
         
         //CheckVertical Up
         for(int i = no.row + 1; i < n; i++) {
            if(mat[i][no.col].val == 1) {
               conflicts++;
            }
            else if(mat[i][no.col].val == 2) {
               break;
            }
         }
         
         //CheckVertical Down
         for(int i = no.row - 1; i >= 0; i--) {
            if(mat[i][no.col].val == 1) {
               conflicts++;
            }
            else if(mat[i][no.col].val == 2) {
               break;
            }
         }
         
         //CheckFwdDiag Up
         for(int i = no.row - 1, j = no.col + 1; j < n && i >= 0; i--, j++) {
            if(mat[i][j].val == 1) {
               conflicts++;    
            }
            else if(mat[i][j].val == 2) {
               break;
            }
         }
         
         //CheckFwdDiag Down
         for(int i = no.row + 1, j = no.col - 1; j >= 0 && i < n; i++, j--) {
            if(mat[i][j].val == 1) {
               conflicts++;
            }
            else if(mat[i][j].val == 2) {
               break;
            }
         }
            
         //CheckBwdDiag
         for(int i = no.row - 1, j = no.col - 1; j >= 0 && i >= 0; i--, j--) {
            if(mat[i][j].val == 1) {
               conflicts++;
            }
            else if(mat[i][j].val == 2) {
               break;
            }
         }
         
         //CheckBwdDiag Down
         for(int i = no.row + 1, j = no.col + 1; j < n && i < n; i++, j++) {
            if(mat[i][j].val == 1) {
               conflicts++;
            }
            else if(mat[i][j].val == 2) {
               break;
            }
         }
         
         //CheckHorizontal Right
         for(int i = no.col + 1; i < n; i++) {
            if(mat[no.row][i].val == 1) {
               conflicts++;
            }
            else if(mat[no.row][i].val == 2) {
               break;
            }
         }
         
         //CheckHorizontal Left
         for(int i = no.col - 1; i >= 0; i--) {
            if(mat[no.row][i].val == 1) {
               conflicts++;
            }
            else if(mat[no.row][i].val == 2) {
               break;
            }
         }
      }
      
      return conflicts;
   }
}