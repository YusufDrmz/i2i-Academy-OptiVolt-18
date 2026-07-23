package com.i2i.optivolt.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class HomeContact {
    private Long homeId;
    private String homeName;
    private String contactEmail;
}
