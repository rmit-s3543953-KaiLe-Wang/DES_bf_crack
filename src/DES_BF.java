import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.*;
import java.io.UnsupportedEncodingException;
import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DES_BF {
	static int[] targetInDec=new int[26];
	static int files=0;
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	static int counter=0;
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	/*
	public static void outConsole(String decryptedMessage,String key,PrintWriter writer){
		
		//System.out.println("Decrypted message: "+decryptedMessage+"\t"+". With key value of: 0x"+key.toString());
	}*/
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
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("How many file this time:)? ");
		Scanner s=new Scanner(System.in);
		files=s.nextInt();
		try{
		File readCount = new File("count.txt");
		Scanner sc = new Scanner(readCount);
		while(sc.hasNextInt())
		{
			counter =sc.nextInt();
			files=files+counter;
		}
		sc.close();
		System.out.println("continue from count:"+counter);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		BigInteger[] dataIn=new BigInteger[100000];
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
		//System.out.println(post);
		BigInteger keyMin=new BigInteger("0000000000");
		BigInteger keyFix = new BigInteger(post);
		BigInteger step = new BigInteger("10995116");
		BigInteger keyMax=new BigInteger("1099511627776");
		for (int i =0;i<4-2;i++)
		{
			dataIn[i]=BigInteger.ZERO;
			BigInteger mult= new BigInteger(String.valueOf(i+1));
			dataIn[i]=dataIn[i].add(step.multiply(mult));
			//System.out.println(dataIn[i]);
		}
		dataIn[99999]=keyMax;
		/*
		BigInteger keyHalf = new BigInteger("549755813888");
		BigInteger keyQuat = new BigInteger("274877906944");
		BigInteger keyTenth= new BigInteger("109951162778");
		*/
		/*3. convert them to Hex string*/
		//String KeyMinHex=keyMin.toString(16);
		String KeyMaxHex=keyMax.toString(16);
		//System.out.println(KeyMaxHex+" L="+KeyMaxHex.length());
		/*
		 * String KeyHalfHex=keyHalf.toString(16);
		   String KeyQuatHex=keyQuat.toString(16);
		*/
		String fixHex=keyFix.toString(16);
		/*
		 * Note for displaying bytes. Java uses signed digit.
		 * So that the range for displaying is: 0,1,2,3,4...127,-128,-127,-126.....-2,-1.
		 * */
		//System.out.println(Arrays.toString(toByteArray(KeyMaxHex)));
		/*4. convert to byte and then implement*/
		byte[] keyAttempted =toByteArray("0000000000");
		byte fixedB[] = toByteArray(fixHex);
		try {	
			for(int i=counter;i<dataIn.length;i++)
			{
				System.out.println(counter);
				keyMin=counter==0?BigInteger.ZERO:dataIn[i];
				String filename="Decrypted"+counter+".txt";
				PrintWriter writer = new PrintWriter(filename, "UTF-8");
				//while(keyMin.compareTo(dataIn[i+1])<=0)
					for(int x =0;x<3;x++)
				{
					long keyVal=keyMin.longValue();
					keyAttempted =toByteArray(Long.toHexString(keyVal));//convert lower bound from int to hex.
					//Note: all 0's will be discarded if the number is small.
					if(keyAttempted.length<5)
					{
						byte[] temp = new byte[5];//The maximum length is 6.
						System.arraycopy(keyAttempted, 0, temp, 5-keyAttempted.length, keyAttempted.length);
						keyAttempted=temp;
						System.out.println("original Key: "+bytesToHex(keyAttempted));
					}
					//key+postfix
					byte[] key = new byte[keyAttempted.length + fixedB.length];
					System.arraycopy(keyAttempted, 0, key, 1, keyAttempted.length);
					System.arraycopy(fixedB, 0, key, keyAttempted.length+1, fixedB.length);
					System.out.println("keyat: "+key.length+"fix: "+fixedB.length+"key length: "+key.length+" key:"+bytesToHex(key));
					//decrypt
					byte[] dbyte = decrypt(key,cipherTxtByte);
					//increment
					keyMin=keyMin.add(BigInteger.ONE);
					String decryptedMessage = new String(dbyte);
					writer.println("T:"+decryptedMessage+"\t"+"O:"+bytesToHex(dbyte)+"\t"+"K:0x"+bytesToHex(key));
				}
					break;
					/*
				System.out.println("One file compeleted! The file name is:"+filename);
				counter++;
				writer.close();
				if(counter ==files)
					break;*/
			}
		}
		 catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		finally{
			PrintWriter writer;
			try {
				writer = new PrintWriter("count.txt", "UTF-8");
				writer.print(counter);
				writer.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Program has terminated. Current count is: "+counter);
		}
}
}

