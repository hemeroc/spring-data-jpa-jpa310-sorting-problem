package com.example.demo

import com.github.marschall.threeten.jpa.zoned.hibernate.ZonedDateTimeType
import org.hibernate.annotations.Columns
import org.hibernate.annotations.Type
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}

@SpringBootApplication
class DemoApplication

@Component
class DemoRunner(
    val demoRepository: DemoRepository,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
            demoRepository.saveAll(
                listOf(
                    DemoEntity(name = "old", zonedDateTime = ZonedDateTime.now().minusDays(1)),
                    DemoEntity(name = "new", zonedDateTime = ZonedDateTime.now().plusDays(1)),
                )
            )
        val ascPaging = demoRepository.findAll(PageRequest.of(0, 10, ASC, "zonedDateTime")).content
        val ascCustom = demoRepository.findASC()
        val descPaging = demoRepository.findAll(PageRequest.of(0, 10, DESC, "zonedDateTime")).content
        val descCustom = demoRepository.findDESC()
        println("""
            ============================================================
            ascPaging:  $ascPaging
            ascCustom:  $ascCustom
            descPaging: $descPaging
            descCustom: $descCustom
            ============================================================
        """.trimIndent())
    }
}

@Repository
interface DemoRepository : JpaRepository<DemoEntity, Long> {
    @Query("SELECT d FROM DemoEntity d ORDER BY d.zonedDateTime DESC")
    fun findDESC(): List<DemoEntity>

    @Query("SELECT d FROM DemoEntity d ORDER BY d.zonedDateTime ASC")
    fun findASC(): List<DemoEntity>
}

@Entity
data class DemoEntity(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0,
    val name: String,
    @Type(type = ZonedDateTimeType.NAME)
    @Columns(
        columns = [
            Column(name = "TIMESTAMP_UTC"),
            Column(name = "ZONE_ID"),
        ]
    )
    val zonedDateTime: ZonedDateTime?,
)