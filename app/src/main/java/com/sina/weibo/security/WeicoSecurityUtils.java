package com.sina.weibo.security;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class WeicoSecurityUtils
{
  private static final String KEY_ALGORITHM = "RSA";
  private static final String KEY_CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
  private static final int MAX_DECRYPT_BLOCK = 128;
  private static final int MAX_ENCRYPT_BLOCK = 117;
  private static String publicKeyInner = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDWcQcgj60fU8fFev9RlvFPg0GcRgHyGFTN9ytE\nLujvfCwGt7n54Q9+k1rDDo+sRQeYdTwA7ZMS8n1RHjZATgmHw9rBBzk/cHXAVIgrJrZ5txDdW1i4\n8ZxEarcdSrmlk9ZFSsvGXE8/0fZYHM0mr4WaIh2y9E0CNkd0rU9VKAR9RQIDAQAB";
  private static final String publicKeyString = "iMxVDGf9f5Z3P3NsFac7tM7SC6DZDJY+H/vXc+xv3HlT2E/LUzWf5fct2P0VauekLzNAaNsH93SZ\n2Z3jUc/0x81FLThPwI8cexCuRT7P1bdnmcwhjZmW3Lc1FCu2K6iBuVQ9I51TR9eTU2lNcq4AW8WV\nEWtwIj6EpLFzQ3qOm3AY4UNgcGrNYYBbF+SiUkchdXbxYRBNFkguDiayaJzMC/5WmTrEnQ0xXwmy\nA2lWpZ6+sUlyDRU/HvPh5Oto0xpuLc6bIjfl0b+PSjxh5e/7/4jXoYoUfdm3r2FtPKJtQ2NeKnsp\nOCdk6HNULtk5WSnkBKjufQqoZblvdrEiixnogQ";
  String source = "RSA/ECB/PKCS1Padding";

  public static String decode(String paramString)
    throws Exception
  {
    return new String(decryptByPublicKey(Base64.decode(paramString, 1), publicKeyInner));
  }

  public static byte[] decryptByPublicKey(byte[] paramArrayOfByte, String paramString)
    throws Exception
  {
    X509EncodedKeySpec localX509EncodedKeySpec = new X509EncodedKeySpec(Base64.decode(paramString, 1));
    PublicKey localPublicKey = KeyFactory.getInstance("RSA").generatePublic(localX509EncodedKeySpec);
    Cipher localCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    localCipher.init(2, localPublicKey);
    int i = paramArrayOfByte.length;
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    int j = 0;
    int k = 0;
    if (i - j > 0)
    {
      if (i - j > 128);
      for (byte[] arrayOfByte2 = localCipher.doFinal(paramArrayOfByte, j, 128); ; arrayOfByte2 = localCipher.doFinal(paramArrayOfByte, j, i - j))
      {
        localByteArrayOutputStream.write(arrayOfByte2, 0, arrayOfByte2.length);
        k++;
        j = k * 128;
        break;
      }
    }
    byte[] arrayOfByte1 = localByteArrayOutputStream.toByteArray();
    localByteArrayOutputStream.close();
    return arrayOfByte1;
  }

  private static byte[] encryptByPublicKey(byte[] paramArrayOfByte, String paramString)
    throws Exception
  {
    X509EncodedKeySpec localX509EncodedKeySpec = new X509EncodedKeySpec(Base64.decode(paramString.getBytes(), 2));
    PublicKey localPublicKey = KeyFactory.getInstance("RSA").generatePublic(localX509EncodedKeySpec);
    Cipher localCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    localCipher.init(1, localPublicKey);
    int i = paramArrayOfByte.length;
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    int j = 0;
    int k = 0;
    if (i - j > 0)
    {
      if (i - j > 117);
      for (byte[] arrayOfByte2 = localCipher.doFinal(paramArrayOfByte, j, 117); ; arrayOfByte2 = localCipher.doFinal(paramArrayOfByte, j, i - j))
      {
        localByteArrayOutputStream.write(arrayOfByte2, 0, arrayOfByte2.length);
        k++;
        j = k * 117;
        break;
      }
    }
    byte[] arrayOfByte1 = localByteArrayOutputStream.toByteArray();
    localByteArrayOutputStream.close();
    return arrayOfByte1;
  }

  public static String securityPsd(String paramString)
  {
    byte[] arrayOfByte = paramString.getBytes();
    try
    {
      String str = new String(Base64.encode(encryptByPublicKey(arrayOfByte, decode("iMxVDGf9f5Z3P3NsFac7tM7SC6DZDJY+H/vXc+xv3HlT2E/LUzWf5fct2P0VauekLzNAaNsH93SZ\n2Z3jUc/0x81FLThPwI8cexCuRT7P1bdnmcwhjZmW3Lc1FCu2K6iBuVQ9I51TR9eTU2lNcq4AW8WV\nEWtwIj6EpLFzQ3qOm3AY4UNgcGrNYYBbF+SiUkchdXbxYRBNFkguDiayaJzMC/5WmTrEnQ0xXwmy\nA2lWpZ6+sUlyDRU/HvPh5Oto0xpuLc6bIjfl0b+PSjxh5e/7/4jXoYoUfdm3r2FtPKJtQ2NeKnsp\nOCdk6HNULtk5WSnkBKjufQqoZblvdrEiixnogQ")), 2));
      return str;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
}

/* Location:           C:\Users\Caij\Desktop\com.eico.weico_4.6.0_460_classes_dex2jar.jar
 * Qualified Name:     com.sina.weibo.security.WeicoSecurityUtils
 * JD-Core Version:    0.6.0
 */