package com.example.yuehaoting.lyrics.utils;


import com.example.yuehaoting.lyrics.formats.LyricsFileReader;
import com.example.yuehaoting.lyrics.formats.LyricsFileWriter;
import com.example.yuehaoting.lyrics.formats.hrc.HrcLyricsFileReader;
import com.example.yuehaoting.lyrics.formats.hrc.HrcLyricsFileWriter;
import com.example.yuehaoting.lyrics.formats.krc.KrcLyricsFileReader;
import com.example.yuehaoting.lyrics.formats.krc.KrcLyricsFileWriter;
import com.example.yuehaoting.lyrics.formats.ksc.KscLyricsFileReader;
import com.example.yuehaoting.lyrics.formats.ksc.KscLyricsFileWriter;
import com.example.yuehaoting.lyrics.formats.lrc.LrcLyricsFileReader;
import com.example.yuehaoting.lyrics.formats.lrc.LrcLyricsFileWriter;
import com.example.yuehaoting.lyrics.formats.lrcwy.WYLyricsFileReader;
import com.example.yuehaoting.lyrics.formats.lrcwy.WYLyricsFileWriter;
import com.example.yuehaoting.lyrics.formats.trc.TrcLyricsFileReader;
import com.example.yuehaoting.lyrics.formats.trc.TrcLyricsFileWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 歌词io操作
 * @author zhangliangming
 * 
 */
public class LyricsIOUtils {
	private static ArrayList<LyricsFileReader> readers;
	private static ArrayList<LyricsFileWriter> writers;

	static {
		readers = new ArrayList<LyricsFileReader>();
		readers.add(new HrcLyricsFileReader());
		readers.add(new KscLyricsFileReader());
		readers.add(new KrcLyricsFileReader());
		readers.add(new TrcLyricsFileReader());
		readers.add(new WYLyricsFileReader());
		readers.add(new LrcLyricsFileReader());
		//
		writers = new ArrayList<LyricsFileWriter>();
		writers.add(new HrcLyricsFileWriter());
		writers.add(new KscLyricsFileWriter());
		writers.add(new KrcLyricsFileWriter());
		writers.add(new TrcLyricsFileWriter());
		writers.add(new WYLyricsFileWriter());
		writers.add(new LrcLyricsFileWriter());
	}

	/**
	 * 获取支持的歌词文件格式
	 * 
	 * @return
	 */
	public static List<String> getSupportLyricsExts() {
		List<String> lrcExts = new ArrayList<String>();
		for (LyricsFileReader lyricsFileReader : readers) {
			lrcExts.add(lyricsFileReader.getSupportFileExt());
		}
		return lrcExts;
	}

	/**
	 * 获取歌词文件读取器
	 * 
	 * @param file
	 * @return
	 */
	public static LyricsFileReader getLyricsFileReader(File file) {
		return getLyricsFileReader(file.getName());
	}

	/**
	 * 获取歌词文件读取器
	 * 
	 * @param fileName
	 * @return
	 */
	public static LyricsFileReader getLyricsFileReader(String fileName) {
		String ext = FileUtils.getFileExt(fileName);
		for (LyricsFileReader lyricsFileReader : readers) {
			if (lyricsFileReader.isFileSupported(ext)) {
				return lyricsFileReader;
			}
		}
		return null;
	}

	/**
	 * 获取歌词保存器
	 *
	 * @param file
	 * @return
	 */
	public static LyricsFileWriter getLyricsFileWriter(File file) {
		return getLyricsFileWriter(file.getName());
	}

	/**
	 * 获取歌词保存器
	 *
	 * @param fileName
	 * @return
	 */
	public static LyricsFileWriter getLyricsFileWriter(String fileName) {
		String ext = FileUtils.getFileExt(fileName);
		for (LyricsFileWriter lyricsFileWriter : writers) {
			if (lyricsFileWriter.isFileSupported(ext)) {
				return lyricsFileWriter;
			}
		}
		return null;
	}
}
