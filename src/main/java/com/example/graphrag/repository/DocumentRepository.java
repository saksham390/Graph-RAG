package com.example.graphrag.repository;

import com.example.graphrag.entity.Document;
import com.example.graphrag.entity.DocumentChunk;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DocumentRepository {

    private final JdbcTemplate jdbcTemplate;

    public DocumentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long saveDocument(String fileName, String content) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO documents(file_name, content) VALUES (?, ?) RETURNING id",
                Long.class, fileName, content);
    }

    public void saveChunk(long documentId, int chunkIndex, String content, double[] embedding) {
        jdbcTemplate.update(
                "INSERT INTO document_chunks(document_id, chunk_index, content, embedding) VALUES (?, ?, ?, CAST(? AS vector))",
                documentId, chunkIndex, content, vectorLiteral(embedding));
    }

    public List<DocumentChunk> similaritySearch(double[] embedding, int limit) {
        return jdbcTemplate.query(
                "SELECT id, document_id, chunk_index, content, 1 - (embedding <=> CAST(? AS vector)) AS score " +
                        "FROM document_chunks ORDER BY embedding <=> CAST(? AS vector) LIMIT ?",
                (resultSet, rowNum) -> new DocumentChunk(
                        resultSet.getLong("id"), resultSet.getLong("document_id"),
                        resultSet.getInt("chunk_index"), resultSet.getString("content"),
                        resultSet.getDouble("score")),
                vectorLiteral(embedding), vectorLiteral(embedding), limit);
    }

    public List<Document> findAll() {
        return jdbcTemplate.query("SELECT id, file_name, content FROM documents ORDER BY id",
                (resultSet, rowNum) -> new Document(resultSet.getLong("id"),
                        resultSet.getString("file_name"), resultSet.getString("content")));
    }

    private String vectorLiteral(double[] embedding) {
        StringBuilder value = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) {
                value.append(',');
            }
            value.append(embedding[i]);
        }
        return value.append(']').toString();
    }
}