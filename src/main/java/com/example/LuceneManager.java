package com.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;

public class LuceneManager {

	private static String path; // project path

	// Data paths
	private static String albumDataDir = "";
	private static String lyricsDataDir = "";
	private static String songDataDir = "";

	// Index paths
	private static String songIndexDir = "";
	private static String albumIndexDir = "";

	private static Indexer songIndexer;
	private static Indexer albumIndexer;
	private static Searcher songSearcher;
	private static Searcher albumSearcher;

	public LuceneManager() {
		try {
			path = new File(".").getCanonicalPath();

			songIndexDir = path + "\\Index\\Songs";
			albumIndexDir = path + "\\Index\\Albums";

			songDataDir = path + "\\Data\\Songs";
			lyricsDataDir = path + "\\Data\\Lyrics";
			albumDataDir = path + "\\Data\\Albums";

			File[] songIndexFiles = new File(songIndexDir).listFiles();
			File[] albumIndexFiles = new File(albumIndexDir).listFiles();

			for (File file : songIndexFiles) {
				if (!file.isDirectory()) {
					file.delete();
				}
			}

			for (File file : albumIndexFiles) {
				if (!file.isDirectory()) {
					file.delete();
				}
			}

			songIndexer = createSongIndex(songIndexDir, songDataDir, lyricsDataDir);
			albumIndexer = createAlbumIndex(albumIndexDir, albumDataDir);
			// this.search("Taylor Swift"); // Here we enter the query for Search
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Indexer createAlbumIndex(String indexDir, String dataDir) throws IOException {
		Indexer indexer = new Indexer(indexDir);
		int numIndexed = indexer.createAlbumIndex(dataDir, new TextFileFilter());
		// indexer.close();
		System.out.println(numIndexed + " Album indexed");
		return indexer;
	}

	private Indexer createSongIndex(String indexDir, String songDataDir, String lyricsDataDir) throws IOException {
		Indexer indexer = new Indexer(indexDir);
		int numIndexed = indexer.createSongIndex(songDataDir, lyricsDataDir, new TextFileFilter());
		// indexer.close();
		System.out.println(numIndexed + " Songs indexed");
		return indexer;
	}

	public void searchSongs(String searchQuery) throws IOException, ParseException {
		search(searchQuery, songIndexDir, songSearcher);
	}

	public void searchAlbums(String searchQuery) throws IOException, ParseException {
		search(searchQuery, albumIndexDir, albumSearcher);
	}

	private void search(String searchQuery, String indexDir, Searcher searcher) throws IOException, ParseException {
		searcher = new Searcher(indexDir);
		TopDocs hits = searcher.search(searchQuery);
		System.out.println(hits.totalHits + " documents found");
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.getDocument(scoreDoc);
			// System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
		}
		searcher.close();
	}

	public void addSong(String singer_name, String song_name, String album_name, String album_year, String album_type,
			String lyrics) throws IOException {
		Document songDoc = new Document();
		TextField singeName = new TextField("singer_name", singer_name, Store.YES);
		TextField songName = new TextField("song_name", song_name, Store.YES);
		TextField albumName = new TextField("album_name", album_name, Store.YES);
		TextField albumYear = new TextField("album_year", album_year, Store.YES);
		TextField albumType = new TextField("album_type", album_type, Store.YES);
		TextField lyricss = new TextField("lyrics", lyrics, Store.YES);
		songDoc.add(singeName);
		songDoc.add(songName);
		songDoc.add(albumName);
		songDoc.add(albumYear);
		songDoc.add(albumType);
		songDoc.add(lyricss);
		songIndexer.addSong(songDoc);
	}

	public void addAlbum(String singer_name, String album_name, String album_year, String album_type)
			throws IOException {
		Document albumDoc = new Document();
		TextField singeName = new TextField("singer_name", singer_name, Store.YES);
		TextField albumName = new TextField("album_name", album_name, Store.YES);
		TextField albumYear = new TextField("album_year", album_year, Store.YES);
		TextField albumType = new TextField("album_type", album_type, Store.YES);
		albumDoc.add(singeName);
		albumDoc.add(albumName);
		albumDoc.add(albumYear);
		albumDoc.add(albumType);
		albumIndexer.addAlbum(albumDoc);
	}

	public void addSongsFromLink(String singer_name, String song_name) throws IOException {
		A_Scraper scraper = new A_Scraper(singer_name, song_name);
		ArrayList<String> songInfo = scraper.getSongInfo();
		Document songDoc = new Document();
		TextField singeName = new TextField("singer_name", songInfo.get(0), Store.YES);
		TextField songName = new TextField("song_name", songInfo.get(1), Store.YES);
		TextField albumName = new TextField("album_name", songInfo.get(2), Store.YES);
		TextField albumYear = new TextField("album_year", songInfo.get(3), Store.YES);
		TextField albumType = new TextField("album_type", songInfo.get(4), Store.YES);
		TextField lyricss = new TextField("lyrics", songInfo.get(5), Store.YES);
		songDoc.add(singeName);
		songDoc.add(songName);
		songDoc.add(albumName);
		songDoc.add(albumYear);
		songDoc.add(albumType);
		songDoc.add(lyricss);
		songIndexer.addSong(songDoc);
	}

	public void addSongsFromFile(File file) throws IOException {
		songIndexer.indexFile(file, "songs");
	}

	public void addLyricsFromFile(File file) throws IOException {
		songIndexer.indexFile(file, "lyrics");
	}

	public void addAlbumsFromFile(File file) throws IOException {
		albumIndexer.indexFile(file, "albums");
	}

	public ArrayList<Document> searchSongQuery(Map<String, String> map, int k) throws IOException, ParseException {
		Searcher searcher = new Searcher(songIndexDir);
		Query query = searcher.buildQuery(map, Occur.MUST);
		TopDocs topDocs = searcher.search(query, k);
		ArrayList<Document> docs = new ArrayList<>();
		for (ScoreDoc score : topDocs.scoreDocs) {
			docs.add(searcher.getDocument(score));
		}
		// System.out.println("Debug 3");
		return docs;
	}

	public ArrayList<Document> searchAlbumQuery(Map<String, String> map, int k) throws IOException, ParseException {
		Searcher searcher = new Searcher(albumIndexDir);
		Query query = searcher.buildQuery(map, Occur.MUST);
		TopDocs topDocs = searcher.search(query, k);
		ArrayList<Document> docs = new ArrayList<>();
		for (ScoreDoc score : topDocs.scoreDocs) {
			docs.add(searcher.getDocument(score));
		}
		return docs;
	}

	public ArrayList<Document> findSimilarSong(Document doc, int k) throws IOException, ParseException {
		Searcher searcher = new Searcher(songIndexDir);
		Map<String, String> map = new HashMap<>();
		for (IndexableField indexableField : doc.getFields()) {
			if (indexableField.name().contains("album") != true) {
				map.put(indexableField.name(), indexableField.stringValue());
			}
		}
		Query query = searcher.buildQuery(map, Occur.SHOULD);
		TopDocs topDocs = searcher.search(query, k);
		ArrayList<Document> docs = new ArrayList<>();
		for (ScoreDoc score : topDocs.scoreDocs) {
			docs.add(searcher.getDocument(score));
		}
		return docs;
	}

	public ArrayList<Document> findSimilarAlbum(Document doc, int k) throws IOException, ParseException {
		Searcher searcher = new Searcher(albumIndexDir);
		Map<String, String> map = new HashMap<>();
		for (IndexableField indexableField : doc.getFields()) {
			map.put(indexableField.name(), indexableField.stringValue());
		}
		Query query = searcher.buildQuery(map, Occur.SHOULD);
		TopDocs topDocs = searcher.search(query, k);
		ArrayList<Document> docs = new ArrayList<>();
		for (ScoreDoc score : topDocs.scoreDocs) {
			docs.add(searcher.getDocument(score));
		}
		return docs;
	}

	public void deleteSong(Document doc) throws IOException, ParseException {
		Searcher searcher = new Searcher(songIndexDir);
		Map<String, String> map = new HashMap<>();
		for (IndexableField indexableField : doc.getFields()) {
			map.put(indexableField.name(), indexableField.stringValue());
		}
		Query query = searcher.buildQuery2(doc);
		songIndexer.deleteDoc(query);
	}

	public void deleteAlbum(Document doc) throws IOException, ParseException {
		Searcher searcher = new Searcher(albumIndexDir);
		Map<String, String> map = new HashMap<>();
		for (IndexableField indexableField : doc.getFields()) {
			map.put(indexableField.name(), indexableField.stringValue());
		}
		Query query = searcher.buildQuery3(doc);
		albumIndexer.deleteDoc(query);
	}
}
