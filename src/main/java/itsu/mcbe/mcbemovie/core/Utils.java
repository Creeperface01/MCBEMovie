package itsu.mcbe.mcbemovie.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * Copied from Jupiter
 * @author JupiterDevelopmentTeam
 */

public class Utils {
	
	/**
	 * 
	 * @param filename ファイルパス
	 * @param content 書き込む内容
	 * 
	 * <p>writeFile - Utils</p>
	 * 
	 * <p>contentで指定した内容をファイルに書き込みます。</p>
	 * 
	 * Jupiter by Jupiter Development Team
	 * 
	 */

    public static void writeFile(String fileName, String content) throws IOException {
        writeFile(fileName, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }
    
	/**
	 * 
	 * @param filename ファイルパス
	 * @param content 書き込む内容(InputStream)
	 * 
	 * <p>writeFile - Utils</p>
	 * 
	 * <p>contentで指定した内容をファイルに書き込みます。</p>
	 * 
	 * Jupiter by Jupiter Development Team
	 * 
	 */

    public static void writeFile(String fileName, InputStream content) throws IOException {
        writeFile(new File(fileName), content);
    }
    
	/**
	 * 
	 * @param file 書き込み先のファイルオブジェクト
	 * @param content 書き込む内容
	 * 
	 * <p>writeFile - Utils</p>
	 * 
	 * <p>contentで指定した内容をファイルに書き込みます。</p>
	 * 
	 * Jupiter by Jupiter Development Team
	 * 
	 */

    public static void writeFile(File file, String content) throws IOException {
        writeFile(file, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }
    
	/**
	 * 
	 * @param file 書き込み先のファイルオブジェクト
	 * @param content 書き込む内容(InputStream)
	 * 
	 * <p>writeFile - Utils</p>
	 * 
	 * <p>contentで指定した内容をファイルに書き込みます。</p>
	 * 
	 * Jupiter by Jupiter Development Team
	 * 
	 */

    public static void writeFile(File file, InputStream content) throws IOException {
        if (content == null) {
            throw new IllegalArgumentException("content must not be null");
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream stream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = content.read(buffer)) != -1) {
            stream.write(buffer, 0, length);
        }
        stream.close();
        content.close();
    }
    
    public static void copyFile(File from, File to) throws IOException {
        if (!from.exists()) {
            throw new FileNotFoundException();
        }
        if (from.isDirectory() || to.isDirectory()) {
            throw new FileNotFoundException();
        }
        FileInputStream fi = null;
        FileChannel in = null;
        FileOutputStream fo = null;
        FileChannel out = null;
        try {
            if (!to.exists()) {
                to.createNewFile();
            }
            fi = new FileInputStream(from);
            in = fi.getChannel();
            fo = new FileOutputStream(to);
            out = fo.getChannel();
            in.transferTo(0, in.size(), out);
        } finally {
            if (fi != null) fi.close();
            if (in != null) in.close();
            if (fo != null) fo.close();
            if (out != null) out.close();
        }
    }
}