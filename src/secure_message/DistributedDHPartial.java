package secure_message;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class DistributedDHPartial implements Serializable {
	public final static BigInteger p = new BigInteger("93230951774919133788312974160275410031");
	public final static BigInteger g = new BigInteger("7594284192988171614460672999940993755");

	private BigInteger partialKey;
	private Set<BigInteger> contribtingClients;
	private static final long serialVersionUID = 1L;

	public DistributedDHPartial() {
		this.partialKey = g;
		this.contribtingClients = new HashSet<BigInteger>();
	}

	public void applyPrivateKey(BigInteger privateKey, BigInteger clientId) {
		partialKey = partialKey.modPow(privateKey, p);
		contribtingClients.add(clientId);
	}

	public boolean hasClientContributed(BigInteger clientId) {
		return contribtingClients.contains(clientId);
	}

	public BigInteger getValue() {
		return partialKey;
	}
}
