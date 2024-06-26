package propensi.c06.sipp.model;

import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="rencana")
@JsonIgnoreProperties(value={"id_vendor", "vendor", "createdBy", "feedback", "listBarangRencana", "logRencana", "isDeleted", "pengadaan"}, allowSetters = true)
public class Rencana {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRencana;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vendor", referencedColumnName = "kodeVendor")
    private Vendor vendor;

    @NotNull
    @Column(name = "createdBy", nullable = false)
    private String createdBy; 

    @NotNull
    @Column(name = "namaRencana", nullable=false)
    private String namaRencana; 

    @NotNull
    @Column(name = "tanggalRencana", nullable=false)
    private LocalDate expectedDate; 

    @OneToMany(mappedBy = "rencana", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<BarangRencana> listBarangRencana; 

    @OneToMany(mappedBy = "rencana", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<LogRencana> logRencana;

    @NotNull
    @Column(name = "isDeleted", nullable=false)
    private Boolean isDeleted = false;

    @Column(name = "latestStatus")
    private String latestStatus;

    @OneToOne(mappedBy = "rencana", cascade = CascadeType.ALL)
    private Pengadaan pengadaan;
}
