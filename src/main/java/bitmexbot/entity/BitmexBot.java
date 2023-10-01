package bitmexbot.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "bots")
public class BitmexBot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "bot_id")
    private int botId;
    @OneToOne(cascade = CascadeType.ALL
            , orphanRemoval = true, fetch = FetchType.EAGER)
    private BitmexBotData bitmexBotData;
    @JsonManagedReference
    @OneToMany(mappedBy = "bitmexBot", cascade = CascadeType.ALL
            , orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<BitmexOrder> bitmexOrders;

}
