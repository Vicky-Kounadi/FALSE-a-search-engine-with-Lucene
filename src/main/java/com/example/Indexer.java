package com.example;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.opencsv.CSVReader;

public class Indexer {
	private IndexWriter writer;
	private String indexPath;

	public Indexer(String indexDirectoryPath) throws IOException {
		Path indexPath = Paths.get(indexDirectoryPath);
		this.indexPath = indexDirectoryPath;
		if (!Files.exists(indexPath)) {
			Files.createDirectory(indexPath);
		}
		Directory indexDirectory = FSDirectory.open(indexPath);
		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		writer = new IndexWriter(indexDirectory, config);
	}

	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}

	// Indexing all album files
	public int createAlbumIndex(String dataDirPath, FileFilter filter) throws IOException {
		writer.addDocument(new Document());
		writer.commit();
		File[] files = new File(dataDirPath).listFiles();
		for (File file : files) {
			if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
				indexFile(file, "albums");
			}
		}
		int indexed = writer.numRamDocs();
		writer.commit();
		return indexed;
	}

	// Indexing all song files
	public int createSongIndex(String songDataDirPath, String lyricsDataDirPath, FileFilter filter) throws IOException {
		writer.addDocument(new Document());
		writer.commit();
		File[] songFiles = new File(songDataDirPath).listFiles();
		for (File file : songFiles) {
			if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
				indexFile(file, "songs");
			}
		}
		writer.commit();

		// Indexing all lyrics files
		File[] lyricFiles = new File(lyricsDataDirPath).listFiles();
		for (File file : lyricFiles) {
			if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
				indexFile(file, "lyrics");
			}
		}
		int indexed = writer.numRamDocs();
		writer.commit();
		return indexed;
	}

	public void indexFile(File file, String datatype) throws IOException {
		ArrayList<Document> DocList = new ArrayList<>();

		if (datatype.equalsIgnoreCase("albums")) {
			DocList = getAlbumDocument(file);
			Searcher searcher = new Searcher(this.indexPath);
			for (int i = 0; i < DocList.size(); i++) {
				try {
					// Create Queries for every common field
					Query singerNameQuery = new QueryParser("singer_name", new StandardAnalyzer())
							.parse("\"" + DocList.get(i).get("singer_name") + "\"");
					Query songNameQuery = new QueryParser("album_name", new StandardAnalyzer())
							.parse("\"" + DocList.get(i).get("album_name") + "\"");
					BooleanQuery booleanQuery = new BooleanQuery.Builder()
							.add(singerNameQuery, Occur.MUST)
							.add(songNameQuery, Occur.MUST)
							.build();
					// Create new document
					// Checking if document already exists
					TopDocs results = searcher.search(booleanQuery);
					System.out.println("TotalHits == " + results.totalHits.value);
					if (results.scoreDocs.length != 0) { // found matching document
						System.out.println("Duplicate doc found.");
						// Get matching document fields
						Document matchingDoc = searcher.getDocument(results.scoreDocs[0]);
						// Delete old document
						writer.deleteDocuments(booleanQuery);
						Document correctDoc = new Document();
						// Construct new document
						correctDoc.add(matchingDoc.getField("singer_name"));
						correctDoc.add(matchingDoc.getField("album_name"));
						correctDoc.add(DocList.get(i).getField("album_type"));
						correctDoc.add(DocList.get(i).getField("album_year"));
						DocList.set(i, correctDoc);
					}
				} catch (ParseException e) {
					System.out.println("Parse incorrect.");
				}
			}
		} else if (datatype.equalsIgnoreCase("songs")) {
			DocList = getSongDocument(file);
			Searcher searcher = new Searcher(this.indexPath);
			for (int i = 0; i < DocList.size(); i++) {
				try {
					// Create Queries for every common field
					Query singerNameQuery = new QueryParser("singer_name", new StandardAnalyzer())
							.parse("\"" + DocList.get(i).get("singer_name") + "\"");
					Query songNameQuery = new QueryParser("song_name", new StandardAnalyzer())
							.parse("\"" + DocList.get(i).get("song_name") + "\"");
					BooleanQuery booleanQuery = new BooleanQuery.Builder()
							.add(singerNameQuery, Occur.MUST)
							.add(songNameQuery, Occur.MUST)
							.build();
					// Create new document
					// Checking if document already exists
					TopDocs results = searcher.search(booleanQuery);
					System.out.println("TotalHits == " + results.totalHits.value);
					if (results.scoreDocs.length != 0) { // found matching document
						System.out.println("Duplicate doc found.");
						// Get matching document fields
						Document matchingDoc = searcher.getDocument(results.scoreDocs[0]);
						// Delete old document
						writer.deleteDocuments(booleanQuery);
						Document correctDoc = new Document();
						// Construct new document
						correctDoc.add(matchingDoc.getField("singer_name"));
						correctDoc.add(matchingDoc.getField("song_name"));
						DocList.set(i, correctDoc);
					}
				} catch (ParseException e) {
					System.out.println("Parse incorrect.");
				}
			}
		} else if (datatype.equalsIgnoreCase("lyrics")) {
			DocList = getLyricsDocument(file); // List of every lyric document
			Searcher searcher = new Searcher(this.indexPath);
			for (int i = 0; i < DocList.size(); i++) {
				try {
					// Create Queries for every common field
					Query singerNameQuery = new QueryParser("singer_name", new StandardAnalyzer())
							.parse("\"" + DocList.get(i).get("singer_name") + "\"");
					Query songNameQuery = new QueryParser("song_name", new StandardAnalyzer())
							.parse("\"" + DocList.get(i).get("song_name") + "\"");
					BooleanQuery booleanQuery = new BooleanQuery.Builder()
							.add(singerNameQuery, Occur.MUST)
							.add(songNameQuery, Occur.MUST)
							.build();
					// Create new document
					// Checking if document already exists
					TopDocs results = searcher.search(booleanQuery);
					// results.scoreDocs[0];
					System.out.println("TotalHits == " + results.totalHits.value);
					if (results.scoreDocs.length != 0) { // found matching document
						System.out.println("Duplicate doc found.");
						// Get matching document fields
						Document matchingDoc = searcher.getDocument(results.scoreDocs[0]);
						// Delete old document
						writer.deleteDocuments(booleanQuery);
						Document correctDoc = new Document();
						// Construct new document
						correctDoc.add(matchingDoc.getField("singer_name"));
						// correctDoc.add(matchingDoc.getField("song_link"));
						correctDoc.add(DocList.get(i).getField("song_name"));
						correctDoc.add(DocList.get(i).getField("lyrics"));
						DocList.set(i, correctDoc);
					}
				} catch (ParseException e) {
					System.out.println("Parse incorrect.");
				}
			}
		}
		// Adding Data To Index
		for (int i = 0; i < DocList.size(); i++) {
			writer.addDocument(DocList.get(i));
		}
		writer.commit();
	}

	private ArrayList<Document> getAlbumDocument(File file) {
		ArrayList<Document> AlbumDocs = new ArrayList<>();
		Document albumDocument = null;
		try {
			// index file contents
			FileReader fileRead = new FileReader(file);
			CSVReader csvReader = new CSVReader(fileRead);
			String[] nextLine;
			while ((nextLine = csvReader.readNext()) != null) {
				TextField singerName = new TextField("singer_name", nextLine[2].replace(" Lyrics", ""), Store.YES);
				TextField albumName = new TextField("album_name", nextLine[3], Store.YES);
				TextField albumType = new TextField("album_type", nextLine[4], Store.YES);
				TextField albumYear = new TextField("album_year", nextLine[5], Store.YES);
				albumDocument = new Document();
				albumDocument.add(singerName);
				albumDocument.add(albumName);
				albumDocument.add(albumType);
				albumDocument.add(albumYear);
				AlbumDocs.add(albumDocument);
			}
			csvReader.close();

		} catch (IndexOutOfBoundsException e) {
			System.out.println("Error file outOfBounds");
		} catch (IOException e) {
			System.out.println("Error 404");
		}
		return AlbumDocs;
	}

	private ArrayList<Document> getSongDocument(File file) {
		ArrayList<Document> songDocs = new ArrayList<>();
		Document songDocument = null;
		try {
			// index file contents
			FileReader fileRead = new FileReader(file);
			CSVReader csvReader = new CSVReader(fileRead);
			String[] nextLine;
			while ((nextLine = csvReader.readNext()) != null) {
				TextField singerName = new TextField("singer_name", nextLine[2].replace(" Lyrics", ""), Store.YES);
				TextField songName = new TextField("song_name", nextLine[3], Store.YES);
				TextField lyrics = new TextField("lyrics", "undefined", Store.YES);
				// TextField songLink = new TextField("song_link", nextLine[4], Store.YES);
				songDocument = new Document();
				songDocument.add(singerName);
				songDocument.add(songName);
				songDocument.add(lyrics);
				// songDocument.add(songLink);
				songDocs.add(songDocument);
			}
			csvReader.close();

		} catch (IndexOutOfBoundsException e) {
			System.out.println("Error file outOfBounds");
		} catch (IOException e) {
			System.out.println("Error 404");
		}
		return songDocs;
	}

	private ArrayList<Document> getLyricsDocument(File file) {
		ArrayList<Document> lyricsDocs = new ArrayList<>();
		Document lyricsDocument = null;
		try {
			// index file contents
			FileReader fileRead = new FileReader(file);
			CSVReader csvReader = new CSVReader(fileRead);
			String[] nextLine;
			while ((nextLine = csvReader.readNext()) != null) {
				// TextField songLink = new TextField("song_link", nextLine[1], Store.YES);
				TextField artist = new TextField("singer_name", nextLine[2].replace(" Lyrics", ""), Store.YES);
				TextField songName = new TextField("song_name", nextLine[3], Store.YES);
				// lower case + chorus + whatever
				TextField lyrics = new TextField("lyrics", chorusProcess(nextLine[4]), Store.YES);
				chorusProcess(nextLine[4]);
				lyricsDocument = new Document();
				// lyricsDocument.add(songLink);
				lyricsDocument.add(artist);
				lyricsDocument.add(songName);
				lyricsDocument.add(lyrics);
				lyricsDocs.add(lyricsDocument);
			}
			csvReader.close();

		} catch (IndexOutOfBoundsException e) {
			System.out.println("Error file outOfBounds");
		} catch (IOException e) {
			System.out.println("Error 404");
		}
		return lyricsDocs;
	}

	public void addSong(Document doc) throws IOException {
		try {
			Searcher searcher = new Searcher(this.indexPath);
			Query singerNameQuery = new QueryParser("singer_name", new StandardAnalyzer())
					.parse("\"" + doc.get("singer_name") + "\"");
			Query songNameQuery = new QueryParser("song_name", new StandardAnalyzer())
					.parse("\"" + doc.get("song_name") + "\"");
			BooleanQuery booleanQuery = new BooleanQuery.Builder()
					.add(singerNameQuery, Occur.MUST)
					.add(songNameQuery, Occur.MUST)
					.build();

			TopDocs results = searcher.search(booleanQuery);
			if (results.scoreDocs.length != 0) { // found matching document
				System.out.println("Duplicate doc found.");
				writer.deleteDocuments(booleanQuery);
				writer.addDocument(doc);
				writer.commit();
				System.out.println("Song added");
			} else {
				writer.addDocument(doc);
				writer.commit();
			}
		} catch (ParseException e) {
			System.out.println("Parse incorrect.");
		}

	}

	public void addAlbum(Document doc) throws IOException {
		try {
			Searcher searcher = new Searcher(this.indexPath);
			Query singerNameQuery = new QueryParser("singer_name", new StandardAnalyzer())
					.parse("\"" + doc.get("singer_name") + "\"");
			Query songNameQuery = new QueryParser("album_name", new StandardAnalyzer())
					.parse("\"" + doc.get("album_name") + "\"");
			BooleanQuery booleanQuery = new BooleanQuery.Builder()
					.add(singerNameQuery, Occur.MUST)
					.add(songNameQuery, Occur.MUST)
					.build();

			TopDocs results = searcher.search(booleanQuery);
			if (results.scoreDocs.length != 0) { // found matching document
				System.out.println("Duplicate doc found.");
				writer.deleteDocuments(booleanQuery);
				writer.addDocument(doc);
				writer.commit();
				System.out.println("Album added");
			} else {
				writer.addDocument(doc);
				writer.commit();
			}
		} catch (ParseException e) {
			System.out.println("Parse incorrect.");
		}
	}

	public void deleteDoc(Query query) throws IOException {
		writer.deleteDocuments(query);
		writer.commit();
	}

	private String chorusProcess(String fileLyrics) {
		String str1, str2[], str3 = "";
		int fistIndex = 0, secondIndex = 0, thirdIndex = 0;
		str1 = fileLyrics.toLowerCase().replaceAll("-", " ").replaceAll("\\(\\)", "").replaceAll("x", "");
		if (str1.contains("[chorus:]") && (str1.contains("[chorus]") || str1.contains("[chorus 2]")
				|| str1.contains("[chorus 3]") || str1.contains("[chorus 4]"))) {
			fistIndex = str1.indexOf("[chorus:]");
			str3 = str1.substring(fistIndex);
			str3 = str3.replace("[chorus:]", "");
			str2 = str3.split("\n\n");
			str3 = str3.replaceAll("\\[chorus\\]", Matcher.quoteReplacement(str2[0] + "\n\n"));
			str3 = str3.replaceAll("\\[chorus 2\\]",
					Matcher.quoteReplacement(str2[0] + "\n\n") + Matcher.quoteReplacement(str2[0] + "\n\n"));
			str3 = str3.replaceAll("\\[chorus 3\\]", Matcher.quoteReplacement(str2[0] + "\n\n")
					+ Matcher.quoteReplacement(str2[0] + "\n\n") + Matcher.quoteReplacement(str2[0] + "\n\n"));
			str3 = str3.replaceAll("\\[chorus 4\\]",
					Matcher.quoteReplacement(str2[0] + "\n\n") + Matcher.quoteReplacement(str2[0] + "\n\n")
							+ Matcher.quoteReplacement(str2[0] + "\n\n") + Matcher.quoteReplacement(str2[0] + "\n\n"));
			str1 = str1.substring(0, fistIndex) + str3;
		}
		if (str1.contains("[pre chorus:]") && (str1.contains("[pre chorus]") || str1.contains("[pre chorus 2]"))) {
			secondIndex = str1.indexOf("[pre chorus:]");
			str3 = str1.substring(secondIndex);
			str3 = str3.replace("[pre chorus:]", "");
			str2 = str3.split("\n\n");
			str3 = str3.replaceAll("\\[pre chorus\\]", Matcher.quoteReplacement(str2[0] + "\n\n"));
			str3 = str3.replaceAll("\\[pre chorus 2\\]",
					Matcher.quoteReplacement(str2[0] + "\n\n") + Matcher.quoteReplacement(str2[0] + "\n\n"));
			str1 = str1.substring(0, secondIndex) + str3;
		}
		if (str1.contains("[post chorus:]") && str1.contains("[post chorus]")) {
			thirdIndex = str1.indexOf("[post chorus:]");
			str3 = str1.substring(thirdIndex);
			str3 = str3.replace("[post chorus:]", "");
			str2 = str3.split("\n\n");
			str3 = str3.replaceAll("\\[post chorus\\]", Matcher.quoteReplacement(str2[0] + "\n\n"));
			str1 = str1.substring(0, thirdIndex) + str3;
		}
		// System.out.println("str1 ====" + str1);
		return str1;
	}
}