package softuni.exam.models.dtos;

import com.google.gson.annotations.Expose;
import com.sun.istack.NotNull;
import org.hibernate.validator.constraints.Length;

public class PicturesSeedDto {
    @Expose
    private String name;
    @Expose
    private String dateAndTime;
    @Expose
    private Long car;

    public PicturesSeedDto() {
    }

    @Length(min = 3, max = 19)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public Long getCar() {
        return car;
    }

    public void setCar(Long car) {
        this.car = car;
    }
}
