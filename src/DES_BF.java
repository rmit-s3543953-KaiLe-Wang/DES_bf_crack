import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class DES_BF {
	static int[] targetInDec=new int[26];
	
	public static void outConsole(String decryptedMessage,String key){
		System.out.println("Decrypted message: "+decryptedMessage+"\t"+". With key value of: 0x"+key);
	}
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	public static String toHex(String text) throws UnsupportedEncodingException
	{
	    byte[] myBytes = text.getBytes(/*"UTF-8"*/);

	    return DatatypeConverter.printHexBinary(myBytes);
	}
	private static byte[] decrypt(byte[] rawKey, byte[] encrypted) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "DES");
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String cypherTxtOrigin="ce126d2ddf2d1e64";
		byte[] cipherTxtByte=hexStringToByteArray(cypherTxtOrigin);
		/*
		 * test1: see if conversion works.
		 * 
		for(int i =0; i <cipherTxtByte.length;i++)
			System.out.print((cipherTxtByte[i]&0xff)+((i==cipherTxtByte.length-1)?"":","));
		*/
		int keyMin=0;
		int keyMax=0;
		//BigInteger attemptKey=new BigInteger("0");
		String attemptKeyStr;
		try {
			for(int i =0; i < 10;i++)
			{
				
				attemptKeyStr=Integer.toString(0);
				byte[] rawKey=hexStringToByteArray(attemptKeyStr);
				byte[] dbyte;
				dbyte = decrypt(rawKey,cipherTxtByte);
				String decryptedMessage = new String(dbyte);
				outConsole(decryptedMessage,attemptKeyStr);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
public static String base26(int num) {
	  if (num < 0) {
		    throw new IllegalArgumentException("Only positive numbers are supported");
		  }
		  StringBuilder s = new StringBuilder("aaaaaaa");
		  for (int pos = 6; pos >= 0 && num > 0 ; pos--) {
		    char digit = (char) ('a' + num % 26);
		    s.setCharAt(pos, digit);
		    num = num / 26;
		  }
		  return s.toString();
		}
}
