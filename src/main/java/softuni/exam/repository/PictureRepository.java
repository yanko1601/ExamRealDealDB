package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entities.Picture;

import java.util.List;
import java.util.Set;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> {

    Picture findPictureByName(String name);

    Set<Picture> findPicturesByCar_Id(Long carId);
}
