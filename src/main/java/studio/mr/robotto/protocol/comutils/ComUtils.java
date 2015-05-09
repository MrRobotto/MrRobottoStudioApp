/*
 * MrRobotto Engine
 * Copyright (c) 2015, Aarón Negrín, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package studio.mr.robotto.protocol.comutils;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComUtils {

    private static int int32ToBytes(int number,byte bytes[], String endianess)
    {
        if("be".equals(endianess.toLowerCase()))
        {
            bytes[0] = (byte)((number >> 24) & 0xFF);
            bytes[1] = (byte)((number >> 16) & 0xFF);
            bytes[2] = (byte)((number >> 8) & 0xFF);
            bytes[3] = (byte)(number & 0xFF);
        }
        else
        {
            bytes[0] = (byte)(number & 0xFF);
            bytes[1] = (byte)((number >> 8) & 0xFF);
            bytes[2] = (byte)((number >> 16) & 0xFF);
            bytes[3] = (byte)((number >> 24) & 0xFF);
        }
        return 4;
    }

    /* Passar de bytes a enters */
    private static int bytesToInt32(byte bytes[], String endianess)
    {
        int number;

        if("be".equals(endianess.toLowerCase()))
        {
            number=((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
                    ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        }
        else
        {
            number=(bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) |
                    ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);
        }
        return number;
    }

    public static class Reader {
        private DataInputStream dis;

        public Reader(InputStream inputStream) {
            dis = new DataInputStream(inputStream);
        }

        /* Llegir un enter de 32 bits */
        public int read_int32() throws IOException
        {
            byte bytes[] = new byte[4];
            bytes  = read_bytes(4);

            return bytesToInt32(bytes,"be");
        }

        public String read_string(int strLen) throws IOException {
            String str;
            byte[] bStr;
            char[] cStr = new char[strLen];

            bStr = read_bytes(strLen);

            for (int i = 0; i < strLen; i++) {
                cStr[i] = (char) bStr[i];
            }

            str = String.valueOf(cStr);

            return str;
        }

        //llegir bytes.
        private byte[] read_bytes(int numBytes) throws IOException{
            /*int len=0 ;
            byte bStr[] = new byte[numBytes];
            do {
                len += dis.read(bStr, len, numBytes-len);
            } while (len < numBytes);
            return bStr;*/
            int len=0 ;
            byte bStr[] = new byte[numBytes];
            int bytesread=0;
            do {
                bytesread= dis.read(bStr, len, numBytes-len);
                if (bytesread == -1)
                    throw new IOException("Broken Pipe");
                len += bytesread;
            } while (len < numBytes);
            return bStr;
        }

        public char read_char() throws IOException {
            return (char)dis.read();
        }

        /* Llegir un string  mida variable size = nombre de bytes especifica la longitud*/
        public  String read_string_variable(int size) throws IOException
        {
            byte bHeader[]=new byte[size];
            char cHeader[]=new char[size];
            int numBytes=0;

            // Llegim els bytes que indiquen la mida de l'string
            bHeader = read_bytes(size);
            // La mida de l'string ve en format text, per tant creem un string i el parsejem
            for(int i=0;i<size;i++){
                cHeader[i]=(char)bHeader[i]; }
            numBytes=Integer.parseInt(new String(cHeader));

            // Llegim l'string
            byte bStr[]=new byte[numBytes];
            char cStr[]=new char[numBytes];
            bStr = read_bytes(numBytes);
            for(int i=0;i<numBytes;i++)
                cStr[i]=(char)bStr[i];
            return String.valueOf(cStr);
        }
    }

    public static class Writer {
        private DataOutputStream dos;

        public Writer(OutputStream outputStream) {
            dos = new DataOutputStream(outputStream);
        }

        /* Escriure un enter de 32 bits */
        public void write_int32(int number) throws IOException
        {
            byte bytes[]=new byte[4];

            int32ToBytes(number,bytes,"be");
            dos.write(bytes, 0, 4);
        }

        /* Escriure un string */
        public void write_string(String str) throws IOException
        {
            int numBytes, lenStr;
            lenStr = str.length();
            byte bStr[] = new byte[lenStr];

            for(int i = 0; i < lenStr; i++)
                bStr[i] = (byte) str.charAt(i);

            dos.write(bStr, 0, lenStr);
        }

        public void write_char(char c) throws IOException {
            dos.write((byte)c);
        }

        /* Escriure un string mida variable, size = nombre de bytes especifica la longitud  */
	/* String str = string a escriure.*/
        public  void write_string_variable(int size,String str) throws IOException
        {

            // Creem una seqÃ¼Ã¨ncia amb la mida
            byte bHeader[]=new byte[size];
            String strHeader;
            int numBytes=0;

            // Creem la capÃ§alera amb el nombre de bytes que codifiquen la mida
            numBytes=str.length();

            strHeader=String.valueOf(numBytes);
            int len;
            if ((len=strHeader.length()) < size)
                for (int i =len; i< size;i++){
                    strHeader= "0"+strHeader;}
            for(int i=0;i<size;i++)
                bHeader[i]=(byte)strHeader.charAt(i);
            // Enviem la capÃ§alera
            dos.write(bHeader, 0, size);
            // Enviem l'string writeBytes de DataOutputStrem no envia el byte mÃ©s alt dels chars.
            dos.writeBytes(str);
        }
    }
}
