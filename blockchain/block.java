package blockchain;

import java.util.Date;
public class block {
	
    private String hash;
    private String previousHash;
    private String data; //Transaction
    private long timeStamp;
    
    @Override
	public String toString() {
		return "block [hash=" + hash + ", previousHash=" + previousHash + ", data=" + data + ", timeStamp=" + timeStamp
				+ ", nonce=" + nonce + "]";
	}


	public String getHash() {
		return hash;
	}


	public void setHash(String hash) {
		this.hash = hash;
	}


	public String getPreviousHash() {
		return previousHash;
	}


	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}


	public String getData() {
		return data;
	}


	public void setData(String data) {
		this.data = data;
	}


	public long getTimeStamp() {
		return timeStamp;
	}


	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}


	public int getNonce() {
		return nonce;
	}


	public void setNonce(int nonce) {
		this.nonce = nonce;
	}


	private int nonce;
   
    
    public block(String hash, String previousHash, long timeStamp,  String data, int nonce) {
        this.data = data;
        this.hash = hash;
        this.nonce = nonce;
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
    }
}
