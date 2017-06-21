package cn.standardai.api.core.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public class FileUtil {

	public static void saveUploadFile(MultipartFile inputFile, String outputFilePath) throws IOException {

		// output file buffer
		BufferedOutputStream outputFileBuffer = null;
		// output file
		FileOutputStream outputFile = null;
		if (!inputFile.isEmpty()) {
			try {
				outputFile = new FileOutputStream(new File(outputFilePath));
				outputFileBuffer = new BufferedOutputStream(outputFile);
				outputFileBuffer.write(inputFile.getBytes());
			} catch (IOException e) {
				throw e;
			} finally {
				if (outputFileBuffer != null) {
					try {
						outputFileBuffer.close();
						outputFileBuffer = null;
					} catch (IOException e) {
						outputFileBuffer = null;
						throw e;
					}
				}
				if (outputFile != null) {
					try {
						outputFile.close();
						outputFile = null;
					} catch (IOException e) {
						outputFile = null;
						throw e;
					}
				}
			}
		} else {
			return;
		}
	}
}
