package bitmex.bitmexspring.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "bots")
public class BitmexBot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "bot_id")
    private int botId;
    @OneToOne(cascade = CascadeType.ALL
            , orphanRemoval = true)
    private BitmexBotData bitmexBotData;
    @OneToMany(cascade = CascadeType.ALL
            , orphanRemoval = true)
    private Set<BitmexOrder> bitmexOrders;
}
