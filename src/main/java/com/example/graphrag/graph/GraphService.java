package com.example.graphrag.graph;

import com.example.graphrag.dto.RelationshipDto;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GraphService {

    private final Driver driver;

    public GraphService(Driver driver) {
        this.driver = driver;
    }

    public void saveRelationships(List<RelationshipDto> relationships) {
        try (Session session = driver.session()) {
            for (RelationshipDto relationship : relationships) {
                String type = relationship.relationship().toUpperCase().replaceAll("[^A-Z0-9_]", "_");
                session.run("MERGE (source:Entity {name: $source}) " +
                                "MERGE (target:Entity {name: $target}) " +
                                "CREATE (source)-[:" + type + "]->(target)",
                        Values.parameters("source", relationship.source(), "target", relationship.target()))
                        .consume();
            }
        }
    }

    public List<String> search(String question) {
        String entity = findKnownEntity(question);
        try (Session session = driver.session()) {
            List<Record> directRecords = session.run(
                    "MATCH (source:Entity)-[relationship]->(target:Entity) " +
                            "WHERE toLower(source.name) CONTAINS toLower($entity) " +
                            "OR toLower(target.name) CONTAINS toLower($entity) " +
                            "RETURN source.name + ' -[' + type(relationship) + ']-> ' + target.name AS result LIMIT 20",
                    Values.parameters("entity", entity)).list();
            List<Record> twoHopRecords = session.run(
                "MATCH (first:Entity)-[firstRelationship]->(middle:Entity)-[secondRelationship]->(last:Entity) " +
                    "WHERE toLower(first.name) CONTAINS toLower($entity) " +
                    "RETURN first.name + ' -[' + type(firstRelationship) + ']-> ' + middle.name + " +
                    "' -[' + type(secondRelationship) + ']-> ' + last.name AS result LIMIT 20",
                Values.parameters("entity", entity)).list();
            return java.util.stream.Stream.concat(directRecords.stream(), twoHopRecords.stream())
                .map(record -> record.get("result").asString()).distinct().toList();
        }
    }

        private String findKnownEntity(String question) {
        try (Session session = driver.session()) {
            List<String> names = session.run("MATCH (entity:Entity) RETURN entity.name AS name")
                .list(record -> record.get("name").asString());
            return names.stream().filter(name -> question.toLowerCase().contains(name.toLowerCase()))
                .findFirst().orElse(question.replaceAll("(?i).*?\\b(?:did|does|what|who|where|is)\\s+", "")
                    .replaceAll("(?i)\\?.*", "").trim());
        }
        }

    public List<String> entities() {
        try (Session session = driver.session()) {
            return session.run("MATCH (entity:Entity) RETURN entity.name AS name ORDER BY name")
                    .list(record -> record.get("name").asString());
        }
    }
}