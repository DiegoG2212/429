package cecs429.documents;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
			return new BufferedReader(new InputStreamReader(new FileInputStream(mFilePath.toString()),"utf-8"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String getTitle() {
		return mFilePath.getFileName().toString();
		
		/*Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try (Reader reader = getContent()) {
			Article article = gson.fromJson(reader, Article.class);
			
			return article.getTitle();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "N/A";*/
	}
	
	public static FileDocument loadJsonFileDocument(Path absolutePath, int documentId) {
		return new JsonFileDocument(documentId, absolutePath);
	}

}

class Article {
	private String title;
	private String body;
	private String url;
	
	public Article(String title, String body, String url) {
		this.title = title;
		this.body = body;
		this.url = url;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getBody() {
		return this.body;
	}
	
	public String getURL() {
		return this.url;
	}
}
