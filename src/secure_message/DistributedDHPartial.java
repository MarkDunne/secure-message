package secure_message;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class DistributedDHPartial implements Serializable {
	public final static BigInteger p = new BigInteger("97926039674283420617920074898555811232815470289823327318349147792084789067861");
	public final static BigInteger g = new BigInteger("20365849262411751799619042098103950515763488105130003440224273615278141546108");

	private BigInteger partialKey;
	private Set<Integer> contribtingClients;
	private static final long serialVersionUID = 1L;

	public DistributedDHPartial(Integer numClients) {
		this.partialKey = g;
		this.contribtingClients = new HashSet<Integer>();
	}

	public void applyPrivateKey(BigInteger privateKey, Integer clientId) {
		partialKey = partialKey.modPow(privateKey, p);
		contribtingClients.add(clientId);
	}

	public boolean hasClientContributed(Integer clientId) {
		return contribtingClients.contains(clientId);
	}

	public BigInteger getPartialValue() {
		return partialKey;
	}
}
