package uk.ac.warwick.dcs.sherlock.module.web.data.models.db

import jakarta.persistence.*
import java.util.stream.Collectors

/**
 * The database table storing detectors assigned to a template
 */
@Entity
@Table(name = "detector")
open class TDetector(
    @JvmField
    @Column(name = "name")
    var name: String?,

    @JvmField
    @ManyToOne
    @JoinColumn(name = "template")
    var template: Template?
) {
    @JvmField
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = 0

    @JvmField
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "tDetector", cascade = [CascadeType.REMOVE])
    var parameters: MutableSet<TParameter> = mutableSetOf()

    val detectorParameters: Set<TParameter>
        get() = parameters.stream().filter { t: TParameter -> !t.postprocessing }.collect(Collectors.toSet())
    val postParameters: Set<TParameter>
        get() = parameters.stream().filter { obj: TParameter -> obj.postprocessing }.collect(Collectors.toSet())

    override operator fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TDetector || javaClass != other.javaClass) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
