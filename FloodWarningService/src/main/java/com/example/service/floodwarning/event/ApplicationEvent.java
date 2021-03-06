package com.example.service.floodwarning.event;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ApplicationEvent implements Serializable {
    private String eventId;
    private Date createdAt;
    private String eventType;
    private String correlationId;
    private String data;
}
