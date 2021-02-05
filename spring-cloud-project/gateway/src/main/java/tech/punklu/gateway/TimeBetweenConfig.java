package tech.punklu.gateway;

import lombok.Data;

import java.time.LocalTime;

@Data
public class TimeBetweenConfig {

    /**
     * 起始时间
     */
    private LocalTime start;

    /**
     * 结束时间
     */
    private LocalTime end;
}
