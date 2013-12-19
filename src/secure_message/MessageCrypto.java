package secure_message;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import distributed_diffe_hellman.DistributedDHPartial;
import messages.RoomMessage.RoomUserMessage;

public class MessageCrypto {
	
	private final static BigInteger INIT_AESIV = new BigInteger("76547383930716012190582141440001525853");
	
	private Cipher AEScipher;
	private SecretKeySpec AESKey;
	private BigInteger AESIV;
	private IvParameterSpec AESIVParameterSpec;
	
	public MessageCrypto() {
		try {
			AESIV = INIT_AESIV; 
			AEScipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}
	
	public void updateIV(){
		AESIV = AESIV.add(BigInteger.ONE);
		AESIVParameterSpec = new IvParameterSpec(AESIV.toByteArray());
	}
	
	public void setKey(DistributedDHPartial sharedKey){
		AESIV = INIT_AESIV;
		AESIVParameterSpec = new IvParameterSpec(AESIV.toByteArray());
		AESKey = new SecretKeySpec(sharedKey.getValue().toByteArray(), "AES");
	}
	
	public RoomUserMessage encrypt(RoomUserMessage message){
		try {
			AEScipher.init(Cipher.ENCRYPT_MODE, AESKey, AESIVParameterSpec);
			final byte[] encryptedContent = AEScipher.doFinal(message.getContent());
			message.setContent(encryptedContent);
			return message;
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public <T extends RoomUserMessage> T decrypt(T message){
		try {
			AEScipher.init(Cipher.DECRYPT_MODE, AESKey, AESIVParameterSpec);
			final byte[] encryptedContent = AEScipher.doFinal(message.getContent());
			message.setContent(encryptedContent);
			return message;
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}	
		return null;
	}
}
