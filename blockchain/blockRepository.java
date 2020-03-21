package blockchain;

import java.util.ArrayList;

public class blockRepository {
	 private static ArrayList<block> blockChain = new ArrayList<>();
	 
	 
	 public static ArrayList<block> findAllBlockChain() {
	        return blockChain;
	 }
	 public static void add(block temp) {
		 blockChain.add(temp);
	 }
}
