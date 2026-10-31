package fr.isaaconnect.sweb.dto.evaluations;

import fr.isaaconnect.sweb.entities.EvaluationEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.lucene.document.Document;

import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationDTO {
    private UUID id;
    private String author;
    private String text;
    private int note;

    public static EvaluationDTO fromEntity(EvaluationEntity entity) {
        return EvaluationDTO.builder()
            .id(entity.getId())
            .author(entity.getAuthor())
            .text(entity.getText())
            .note(entity.getNote())
            .build();
    }

    public static EvaluationDTO fromLuceneDocument(Document document) {
        return EvaluationDTO.builder()
            .id(UUID.fromString(document.getField("evaluation_id").stringValue()))
            .author(document.getField("author").stringValue())
            .text(document.getField("content").stringValue())
            .note(document.getField("note").numericValue().intValue())
            .build();
    }
}
