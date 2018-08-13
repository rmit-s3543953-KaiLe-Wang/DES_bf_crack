import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DES_BF {
	static int[] targetInDec=new int[26];
	
	public static void outConsole(String decryptedMessage,String key){
		System.out.println("Decrypted message: "+decryptedMessage+"\t"+". With key value of: 0x"+key);
	}
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len-1; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) +Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	public static String toHex(String text) throws UnsupportedEncodingException
	{
	    byte[] myBytes = text.getBytes(/*"UTF-8"*/);
	    return DatatypeConverter.printHexBinary(myBytes);
	}
	public static byte[] toByteArray(String s) {
		if(s.length()%2>0)
		{
			s="0"+s;
		}
	    return DatatypeConverter.parseHexBinary(s);
	}
	private static byte[] decrypt(byte[] rawKey, byte[] encrypted) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "DES");
		Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}
	public static byte[] makeSMBKey(byte[] key7, byte[] key8) {

	    int i;

	    key8[0] = (byte) ( ( key7[0] >> 1) & 0xff);
	    key8[1] = (byte)(( ((key7[0] & 0x01) << 6) | (((key7[1] & 0xff)>>2) & 0xff)) & 0xff );
	    key8[2] = (byte)(( ((key7[1] & 0x03) << 5) | (((key7[2] & 0xff)>>3) & 0xff)) & 0xff );
	    key8[3] = (byte)(( ((key7[2] & 0x07) << 4) | (((key7[3] & 0xff)>>4) & 0xff)) & 0xff );
	    key8[4] = (byte)(( ((key7[3] & 0x0F) << 3) | (((key7[4] & 0xff)>>5) & 0xff)) & 0xff );
	    key8[5] = (byte)(( ((key7[4] & 0x1F) << 2) | (((key7[5] & 0xff)>>6) & 0xff)) & 0xff );
	    key8[6] = (byte)(( ((key7[5] & 0x3F) << 1) | (((key7[6] & 0xff)>>7) & 0xff)) & 0xff );
	    key8[7] = (byte)(key7[6] & 0x7F);
	    for (i=0;i<8;i++) {
	        key8[i] = (byte)( key8[i] << 1);
	    }
	    return key8;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*1. set cyphertext and then convert to byte*/
		String cypherTxtOrigin="ce126d2ddf2d1e64";
		byte[] cipherTxtByte=hexStringToByteArray(cypherTxtOrigin);
		/*
		for(int i =0; i <cipherTxtByte.length;i++)
			System.out.print((cipherTxtByte[i]&0xff)+((i==cipherTxtByte.length-1)?"":","));
		*/
		/*2.set key range also post-fix that's available*/
		int postfix = Integer.parseInt("F666",16);
		String post = Integer.toString(postfix);
		System.out.println(post);
		BigInteger keyMin=new BigInteger("0000000000");
		//System.out.println(keyMin.toString(16));
		BigInteger keyFix = new BigInteger(post);
		BigInteger keyMax=new BigInteger("1099511627776");
		BigInteger keyHalf = new BigInteger("549755813888");
		BigInteger keyQuat = new BigInteger("274877906944");
		/*3. convert them to Hex string*/
		String KeyMinHex=keyMin.toString(16);
		String KeyMaxHex=keyMax.toString(16);
		System.out.println(KeyMaxHex+" L="+KeyMaxHex.length());
		String KeyHalfHex=keyHalf.toString(16);
		String KeyQuatHex=keyQuat.toString(16);
		String fixHex=keyFix.toString(16);
		/*
		 * Note for displaying bytes. Java uses signed digit.
		 * So that the range for displaying is: 0,1,2,3,4...127,-128,-127,-126.....-2,-1.
		 * */
		//System.out.println(Arrays.toString(toByteArray(KeyMaxHex)));
		/*4. convert to byte and then implement*/
		byte[] keyMaxByte=toByteArray(KeyQuatHex);
		byte[] keyAttempted =toByteArray("0000000000");
		byte fixedB[] = toByteArray(fixHex);
		//while(keyMin.compareTo(keyQuat)!=0)
		try {	
			
			for (int i=0; i <10;i++)
			{
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
				keyAttempted =toByteArray(keyMin.toString());
				if(keyAttempted.length<6)
				{
					byte[] temp = new byte[5];
					System.arraycopy(keyAttempted, 0, temp, 0, keyAttempted.length);
					keyAttempted=temp;
				}
				//key+postfix
				byte[] key = new byte[keyAttempted.length + fixedB.length];
				System.arraycopy(fixedB, 0, key, 0, fixedB.length);
				System.arraycopy(keyAttempted, 0, key, fixedB.length, keyAttempted.length);
				
				byte[] key8 = new byte[8];
				key=makeSMBKey(key, key8 );
				System.out.println("keyat: "+key.length+"fix: "+fixedB.length+"key: "+key.length);
				//decrypt
				byte[] dbyte = decrypt(key,cipherTxtByte);
				//increment
				keyMin=keyMin.add(BigInteger.ONE);
				String decryptedMessage = new String(dbyte);
				outConsole(decryptedMessage,Arrays.toString(key));
			}
		}
		 catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
}
}

