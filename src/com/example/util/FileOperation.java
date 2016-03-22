package com.example.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

public class FileOperation {
	/*
	 * the common file operations: create file, open file, clear file, 
	 *                             save to file, append to file, getAbsolutePath.
	 */
	
	private static final String SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	private static File file = null;
	
	
	public static void customCreateFile(File file) throws IOException {
		/*
		 * in a directory, make another directory,then before make a file, delete it when the file exists.
		 */
		if (file.exists()) {
			file.delete();
		}
		File parent = file.getParentFile(); 
		if(parent != null && !parent.exists()) {
			parent.mkdirs(); 
		}
		file.createNewFile();
	} 
	
	
	public static void openFile(Context context, String myFolder) {
		/*
		 * open the text file.
		 */
		Intent intent = new Intent("android.intent.action.VIEW");     
        intent.addCategory("android.intent.category.DEFAULT");     
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(FileOperation.getAbsolutePath() + myFolder));     
        intent.setDataAndType(uri, "text/plain"); 
        context.startActivity(intent);
	}
	
	public static void clearFile(String str) throws FileNotFoundException {
		/*
		 * clear the file.
		 */
		File file = new File(FileOperation.getAbsolutePath() + str);
		PrintWriter writer = new PrintWriter(file);
		writer.print("");
		writer.close();
	}
	
	public static void saveToFile(String recordData, String path) throws IOException {
		/*
		 * save recordData to path, if the file has exited, it will recreate.
		 */
		file = new File(SDPATH + path);
		customCreateFile(file);
		try{
			FileOutputStream out = new FileOutputStream(file, true);
			out.write(recordData.getBytes());
			out.close();
		}catch(IOException e) {
			
		}
	}
	
	public static void appendToFile(String recordData, String path) throws IOException {
		/*
		 * append recordData to path.
		 */
		file = new File(SDPATH + path);
		try{
			FileOutputStream out = new FileOutputStream(file, true);
			out.write(recordData.getBytes());
			out.close();
		}catch(IOException e) {
			
		}
	}
	
	
	public static String getAbsolutePath() {
		/*
		 * get the absolute path of the android device.
		 */
		return SDPATH;
	}
}

