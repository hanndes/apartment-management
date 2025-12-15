package com.group23.apartment_management.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Setter ve Getter'ları sağlar (setDoorNumber hatasını çözer)
@NoArgsConstructor // Parametresiz constructor'ı sağlar (new ApartmentDropdownDTO() hatasını çözer)
@AllArgsConstructor // 2 parametreli constructor'ı sağlar
public class ApartmentDropdownDTO {

    private int id;            // apartment_id
    private String doorNumber; // door_number
    public String getLabel() {
    return this.doorNumber;
}
}