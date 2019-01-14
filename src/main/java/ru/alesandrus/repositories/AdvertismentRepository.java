package ru.alesandrus.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.alesandrus.models.DmitryAdvertisement;

import java.util.Optional;

/**
 * @author Alexander Ivanov
 * @version 1.0
 * @since 13.01.2019
 */
public interface AdvertismentRepository extends CrudRepository<DmitryAdvertisement, Long> {
    Optional<DmitryAdvertisement> findByUrl(String url);
}
