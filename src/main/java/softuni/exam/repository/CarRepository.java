package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entities.Car;

import java.util.Set;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Car findCarByMakeAndModelAndKilometers(String make, String model, Integer kilometers);

    Car findCarById(Long id);

    @Query("SELECT c FROM Car AS c ORDER BY c.pictures.size DESC, c.make")
    Set<Car> getCarsOrderByPicturesCountThenByMake();

}
