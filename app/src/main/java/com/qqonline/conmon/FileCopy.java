package com.qqonline.conmon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileCopy {

	/**  
     * ���Ƶ����ļ�  
     * @param oldPath String ԭ�ļ�·�� �磺c:/fqf.txt  
     * @param newPath String ���ƺ�·�� �磺f:/fqf.txt   
     */   
   public static void copyFile(String oldPath, String newPath) throws Exception{   
//       try {   
           int bytesum = 0;   
           int byteread = 0;   
           File oldfile = new File(oldPath);   
           if (oldfile.exists()) { //�ļ�����ʱ   
               InputStream inStream = new FileInputStream(oldPath); //����ԭ�ļ�   
               FileOutputStream fs = new FileOutputStream(newPath);   
               byte[] buffer = new byte[1444];   
               int length;   
               while ( (byteread = inStream.read(buffer)) != -1) {   
                   bytesum += byteread; //�ֽ��� �ļ���С   
                   System.out.println(bytesum);   
                   fs.write(buffer, 0, byteread);   
               }   
               inStream.close();   
           }   
//       }   
//       catch (Exception e) {   
//           System.out.println("���Ƶ����ļ���������");   
//           e.printStackTrace();   
//  
//       }   
  
   }   
  
   /**  
     * ���������ļ�������  
     * @param oldPath String ԭ�ļ�·�� �磺c:/fqf  
     * @param newPath String ���ƺ�·�� �磺f:/fqf/ff  
     */   
   public static void copyFolder(String oldPath, String newPath) throws Exception{   
  
 //      try {
	   		File newFolder=new File(newPath);
	   		if (!newFolder.exists()) {
	   			newFolder.mkdir();
			}
	   		else {
				
			}
   //        (new File(newPath)).mkdirs(); //����ļ��в����� �������ļ���   
           File a=new File(oldPath);   
           String[] file=a.list();   
           File temp=null;   
           for (int i = 0; i < file.length; i++) {   
               if(oldPath.endsWith(File.separator)){   
                   temp=new File(oldPath+file[i]);   
               }   
               else{   
                   temp=new File(oldPath+File.separator+file[i]);   
               }   
  
               if(temp.isFile()){   
                   FileInputStream input = new FileInputStream(temp);   
                   FileOutputStream output = new FileOutputStream(newPath + "/" +   
                           (temp.getName()).toString());   
                   byte[] b = new byte[1024 * 5];   
                   int len;   
                   while ( (len = input.read(b)) != -1) {   
                       output.write(b, 0, len);   
                   }   
                   output.flush();   
                   output.close();   
                   input.close();   
               }   
               if(temp.isDirectory()){//��������ļ���   
                   copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);   
               }   
           }   
 //      }   
//       catch (Exception e) {   
//           System.out.println("���������ļ������ݲ�������");   
//           e.printStackTrace();   
//  
//       }   
  
   }  

}
