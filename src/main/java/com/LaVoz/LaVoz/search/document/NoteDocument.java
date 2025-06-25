package com.LaVoz.LaVoz.search.document;

import com.LaVoz.LaVoz.domain.Note;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "notes")
public class NoteDocument {
    
    @Id
    private Long id;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String content;
    
    @Field(type = FieldType.Keyword)
    private String emotion;
    
    @Field(type = FieldType.Keyword)
    private String time;
    
    @Field(type = FieldType.Long)
    private Long memberId;
    
    @Field(type = FieldType.Keyword)
    private String memberName;
    
    @Field(type = FieldType.Long)
    private Long organizationId;
    
    @Field(type = FieldType.Keyword)
    private String organizationName;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate createdAt;
    
    // Note 엔티티를 NoteDocument로 변환하는 메서드
    public static NoteDocument from(Note note) {
        return NoteDocument.builder()
                .id(note.getNoteId())
                .title(note.getTitle())
                .content(note.getContent())
                .emotion(note.getEmotion())
                .time(note.getTime())
                .memberId(note.getMember().getMemberId())
                .memberName(note.getMember().getName())
                .organizationId(note.getOrganization() != null ? note.getOrganization().getOrganizationId() : null)
                .organizationName(note.getOrganization() != null ? note.getOrganization().getName() : null)
                .createdAt(note.getCreatedAt().toLocalDate())
                .build();
    }
}