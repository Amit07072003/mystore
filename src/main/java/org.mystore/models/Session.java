package org.mystore.models;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Session extends BaseModel {
    @Column(length = 1000)
    private String token;

    @ManyToOne
    private User user;

    private SessionState state;
}
