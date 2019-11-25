/**
 * 
 */
package datastore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JOptionPane;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import datastore.RangeColumn.*;
import util.FileUtils;

/**
 * @author andy
 *
 */
public class EvTree {

	/**
	 * @param args
	 * 
	 */
	public class TreeNode {
		public String name;
		public double d;
		public double FAD;
		public double LAD;
		public ArrayList<TreeNode> child;
		public TreeNode parent;
		public HashMap<String, Double> attributes;
		
		public TreeNode() {
			
		}
		
		public TreeNode(String name, double d) {
			this.name = name;
			this.FAD = this.LAD = -1;
			this.d = d;
			this.parent = null;
			this.child = null; 
			this.attributes = null;
		}
		
		public TreeNode(String name, double FAD, double LAD) {
			this.name = name;
			this.LAD = LAD;
			this.FAD = FAD;
			this.d = -1;
			this.parent = null;
			this.child = null;
			this.attributes = null;
		}
		public TreeNode(String name, double FAD, double LAD, double d) {
			this.name = name;
			this.LAD = LAD;
			this.FAD = FAD;
			this.d = d;
			this.parent = null;
			this.child = null;
			this.attributes = null;
		}
		
		public TreeNode(TreeNode tn) {
			this.name = tn.name;
			this.LAD = tn.LAD;
			this.FAD = tn.FAD;
			this.d = tn.d;
			this.parent = tn.parent;
			this.child = tn.child;
			this.attributes = tn.attributes;
		}
	}
	
	public ArrayList<TreeNode> createExampleTree1() {
		ArrayList<TreeNode> tree = null; 
		tree = new ArrayList<TreeNode>();
		
		TreeNode t1 = new TreeNode("F", 1);
		t1.child = new ArrayList<TreeNode>();
		tree.add(t1);

		TreeNode t11 = new TreeNode("A", 0.1);
		t1.child.add(t11);
		
		TreeNode t12 = new TreeNode("B", 0.2);
		t1.child.add(t12);
		
		TreeNode t13 = new TreeNode("E", 0.5);
		t1.child.add(t13);
		t13.child = new ArrayList<TreeNode>();

		TreeNode t131 = new TreeNode("C",  0.3);
		t13.child.add(t131);

		TreeNode t132 = new TreeNode("D", 0.4);
		t13.child.add(t132);
		
		return(tree);
	}
	
	public ArrayList<TreeNode> createExampleTree2() {
		ArrayList<TreeNode> tree = null;
		tree = new ArrayList<TreeNode>();
		
		TreeNode t1 = new TreeNode("Globanomalina compressa", 1.8233333333333);
		t1.child = new ArrayList<TreeNode>();
		tree.add(t1);

		TreeNode t11 = new TreeNode("Globanomalina compressa", 0.368333333333297);
		t1.child.add(t11);
		
		TreeNode t12 = new TreeNode("Globanomalina ehrenbergi", 1.925);
		t1.child.add(t12);
		
		t12.child = new ArrayList<TreeNode>();
		TreeNode t121 = new TreeNode("Globanomalina ehrenbergi", 0.209999999999994);
		t12.child.add(t121);
		
		TreeNode t122 = new TreeNode("Globanomalina chapmani",  6.8157142857143);
		t12.child.add(t122);

		t121.child = new ArrayList<TreeNode>();
		TreeNode t1211 = new TreeNode("Globanomalina ehrenbergi", 0.209999999999994);
		t121.child.add(t1211);

		TreeNode t1212 = new TreeNode("Globanomalina pseudomenardii", 3.63);
		t121.child.add(t1212);
		
		t122.child = new ArrayList<TreeNode>();
		TreeNode t1221 = new TreeNode("Globanomalina chapmani", 3.2457142857143);
		t122.child.add(t1221);

		TreeNode t1222 = new TreeNode("Globanomalina planoconica", 7.125);
		t122.child.add(t1222);
		
		t1222.child = new ArrayList<TreeNode>();
		TreeNode t12221 = new TreeNode("Globanomalina planoconica", 0.234999999999999);
		t1222.child.add(t12221);

		TreeNode t12222 = new TreeNode("Planoglobanomalina pseudoalgeriana", 6.0748120300752);
		t1222.child.add(t12222);
		
		return(tree);
	}
	
	public ArrayList<TreeNode> createExampleTree3() {
		ArrayList<TreeNode> tree = null;
		tree = new ArrayList<TreeNode>();
		
		TreeNode t1 = new TreeNode("Globanomalina compressa", 63.9, 63.9 - 1.8233333333333);
		t1.child = new ArrayList<TreeNode>();
		tree.add(t1);

		TreeNode t11 = new TreeNode("Globanomalina compressa", 63.9 - 1.8233333333333, 63.9 - 1.8233333333333 - 0.368333333333297);
		t1.child.add(t11);
		
		TreeNode t12 = new TreeNode("Globanomalina ehrenbergi", 62.445, 62.445 - 1.925);
		t1.child.add(t12);
		
		t12.child = new ArrayList<TreeNode>();
		TreeNode t121 = new TreeNode("Globanomalina ehrenbergi", 62.445 - 1.925, 62.445 - 1.925 - 0.209999999999994);
		t12.child.add(t121);
		
		TreeNode t122 = new TreeNode("Globanomalina chapmani", 61.13, 61.13 - 6.8157142857143);
		t12.child.add(t122);

		t121.child = new ArrayList<TreeNode>();
		TreeNode t1211 = new TreeNode("Globanomalina ehrenbergi", 62.445 - 1.925 - 0.209999999999994, 62.445 - 1.925 - 0.209999999999994 - 0.209999999999994);
		t121.child.add(t1211);

		TreeNode t1212 = new TreeNode("Globanomalina pseudomenardii", 60.73, 60.73 - 3.63);
		t121.child.add(t1212);
		
		t122.child = new ArrayList<TreeNode>();
		TreeNode t1221 = new TreeNode("Globanomalina chapmani", 61.13 - 6.8157142857143, 61.13 - 6.8157142857143 - 3.2457142857143);
		t122.child.add(t1221);

		TreeNode t1222 = new TreeNode("Globanomalina planoconica", 57.56, 57.56 - 7.125);
		t122.child.add(t1222);
		
		t1222.child = new ArrayList<TreeNode>();
		TreeNode t12221 = new TreeNode("Globanomalina planoconica", 57.56 - 7.125, 57.56 - 7.125 - 0.234999999999999);
		t1222.child.add(t12221);

		TreeNode t12222 = new TreeNode("Planoglobanomalina pseudoalgeriana", 50.67, 50.67 - 6.0748120300752);
		t1222.child.add(t12222);
		
		return(tree);
	}
	
	public ArrayList<TreeNode> createExampleTree4() {
		ArrayList<TreeNode> tree = null;
		tree = new ArrayList<TreeNode>();
		
		TreeNode t1 = new TreeNode("Globanomalina compressa", 63.9, 62.0766666666667, -1);
		t1.child = new ArrayList<TreeNode>();
		tree.add(t1);

		TreeNode t11 = new TreeNode("Globanomalina ehrenbergi", 62.445, 60.52, -1);
		t1.child.add(t11);
		
		t11.child = new ArrayList<TreeNode>();
		
		TreeNode t111 = new TreeNode("Globanomalina chapmani", 61.13, 54.3142857142857, -1);
		t11.child.add(t111);
		
		TreeNode t112 = new TreeNode("Globanomalina pseudomenardii", 60.73, 57.1, -1);
		t11.child.add(t112);

		t111.child = new ArrayList<TreeNode>();
		TreeNode t1111 = new TreeNode("Globanomalina planoconica", 57.56, 50.435, -1);
		t111.child.add(t1111);
		
		t1111.child = new ArrayList<TreeNode>();
		TreeNode t11111 = new TreeNode("Planoglobanomalina pseudoalgeriana", 50.67, 44.5951879699248, -1);
		t1111.child.add(t11111);
		
		return(tree);
	}
	
	public TreeNode createTreeFromRanges(TreeNode tree, TreeNode parent, Range r) {
		if (r.alreadyInsideTree == false && r.isPhenonRange == false && tree.parent == parent) {
			String rName = r.name.split(" <img")[0];
			double rFAD = r.base;
			double rLAD = r.top;
			
			tree = new TreeNode(rName, rFAD, rLAD);
			r.alreadyInsideTree = true;
			
			SortedSet<Range> sortedChildren = new TreeSet<Range> (
					new Comparator<Range>() {
						@Override
						public int compare(Range r1, Range r2) {
							if (r1.base == r2.base)
								return -1; // this is necessary to equalize the size of r.children and sorted children
							else if (r2.base < r1.base)
								return -1;
							else
								return 1;
						}
					}
					
			);
			
			sortedChildren.addAll(r.children);
			if (sortedChildren.size() != r.children.size()) {
				print("!!!Something is wrong.");
			}
			Iterator<Range> iterC = sortedChildren.iterator();
			while(iterC.hasNext()) {
				Range cr = iterC.next();

				String cName = cr.name.split(" <img")[0];
				double cFAD = cr.base;
				double cLAD = cr.top;
			
				TreeNode childTree = new TreeNode(cName, cFAD, cLAD);
				childTree.parent = tree;
				childTree = createTreeFromRanges(childTree, tree, cr);
				if (tree.child == null) {
					tree.child = new ArrayList<TreeNode>();
				}
				tree.child.add(childTree);
				cr.alreadyInsideTree = true;
			}
		}
		
		return(tree);
	}
	

	
	public class Path {
		public String path;
		public double d;
		
		public Path() {
			this.path = "";
			this.d = 0;
		}
		
		public Path(String path, double d) {
			this.path = path;
			this.d = d;
		}
		
		public Path(Path p) {
			this.path = p.path;
			this.d = p.d;
		}
	}
	
//	get Path to a node
	public String getPathToNode(TreeNode tree, String node, String path) {
	  if (tree.name == node) { 
	    return(tree.name); //+ ":" + tree.d);
	  }
	  
	  if (tree.child == null) {
	    return(tree.name); // + ":" + tree.d);
	  }
	  
	  ArrayList<TreeNode> children =  tree.child;
	  TreeNode c = null;
	  String prevPath = "";
	  for(int ci=0; ci<children.size(); ci++) {
	    prevPath = path;
	    c = children.get(ci);
	    path = tree.name; // + ":" + tree.d;
	    String npath = getPathToNode(c, node, path);
	    
	    if (npath.equalsIgnoreCase("") == false) {
	    	path = path + "->" + npath;
	    }
	    
	    if (path.contains(node)) {
	      return(path);
	    }
	    
	    if (ci + 1 != children.size())
	    	path = prevPath;
	  }
	  
	  return(path);
	}
	
//	get Path to a node
	public Path getPathToNode(TreeNode tree, String node, Path path) {
	  if (tree.name.equalsIgnoreCase(node)) { 
		Path p = null;
		String np = tree.name;
		double nd = path.d + tree.d;
		if (path.path.equalsIgnoreCase("") == false) {
			np = path.path + "->" + tree.name;
		}
		p = new Path(np, nd);
	    return(p);
	  }
	  
	  if (tree.child == null) {
		Path p = new Path(tree.name, tree.d);
	    return(p);
	  }
	  
	  ArrayList<TreeNode> children =  tree.child;
	  TreeNode c = null;
	  Path prevPath = null;
	  for(int ci=0; ci<children.size(); ci++) {
	    prevPath = new Path(path);
	    c = children.get(ci);
	    path.path = tree.name;
	    path.d = tree.d;
	    Path npath = new Path(getPathToNode(c, node, path));
	    
	    if (prevPath.path.equalsIgnoreCase("") == false) {
	    	path.path = prevPath.path + "->" + npath.path;
	    } else {
	    	path.path = npath.path;
	    }
	    path.d = prevPath.d + npath.d;
	    
	    if (path.path.contains(node)) {
	      return(path);
	    }
	    
	    path = prevPath;
	  }
	  
	  return(path);
	}
	
	
	public String getPathToNode(TreeNode tree, String node) {
		Path p = new Path();
		Path path = getPathToNode(tree, node, p);
		
		return(path.path);
	}
	
	public String getCommonAncestor(TreeNode tree, String node1, String node2) {
		String commonAncestorName = null;
		
		String path1 = getPathToNode(tree, node1);
		String path2 = getPathToNode(tree, node2);
		
		String[] paths1 = path1.split("->");
		String[] paths2 = path2.split("->");
		
		int mp = Math.min(paths1.length, paths2.length);
		
		int match = 0;
		int no_match = -1;
		for(int i=0; i<mp; i++) {
			if(paths1[i].equalsIgnoreCase(paths2[i])) {
				match += 1;
			} else {
				no_match = i-1;
				break;
			}
		}
		
		if (no_match != -1) {
			commonAncestorName = paths1[no_match];
		}
		
		return(commonAncestorName);
		
	}
	

	public ArrayList<String> getPathArrayFromRootToCommonAncestor(TreeNode tree, String node1, String node2) {
 		String commonAncestorName = null;
		ArrayList<String> pathFromRootToCommonAncestor = null;
		
		String path1 = getPathToNode(tree, node1);
		String path2 = getPathToNode(tree, node2);
		
		String[] paths1 = path1.split("->");
		String[] paths2 = path2.split("->");
		
		int mp = Math.min(paths1.length, paths2.length);
		
		int match = 0;
		int no_match = -1;
		for(int i=0; i<mp; i++) {
			if(paths1[i].equalsIgnoreCase(paths2[i])) {
				match += 1;
			} else {
				no_match = i-1;
				break;
			}
		}
		
		if (no_match != -1) {
			commonAncestorName = paths1[no_match];
			pathFromRootToCommonAncestor = new ArrayList<String>() ;
			for(int i=0; i<no_match+1; i++) {
				pathFromRootToCommonAncestor.add(paths1[i]);
			}
		} else if (match != 0) {
			// handling the case when they are on the same path
			if(paths1.length >= paths2.length) {
				commonAncestorName = node2;
				pathFromRootToCommonAncestor = new ArrayList<String>() ;
				for(int i=0; i<paths2.length; i++) {
					pathFromRootToCommonAncestor.add(paths2[i]);
				}
			} else {
				commonAncestorName = node1;
				pathFromRootToCommonAncestor = new ArrayList<String>() ;
				for(int i=0; i<paths1.length; i++) {
					pathFromRootToCommonAncestor.add(paths1[i]);
				}
			}
		}
		
		
		
		return(pathFromRootToCommonAncestor);
		
	}
	public String getPathFromRootToCommonAncestor(TreeNode tree, String node1, String node2) {
		String pathFromRootToCommonAncestorS = "";
		
		ArrayList<String> pathFromRootToCommonAncestor = getPathArrayFromRootToCommonAncestor(tree, node1, node2);
		for (int i=0; i<pathFromRootToCommonAncestor.size(); i++) {
			pathFromRootToCommonAncestorS += pathFromRootToCommonAncestor.get(i);
			if (i+1 != pathFromRootToCommonAncestor.size()) {
				pathFromRootToCommonAncestorS += "->";
			}
		}
		
		return(pathFromRootToCommonAncestorS);
	}
	
	public ArrayList<String> convertToStringArrayList(String[] s) {
		ArrayList<String> r = new ArrayList<String>();	
		for(int i=0; i<s.length; i++) {
			r.add(s[i]);
		}
		
		return(r);
	}

	public int stringExistsInArrayList(ArrayList<String> sA, String s) {
		int loc = -1;
		
		for(int i=0; i<sA.size(); i++) {
			if (sA.get(i).equals(s) == true) {
				loc = i;
				break;
			}
		}
		
		return(loc);
	}
	
	
	public ArrayList<String> getPathArrayFromNodeToNode(TreeNode tree, String node1, String node2) {
		ArrayList<String> pathFromNodeToNode = null;
		
		String path1 = getPathToNode(tree, node1);
		String path2 = getPathToNode(tree, node2);
		
		String[] paths1 = path1.split("->");
		String[] paths2 = path2.split("->");
		ArrayList<String> pathA1 = convertToStringArrayList(paths1);
		ArrayList<String> pathA2 = convertToStringArrayList(paths2);
		
		ArrayList<String> longerPath = null;
		if (pathA1.size() > pathA2.size()) {
			longerPath = pathA1;
			int loc = stringExistsInArrayList(longerPath, node2);
			if (loc == -1)
				return(null);
			
			pathFromNodeToNode = new ArrayList<String>();
			pathFromNodeToNode.add(node2);
			for(int i=loc+1; i<longerPath.size(); i++) {
				pathFromNodeToNode.add(longerPath.get(i));
			}
			
		} else {
			longerPath = pathA2;
			int loc = stringExistsInArrayList(longerPath, node1);
			if (loc == -1)
				return(null);
			
			pathFromNodeToNode = new ArrayList<String>();
			pathFromNodeToNode.add(node1);
			for(int i=loc+1; i<longerPath.size(); i++) {
				pathFromNodeToNode.add(longerPath.get(i));
			}
		}
		
		return(pathFromNodeToNode);
		
	}
	public String getPathFromNodeToNode(TreeNode tree, String node1, String node2) {
		String pathFromNodeToNodeS = "";
		
		ArrayList<String> pathFromNodeToNode = getPathArrayFromNodeToNode(tree, node1, node2);
		for (int i=0; i<pathFromNodeToNode.size(); i++) {
			pathFromNodeToNodeS += pathFromNodeToNode.get(i);
			if (i+1 != pathFromNodeToNode.size()) {
				pathFromNodeToNodeS += "->";
			}
		}
		
		return(pathFromNodeToNodeS);
	}
		

	public double getDistanceToNode(TreeNode tree, String node) {
		Path p = new Path();
		Path path = getPathToNode(tree, node, p);
		
		return(path.d);
	}

	// Distance calculation has some bug. Not giving accurate path length
	public double getDistanceToNode2(TreeNode tree, String node, double distance) {
		  if (tree.name.equalsIgnoreCase(node) && tree.child == null) { 
		    return(tree.d);
		  }
		  
		  if (tree.child == null) {
			    return(-1);
		  }
		  
		  ArrayList<TreeNode> children =  tree.child;
		  TreeNode c = null;
		  double prevDistance;
		  double totalDistance = 0;
		  for(int ci=0; ci<children.size(); ci++) {
		    prevDistance = distance;
		    c = children.get(ci);
		    double ndistance = getDistanceToNode2(c, node, tree.d);
		    if (ndistance == -1) {
		    	continue;
		    }
		    
		    distance = distance + ndistance;
		    if (distance > totalDistance) {
		    	totalDistance = distance;
		    }
		    
		    if (ci + 1 != children.size())
		    	distance = prevDistance;
		  }

		  if (tree.parent == null) {
			  totalDistance += 2*tree.d;
		  }
		  
		  return(totalDistance);
	}
	
	
	public void getTreeNodes(TreeNode tree, HashSet<TreeNode> treeNodes) {
		if (tree.child == null) {
			return;
		}

		treeNodes.add(tree);
		ArrayList<TreeNode> children = tree.child;
		TreeNode c = null;
		for(int ci=0; ci<children.size(); ci++) {
		   c = children.get(ci);
		   treeNodes.add(c);
		   getTreeNodes(c, treeNodes);
		}
		return;
	}

	public ArrayList<String> getTreeNodeNames(TreeNode tree) {
		HashSet<TreeNode> treeNodes = new HashSet<TreeNode>();
		getTreeNodes(tree, treeNodes);
		
		ArrayList<String> treeNodeNames = new ArrayList<String>();
		Iterator<TreeNode> itr = treeNodes.iterator();
		while(itr.hasNext()){
			TreeNode t = itr.next();
			treeNodeNames.add(t.name);
		}
		
		return(treeNodeNames);
	}
	
	public HashSet<String> getUniqueTreeNodeNames(TreeNode tree) {
		HashSet<TreeNode> treeNodes = new HashSet<TreeNode>();
		getTreeNodes(tree, treeNodes);
		
		HashSet<String> uniqueTreeNodeNames = new HashSet<String>();
		Iterator<TreeNode> itr = treeNodes.iterator();
		while(itr.hasNext()){
			TreeNode t = itr.next();
			uniqueTreeNodeNames.add(t.name);
		}

		return(uniqueTreeNodeNames);	
	}

	public void getLeafNodes(TreeNode tree, ArrayList<TreeNode> leafNodes) {
		if (tree.child == null) {
			leafNodes.add(tree);
			return;
		}

		ArrayList<TreeNode> children = tree.child;
		TreeNode c = null;
		for(int ci=0; ci<children.size(); ci++) {
		   c = children.get(ci);
		   getLeafNodes(c, leafNodes);
		}
		return;
	}
	
	public double roundWithPrecision(double val, int place) {
		String mps = null;
		if (place == 2)
			mps = String.format("%.2f", val);
		else 
			mps = String.format("%.3f", val);
			
		double r = Double.parseDouble(mps);
		
		return(r);
	}
	
	public void getLivingNodes(TreeNode tree, ArrayList<TreeNode> livingNodes) {
		ArrayList<TreeNode> leafNodes = new ArrayList<TreeNode>();
		getLeafNodes(tree, leafNodes);
		
		double maxPathLength = getLongestPathLength(tree, 0);
		double mp, d, dp;
		mp = roundWithPrecision(maxPathLength, 2);
		
		for(int i=0; i< leafNodes.size(); i++) {
			TreeNode t = leafNodes.get(i);
			d = getDistanceToNode(tree, t.name);
			dp = roundWithPrecision(d, 2);
			if (mp == d) {
				livingNodes.add(t);
			}
		}
		
		return;
	}
	
	public HashSet<String> getLivingNodeNames(TreeNode tree) {
		ArrayList<TreeNode> livingNodes = new ArrayList<TreeNode>();
		
		getLivingNodes(tree, livingNodes);
		
		HashSet<String> livingNodeNames = new HashSet<String>();
		for(int i=0; i<livingNodes.size(); i++) {
			TreeNode t = livingNodes.get(i);
			livingNodeNames.add(t.name);
		}
		
		return(livingNodeNames);
	}
	
	public int getTotalNumberOfNodes(TreeNode tree) {
		if (tree.child == null) {
			return(1);
		}

		ArrayList<TreeNode> children = tree.child;
		TreeNode c = null;
		int total = 1; 
		for(int ci=0; ci<children.size(); ci++) {
		   c = children.get(ci);
		   total += getTotalNumberOfNodes(c);;
		}
		
		return(total);
	}
	
	public double getLongestPathLength(TreeNode tree, double length) {
		  if (tree.child == null) {
		    return(tree.d);
		  } 
		   
		  ArrayList<TreeNode> children =  tree.child;
		  TreeNode c = null;
		  double prevLength = length;
		  double maxLength = -1;
		  for(int ci=0; ci<children.size(); ci++) {
			//print(ci + ". ");
		    prevLength = length;
		    c = children.get(ci);
		    //print(c.name);
		    //print(length);
		    double newLength = getLongestPathLength(c, c.d);
		    length = prevLength + newLength;
		    //print(length + "\n");
		    
		    if (length > maxLength) {
		    	maxLength = length;
		    }
		    
		    length = prevLength;
		  }
		  
		  // add the distance for the root
		  if (tree.parent == null) {
			  maxLength += tree.d;
		  }
		  
		  return(maxLength);
	}
	
	public String convertToNexusFormat(TreeNode tree, TreeNode parent) {
		ArrayList<String> tNodes = getTreeNodeNames(tree);
		
		String taxLabels = "";
		for(int i=0; i<tNodes.size(); i++) {
			taxLabels += "\"" + tNodes.get(i) + "\"";
			if(i != tNodes.size() -1)
				taxLabels += " ";
		}
		
		String nex="";
		
		nex += "#NEXUS" + "\n";
		
		//TAXA block
		/*
		nex += "BEGIN taxa;" + "\n";
		nex += "TaxLabels " + taxLabels + ";" + "\n";
		nex += "END;" + "\n";
		*/
		
//		nex +=  "\n";
		//TREE block
		nex += "BEGIN trees;" + "\n";
		String nwk = convertToNewickFormat(tree, parent);
		nex += "\t" + "Tree tree = " + nwk + "\n";
		nex += "END;" + "\n";

//		nex += "\n";
		/*
		//Data block
		nex += "BEGIN data;" + "\n";
		nex += "END;" + "\n";
		*/
		
		return(nex);
	}
	
	public String convertNexusToNewickFormat(String nex) {
		String nwk="";
		HashMap<Integer, String> taxMap = null;
		taxMap = processTaxLabelTranslation(nex);
		
		String[] lines = nex.split("\n");
		
		for(int i=0; i<lines.length; i++) {
			String s = lines[i];
			
			if(s.contains(" = ") || s.contains("Tree ") || s.contains("tree = ") || s.contains("tree")) {
				if (s.contains("[") || s.contains("]")) {
					// handle edge attributes in newick file
					nwk = processNewickString(s, taxMap);
					if (nwk.length() == 0 || nwk.length() <= 3) {
						if (s.contains(" = ")) {
							nwk = s.split(" = ")[1];
						} else if (s.contains("=")) {
							nwk = s.split("=")[1];
						} else {
							String message = "Could not convert from nexus to newick";
							JOptionPane.showMessageDialog(null, message); 
						}
					} 
					//print(nwk);
					break;
				} else {
					if (s.contains(" = ")) {
						nwk = s.split(" = ")[1];
					} else if (s.contains("=")) {
						nwk = s.split("=")[1];
					} 
				}
			}
		}
		
		return(nwk);
	}
	
	public HashMap<Integer, String> processTaxLabelTranslation(String nex) {
		String[] lines = nex.split("\n");
		HashMap<Integer, String> taxMap = new HashMap<Integer, String>();
		
		boolean st = false;
		for(int i=0; i<lines.length; i++) {
			String l = lines[i];
			if(l.contains("translate")) {
				st = true;
			} else if(st == true) {
				l = l.replace("\t", " ");
				String[] s = l.split(" ");
				
				int taxID = -1;
				int t=-1;
				for(int k=0; k<s.length; k++) {
					if(s[k].equals("") == false) {
						try {
						    t = Integer.parseInt(s[k]);
						    taxID = t;
						} catch(NumberFormatException e) {
							String sname = s[k];
							//print(sname);
							if (sname.contains("}")) {
								sname = sname.split("}")[0];
							}
							
							if (sname.contains(",")) {
								sname = sname.split(",")[0];
							}
							if (taxID != -1) {
								taxMap.put(taxID, sname);
							}
						}
					}
				}
				//print(s);
			}
		}
		
		return(taxMap);
	}
	
	public String loadTreeFromFile(String fpath) {
		String nex = "";
		InputStream inputStream;
		StringBuffer sb = null;
		try {
			inputStream = new FileInputStream(fpath);
			InputStreamReader isReader = new InputStreamReader(inputStream);
	        BufferedReader reader = new BufferedReader(isReader);
	        sb = new StringBuffer();
	        String str;
	        try {
				while((str = reader.readLine())!= null){
				   sb.append(str+"\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		nex = sb.toString();
		
		return(nex);
	}
	
	public String processNewickString(String s, HashMap<Integer, String> taxMap) {
		String nwk = s;
		String newNwk = "";
	
		ArrayList<String> nodes = new ArrayList<String>();
		ArrayList<String> distances = new ArrayList<String>();
		ArrayList<String> nodeAttrs = new ArrayList<String>();
		boolean fNode = false;
		ArrayList<String> dAttrs = new ArrayList<String>();
		boolean dNode = false;
		
		boolean fLFB = false;
		int lLFB = -1;
		boolean fRFB = false;
		int lRFB = -1;

		boolean fLSB = false;
		int lLSB = -1;
		boolean fRSB = false;
		int lRSB = -1;

		boolean fLTB = false;
		int lLTB = -1;
		boolean fRTB = false;
		int lRTB = -1;

		boolean st = true;
		int branchNodeCount = 1;
		for(int i=0; i<nwk.length(); i++) {
			char c = nwk.charAt(i);
			if (c == '=' && st == true) {
				if (nwk.charAt(i+1) == ' ') {
					nwk = nwk.substring(i+2, nwk.length());
				} else {
					nwk = nwk.substring(i+1, nwk.length());
				}
				
				if(nwk.substring(0, 4).equals("[&U]") == true) {
					if (nwk.charAt(4) != ' ') {
						nwk = nwk.substring(4, nwk.length());
					} else {
						nwk = nwk.substring(5, nwk.length());
					}
				}
				if(nwk.charAt(nwk.length()-1) == '\n') {
					nwk = nwk.substring(0, nwk.length()-1);
				}
				
				String etmp = nwk.substring(nwk.length() - 4, nwk.length());
				
				if(etmp.equalsIgnoreCase("end;")) {
					nwk = nwk.substring(0, nwk.length()-4);
				}
				
				i=0;
				st = false;
			}
			
			c = nwk.charAt(i);
			if (c == '(') {
				fLFB = true;
				lLFB = i;
			} else if(fLFB== true && c == '[') {
				fLSB = true;
				lLSB = i;
			} else if(fLFB==true && fLSB==true && c == ']') {
				fRSB = true;
				lRSB = i;
			} else if (c == ')') {
//				fRFB = true;
//				lRFB = i;
//
//				fLFB = false;
			}
			
			if (fLSB==true && fRSB==true && lLSB < lRSB) {
				if(lLSB > nwk.length()) {
					lLSB = nwk.length();
				}
				String beforeAttr = nwk.substring(0, lLSB);
				if(lRSB > nwk.length()) {
					lRSB = nwk.length();
				}
				String attr = nwk.substring(lLSB+2, lRSB);
				String afterAttr = nwk.substring(lRSB + 1, nwk.length());
				
				if (afterAttr.charAt(0) == ':') {
					fNode = true;
					String tmpNodeName = beforeAttr;
					
					String firstChar = "";
					boolean isBranchNode = false;
					if(beforeAttr.equals(")")) {
						isBranchNode = true;
						firstChar = ")";
						String b = "bn" + branchNodeCount;
						branchNodeCount += 1;
						tmpNodeName = b;
					} else if (beforeAttr.charAt(0) == ')') { 
						firstChar = ")";
						tmpNodeName = beforeAttr.substring(1, beforeAttr.length());
					} else if(beforeAttr.charAt(0) == '(') {
						firstChar = "(";
						tmpNodeName = beforeAttr.substring(1, beforeAttr.length());
					} else if(beforeAttr.startsWith(",(") == true) {
						int locst = 2;
						for(int ij=2; ij<beforeAttr.length(); ij++) {
							if(beforeAttr.charAt(ij) != '(') {
								locst = ij;
								break;
							}
						}
						firstChar = beforeAttr.substring(0, locst);
						tmpNodeName = beforeAttr.substring(locst, beforeAttr.length());
					} else if(beforeAttr.charAt(0) == ',') { 
						firstChar = ",";
						tmpNodeName = beforeAttr.substring(1, beforeAttr.length());
					
					} 

					String nodeName = tmpNodeName; 
					try {
						if (isBranchNode == false) {
							int taxID = Integer.parseInt(nodeName);
							if (taxMap != null) {
								nodeName = firstChar + taxMap.get(taxID);
							} else {
								nodeName = firstChar + taxID;
							}
						} else {
							nodeName = firstChar + tmpNodeName;
						}
					} catch(NumberFormatException e) {
						nodeName = beforeAttr;
					}
					
					newNwk += nodeName;
					//print(newNwk);
					nwk = afterAttr.substring(1, afterAttr.length());
					nodeAttrs.add(attr);
					fLSB = false;
					fRSB = false;
					
					i=0;
				} else if(fLSB==true && fRSB==true && fNode == true) {
					dNode = true;
					fNode = false;
					
					newNwk += ":" + Double.parseDouble(beforeAttr);
					dAttrs.add(attr);
					nwk = afterAttr;
					
					fLSB = false;
					fRSB = false;
					dNode = false;
					fNode = false;
					
					i=0;
					//print(newNwk);
				} 
			}
			
			if (c==';') {
				newNwk = "(" + newNwk + ")bn" + branchNodeCount + ":" + 0.0000001 + ");";
				break;
			}
			
		}
		
		return(newNwk);
	}

	public String convertToNewickFormat(TreeNode tree, TreeNode parent) {
		if (tree.child == null) {
			double d = tree.LAD - parent.LAD;
			d = Math.abs(d);
			String tr = "'" + tree.name + "'" + ":" + d;
			return(tr);
		}
		ArrayList<TreeNode> children = tree.child;
		
		SortedSet<Map.Entry<Double, Integer>> c_FAD = new TreeSet<Map.Entry<Double, Integer>>(
				new Comparator<Map.Entry<Double, Integer>>() {
	                @Override
	                public int compare(Map.Entry<Double, Integer> e1,
	                        Map.Entry<Double, Integer> e2) {
	                    return e1.getValue().compareTo(e2.getValue());
	                }
				}
		);
		
		TreeMap<Double, Integer> m_FAD = new TreeMap<Double, Integer>();
		
		for (int i=0; i<children.size(); i++) {
			TreeNode c = children.get(i);
			m_FAD.put(c.FAD, i);
		}
		
		c_FAD.addAll(m_FAD.entrySet());

		ArrayList<Integer> oi = new ArrayList<Integer>();
		
		Iterator<Map.Entry<Double, Integer>> it = c_FAD.iterator();
		double max_child_FAD = Integer.MIN_VALUE; 
		while(it.hasNext()){
			Map.Entry<Double, Integer> e = it.next();
			double tmpFAD = e.getKey();
			int tmpFADID = e.getValue();
			oi.add(tmpFADID);
			max_child_FAD = Math.max(max_child_FAD, tmpFAD);
		}
		
		int max_child_id = oi.get(0);

		String clist = "(";
		TreeNode new_parent = new TreeNode(tree);
		if(new_parent.LAD <= max_child_FAD) {
			TreeNode new_child = new TreeNode(new_parent);
			new_child.FAD = max_child_FAD;
			new_parent.LAD = max_child_FAD;
			if(new_child.name.equals(new_parent.name) == true) {
				new_child.name = new_child.name + " ext";
			}

			ArrayList<TreeNode> new_children = new ArrayList<TreeNode>();
			int cin1 = 0;
			int cin2 = 0;
			for(int j=0; j<children.size(); j++) {
				if (cin1 != max_child_id) {
					new_children.add(children.get(cin1));
					cin2 = cin2 + 1;
				}
				cin1 = cin1 + 1;
			}
			
			if (new_children.size() > 0) {
				new_child.child = new_children;
			} else {
				new_child.child = null;
			}
			
			String cr1 = convertToNewickFormat(children.get(max_child_id), new_parent);
			clist = clist + cr1;
			if (tree.LAD != max_child_FAD) {
				String cr2 = convertToNewickFormat(new_child, new_parent);
				clist = clist + "," + cr2;
			} else {
				int cn = 0;
				ArrayList<Integer> noi = new ArrayList<Integer>(oi);
				noi.remove(max_child_id);
				
				for(int ii=0; ii<noi.size(); ii++) {
					int ci = noi.get(ii);
					TreeNode c = children.get(ci);
					if (new_parent.LAD == c.FAD) {
						String cr = convertToNewickFormat(c, new_parent);
						clist = clist + "," + cr;
						cn = cn+1;
					}
				}
			}
		}
		
		double d;
		if (parent == null) {
			d = max_child_FAD - tree.FAD;
		} else {
			d = max_child_FAD - parent.LAD;
		}
		d = Math.abs(d);
		String tname = "'" + tree.name + "'";
		clist = clist + ")" + tname + ":" + d;
		
		if (parent == null) {
			clist = "(" + clist + ");";
		}

		return(clist);
	}
	
	public String convertNewickTreeToTSCUtilQueue(TreeNode tree, double FAD, String tsf) {
		Queue<TreeNode> Q = new LinkedList<TreeNode>() ;
		Q.add(tree);
		
		tree.FAD = FAD;
		tree.LAD = tree.FAD - tree.d;
		while(Q.isEmpty() == false) {
			TreeNode t = Q.poll();
			String otherInfo = ""; // t.popup
			tsf += "\t" + t.name + "\t" + t.LAD + "\t" + "TOP" + "\t" + otherInfo + "\n";
			
			ArrayList<TreeNode> children = t.child;
			
			
			for(int i=0; children!=null && i<children.size(); i++) {
				TreeNode c = children.get(i);
				c.FAD = t.LAD;
				c.LAD = t.LAD - c.d;
				otherInfo = ""; // c.popup
			
				String pn =  t.name.split(" ext")[0];
				String cn = c.name.split(" ext")[0];
				if (pn.equals(cn) == true) {
					c.name = t.name + " ext";
				}
				
				tsf += "\t" + t.name + "\t" + c.FAD + "\t" + "branch" + "\t" + c.name + "\t" + otherInfo + "\n";
				
			}
			tsf += "\t" + t.name + "\t" + t.FAD + "\t" + "frequent" + "\t" + otherInfo + "\n";

			for(int i=0; children!=null && i<children.size(); i++) {
				TreeNode c = children.get(i);
				Q.add(c);
			}
		}
		
		return(tsf);
	}
	
	public String convertNewickTreeToTSCUtilStack(TreeNode tree, double FAD, String tsf) {
		tree.FAD = FAD;
		tree.LAD = tree.FAD - tree.d;
		
		TreeNode t = tree;
		String otherInfo = ""; // t.popup
		tsf += "\t" + t.name + "\t" + t.LAD + "\t" + "TOP" + "\t" + otherInfo + "\n";
		
		ArrayList<TreeNode> children = t.child;
		
		
		for(int i=0; children!=null && i<children.size(); i++) {
			TreeNode c = children.get(i);
			c.FAD = t.LAD;
			c.LAD = t.LAD - c.d;
			otherInfo = ""; // c.popup
		
			String pn =  t.name.split(" ext")[0];
			String cn = c.name.split(" ext")[0];
			if (pn.equals(cn) == true) {
				c.name = t.name + " ext";
			}
			
			tsf += "\t" + t.name + "\t" + c.FAD + "\t" + "branch" + "\t" + c.name + "\t" + otherInfo + "\n";
			
		}
		tsf += "\t" + t.name + "\t" + t.FAD + "\t" + "frequent" + "\t" + otherInfo + "\n";

		for(int i=0; children!=null && i<children.size(); i++) {
			TreeNode c = children.get(i);
			tsf = convertNewickTreeToTSCUtilStack(c, t.LAD, tsf);
		}
		
		return(tsf);
	}
	
	
	public String convertNewickTreeToTSC(TreeNode tree, String nwkS) {
		String tsf = "";
		
		SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd"); // 'at' HH:mm:ss z");
		Date date = new Date(System.currentTimeMillis());
		String today = formatter.format(date);
		print(today);
		
		tsf += "format version:" + "\t" + "1.3" + "\n";	
		tsf += "date: " + "\t" + today + "\n" + "\n";	
		
		String treeColumnName = "Tree";
					
		tsf += treeColumnName + "\t" + "range" + "\t" + "100" + "\t" +	"255/255/255" + "\n";
		
		double FAD = getLongestPathLength(tree, 0);
		
		//FAD = 63.9;
		
		tsf = convertNewickTreeToTSCUtilQueue(tree, FAD, tsf);
		
		
		return(tsf);
	}
	
	public String convertNewickToTSC(String nwkS) {
		String tsf = "";
		
		TreeNode tree = convertNewickToTree(nwkS);
		tsf = convertNewickTreeToTSC(tree, nwkS);
		
		return(tsf);
	}
	
	public void print(Object line) {
	    System.out.println(line);
	}
	
	public TreeNode convertNewickToTree(String nwkS) {
		int countBracket = 0;
		char[] s = nwkS.toCharArray();
		
		int bracketStartID, bracketEndID, i;
		
		i = 0;
		bracketStartID = -1;
		bracketEndID = -1;
		
		// find the left most bracket start id and the rightmost bracket end id
		if (s.length > 0 && s[0] == '(') {
			while(s.length >= 2) {
				if(s[i] == '(') {
					countBracket++;
					if (countBracket == 1) {
						bracketStartID = i;
					}
				} else if (s[i] == ')') {
					countBracket--;
				}
				
				if(countBracket == 0) {
					bracketEndID = i;
					break;
				}
				
				i++;
			}
		} 
		
		// find the semicolon id at the end of newick string
		int semicolonID = nwkS.length() - 1;
		i = semicolonID;
		while(i >= 0 && i <= nwkS.length()) {
			if(s[i] == ';') {
				semicolonID = i;
				break;
			}
			i--;
		}
		
		int colonID = i;
		int cID = colonID;
		int cIDS = colonID;
		i = semicolonID;
		int prevBracketID = -1;
		int cnt = 0;
		//print(nwkS);
		while(i >= 0 && i <= nwkS.length()) {
			if(s[i] == ':') {
				colonID = i;
			}
			else if (s[i] == ')') {
				prevBracketID = i;
				cnt++;
				if (cnt == 2)
					break;
			}
			i--;
		}
		
		int sID = prevBracketID;
		if(bracketEndID == prevBracketID) {
			if (nwkS.charAt(1) == '\'') {
				sID = 2;
				cID = colonID - 1;
			} else {
				sID = 1;
				cID = colonID;
			}
			cIDS = colonID + 1;
		} else {
			if (nwkS.charAt(prevBracketID + 1) == '\'') {
				sID = prevBracketID + 2;
				cID = colonID - 1;
			} else {
				sID = prevBracketID + 1;
				cID = colonID;
			}
			cIDS = colonID + 1;
		}
			
		String treeName = "";
		String treeDs = "";
		if (nwkS.split("\n")[0].endsWith(");") == true && cID != -1) {
			treeName = nwkS.substring(sID, cID);
			print(treeName);
			treeDs = nwkS.substring(cIDS, semicolonID-1);
			print(treeDs);
		} else {
			if (cID == -1 || cID < bracketEndID) {
				treeName = nwkS.substring(bracketEndID + 1, semicolonID);
				treeDs = "0";
			} else { 
				treeDs = nwkS.substring(bracketEndID + 1, cID);
				treeDs = nwkS.substring(cIDS, semicolonID);
			}
			
		}
		double treeD = -1;
		try {
			treeD = Double.parseDouble(treeDs);
		} catch(NumberFormatException e) {
			treeD = 0;
		}
		// the parent tree
		TreeNode tree = new TreeNode(treeName, treeD);
		
		//print(tree.name);
		//print(tree.d);
		
		// get the children of the parent
		int startID = bracketStartID;
		int endID = prevBracketID;
		if (nwkS.split("\n")[0].endsWith(");") == true){
			startID = bracketStartID + 1;
			endID = prevBracketID + 1;
		} else {
			startID = bracketStartID + 1;
			endID = bracketEndID;
		}
		
		if(bracketEndID == prevBracketID) {
			return (tree);
		}
		
		
		String sss = nwkS.substring(startID, endID);
		if(sss.charAt(0) == '(' && sss.charAt(sss.length()-1) == ')') {
			sss = sss.substring(1, sss.length()-1);
		}
		
		ArrayList<String> children;
		if (sss.length() > 0) {
			children = new ArrayList<String>();
		} else {
			children = null;
		}
		
		countBracket = 0;
		int j = 0;
		int childStartID = 0;
		int childEndID = -1;
		while(j < sss.length()) {
			if(sss.charAt(j) == '(')
				countBracket++;
			else if(sss.charAt(j) == ')') {
				countBracket--;
			}
			if((sss.charAt(j) == ',' || j == sss.length() - 1) && countBracket == 0) {
				childEndID = j;
				if (j == sss.length()-1) {
					childEndID = j+1;
				}
				String child = sss.substring(childStartID, childEndID);
				if(child.startsWith("(") == true && child.endsWith(")") == true) {
					child = child + ";";
				} else {
					child = "(" + child + ");";
				}
				children.add(child);
				//print(child);
				if (j+1 < sss.length()) {
					childStartID = j+1;
				}
			}
			
			j++;
		}
		
		int ci;
		if (children.size()>0) {
			tree.child = new ArrayList<TreeNode>();
		}
		for(ci=0; ci<children.size(); ci++) {
			String childNwkS = children.get(ci);
			TreeNode childTree = convertNewickToTree(childNwkS);
			childTree.parent = tree;
			tree.child.add(childTree);

			//print(childTree.name);
			//print(childTree.d);
			//print("---------------");
		}

		
		return(tree);
	}
	
	public static void main(String[] args) {
		EvTree T = new EvTree();
		
	/*
		ArrayList<TreeNode> tree = null;
		tree = T.createExampleTree2();
		
		String path = "";
		double d, m;
		
		String nwkS;
		String nwkS2 = "(('A':0.1,'B':0.2,('C':0.3,'D':0.4)'E':0.5)'F':0);";
		String nwkS3 = "((((('Planoglobanomalina pseudoalgeriana':6.0748120300752,'Globanomalina planoconica':0.234999999999999)'Globanomalina planoconica':6.89,'Globanomalina chapmani':3.2457142857143)'Globanomalina chapmani':3.57,('Globanomalina pseudomenardii':3.63,'Globanomalina ehrenbergi':0.209999999999994)'Globanomalina ehrenbergi':0.400000000000006)'Globanomalina ehrenbergi':1.315,'Globanomalina compressa':0.368333333333297)'Globanomalina compressa':1.455);";
		nwkS = "(((((('Eoglobigerina spiralis':0.634999999999998,'Eoglobigerina edita':0.363749999999996)'Eoglobigerina edita':3.0301648351649,(('Subbotina triloculinoides':7.46,(((((((((('Turborotalita clarkei':1.69494285714286,'Turborotalita cristata':1.69494285714286)'Turborotalita cristata':2.85505714285714,'Turborotalita humilis':4.55)'Turborotalita humilis':1.26219512195122,'Turborotalita quinqueloba':5.81219512195122)'Turborotalita quinqueloba':19.0428891738759,'Turborotalita praequinqueloba':3.1943946406547)'Turborotalita praequinqueloba':11.225824795082,'Turborotalita carcoselleensis':0.0954545454546007)'Turborotalita carcoselleensis':7.7690909090909,((((((('Globigerinoides extremus':6.91678167420814,('Globigerinoides conglobatus':6.20971428571429,'Globigerinoides obliquus':4.90971428571429)'Globigerinoides obliquus':2.68706738849385)'Globigerinoides obliquus':12.4281169059136,'Globigerinoides altiapertura':4.9448985801217)'Globigerinoides altiapertura':0.5575507099392,('Globigerinoides mitra':5.9202750136686,('Globigerinoides diminutus':3.3,(('Globigerinoides seigliei':3.92231421232877,'Globigerinoides ruber':9.2434375)'Globigerinoides ruber':5.8565625,'Globigerinoides subquadratus':3.56)'Globigerinoides subquadratus':2.44)'Globigerinoides subquadratus':0.784258064516102)'Globigerinoides subquadratus':3.5581912255448)'Globigerinoides subquadratus':0.398250507099299,'Globoturborotalita brazieri':7.1806997971602)'Globoturborotalita brazieri':0.679300202839801,((((((((('Orbulina universa':14.9443902439024,'Orbulina suturalis':14.9443902439024)'Orbulina suturalis':0.155609756097599,'Praeorbulina circularis':0.6052941176471)'Praeorbulina circularis':0.860000000000001,'Praeorbulina glomerosa':1.1712195121951)'Praeorbulina glomerosa':0.309999999999999,'Praeorbulina curva':1.4812195121951)'Praeorbulina curva':0.0100000000000016,'Praeorbulina sicanus':1.75)'Praeorbulina sicanus':0.0999999999999979,'Globigerinoides bisphericus':0.7120454545455)'Globigerinoides bisphericus':2.1870107526882,(('Globigerinoides fistulosus':1.45,'Globigerinoides sacculifer':3.33)'Globigerinoides sacculifer':7.57,'Globigerinoides trilobus':10.9)'Globigerinoides trilobus':7.6670107526882)'Globigerinoides trilobus':3.3950886387925,'Globoturborotalita connecta':5.126432724814)'Globoturborotalita connecta':0.318600405679501,('Globigerinoides parawoodi':4.74,((('Sphaeroidinellopsis kochi':9.5491697191697,(('Sphaeroidinella dehiscens':5.53,'Sphaeroidinellopsis paenedehiscens':3.23)'Sphaeroidinellopsis paenedehiscens':1.83919250482802,'Sphaeroidinellopsis seminulina':4.20919250482802)'Sphaeroidinellopsis seminulina':6.70997721434168)'Sphaeroidinellopsis seminulina':2.3008302808303,'Sphaeroidinellopsis disjuncta':3.9760169491525)'Sphaeroidinellopsis disjuncta':2.0656344086022,(('Globoturborotalita kennetti':3.61091998155954,'Globoturborotalita bollii':6.54204326923077)'Globoturborotalita bollii':4.43129006410253,('Globoturborotalita nepenthes':7.26,((('Globoturborotalita tenella':2.48367088607595,'Globoturborotalita rubescens':2.48367088607595)'Globoturborotalita rubescens':1.11632911392405,'Globoturborotalita decoraperta':0.85)'Globoturborotalita decoraperta':7.89,('Globoturborotalita apertura':9.54,'Globoturborotalita woodi':8.88)'Globoturborotalita woodi':0.31)'Globoturborotalita woodi':0.140000000000001)'Globoturborotalita woodi':1.7333333333333)'Globoturborotalita woodi':5.0823010752689)'Globoturborotalita woodi':2.6743655913978)'Globoturborotalita woodi':1.1606997971602)'Globoturborotalita woodi':0.679300202839801)'Globoturborotalita woodi':7.7244444444444,('Globoturborotalita labiacrassata':14.0714814814815,'Globoturborotalita martini':0.134814814814799)'Globoturborotalita martini':0.269629629629598)'Globoturborotalita martini':12.8555555555556,((('Globoturborotalita anguliofficinalis':11.8150621888897,'Globoturborotalita gnaucki':4.5675384615385)'Globoturborotalita gnaucki':0.670153846153802,'Globoturborotalita ouachitaensis':12.4852160350435)'Globoturborotalita ouachitaensis':7.1539743589744,'Globoturborotalita bassriverensis':0.670000000000002)'Globoturborotalita bassriverensis':0.868333333333297)'Globoturborotalita bassriverensis':0.310000000000002)'Globoturborotalita bassriverensis':11.96,'Subbotina hornibrooki':2.77714285714291)'Subbotina hornibrooki':0.434999999999995,'Subbotina velascoensis':1.04499999999999)'Subbotina velascoensis':4.485,((('Subbotina angiporoides':11.9766666666667,('Subbotina utilisindex':5.3121538461538,'Subbotina linaperta':1.6921538461538)'Subbotina linaperta':6.2978461538462)'Subbotina linaperta':10.276,'Subbotina patagonica':7.617679197995)'Subbotina patagonica':6.1295555555556,'Subbotina cancellata':0.404444444444501)'Subbotina cancellata':2.4344444444444)'Subbotina cancellata':2.1588888888889,((((('Subbotina corpulenta':14.3608484162896,'Subbotina hagni':11.8867307692308)'Subbotina hagni':0.846730769230696,('Subbotina jacksonensis':9.485,(('Subbotina sp2':7.4394736842105,'Subbotina sp1':1.2947058823529)'Subbotina sp1':0.390000000000001,'Subbotina eocaena':2.1082352941176)'Subbotina eocaena':8.965)'Subbotina eocaena':4.0284615384615)'Subbotina eocaena':3.1390384615385,('Subbotina senni':12.445,(('Subbotina crociapertura':5.75,('Subbotina gortanii':12.4835460824005,'Subbotina yeguaensis':5.17407239819)'Subbotina yeguaensis':6.64592760181)'Subbotina yeguaensis':4.48,((((('Protentella nicobarensis':24.9656308243728,'Protentella prolixa':18.7078888888889)'Protentella prolixa':0.0121111111110999,((((('Beella megastoma':0.281538461538461,'Beella digitata':0.281538461538461)'Beella digitata':4.7348177028451,'Beella praedigitata':4.21635616438356)'Beella praedigitata':7.38364383561644,(('Globigerinella adamsi':0.0142,'Globigerinella calida':0.0142)'Globigerinella calida':4.3858,'Globigerinella siphonifera':4.4)'Globigerinella siphonifera':8)'Globigerinella siphonifera':0.963333333333299,'Globigerinella praesiphonifera':2.0025)'Globigerinella praesiphonifera':9.0766666666667,'Globigerinella obesa':6.7720454545455)'Globigerinella obesa':6.74)'Globigerinella obesa':2.1111111111111,('Globigerinoides primordius':6.7247368421053,(('Globigerina druryi':20.5569230769231,'Globigerina eamesi':19.0285966981132)'Globigerina eamesi':4.7886985645933,('Globigerina falconensis':18.1772839506173,(('Globigerina umbilicata':2.01426484907498,'Globigerina bulloides':2.5773417721519)'Globigerina bulloides':11.6626582278481,'Globigerina praebulloides':9.42682191780822)'Globigerina praebulloides':3.9372839506173)'Globigerina praebulloides':7.731414613976)'Globigerina praebulloides':0.116038277511997)'Globigerina praebulloides':5.2663742690058)'Globigerina praebulloides':2.8656388888889,(('Globigerina angulisuturalis':8.24,'Globigerina ciperoensis':6.28)'Globigerina ciperoensis':2.1111111111111,'Globigerina officinalis':8.3311111111111)'Globigerina officinalis':2.8656388888889)'Globigerina officinalis':8.96158333333329,'Subbotina roesnaesensis':0.334999999999994)'Subbotina roesnaesensis':7.08166666666671)'Subbotina roesnaesensis':0.234999999999999)'Subbotina roesnaesensis':0.1175)'Subbotina roesnaesensis':5.6925,'Subbotina triangularis':1.04499999999999)'Subbotina triangularis':6.2465)'Subbotina trivialis':0.397388888888898)'Subbotina trivialis':2.3611111111111)'Subbotina trivialis':0.705164835164894,'Eoglobigerina eobulloides':1.3715109890111)'Eoglobigerina eobulloides':0)'Eoglobigerina eobulloides':0.0848351648351127,(((('Parasubbotina variospira':1.77,(('Parasubbotina prebetica':0.997333333333302,((('Clavigerinella colombiana':3.8211538461538,('Clavigerinella jarvisi':3.6717307692308,('Clavigerinella akersi':4.2671794871795,((((('Hantkenina lehneri':2.32525,(('Hantkenina australis':3.8014479638009,(('Hantkenina primitiva':6.07,((('Cribrohantkenina inflata':2.5627272727273,'Hantkenina nanggulanensis':2.5627272727273)'Hantkenina nanggulanensis':1.6258247634718,'Hantkenina alabamensis':4.1885520361991)'Hantkenina alabamensis':1.6817840982547,'Hantkenina compressa':5.8703361344538)'Hantkenina compressa':0.199663865546199)'Hantkenina compressa':1.0094444444445,'Hantkenina dumblei':2.0039240824535)'Hantkenina dumblei':0.910555555555497)'Hantkenina dumblei':1.5725,'Hantkenina liebusi':3.7919957983193)'Hantkenina liebusi':0.00775000000000148)'Hantkenina liebusi':0.22475,'Hantkenina mexicana':0.310000000000002)'Hantkenina mexicana':0.163904761904803,'Hantkenina singanoae':0.00890476190480172)'Hantkenina singanoae':0.0801428571427962,'Clavigerinella caucasica':0.0445238095238025)'Clavigerinella caucasica':0.0937343358395992,'Clavigerinella eocanica':9.7427819548872)'Clavigerinella eocanica':2.504064198959)'Clavigerinella eocanica':0.0298846153846029)'Clavigerinella eocanica':0.149423076923)'Clavigerinella eocanica':0.697307692307696,'Parasubbotina eoclava':3.8734615384615)'Parasubbotina eoclava':1.2340384615385,(('Pseudoglobigerinella bolivariana':4.7417948717948,'Parasubbotina griffinae':10.0916433566433)'Parasubbotina griffinae':0.298846153846199,(((((('Orbulinoides beckmanni':0.43,('Globigerinatheka luterbacheri':5.548,'Globigerinatheka euganea':0.0859999999999985)'Globigerinatheka euganea':0.171999999999997)'Globigerinatheka euganea':1.959,'Globigerinatheka curryi':1.0484444444444)'Globigerinatheka curryi':1.14225,'Globigerinatheka kugleri':3.7309138655462)'Globigerinatheka kugleri':0.348750000000003,(('Globigerinatheka semiinvoluta':2.5927601809955,'Globigerinatheka mexicana':2.4018510900864)'Globigerinatheka mexicana':5.3672398190045,('Globigerinatheka korotkovi':8.1058846153846,('Globigerinatheka barri':7.1727272727273,(('Globigerinatheka tropicalis':5.04407239819,'Globigerinatheka index':4.39407239819)'Globigerinatheka index':4.31092760181,'Globigerinatheka subconglobata':3.8143277310924)'Globigerinatheka subconglobata':0.155000000000001)'Globigerinatheka subconglobata':0.232500000000002)'Globigerinatheka subconglobata':0.0775000000000006)'Globigerinatheka subconglobata':0)'Globigerinatheka subconglobata':1.4481954887218,'Guembelitrioides nuttalli':3.4081954887218)'Guembelitrioides nuttalli':0.421804511278197,'Parasubbotina inaequispira':0.937343358395999)'Parasubbotina inaequispira':1.9923076923077)'Parasubbotina inaequispira':0.935192307692304)'Parasubbotina inaequispira':3.1445)'Parasubbotina inaequispira':4.168,((('Paragloborotalia opima':3.7544444444444,(((('Paragloborotalia acrostoma':9.0533333333333,'Paragloborotalia mayeri':11.98)'Paragloborotalia mayeri':5.2357142857143,('Paragloborotalia bella':5.1743842609004,'Paragloborotalia siakensis':8.5925161290323)'Paragloborotalia siakensis':8.623198156682)'Paragloborotalia siakensis':2.6042857142857,'Paragloborotalia semivera':14.7256363636364)'Paragloborotalia semivera':0.269629629629598,('Paragloborotalia incognita':4.3435185185185,'Paragloborotalia nana':5.6235185185185)'Paragloborotalia nana':9.8261111111111)'Paragloborotalia nana':0.134814814814799)'Paragloborotalia nana':16.1313247863248,'Paragloborotalia griffinoides':12.9157692307692)'Paragloborotalia griffinoides':9.0692307692308,(((('Catapsydrax africanus':4.295520361991,'Catapsydrax howei':5.075520361991)'Catapsydrax howei':4.835729638009,('Catapsydrax globiformis':5.634,('Catapsydrax dissimilis':20.0681818181818,(('Catapsydrax parvulus':7.4416666666667,'Catapsydrax stainforthi':0.2903333333334)'Catapsydrax stainforthi':9.9897619047619,'Catapsydrax unicavus':9.7214285714286)'Catapsydrax unicavus':10.3467532467532)'Catapsydrax unicavus':2.7058181818182)'Catapsydrax unicavus':3.49725)'Catapsydrax unicavus':11.7445833333333,(('Globorotaloides testarugosa':10.746962474645,(('Protentelloides dalhousiei':2.2506172248804,'Protentelloides primitiva':2.2506172248804)'Protentelloides primitiva':0.6980813397129,(('Clavatorella bermudezi':3.3260169491525,'Globorotaloides hexagonus':15.73)'Globorotaloides hexagonus':1.81,('Globorotaloides variabilis':12.4584885844749,'Globorotaloides eovariabilis':1.3116666666667)'Globorotaloides eovariabilis':0.268333333333299)'Globorotaloides eovariabilis':8.3686985645933)'Globorotaloides eovariabilis':6.7207132001126)'Globorotaloides eovariabilis':11.9189090372991,'Globorotaloides quadrocameratus':10.648320802005)'Globorotaloides quadrocameratus':11.0075125313283)'Globorotaloides quadrocameratus':0.127083333333402,('Parasubbotina pseudowilsoni':6.5153846153846,'Parasubbotina varianta':4.3553846153846)'Parasubbotina varianta':8.7675320512821)'Parasubbotina varianta':0.202083333333299)'Parasubbotina varianta':0.0750000000000028)'Parasubbotina varianta':6.33)'Parasubbotina varianta':1.61,'Parasubbotina pseudobulloides':3.275)'Parasubbotina pseudobulloides':1.87230769230761)'Parasubbotina aff_pseudobulloides':0.261025641025697,(((((((((('Morozovella acutispira':2.94,('Morozovella occlusa':5.4775,'Morozovella pasionensis':5.4775)'Morozovella pasionensis':0.0524999999999949)'Morozovella pasionensis':0.100000000000001,('Morozovella acuta':3.0441666666667,('Morozovella allisonensis':0.2135416666667,('Morozovella edgari':0.882291666666703,'Morozovella velascoensis':0.292291666666699)'Morozovella velascoensis':0.467708333333299)'Morozovella velascoensis':2.4366666666667)'Morozovella velascoensis':2.4333333333333)'Morozovella velascoensis':0.5,'Morozovella conicotruncata':1.51777777777779)'Morozovella conicotruncata':0.960000000000001,(((('Morozovella marginodentata':3.7614166666667,(('Morozovella formosa':3.94,'Morozovella gracilis':3.94)'Morozovella gracilis':0.7783823529412,((('Morozovella caucasica':7.0031818181818,'Morozovella crater':7.2075)'Morozovella crater':3.4471428571429,('Morozovella aragonensis':9.31,'Morozovella lensiformis':2.105)'Morozovella lensiformis':1.5771428571429)'Morozovella lensiformis':0.492857142857098,'Morozovella subbotinae':3.94)'Morozovella subbotinae':0.7783823529412)'Morozovella subbotinae':0.040367647058801)'Morozovella subbotinae':1.67125,'Morozovella aequa':6.43)'Morozovella aequa':0.689999999999998,(('Planorotalites capdevilensis':13.6418181818182,('Astrorotalia palmerae':2.1869230769231,'Planorotalites pseudoscitula':3.3325)'Planorotalites pseudoscitula':1.1475)'Planorotalites pseudoscitula':6.9,'Morozovella apanthesma':1.6077083333333)'Morozovella apanthesma':0.689999999999998)'Morozovella apanthesma':3.7533333333333,'Morozovella angulata':1.1244444444444)'Morozovella angulata':0.746666666666698)'Morozovella angulata':0,(((((((('Acarinina africana':0.0635416666667012,'Acarinina sibaiyaensis':0.0635416666667012)'Acarinina sibaiyaensis':0.149999999999999,'Acarinina esnehensis':5.76)'Acarinina esnehensis':0.854999999999997,(('Acarinina cuneicamerata':6.66,'Acarinina angulosa':4.48)'Acarinina angulosa':6.33,((('Acarinina aspensis':2.1888461538461,(('Acarinina medizzai':6.201,'Acarinina collactea':11.811)'Acarinina collactea':8.109,'Acarinina pentacamerata':3.88230769230771)'Acarinina pentacamerata':0)'Acarinina pentacamerata':2.34,'Acarinina interposita':2.34)'Acarinina interposita':2.07,((('Acarinina echinata':10.8855555555555,'Acarinina pseudosubsphaerica':0.893333333333302)'Acarinina pseudosubsphaerica':7.4166666666667,'Acarinina alticonica':3.88230769230771)'Acarinina alticonica':3.62142857142859,'Acarinina soldadoensis':5.5114285714286)'Acarinina soldadoensis':0.788571428571402)'Acarinina soldadoensis':1.92)'Acarinina soldadoensis':0.284999999999997)'Acarinina soldadoensis':0.975000000000001,'Acarinina mckannai':0.229999999999997)'Acarinina mckannai':2.6288888888889,'Acarinina subsphaerica':5.5728888888889)'Acarinina subsphaerica':0.101111111111102,(((('Dentoglobigerina pseudovenezuelana':10.0277033492823,((('Dentoglobigerina altispira':19.794,'Dentoglobigerina globosa':19.0572258064516)'Dentoglobigerina globosa':3.5533684210526,((((('Globorotalia zealandica':1.16,((((((('Globoconella inflata':2.67101265822785,'Globoconella puncticulata':0.28101265822785)'Globoconella puncticulata':1.93898734177215,'Globoconella sphericomiozea':0.0600000000000005)'Globoconella sphericomiozea':0.84533333333333,('Globoconella pliozea':0.890053910737961,'Globoconella terminalis':0.0507945205479503)'Globoconella terminalis':0.38818264840182)'Globoconella terminalis':0.264666666666669,'Globoconella conomiozea':0.12875)'Globoconella conomiozea':0.42,'Globoconella conoidea':0.312154471544719)'Globoconella conoidea':10.24,'Globoconella miozea':6.172)'Globoconella miozea':0.310000000000002,(((((('Menardella miocenica':1.38,'Menardella pseudomiocenica':1.47)'Menardella pseudomiocenica':4.00228728012944,('Menardella multicamerata':3.75322094055013,(('Menardella pertenuis':1.7162,'Menardella exilis':1.43)'Menardella exilis':0.93,'Menardella limbata':2.06)'Menardella limbata':2.28322094055013)'Menardella limbata':1.03906633957931)'Menardella limbata':2.86771271987056,(((('Globorotalia ungulata':4.91476712328767,('Globorotalia flexuosa':0.337184615384615,'Globorotalia tumida':0.401)'Globorotalia tumida':4.51376712328767)'Globorotalia tumida':0.80523287671233,'Globorotalia plesiotumida':1.95)'Globorotalia plesiotumida':2.86,'Globorotalia merotumida':2.63873170731707)'Globorotalia merotumida':1.376,('Menardella fimbriata':0.187692307692308,'Menardella menardii':0.187692307692308)'Menardella menardii':9.76830769230769)'Menardella menardii':0.684000000000001)'Menardella menardii':2.5013076923077,'Menardella praemenardii':0.0113076923076996)'Menardella praemenardii':0.933841269841299,'Menardella archeomenardii':0.205148962149)'Menardella archeomenardii':2.184851037851,(('Hirsutella juanai':4.87682191780822,'Hirsutella challengeri':0.436182692307689)'Hirsutella challengeri':5.4667954545455,(('Hirsutella gigantea':5.92624450943608,((((((((('Truncorotalia pachytheca':1.9088679245283,'Truncorotalia excelsa':1.9088679245283)'Truncorotalia excelsa':0.04361635220126,('Truncorotalia cavernula':0.893482142857143,'Truncorotalia truncatulinoides':0.893482142857143)'Truncorotalia truncatulinoides':1.05900213387242)'Truncorotalia truncatulinoides':0.0426100628930799,'Truncorotalia tosaensis':1.38509433962264)'Truncorotalia tosaensis':1.35490566037736,'Truncorotalia tenuitheca':2.00152056277056)'Truncorotalia tenuitheca':0.24342391304348,('Truncorotalia hessi':1.53348330914369,'Truncorotalia ronda':0.9019929245283)'Truncorotalia ronda':1.68455598851518)'Truncorotalia ronda':0.60883415147265,'Truncorotalia oceanica':4.20225806451613)'Truncorotalia oceanica':1.38899193548387,('Truncorotalia crassaconica':0.69826086956522,('Truncorotalia viola':2.26643068887634,'Truncorotalia crassaformis':3.73173913043478)'Truncorotalia crassaformis':0.69826086956522)'Truncorotalia crassaformis':1.16125)'Truncorotalia crassaformis':0.236595528455281,('Truncorotalia crassula':4.76214285714286,'Hirsutella cibaoensis':1.165625)'Hirsutella cibaoensis':0.172220528455281)'Hirsutella cibaoensis':3.61215447154472,(((('Hirsutella evoluta':0.69479098541759,('Hirsutella theyeri':4.61,('Hirsutella hirsuta':4.35322580645161,'Hirsutella margaritae':0.50322580645161)'Hirsutella margaritae':0.256774193548391)'Hirsutella margaritae':0.20317808219178)'Hirsutella margaritae':1.21905606414968,'Hirsutella primitiva':1.21905606414968)'Hirsutella primitiva':0.0101073170731807,'Hirsutella praemargaritae':0.21449593495936)'Hirsutella praemargaritae':2.43536167349229,('Hirsutella bermudezi':2.09140330188679,'Hirsutella scitula':2.09140330188679)'Hirsutella scitula':6.38629983502014)'Hirsutella scitula':0.962296863093069)'Hirsutella scitula':5.3176585365854)'Hirsutella scitula':0.0311219512195002,'Hirsutella praescitula':1.0587804878049)'Hirsutella praescitula':0.368014966740601)'Hirsutella praescitula':1.1032045454545)'Hirsutella praescitula':0.43)'Hirsutella praescitula':0.849999999999998)'Hirsutella praescitula':0.720000000000002,('Neogloboquadrina pachyderma':10.8,((('Neogloboquadrina dutertrei':7.16764511717731,'Neogloboquadrina humerosa':5.56764511717731)'Neogloboquadrina humerosa':1.39243651547575,((((('Pulleniatina spectabilis':0.4,'Pulleniatina praespectabilis':0.12)'Pulleniatina praespectabilis':0.60953424657534,('Pulleniatina finalis':2.04,'Pulleniatina obliquiloculata':2.04)'Pulleniatina obliquiloculata':3.17953424657534)'Pulleniatina obliquiloculata':0.48046575342466,'Pulleniatina praecursor':3.84177142857143)'Pulleniatina praecursor':0.54406625258799,'Pulleniatina primalis':2.58406625258799)'Pulleniatina primalis':0.36401538006507,'Neogloboquadrina acostaensis':4.80428163265306)'Neogloboquadrina acostaensis':1.952)'Neogloboquadrina acostaensis':1.26991836734694,'Neogloboquadrina continuosa':1.75539163839449)'Neogloboquadrina continuosa':0.970000000000001)'Neogloboquadrina continuosa':7.46)'Neogloboquadrina continuosa':4.18,(('Fohsella birnageae':0.747,((((('Fohsella robusta':1.7725714285714,'Fohsella lobata':1.7725714285714)'Fohsella lobata':0.0762857142856994,'Fohsella fohsi':1.8488571428571)'Fohsella fohsi':0.0381428571428994,'Fohsella praefohsi':0.114428571428599)'Fohsella praefohsi':0.159190476190501,(('Fohsella paralenguaensis':4.66554532967032,'Fohsella lenguaensis':6.79299538598046)'Fohsella lenguaensis':0.0405238095237994,'Fohsella peripheroacuta':0.116809523809501)'Fohsella peripheroacuta':0.0423809523810004)'Fohsella peripheroacuta':0.202771672771599,'Fohsella peripheroronda':0.202771672771599)'Fohsella peripheroronda':3.0880378510379)'Fohsella peripheroronda':4.3571987829615,'Paragloborotalia kugleri':0.364198782961498)'Paragloborotalia kugleri':0.955801217038502)'Paragloborotalia kugleri':0.52,'Paragloborotalia pseudokugleri':0.266041666666702)'Paragloborotalia pseudokugleri':2.25,'Dentoglobigerina globularis':5.91)'Dentoglobigerina globularis':1.2673684210526)'Dentoglobigerina globularis':7.8126315789474,(((('Dentoglobigerina binaiensis':6.2218421052632,'Dentoglobigerina sellii':1.9687532355479)'Dentoglobigerina sellii':8.2705108359133,'Dentoglobigerina tapuriensis':8.7873119575699)'Dentoglobigerina tapuriensis':0.317647058823496,((('Dentoglobigerina rohri':6.3619119473422,('Globoquadrina conglomerata':4.37,'Dentoglobigerina venezuelana':1.07)'Dentoglobigerina venezuelana':25.5433333333333)'Dentoglobigerina venezuelana':3.4572549019608,'Dentoglobigerina prasaepis':10.3381119626453)'Dentoglobigerina prasaepis':0.105882352941201,'Dentoglobigerina sp':0.423529411764704)'Dentoglobigerina sp':0.423529411764697)'Dentoglobigerina sp':0.227499999999999,('Dentoglobigerina baroemoenensis':28.8,(('Globoquadrina dehiscens':16.5151219512195,'Dentoglobigerina larmeui':12.232)'Dentoglobigerina larmeui':4.2636842105263,'Dentoglobigerina galavisi':0.113157894736801)'Dentoglobigerina galavisi':5.3963157894737)'Dentoglobigerina galavisi':2.0275)'Dentoglobigerina galavisi':0.162500000000001)'Dentoglobigerina galavisi':2.5545454545455)'Dentoglobigerina galavisi':2.13097490744551,'Acarinina primitiva':0.197104072398204)'Acarinina primitiva':11.694479638009,'Acarinina coalingensis':0.875)'Acarinina coalingensis':7.12,(((('Acarinina quetra':4.41,(('Acarinina bullbrooki':7.91,('Acarinina punctocarinata':5.9176923076923,'Acarinina boudreauxi':3.0101923076923)'Acarinina boudreauxi':1.9923076923077)'Acarinina boudreauxi':1.08,(((((('Morozovelloides lehneri':4.023649122807,'Morozovelloides coronatus':4.023649122807)'Morozovelloides coronatus':0.703007518797001,'Morozovelloides crassatus':6.792656641604)'Morozovelloides crassatus':0.468671679198003,'Morozovelloides bandyi':3.361328320802)'Morozovelloides bandyi':2.16213321765949,(('Acarinina rohri':4.1718959276018,'Acarinina topilensis':2.131)'Acarinina topilensis':0.424333333333301,'Acarinina praetopilensis':2.8133333333333)'Acarinina praetopilensis':4.63012820512819)'Acarinina praetopilensis':0.597692307692405,'Acarinina mcgowrani':10.0211538461539)'Acarinina mcgowrani':0.636346153846098,'Acarinina pseudotopilensis':2.0807692307692)'Acarinina pseudotopilensis':0.7425)'Acarinina pseudotopilensis':5.22)'Acarinina pseudotopilensis':1.35,'Acarinina wilcoxensis':5.1653333333333)'Acarinina wilcoxensis':0.57,'Acarinina esnaensis':5.3613333333333)'Acarinina esnaensis':1.03)'Acarinina nitida':0.229999999999997)'Acarinina nitida':2.73)'Acarinina nitida':0,'Acarinina strabocella':0.404444444444401)'Acarinina strabocella':1.45,'Morozovella praeangulata':0.640000000000001)'Morozovella praeangulata':0.32)'Morozovella praeangulata':0.310000000000002,((((('Igorina anapetes':1.552656641604,'Igorina broedermanni':1.475156641604)'Igorina broedermanni':11.462343358396,'Igorina lodoensis':5.92749999999999)'Igorina lodoensis':0.57,'Igorina tadjikistanensis':0.854999999999997)'Igorina tadjikistanensis':5.0483333333333,('Igorina albeari':3.77)'Igorina pusilla':0.533333333333303)'Igorina pusilla':0.426666666666698,('Praemurica lozanoi':19.57,'Praemurica uncinata':0.053333333333299)'Praemurica uncinata':0.159999999999997)'Praemurica uncinata':0.310000000000002)'Praemurica uncinata':0,'Praemurica inconstans':0.310000000000002)'Praemurica inconstans':1.3,'Praemurica pseudoinconstans':1.3775)'Praemurica pseudoinconstans':1.9246153846154,'Praemurica taurica':1.8915734265734)'Praemurica taurica':0.195384615384597)'Hedbergella monmouthensis':0.013333333333307)'Hedbergella monmouthensis':0.00666666666670324)'Hedbergella monmouthensis':3.8668571428571,(((((((('Pseudohastigerina naguewichiensis':3.79,'Pseudohastigerina micra':3.5782352941176)'Pseudohastigerina micra':11.5234615384615,('Pseudohastigerina sharkriverensis':8.042669683258,'Pseudohastigerina wilcoxensis':4.4188461538462)'Pseudohastigerina wilcoxensis':0.0996153846152978)'Pseudohastigerina wilcoxensis':8.3965384615385,'Globanomalina luxorensis':1.2)'Globanomalina luxorensis':0.719999999999999,'Globanomalina ovalis':0.751770833333303)'Globanomalina ovalis':1.26,((('Turborotalia possagnoensis':2.8529166666667,(((('Turborotalia cunialensis':0.390000000000001,'Turborotalia cocoaensis':0.390000000000001)'Turborotalia cocoaensis':4.1613122171946,'Turborotalia cerroazulensis':4.5513122171946)'Turborotalia cerroazulensis':2.8947988939165,('Turborotalia altispiroides':3.4219696969697,((('Turborotalia euapertura':2.5693464052287,'Turborotalia ampliapertura':2.5082352941176)'Turborotalia ampliapertura':2.478149321267,'Turborotalia increbescens':4.9863846153846)'Turborotalia increbescens':3.9062398190045,'Turborotalia pomeroli':4.2413167420814)'Turborotalia pomeroli':1.4757088989442)'Turborotalia pomeroli':0.827777777777804)'Turborotalia pomeroli':0.525555555555599,'Turborotalia frontosa':1.3533333333334)'Turborotalia frontosa':1.4995833333333)'Turborotalia frontosa':4.80875,'Globanomalina australiformis':4.77)'Globanomalina australiformis':7.5833333333333)'Globanomalina imitata':1.8966666666667)'Globanomalina imitata':4.8822222222222)'Globanomalina planocompressa':3.23085470085469,((((('Planoglobanomalina pseudoalgeriana':6.0748120300752,'Globanomalina planoconica':0.234999999999999)'Globanomalina planoconica':6.89,'Globanomalina chapmani':3.2457142857143)'Globanomalina chapmani':3.57,('Globanomalina pseudomenardii':3.63,'Globanomalina ehrenbergi':0.209999999999994)'Globanomalina ehrenbergi':0.400000000000006)'Globanomalina ehrenbergi':1.315,'Globanomalina compressa':0.368333333333297)'Globanomalina compressa':1.455,'Globanomalina archeocompressa':0.144444444444396)'Globanomalina archeocompressa':2.0030769230769)'Globanomalina archeocompressa':0.116923076923101)'Hedbergella holmdelensis':3.88685714285711)'Hedbergella holmdelensis':1.0121751152074,'Hedbergella [ancestor]':0.779032258064504)'Hedbergella [ancestor]':0.830967741935495);";
		
		TreeNode tn = T.convertNewickToTree(nwkS3);
		String name = "Planoglobanomalina pseudoalgeriana";
		T.print("Path from root to " + name + ":");
		path = "";
		path = T.getPathToNode(tn, name, path);
		T.print(path);
		T.print("---------------");

		name = "Eoglobigerina spiralis";
		T.print("Path from root to " + name + ":");
		path = "";
		path = T.getPathToNode(tn, "Eoglobigerina spiralis", path);
		T.print(path);
		T.print("---------------");

		name = "Turborotalia euapertura";
		T.print("Path from root to " + name + ":");
		path = "";
		path = T.getPathToNode(tn, "Turborotalia euapertura", path);
		T.print(path);
		T.print("---------------");

		name = "Globoquadrina conglomerata";
		T.print("Path from root to " + name + ":");
		path = "";
		path = T.getPathToNode(tn, "Globoquadrina conglomerata", path);
		T.print(path);
		T.print("---------------");
		path = "";
		T.print("Path from root to " + name + ":");
		path = T.getPathToNode(tn, "Globoquadrina conglomerata");
		T.print(path);
		d = 0;
		d = T.getDistanceToNode2(tn, name, d); // has bugs in this algorithm
		T.print("---------------");
		T.print("Distance from root to " + name + ":");
		d = T.getDistanceToNode(tn, name);
		T.print(d);
		T.print("---------------");
		name = "Turborotalia euapertura";
		path = "";
		T.print("Path from root to " + name + ":");
		path = T.getPathToNode(tn, name);
		T.print(path);
		T.print("---------------");
		T.print("Distance from root to " + name + ":");
		d = T.getDistanceToNode(tn, name);
		T.print(d);
		T.print("---------------");
		
		m = T.getLongestPathLength(tn, 0);
		T.print("max path length = " + m);
		T.print("---------------");
		
		ArrayList<EvTree.TreeNode> leaveNodes=  new ArrayList<EvTree.TreeNode>();
		T.getLeafNodes(tn, leaveNodes);
		
		int nnodes = T.getTotalNumberOfNodes(tn);
		T.print("Total number of nodes = " + nnodes);
		T.print("---------------");
		
		HashSet<EvTree.TreeNode> treeNodes=  new HashSet<EvTree.TreeNode>();
		T.getTreeNodes(tn, treeNodes);
		int nTreeNodes = treeNodes.size();
		T.print("Total number of tree nodes = " + nTreeNodes);
		T.print("---------------");
		
		HashSet<String> nUTNodeNames = T.getUniqueTreeNodeNames(tn);
		T.print("Total number of unique tree nodes = " + nUTNodeNames.size());
		T.print("Unique tree nodes = " + nUTNodeNames);
		TreeSet<String> nUTS = new TreeSet<String>(nUTNodeNames);
		T.print("Unique tree nodes (sorted) = " + nUTS);
		T.print("---------------");
		
		ArrayList<EvTree.TreeNode> livingNodes=  new ArrayList<EvTree.TreeNode>();
		T.getLivingNodes(tn, livingNodes);
		int nLivingNodes = livingNodes.size();
		T.print("Number of living nodes = " + nLivingNodes);
		HashSet<String> livingNodeNames = T.getLivingNodeNames(tn);
		T.print("Living nodes = " + livingNodeNames);
		TreeSet<String> sLivingNodeNames = new TreeSet<String>(livingNodeNames);
		T.print("Living nodes (sorted)= " + sLivingNodeNames);

		ArrayList<TreeNode> tree4 = T.createExampleTree4();
		TreeNode tn4 = tree4.get(0);
		String nwk = T.convertToNewickFormat(tn4, null);
		T.print(nwk);
		T.print(nwkS3);
		T.print("--------------------------");

		String nwkPath = "/Users/andy/Desktop/realTree.nex";
		String nexN = T.loadTreeFromFile(nwkPath);
		String nwkN = T.convertNexusToNewickFormat(nexN);
		String tsf = T.convertNewickToTSC(nwkN);
		T.print(tsf);
		T.print("--------------------------");
		
		try {
			String fpath = "/Users/andy/Desktop/treeTSC.txt";
			BufferedWriter writer = new BufferedWriter(new FileWriter(fpath));
		    writer.write(tsf);
		     
		    writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		String nwkPath = "/Users/andy/Desktop/tree_small.nwk";
		String nwkN = T.loadTreeFromFile(nwkPath);
		String tsf = T.convertNewickToTSC(nwkN);
		T.print(tsf);
		T.print("--------------------------");
		
		try {
			String fpath = "/Users/andy/Desktop/treeTSC.txt";
			BufferedWriter writer = new BufferedWriter(new FileWriter(fpath));
		    writer.write(tsf);
		     
		    writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	/*
		String n1 = "Subbotina trivialis";//"Pseudohastigerina micra";
		String n2 = "Subbotina sp1"; //"Turborotalia cocoaensis"; // "Globigerina officinalis";
		
		String p1 = T.getPathToNode(tn, n1);
		T.print("Path from root to node : " + p1);
		String p2 = T.getPathToNode(tn, n2);
		T.print("Path from root to node : " + p2);
		String cA = T.getCommonAncestor(tn, n1, n2);
		T.print("Common ancestor of " + n1 + " and " + n2 + "is: " + cA);
		ArrayList<String> pcA = T.getPathArrayFromRootToCommonAncestor(tn, n1, n2);
		T.print("Path from root to Common ancestor : " + pcA );
		T.print("--------------------------");
		ArrayList<String> pN2N = T.getPathArrayFromNodeToNode(tn, n1, n2);
		T.print("Path from " + n1 + " to " + n2 + " : " + pN2N);
		T.print("--------------------------");

		String fpath = "/Users/andy/Documents/TSCreator/EvolutionaryTree/Fordham and Zehady shared/180724/mmc2/mb_A1_SFig02_nmt.nex.con.nwk";
		String nex = T.loadTreeFromFile(fpath);
		HashMap<Integer, String> taxMap = T.processTaxLabelTranslation(nex);
	*/
		
		String nwkComplex = "   tree con_50_majrule = [&U] (14[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:2.233411e-01[&length_mean=2.238061418277574e-01,length_median=2.233411000000000e-01,length_95%HPD={1.973215000000000e-01,2.522058000000000e-01}],(102[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:2.161846e-02[&length_mean=2.176788279929360e-02,length_median=2.161846000000000e-02,length_95%HPD={1.617482000000000e-02,2.762582000000000e-02}],((106[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:2.357874e-03[&length_mean=2.450586743144265e-03,length_median=2.357874000000000e-03,length_95%HPD={1.018898000000000e-03,4.051418000000000e-03}],107[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.341765e-03[&length_mean=1.436839376349155e-03,length_median=1.341765000000000e-03,length_95%HPD={4.040900000000000e-04,2.687991000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.895275e-03[&length_mean=5.015779614984656e-03,length_median=4.895275000000000e-03,length_95%HPD={2.594622000000000e-03,7.713333000000000e-03}],109[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.070787e-03[&length_mean=4.175231281362470e-03,length_median=4.070787000000000e-03,length_95%HPD={2.011886000000000e-03,6.716031000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:2.455818e-02[&length_mean=2.468343903846154e-02,length_median=2.455818000000000e-02,length_95%HPD={1.883503000000000e-02,3.093022000000000e-02}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:3.259232e-02[&length_mean=3.271713160511946e-02,length_median=3.259232000000000e-02,length_95%HPD={2.449788000000000e-02,4.118775000000000e-02}],(((((1[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:3.750640e-04[&length_mean=4.910334907714302e-04,length_median=3.750640000000000e-04,length_95%HPD={3.163094000000000e-08,1.352026000000000e-03}],2[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:3.621893e-03[&length_mean=3.681494424943335e-03,length_median=3.621893000000000e-03,length_95%HPD={1.872249000000000e-03,5.708362000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.462238e-02[&length_mean=1.484632114834687e-02,length_median=1.462238000000000e-02,length_95%HPD={9.065201000000000e-03,2.104826000000000e-02}],103[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:9.964884e-02[&length_mean=9.994532104319423e-02,length_median=9.964884000000000e-02,length_95%HPD={8.550344999999999e-02,1.138283000000000e-01}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:3.409916e-02[&length_mean=3.429612109118787e-02,length_median=3.409916000000000e-02,length_95%HPD={2.622402000000000e-02,4.259795000000000e-02}],(60[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.152947e-01[&length_mean=1.156511144547399e-01,length_median=1.152947000000000e-01,length_95%HPD={9.980272000000000e-02,1.321986000000000e-01}],105[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.251665e-02[&length_mean=4.263861293660858e-02,length_median=4.251665000000000e-02,length_95%HPD={3.405307000000000e-02,5.195993000000000e-02}])[&prob=9.888681509132117e-01,prob_stddev=2.765561548682660e-03,prob_range={9.849353419544061e-01,9.908012265031330e-01},prob(percent)=\"99\",prob+-sd=\"99+-0\"]:6.507297e-03[&length_mean=6.775622987788469e-03,length_median=6.507297000000000e-03,length_95%HPD={2.004524000000000e-03,1.220449000000000e-02}])[&prob=9.381749100119984e-01,prob_stddev=4.630197303509461e-03,prob_range={9.326756432475670e-01,9.440074656712438e-01},prob(percent)=\"94\",prob+-sd=\"94+-0\"]:4.915209e-03[&length_mean=5.057213955721305e-03,length_median=4.915209000000000e-03,length_95%HPD={1.422209000000000e-03,8.879801000000000e-03}],(((15[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.834194e-02[&length_mean=1.858147994810696e-02,length_median=1.834194000000000e-02,length_95%HPD={1.389868000000000e-02,2.352769000000000e-02}],((46[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.472418e-04[&length_mean=7.351652144314100e-04,length_median=6.472418000000000e-04,length_95%HPD={9.182609000000000e-05,1.563291000000000e-03}],47[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.629643e-03[&length_mean=1.708605093904158e-03,length_median=1.629643000000000e-03,length_95%HPD={5.782702000000000e-04,3.023099000000000e-03}],(48[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:7.630060e-04[&length_mean=8.474998098366899e-04,length_median=7.630060000000000e-04,length_95%HPD={1.211887000000000e-04,1.768705000000000e-03}],49[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.462061e-04[&length_mean=7.276176951886416e-04,length_median=6.462061000000000e-04,length_95%HPD={7.269770999999999e-05,1.557569000000000e-03}])[&prob=9.953006265831222e-01,prob_stddev=2.872478526385178e-03,prob_range={9.918677509665378e-01,9.978669510731902e-01},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:7.572418e-04[&length_mean=8.400011363263214e-04,length_median=7.572418000000000e-04,length_95%HPD={8.461087000000000e-05,1.760837000000000e-03}],76[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.626930e-04[&length_mean=2.328068987564645e-04,length_median=1.626930000000000e-04,length_95%HPD={1.635109000000000e-08,6.848211000000000e-04}])[&prob=9.912678309558726e-01,prob_stddev=4.636750113589830e-03,prob_range={9.861351819757366e-01,9.960005332622317e-01},prob(percent)=\"99\",prob+-sd=\"99+-0\"]:8.685082e-04[&length_mean=9.487583223640005e-04,length_median=8.685082000000000e-04,length_95%HPD={2.041038000000000e-04,1.944720000000000e-03}],67[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.754631e-04[&length_mean=2.568709726021934e-04,length_median=1.754631000000000e-04,length_95%HPD={2.883300000000000e-08,7.729917000000000e-04}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.213178e-02[&length_mean=1.223325941047860e-02,length_median=1.213178000000000e-02,length_95%HPD={8.378793000000001e-03,1.666275000000000e-02}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.028644e-02[&length_mean=1.040634670770567e-02,length_median=1.028644000000000e-02,length_95%HPD={6.414797000000000e-03,1.461724000000000e-02}],((((((16[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.181288e-04[&length_mean=6.926145337599964e-04,length_median=6.181288000000000e-04,length_95%HPD={2.966878000000000e-05,1.512021000000000e-03}],(17[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:9.048274e-04[&length_mean=9.954978907319009e-04,length_median=9.048274000000000e-04,length_95%HPD={1.252843000000000e-04,2.020867000000000e-03}],18[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.123088e-03[&length_mean=1.198017604754361e-03,length_median=1.123088000000000e-03,length_95%HPD={2.439444000000000e-04,2.259020000000000e-03}])[&prob=9.913344887348354e-01,prob_stddev=2.798568456442958e-03,prob_range={9.880015997866951e-01,9.946673776829756e-01},prob(percent)=\"99\",prob+-sd=\"99+-0\"]:6.518557e-04[&length_mean=7.343345797152346e-04,length_median=6.518557000000000e-04,length_95%HPD={5.812181000000000e-05,1.549925000000000e-03}],94[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.110813e-03[&length_mean=1.186466109782025e-03,length_median=1.110813000000000e-03,length_95%HPD={2.895662000000000e-04,2.282919000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.777031e-03[&length_mean=1.866471523340219e-03,length_median=1.777031000000000e-03,length_95%HPD={6.822767000000000e-04,3.282397000000000e-03}],(40[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:7.190920e-04[&length_mean=8.023115387987567e-04,length_median=7.190920000000000e-04,length_95%HPD={9.040385000000000e-05,1.679272000000000e-03}],(57[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.097704e-04[&length_mean=4.904697130877222e-04,length_median=4.097704000000000e-04,length_95%HPD={1.617388000000000e-05,1.180993000000000e-03}],59[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.393245e-03[&length_mean=1.474673692954272e-03,length_median=1.393245000000000e-03,length_95%HPD={3.939698000000000e-04,2.638801000000000e-03}])[&prob=6.177176376483136e-01,prob_stddev=2.996655790852456e-02,prob_range={5.912544994000800e-01,6.481802426343154e-01},prob(percent)=\"62\",prob+-sd=\"62+-3\"]:3.419379e-04[&length_mean=4.193690577841039e-04,length_median=3.419379000000000e-04,length_95%HPD={2.074705000000000e-07,1.079828000000000e-03}],58[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.172459e-04[&length_mean=6.885095056759692e-04,length_median=6.172459000000000e-04,length_95%HPD={5.911502000000000e-05,1.453429000000000e-03}],(62[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:8.730292e-04[&length_mean=9.389734561255109e-04,length_median=8.730292000000000e-04,length_95%HPD={1.537132000000000e-04,1.879108000000000e-03}],100[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:5.124837e-04[&length_mean=5.943927439141725e-04,length_median=5.124837000000000e-04,length_95%HPD={1.315034000000000e-05,1.390729000000000e-03}])[&prob=5.876883082255699e-01,prob_stddev=6.156911946441777e-02,prob_range={5.135315291294494e-01,6.508465537928276e-01},prob(percent)=\"59\",prob+-sd=\"59+-6\"]:4.014143e-04[&length_mean=4.829068620362933e-04,length_median=4.014143000000000e-04,length_95%HPD={1.433710000000000e-05,1.164524000000000e-03}],95[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:3.120629e-04[&length_mean=3.971923638137097e-04,length_median=3.120629000000000e-04,length_95%HPD={1.075782000000000e-07,1.056596000000000e-03}],(97[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.915079e-04[&length_mean=2.729060741699591e-04,length_median=1.915079000000000e-04,length_95%HPD={5.083996000000000e-08,8.221754000000000e-04}],99[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:3.201295e-04[&length_mean=4.071390453006973e-04,length_median=3.201295000000000e-04,length_95%HPD={1.455409000000000e-08,1.062255000000000e-03}])[&prob=5.614584722037061e-01,prob_stddev=1.581630518291518e-02,prob_range={5.428609518730836e-01,5.763231569124116e-01},prob(percent)=\"56\",prob+-sd=\"56+-2\"]:4.779612e-04[&length_mean=5.610900965885290e-04,length_median=4.779612000000000e-04,length_95%HPD={1.258026000000000e-07,1.326554000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.713449e-03[&length_mean=1.782796652639651e-03,length_median=1.713449000000000e-03,length_95%HPD={5.710458000000000e-04,3.083893000000000e-03}])[&prob=9.998666844420745e-01,prob_stddev=1.885366700937832e-04,prob_range={9.996000533262231e-01,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.328852e-03[&length_mean=1.420488502392006e-03,length_median=1.328852000000000e-03,length_95%HPD={3.948738000000000e-04,2.708857000000000e-03}],108[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.662262e-03[&length_mean=4.761315109752084e-03,length_median=4.662262000000000e-03,length_95%HPD={2.676643000000000e-03,6.854150000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.488780e-03[&length_mean=4.593286456972412e-03,length_median=4.488780000000000e-03,length_95%HPD={2.489458000000000e-03,6.894177000000000e-03}],((43[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:3.945782e-04[&length_mean=4.696530474488390e-04,length_median=3.945782000000000e-04,length_95%HPD={4.182725000000000e-08,1.105131000000000e-03}],(44[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:8.670817e-04[&length_mean=9.416958486468493e-04,length_median=8.670817000000000e-04,length_95%HPD={1.590827000000000e-04,1.888217000000000e-03}],45[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.613440e-03[&length_mean=1.697247984888676e-03,length_median=1.613440000000000e-03,length_95%HPD={5.466006000000000e-04,2.991210000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.452473e-04[&length_mean=7.237798265072611e-04,length_median=6.452473000000000e-04,length_95%HPD={6.562767999999999e-05,1.563543000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:2.391371e-03[&length_mean=2.502901559962011e-03,length_median=2.391371000000000e-03,length_95%HPD={1.043327000000000e-03,4.140170000000000e-03}],((50[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:5.721794e-04[&length_mean=6.626320342112689e-04,length_median=5.721794000000001e-04,length_95%HPD={7.371237000000000e-06,1.516278000000000e-03}],54[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:8.997677e-04[&length_mean=9.748161113594864e-04,length_median=8.997677000000000e-04,length_95%HPD={1.581493000000000e-04,1.903498000000000e-03}])[&prob=9.998666844420744e-01,prob_stddev=2.666311158512254e-04,prob_range={9.994667377682975e-01,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:9.061969e-04[&length_mean=9.948348401173420e-04,length_median=9.061969000000000e-04,length_95%HPD={1.280386000000000e-04,2.028978000000000e-03}],(51[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:7.207409e-04[&length_mean=8.056126369737323e-04,length_median=7.207409000000000e-04,length_95%HPD={8.175804000000000e-05,1.732133000000000e-03}],53[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.456618e-04[&length_mean=7.223296765338249e-04,length_median=6.456618000000000e-04,length_95%HPD={8.731681000000000e-05,1.562772000000000e-03}],55[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.664389e-04[&length_mean=2.398741760562026e-04,length_median=1.664389000000000e-04,length_95%HPD={1.012020000000000e-07,7.297068000000001e-04}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.497624e-03[&length_mean=1.573440050286632e-03,length_median=1.497624000000000e-03,length_95%HPD={5.124688000000000e-04,2.814589000000000e-03}],52[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:8.721993e-04[&length_mean=9.516002897560274e-04,length_median=8.721993000000000e-04,length_95%HPD={1.588068000000000e-04,1.879702000000000e-03}])[&prob=9.164778029596053e-01,prob_stddev=1.199272061380361e-02,prob_range={9.016131182508998e-01,9.280095987201706e-01},prob(percent)=\"92\",prob+-sd=\"92+-1\"]:5.733327e-04[&length_mean=6.576477953465348e-04,length_median=5.733327000000000e-04,length_95%HPD={2.723564000000000e-06,1.465611000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.581764e-03[&length_mean=4.688205884582064e-03,length_median=4.581764000000000e-03,length_95%HPD={2.516001000000000e-03,6.913074000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.316349e-03[&length_mean=6.389703951139860e-03,length_median=6.316349000000000e-03,length_95%HPD={3.669989000000000e-03,8.976221000000000e-03}],(((((19[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.047128e-04[&length_mean=4.823705893317249e-04,length_median=4.047128000000000e-04,length_95%HPD={1.478893000000000e-05,1.141801000000000e-03}],23[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.115042e-04[&length_mean=4.895509665280307e-04,length_median=4.115042000000000e-04,length_95%HPD={1.460541000000000e-05,1.174576000000000e-03}],25[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.660006e-04[&length_mean=2.437418393668879e-04,length_median=1.660006000000000e-04,length_95%HPD={3.459126000000000e-08,7.548563000000000e-04}],(28[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:8.964830e-04[&length_mean=9.707447304699349e-04,length_median=8.964830000000000e-04,length_95%HPD={1.617496000000000e-04,1.901272000000000e-03}],33[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.632424e-04[&length_mean=2.355357347986928e-04,length_median=1.632424000000000e-04,length_95%HPD={3.201089000000000e-08,7.069152000000000e-04}],96[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.020721e-04[&length_mean=4.763263750100251e-04,length_median=4.020721000000000e-04,length_95%HPD={8.470578000000000e-06,1.117899000000000e-03}])[&prob=9.757032395680576e-01,prob_stddev=4.052990298242956e-03,prob_range={9.704039461405146e-01,9.802692974270097e-01},prob(percent)=\"98\",prob+-sd=\"98+-0\"]:4.002816e-04[&length_mean=4.808068709746122e-04,length_median=4.002816000000000e-04,length_95%HPD={1.266044000000000e-05,1.157662000000000e-03}])[&prob=9.794694040794560e-01,prob_stddev=3.699356599256568e-03,prob_range={9.770697240367950e-01,9.849353419544061e-01},prob(percent)=\"98\",prob+-sd=\"98+-0\"]:4.044784e-04[&length_mean=4.797105587174003e-04,length_median=4.044784000000000e-04,length_95%HPD={1.594002000000000e-05,1.142800000000000e-03}],(20[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.545127e-04[&length_mean=7.414056590574556e-04,length_median=6.545127000000000e-04,length_95%HPD={6.598113000000000e-05,1.610588000000000e-03}],35[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.554123e-04[&length_mean=7.357612339489018e-04,length_median=6.554123000000000e-04,length_95%HPD={6.987166000000000e-05,1.568309000000000e-03}])[&prob=9.213438208238902e-01,prob_stddev=9.145486141123836e-03,prob_range={9.093454206105852e-01,9.310758565524597e-01},prob(percent)=\"92\",prob+-sd=\"92+-1\"]:3.955568e-04[&length_mean=4.783294137852877e-04,length_median=3.955568000000000e-04,length_95%HPD={5.059896000000000e-06,1.152345000000000e-03}],24[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.052359e-04[&length_mean=4.823227247180385e-04,length_median=4.052359000000000e-04,length_95%HPD={6.526217000000000e-06,1.130480000000000e-03}],26[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.300166e-04[&length_mean=7.085551158455564e-04,length_median=6.300166000000000e-04,length_95%HPD={7.828044000000001e-05,1.501999000000000e-03}],27[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.722889e-04[&length_mean=2.488637336980471e-04,length_median=1.722889000000000e-04,length_95%HPD={3.794649000000000e-08,7.308953000000000e-04}],36[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.115742e-04[&length_mean=4.913393002596972e-04,length_median=4.115742000000000e-04,length_95%HPD={2.236099000000000e-05,1.158868000000000e-03}],37[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.374064e-04[&length_mean=7.083848018750847e-04,length_median=6.374064000000000e-04,length_95%HPD={5.145686000000000e-05,1.488262000000000e-03}],(39[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.725162e-04[&length_mean=2.441536222372232e-04,length_median=1.725162000000000e-04,length_95%HPD={2.527904000000000e-08,7.311384000000000e-04}],(64[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:3.945505e-04[&length_mean=4.658783162542667e-04,length_median=3.945505000000000e-04,length_95%HPD={8.749956000000001e-06,1.107212000000000e-03}],65[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:3.892931e-04[&length_mean=4.712233998761512e-04,length_median=3.892931000000000e-04,length_95%HPD={1.151709000000000e-05,1.144888000000000e-03}],66[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.756290e-04[&length_mean=2.457327251721422e-04,length_median=1.756290000000000e-04,length_95%HPD={9.387912000000000e-08,7.315110000000000e-04}])[&prob=9.954339421410479e-01,prob_stddev=2.523622781762759e-04,prob_range={9.950673243567524e-01,9.956005865884549e-01},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.059876e-04[&length_mean=4.785986786054168e-04,length_median=4.059876000000000e-04,length_95%HPD={9.566920000000000e-06,1.135952000000000e-03}],101[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.645868e-04[&length_mean=2.394560513886307e-04,length_median=1.645868000000000e-04,length_95%HPD={4.524528000000000e-08,7.155334000000000e-04}])[&prob=9.998000266631115e-01,prob_stddev=3.999466737768582e-04,prob_range={9.992001066524463e-01,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:9.058396e-04[&length_mean=9.753194673864928e-04,length_median=9.058396000000000e-04,length_95%HPD={2.037320000000000e-04,1.903124000000000e-03}],61[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.039909e-04[&length_mean=4.884675870383264e-04,length_median=4.039909000000000e-04,length_95%HPD={1.633269000000000e-06,1.169809000000000e-03}],63[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.683932e-04[&length_mean=2.435697312942522e-04,length_median=1.683932000000000e-04,length_95%HPD={3.928855000000000e-08,7.237705000000001e-04}])[&prob=9.918344220770563e-01,prob_stddev=5.681573746796439e-03,prob_range={9.842687641647780e-01,9.974670043994134e-01},prob(percent)=\"99\",prob+-sd=\"99+-1\"]:7.592030e-04[&length_mean=8.489978316781486e-04,length_median=7.592030000000000e-04,length_95%HPD={9.425047000000001e-05,1.799829000000000e-03}],31[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:2.789697e-04[&length_mean=3.664355685077312e-04,length_median=2.789697000000000e-04,length_95%HPD={9.684879000000000e-08,1.038503000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.409228e-03[&length_mean=4.486560560625240e-03,length_median=4.409228000000000e-03,length_95%HPD={2.444843000000000e-03,6.600494000000000e-03}],(((((29[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.110392e-03[&length_mean=1.195659682823954e-03,length_median=1.110392000000000e-03,length_95%HPD={3.197782000000000e-04,2.306243000000000e-03}],32[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.111013e-04[&length_mean=4.934283586891096e-04,length_median=4.111013000000000e-04,length_95%HPD={3.887744000000000e-06,1.156994000000000e-03}],34[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.028845e-04[&length_mean=4.736503860578380e-04,length_median=4.028845000000000e-04,length_95%HPD={1.304829000000000e-05,1.120313000000000e-03}],87[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.701912e-04[&length_mean=2.416209459012097e-04,length_median=1.701912000000000e-04,length_95%HPD={1.681466000000000e-08,7.087409000000000e-04}])[&prob=5.246967071057191e-01,prob_stddev=1.982009024944946e-02,prob_range={5.031329156112518e-01,5.420610585255299e-01},prob(percent)=\"52\",prob+-sd=\"52+-2\"]:3.691845e-04[&length_mean=4.495529154161344e-04,length_median=3.691845000000000e-04,length_95%HPD={1.333753000000000e-06,1.114341000000000e-03}],77[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.081114e-04[&length_mean=4.815088361721117e-04,length_median=4.081114000000000e-04,length_95%HPD={1.609098000000000e-05,1.111318000000000e-03}])[&prob=5.542594320757233e-01,prob_stddev=1.822266493589935e-02,prob_range={5.403279562724970e-01,5.805892547660312e-01},prob(percent)=\"55\",prob+-sd=\"55+-2\"]:3.740879e-04[&length_mean=4.546090835104388e-04,length_median=3.740879000000000e-04,length_95%HPD={2.108335000000000e-07,1.135927000000000e-03}],30[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.723206e-04[&length_mean=2.407346960881420e-04,length_median=1.723206000000000e-04,length_95%HPD={1.771384000000000e-08,6.973470000000000e-04}],38[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:8.748814e-04[&length_mean=9.680527170363954e-04,length_median=8.748814000000000e-04,length_95%HPD={1.939045000000000e-04,2.003639000000000e-03}],91[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.606404e-04[&length_mean=2.368723437368288e-04,length_median=1.606404000000000e-04,length_95%HPD={1.076586000000000e-08,7.083408000000000e-04}])[&prob=9.999000133315558e-01,prob_stddev=1.999733368884291e-04,prob_range={9.996000533262231e-01,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.499663e-04[&length_mean=7.221307837459055e-04,length_median=6.499663000000000e-04,length_95%HPD={7.161996000000001e-05,1.509420000000000e-03}],41[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.007791e-04[&length_mean=4.798668730495898e-04,length_median=4.007791000000000e-04,length_95%HPD={1.326511000000000e-05,1.136275000000000e-03}],(42[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:5.023634e-04[&length_mean=5.932247707962326e-04,length_median=5.023633999999999e-04,length_95%HPD={2.472351000000000e-05,1.393438000000000e-03}],68[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:9.043790e-03[&length_mean=9.168859010698516e-03,length_median=9.043789999999999e-03,length_95%HPD={6.327014000000000e-03,1.214078000000000e-02}],72[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:2.658920e-04[&length_mean=3.476470972051608e-04,length_median=2.658920000000000e-04,length_95%HPD={2.812844000000000e-08,9.580240000000000e-04}],73[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.395000e-04[&length_mean=7.172706602859606e-04,length_median=6.395000000000000e-04,length_95%HPD={6.583280000000000e-05,1.538732000000000e-03}],74[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.051757e-04[&length_mean=4.835184153280528e-04,length_median=4.051757000000000e-04,length_95%HPD={1.449522000000000e-05,1.137791000000000e-03}],75[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.136666e-04[&length_mean=4.834418101840012e-04,length_median=4.136666000000000e-04,length_95%HPD={7.600423000000000e-06,1.126110000000000e-03}],98[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:8.805501e-04[&length_mean=9.502557297803647e-04,length_median=8.805501000000001e-04,length_95%HPD={1.779595000000000e-04,1.906823000000000e-03}])[&prob=7.886615117984268e-01,prob_stddev=1.943006606898778e-02,prob_range={7.702972936941741e-01,8.128249566724437e-01},prob(percent)=\"79\",prob+-sd=\"79+-2\"]:3.853694e-04[&length_mean=4.618941051455552e-04,length_median=3.853694000000000e-04,length_95%HPD={1.479460000000000e-07,1.103156000000000e-03}])[&prob=9.742034395413945e-01,prob_stddev=7.059852891950508e-03,prob_range={9.642714304759366e-01,9.809358752166378e-01},prob(percent)=\"97\",prob+-sd=\"97+-1\"]:4.038421e-04[&length_mean=4.798706911982897e-04,length_median=4.038421000000000e-04,length_95%HPD={1.740685000000000e-05,1.156042000000000e-03}],69[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.483086e-04[&length_mean=7.225084149727666e-04,length_median=6.483086000000000e-04,length_95%HPD={9.173806000000000e-05,1.536828000000000e-03}],70[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.467148e-04[&length_mean=7.187240408168894e-04,length_median=6.467148000000000e-04,length_95%HPD={6.500689000000000e-05,1.507006000000000e-03}],71[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.322775e-04[&length_mean=7.070351550963233e-04,length_median=6.322775000000001e-04,length_95%HPD={8.891761000000000e-05,1.499914000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:5.948662e-03[&length_mean=6.046323846687103e-03,length_median=5.948662000000000e-03,length_95%HPD={3.713046000000000e-03,8.724361999999999e-03}])[&prob=9.999000133315558e-01,prob_stddev=1.276399290436512e-04,prob_range={9.997333688841488e-01,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.519317e-03[&length_mean=1.616776716351113e-03,length_median=1.519317000000000e-03,length_95%HPD={4.175973000000000e-04,3.122133000000000e-03}],(21[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.689005e-03[&length_mean=1.774160692167709e-03,length_median=1.689005000000000e-03,length_95%HPD={6.219815000000000e-04,3.155175000000000e-03}],(22[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:8.758932e-04[&length_mean=9.359915006075838e-04,length_median=8.758932000000001e-04,length_95%HPD={1.609538000000000e-04,1.857120000000000e-03}],56[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.110298e-04[&length_mean=4.867551236855965e-04,length_median=4.110298000000000e-04,length_95%HPD={6.093614000000000e-06,1.161413000000000e-03}])[&prob=7.528996133848820e-01,prob_stddev=1.297688463800279e-02,prob_range={7.360351953072923e-01,7.644314091454473e-01},prob(percent)=\"75\",prob+-sd=\"75+-1\"]:3.786072e-04[&length_mean=4.546856808615468e-04,length_median=3.786072000000000e-04,length_95%HPD={9.620158000000000e-07,1.091422000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.974762e-03[&length_mean=5.054589494134122e-03,length_median=4.974762000000000e-03,length_95%HPD={2.941968000000000e-03,7.339403000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.787678e-03[&length_mean=4.851770360318654e-03,length_median=4.787678000000000e-03,length_95%HPD={2.495343000000000e-03,7.294482000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:5.773022e-03[&length_mean=5.857002869983994e-03,length_median=5.773022000000000e-03,length_95%HPD={3.116102000000000e-03,8.686048000000000e-03}],(((78[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.677418e-04[&length_mean=2.444771491727322e-04,length_median=1.677418000000000e-04,length_95%HPD={3.544103000000000e-08,7.277246000000000e-04}],(82[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:3.956773e-04[&length_mean=4.796176214796681e-04,length_median=3.956773000000000e-04,length_95%HPD={1.308715000000000e-05,1.158955000000000e-03}],85[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.682508e-04[&length_mean=2.437625237189042e-04,length_median=1.682508000000000e-04,length_95%HPD={3.030997000000000e-08,7.349962000000000e-04}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.467646e-04[&length_mean=7.296503401786404e-04,length_median=6.467646000000000e-04,length_95%HPD={7.301457000000000e-05,1.568198000000000e-03}])[&prob=9.639714704706039e-01,prob_stddev=5.836902665864670e-03,prob_range={9.589388081589122e-01,9.724036795093988e-01},prob(percent)=\"96\",prob+-sd=\"96+-1\"]:4.144170e-04[&length_mean=4.935054531372611e-04,length_median=4.144170000000000e-04,length_95%HPD={8.468325999999999e-06,1.163266000000000e-03}],((83[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:3.938615e-04[&length_mean=4.748793623288206e-04,length_median=3.938615000000000e-04,length_95%HPD={9.531803000000001e-06,1.130163000000000e-03}],86[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.101799e-04[&length_mean=4.861958636535438e-04,length_median=4.101799000000000e-04,length_95%HPD={1.000996000000000e-05,1.180262000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:8.737473e-04[&length_mean=9.472269446303794e-04,length_median=8.737473000000000e-04,length_95%HPD={1.759574000000000e-04,1.875899000000000e-03}],84[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:8.576574e-04[&length_mean=9.463889830615864e-04,length_median=8.576574000000000e-04,length_95%HPD={1.577971000000000e-04,1.901916000000000e-03}],(88[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.681422e-04[&length_mean=2.408792396746413e-04,length_median=1.681422000000000e-04,length_95%HPD={1.050247000000000e-08,7.315366000000000e-04}],89[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.734492e-04[&length_mean=2.459166596653934e-04,length_median=1.734492000000000e-04,length_95%HPD={1.552492000000000e-08,7.286516000000000e-04}],92[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.589898e-04[&length_mean=2.362497601566338e-04,length_median=1.589898000000000e-04,length_95%HPD={7.372769000000000e-08,7.099537000000000e-04}])[&prob=9.840354619384082e-01,prob_stddev=4.391157606741586e-03,prob_range={9.790694574056792e-01,9.892014398080256e-01},prob(percent)=\"98\",prob+-sd=\"98+-0\"]:3.963884e-04[&length_mean=4.756233988658016e-04,length_median=3.963884000000000e-04,length_95%HPD={5.010572000000000e-06,1.142350000000000e-03}],93[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.142675e-03[&length_mean=1.214464703716168e-03,length_median=1.142675000000000e-03,length_95%HPD={3.106121000000000e-04,2.285243000000000e-03}])[&prob=9.568390881215838e-01,prob_stddev=2.025855544711956e-03,prob_range={9.541394480735902e-01,9.589388081589122e-01},prob(percent)=\"96\",prob+-sd=\"96+-0\"]:3.944639e-04[&length_mean=4.796610162469966e-04,length_median=3.944639000000000e-04,length_95%HPD={4.669523000000000e-06,1.135909000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:2.613926e-03[&length_mean=2.691023812518337e-03,length_median=2.613926000000000e-03,length_95%HPD={1.137685000000000e-03,4.376386000000000e-03}],(79[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:8.836801e-04[&length_mean=9.709753774620095e-04,length_median=8.836801000000000e-04,length_95%HPD={1.862992000000000e-04,1.955559000000000e-03}],((80[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.422716e-04[&length_mean=7.211715554452702e-04,length_median=6.422716000000000e-04,length_95%HPD={7.440137999999999e-05,1.562380000000000e-03}],81[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.646258e-04[&length_mean=7.466862152373038e-04,length_median=6.646258000000000e-04,length_95%HPD={7.928462000000000e-05,1.592689000000000e-03}])[&prob=9.964004799360084e-01,prob_stddev=2.475030052107603e-03,prob_range={9.932009065457938e-01,9.986668444207439e-01},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:5.318315e-04[&length_mean=6.190280436143280e-04,length_median=5.318315000000000e-04,length_95%HPD={2.341030000000000e-05,1.412319000000000e-03}],90[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.375762e-03[&length_mean=1.450464103102919e-03,length_median=1.375762000000000e-03,length_95%HPD={4.066308000000000e-04,2.626253000000000e-03}])[&prob=9.840354619384082e-01,prob_stddev=1.486034982642856e-03,prob_range={9.826689774696707e-01,9.861351819757366e-01},prob(percent)=\"98\",prob+-sd=\"98+-0\"]:5.143125e-04[&length_mean=5.983789437787951e-04,length_median=5.143125000000000e-04,length_95%HPD={1.151804000000000e-05,1.354124000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:3.125448e-03[&length_mean=3.203830031805733e-03,length_median=3.125448000000000e-03,length_95%HPD={1.482303000000000e-03,5.049017000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:5.699802e-03[&length_mean=5.810016320390573e-03,length_median=5.699802000000000e-03,length_95%HPD={3.312144000000000e-03,8.706360999999999e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.319823e-02[&length_mean=1.330024495427286e-02,length_median=1.319823000000000e-02,length_95%HPD={8.715664999999999e-03,1.782668000000000e-02}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.857504e-02[&length_mean=1.867899597860293e-02,length_median=1.857504000000000e-02,length_95%HPD={1.275419000000000e-02,2.447803000000000e-02}],104[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.439121e-02[&length_mean=6.449995901479676e-02,length_median=6.439121000000000e-02,length_95%HPD={5.475674000000000e-02,7.472280000000001e-02}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.196169e-02[&length_mean=1.214097522940265e-02,length_median=1.196169000000000e-02,length_95%HPD={7.044229000000000e-03,1.750589000000000e-02}])[&prob=7.140381282495667e-01,prob_stddev=7.760599292917216e-03,prob_range={7.076389814691374e-01,7.239034795360618e-01},prob(percent)=\"71\",prob+-sd=\"71+-1\"]:4.034893e-03[&length_mean=4.245973118681845e-03,length_median=4.034893000000000e-03,length_95%HPD={7.216214000000000e-04,8.155646000000001e-03}],((((((3[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.767551e-03[&length_mean=4.880657167011049e-03,length_median=4.767551000000000e-03,length_95%HPD={2.771551000000000e-03,7.173621000000000e-03}],4[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:3.495889e-04[&length_mean=4.557593776520010e-04,length_median=3.495889000000000e-04,length_95%HPD={2.605841000000000e-07,1.216950000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.108122e-02[&length_mean=1.116947736968411e-02,length_median=1.108122000000000e-02,length_95%HPD={7.605603000000000e-03,1.481174000000000e-02}],(12[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.993911e-02[&length_mean=2.002721223136900e-02,length_median=1.993911000000000e-02,length_95%HPD={1.545131000000000e-02,2.490270000000000e-02}],13[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.509195e-02[&length_mean=1.518778432652309e-02,length_median=1.509195000000000e-02,length_95%HPD={1.089002000000000e-02,1.937566000000000e-02}])[&prob=9.997333688841488e-01,prob_stddev=1.088516972307388e-04,prob_range={9.996000533262231e-01,9.998666844420744e-01},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:2.474750e-03[&length_mean=2.591456993829153e-03,length_median=2.474750000000000e-03,length_95%HPD={6.397801000000000e-04,4.554593000000000e-03}])[&prob=9.586721770430610e-01,prob_stddev=3.996503071753466e-03,prob_range={9.530729236101854e-01,9.621383815491268e-01},prob(percent)=\"96\",prob+-sd=\"96+-0\"]:2.405978e-03[&length_mean=2.563098639077335e-03,length_median=2.405978000000000e-03,length_95%HPD={3.828833000000000e-04,4.696069000000000e-03}],(8[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:2.627936e-03[&length_mean=2.761247603079587e-03,length_median=2.627936000000000e-03,length_95%HPD={1.088326000000000e-03,4.708048000000000e-03}],11[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:5.578457e-03[&length_mean=5.672043116851089e-03,length_median=5.578457000000000e-03,length_95%HPD={3.321915000000000e-03,8.272396000000000e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.623198e-02[&length_mean=1.633286353369564e-02,length_median=1.623198000000000e-02,length_95%HPD={1.210358000000000e-02,2.091678000000000e-02}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.254921e-03[&length_mean=6.375803925463276e-03,length_median=6.254921000000000e-03,length_95%HPD={2.873486000000000e-03,1.031156000000000e-02}],(6[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.145205e-02[&length_mean=1.160531602196375e-02,length_median=1.145205000000000e-02,length_95%HPD={8.053004000000001e-03,1.545023000000000e-02}],10[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.653762e-02[&length_mean=1.662879457082388e-02,length_median=1.653762000000000e-02,length_95%HPD={1.236350000000000e-02,2.147759000000000e-02}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.199724e-02[&length_mean=1.210016685441945e-02,length_median=1.199724000000000e-02,length_95%HPD={7.599532000000000e-03,1.663637000000000e-02}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:6.230100e-02[&length_mean=6.248493786728445e-02,length_median=6.230100000000000e-02,length_95%HPD={5.158393000000000e-02,7.379197000000000e-02}],(5[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:4.943148e-03[&length_mean=5.039288559375459e-03,length_median=4.943148000000000e-03,length_95%HPD={2.150515000000000e-03,7.886641999999999e-03}],7[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:5.767556e-03[&length_mean=5.836397278096195e-03,length_median=5.767556000000000e-03,length_95%HPD={2.904217000000000e-03,8.948062999999999e-03}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:5.000414e-02[&length_mean=5.008516978702834e-02,length_median=5.000414000000000e-02,length_95%HPD={3.971990000000000e-02,6.005726000000000e-02}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:1.759561e-02[&length_mean=1.788367992541012e-02,length_median=1.759561000000000e-02,length_95%HPD={1.032215000000000e-02,2.564159000000000e-02}],9[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:7.463525e-02[&length_mean=7.478535756032610e-02,length_median=7.463525000000000e-02,length_95%HPD={6.203630000000000e-02,8.767021000000000e-02}])[&prob=1.000000000000000e+00,prob_stddev=0.000000000000000e+00,prob_range={1.000000000000000e+00,1.000000000000000e+00},prob(percent)=\"100\",prob+-sd=\"100+-0\"]:7.923954e-02[&length_mean=7.935199156045922e-02,length_median=7.923954000000000e-02,length_95%HPD={6.549944000000001e-02,9.239187000000000e-02}])[&prob=5.562924943340888e-01,prob_stddev=1.380288237169630e-02,prob_range={5.428609518730836e-01,5.699240101319824e-01},prob(percent)=\"56\",prob+-sd=\"56+-1\"]:3.264752e-03[&length_mean=3.589768101536441e-03,length_median=3.264752000000000e-03,length_95%HPD={2.364439000000000e-05,7.760774000000000e-03}]);end;\n";
		
		String nexFilePath = "/Users/andy/Documents/TSCreator/EvolutionaryTree/Fordham and Zehady shared/180724/mmc2/mb_A1_SFig02_nmt.nex.con.nwk";
		String nexNN = T.loadTreeFromFile(nexFilePath);
		String newNwk = T.convertNexusToNewickFormat(nexNN);

		//String newickFilePath = "/Users/andy/Documents/TSCreator/EvolutionaryTree/Fordham and Zehady shared/180724/mmc2/fig02.nwk";
		//String newNwk = T.loadTreeFromFile(newickFilePath);
		T.print(newNwk);
		T.print("----------------------------");
		
		String tsfN = T.convertNewickToTSC(newNwk);
		T.print(tsfN);
		T.print("--------------------------");
		
		try {
			String fpathN = "/Users/andy/Desktop/treeRealTSC.txt";
			BufferedWriter writer = new BufferedWriter(new FileWriter(fpathN));
		    writer.write(tsfN);
		     
		    writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
}
