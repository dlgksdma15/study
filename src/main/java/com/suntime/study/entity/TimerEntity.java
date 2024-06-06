package com.suntime.study.entity;

import com.suntime.study.dto.TimerDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "subject_table")
public class TimerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    @Column
    private String subject;

    @Column
    private Integer hours;

    @Column
    private Integer minutes;

    @Column
    private Integer seconds;

    public void addTime(int hours, int minutes, int seconds) {
        this.hours += hours;
        this.minutes += minutes;
        this.seconds += seconds;

        // 초가 60을 넘으면 분으로 넘김
        this.minutes += this.seconds / 60;
        this.seconds %= 60;

        // 분이 60을 넘으면 시간으로 넘김
        this.hours += this.minutes / 60;
        this.minutes %= 60;
    }

    public static TimerEntity toTimerEntity(TimerDTO timerDTO){
        TimerEntity timerEntity = new TimerEntity();
        timerEntity.setEmail(timerDTO.getEmail());
        timerEntity.setSubject(timerDTO.getSubject());
        return timerEntity;
    }
}
