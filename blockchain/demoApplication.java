package blockchain;

import java.util.ArrayList;
import java.util.Date;

public class demoApplication {
	static int targetDepth = 5;
	static String target = "00000";
	static ArrayList<String> previous_addr = new ArrayList<>();
	public static String makeHashData(String data) {
        return StringUtil.getSha256(data);
    }
	public static String makeHashBlock(String previousHash, long timeStamp, String data, int nonce) {

        return StringUtil.getSha256(
                    previousHash +
                    Long.toString(timeStamp) +
                    data +
                    Integer.toString(nonce)
        );
    }
	 private static String mineNewBlock(String previousHash, long timeStamp, String data, int nonce){
	        // 조건에 맞는 Hash 값을 찾을 때까지 계속 반복한다.
	        while(true) {	            
	        	String hash = makeHashBlock(previousHash, timeStamp, data, nonce);
	        	if(hash.substring(0, targetDepth).equals(target)) {
	            	block new_block =new block(hash, previousHash, timeStamp, data, nonce); 
	            	blockRepository.add(new_block);
	            	previous_addr.add(hash);
	            	int temp = Integer.parseInt(data);
	            	temp+=1;
	            	data = Integer.toString(temp);
	            	break;
	        	}
            	nonce ++;
	        }
	        return data;
	 }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String s = StringUtil.getSha256("Genesis Block");
		long timeStamp = new Date().getTime();
		block b = new block(s,"",timeStamp,"Genesis Block",0);
		blockRepository.add(b);
		int nonce = 0;
		String cnt = "1";
		String hash = b.getHash();
		previous_addr.add(hash);
		for(int i=0; i<=5; i++) {
			String pre = previous_addr.get(previous_addr.size()-1);
			cnt = mineNewBlock(pre,timeStamp,cnt,nonce);
		}
		ArrayList<block> block_repository = blockRepository.findAllBlockChain();
		for(int i=0; i<block_repository.size(); i++) {
			System.out.println(block_repository.get(i));
		}
	}
}
