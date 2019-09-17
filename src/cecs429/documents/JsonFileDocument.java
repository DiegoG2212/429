package cecs429.documents;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonFileDocument implements FileDocument {
	private int mDocumentId;
	private Path mFilePath;
	
	/**
	 * Constructs a JsonFileDocument with the given document ID representing the file at the given
	 * absolute file path.
	 */
	public JsonFileDocument(int id, Path absoluteFilePath) {
		mDocumentId = id;
		mFilePath = absoluteFilePath;
	}
	
	// Taken from DirectoryCorpus
	private static String getFileExtension(Path file) {
		String fileName = file.getFileName().toString();
		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
		return "." + extension;
	}
	
	@Override
	public Path getFilePath() {
		try {
			if (getFileExtension(mFilePath).equalsIgnoreCase("json"))
				return mFilePath;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return null;
	}
	
	
	
	@Override
	public int getId() {
		return mDocumentId;
	}
	
	@Override
	public Reader getContent() {
		try {
			return Files.newBufferedReader(mFilePath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String getTitle() {
		return mFilePath.getFileName().toString();
	}
	
	public static FileDocument loadJsonFileDocument(Path absolutePath, int documentId) {
		return new JsonFileDocument(documentId, absolutePath);
	}

}
